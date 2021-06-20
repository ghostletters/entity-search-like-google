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

public class DebeziumSourceConnector {

    public static final String SOURCE_NAME = "debezium-postgres-source";
    public static final String TENANT = "public";
    public static final String NAMESPACE = "default";
    public static final String PULSAR_VERSION = "2.7.2";

    public void onStart(@Observes StartupEvent startupEvent) throws PulsarClientException, PulsarAdminException {
        PulsarAdmin admin = buildPulsarAdmin();

        if (isDebeziumAlreadyRunning(admin)) {
            System.out.println(SOURCE_NAME + " is already running. Do not try to create it.");
            return;
        }

        createSource(admin, buildDebeziumConfig());
    }

    private PulsarAdmin buildPulsarAdmin() throws PulsarClientException {
        return PulsarAdmin.builder()
                .serviceHttpUrl("http://localhost:8080/")
                .build();
    }

    private boolean isDebeziumAlreadyRunning(PulsarAdmin admin) throws PulsarAdminException {
        List<String> sources = admin.sources().listSources(TENANT, NAMESPACE);

        if (sources.contains(SOURCE_NAME)) {
            SourceStatus sourceStatus = admin.sources().getSourceStatus(TENANT, NAMESPACE, SOURCE_NAME);
            return sourceStatus.numRunning > 0;
        }

        return false;
    }

    private void createSource(PulsarAdmin admin, SourceConfig sourceConfig) {
        System.out.println("Downloading debezium source connector...");

        admin.sources().createSourceWithUrlAsync(sourceConfig,
                "https://downloads.apache.org/pulsar/pulsar-" + PULSAR_VERSION +
                        "/connectors/pulsar-io-debezium-postgres-" + PULSAR_VERSION + ".nar");
//             Do avoid heavy download (160 MB)
//             admin.sources().createSource(sourceConfig, "../docker/pulsar/pulsar-io-debezium-postgres-" +
//                                    PULSAR_VERSION + ".nar");
    }

    private SourceConfig buildDebeziumConfig() {
        Map<String, Object> configMap = buildDebeziumSpecificConfig();

        return SourceConfig.builder()
                .tenant(TENANT)
                .namespace(NAMESPACE)
                .name(SOURCE_NAME)
                .topicName("debezium-postgres-topic")
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
}
