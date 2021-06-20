package xyz.ghostletters.searchapp.pulsar;

import io.quarkus.runtime.StartupEvent;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.common.io.SourceConfig;
import org.apache.pulsar.common.policies.data.SourceStatus;

import javax.enterprise.event.Observes;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class PulsarSourceAdmin {

    public static final String SOURCE_NAME = "debezium-postgres-source";
    public static final String TENANT = "public";
    public static final String NAMESPACE = "default";
    public static final String DEBEZIUM_POSTGRES_2_7_2_NAR = "https://downloads.apache.org/pulsar/pulsar-2.7.2/connectors/pulsar-io-debezium-postgres-2.7.2.nar";

    public void onStart(@Observes StartupEvent startupEvent) {
        PulsarAdmin admin = buildPulsarAdmin();

        if (isDebeziumAlreadyRunning(admin)) {
            System.out.println(SOURCE_NAME + " is already running. Do not try to create it.");
            return;
        }

        SourceConfig sourceConfig = buildDebeziumConfig();

        createSource(admin, sourceConfig);
    }

    private boolean isDebeziumAlreadyRunning(PulsarAdmin admin) {
        try {
            List<String> sources = admin.sources().listSources(TENANT, NAMESPACE);

            if (sources.contains(SOURCE_NAME)) {
                SourceStatus sourceStatus = admin.sources().getSourceStatus(TENANT, NAMESPACE, SOURCE_NAME);
                return sourceStatus.numRunning > 0;
            }
        } catch (PulsarAdminException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void createSource(PulsarAdmin admin, SourceConfig sourceConfig) {
            admin.sources().createSourceWithUrlAsync(
                    sourceConfig,
                    DEBEZIUM_POSTGRES_2_7_2_NAR
            );
            // Do avoid heavy download (160 MB)
            // admin.sources().createSource(sourceConfig, "../docker/pulsar/pulsar-io-debezium-postgres-2.7.2.nar");
    }

    private SourceConfig buildDebeziumConfig() {
        Map<String, Object> configMap = buildDebeziumSpecificConfig();

        return SourceConfig.builder()
                .tenant(TENANT)
                .namespace(NAMESPACE)
                .name(SOURCE_NAME)
                .topicName("debezium-postgres-source")
                .archive("/pulsar/connectors/pulsar-io-debezium-postgres-2.7.2.nar")
                .parallelism(1)
                .configs(configMap)
                .build();
    }

    private Map<String, Object> buildDebeziumSpecificConfig() {
        return Map.ofEntries(
                entry("database.hostname", "postgres"),
                entry("database.port", "5432"),
                entry("database.user", "postgres"),
                entry("database.password", "changeme"),
                entry("database.dbname", "entity_search"),
                entry("database.server.name", "foobar"),
                entry("plugin.name", "pgoutput"),
                entry("schema.whitelist", "public"),
                entry("table.whitelist", "public.book"),
                entry("pulsar.service.url", "pulsar://127.0.0.1:6650")
        );
    }

    private PulsarAdmin buildPulsarAdmin() {
        String url = "http://localhost:8080/";
        boolean useTls = false;
        boolean tlsAllowInsecureConnection = false;
        String tlsTrustCertsFilePath = null;
        try {
            return PulsarAdmin.builder()
                    .serviceHttpUrl(url)
                    .tlsTrustCertsFilePath(tlsTrustCertsFilePath)
                    .allowTlsInsecureConnection(tlsAllowInsecureConnection)
                    .build();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
        return null;
    }
}
