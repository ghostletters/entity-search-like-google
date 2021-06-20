package xyz.ghostletters.searchapp.pulsar;

import io.quarkus.runtime.StartupEvent;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.common.io.SourceConfig;

import javax.enterprise.event.Observes;
import java.util.List;
import java.util.Map;

public class DebeziumSourceConnector {

    private static final String SOURCE_NAME = "debezium-postgres-source";
    private static final String TENANT = "public";
    private static final String NAMESPACE = "default";
    private static final String PULSAR_VERSION = "2.8.0";


    public void onStart(@Observes StartupEvent startupEvent) throws PulsarClientException, PulsarAdminException {
        PulsarAdmin admin = buildAdmin();

        if (isDebeziumRunning(admin)) {
            return; // do nothing
        }

        createSource(admin);
    }

    private void createSource(PulsarAdmin admin) throws PulsarAdminException {
        SourceConfig sourceConfig = buildSourceConfig();

        admin.sources().createSourceWithUrl(sourceConfig,
                "https://downloads.apache.org/pulsar/pulsar-" + PULSAR_VERSION +
                        "/connectors/pulsar-io-debezium-postgres-" + PULSAR_VERSION + ".nar");
    }

    private SourceConfig buildSourceConfig() {
        Map<String, Object> configMap = buildDebeziumConfig();

        // https://pulsar.apache.org/docs/en/io-debezium-source/#example-of-postgresql
        return SourceConfig.builder()
                .tenant(TENANT)
                .namespace(NAMESPACE)
                .name(SOURCE_NAME)
                .topicName("debezium-postgres-topic")
                .archive("/pulsar/connectors/pulsar-io-debezium-postgres-" + PULSAR_VERSION + ".nar")
                .parallelism(1)
                .configs(configMap)
                .build();
    }

    private Map<String, Object> buildDebeziumConfig() {
        // https://pulsar.apache.org/docs/en/io-debezium-source/#example-of-postgresql
        return Map.ofEntries(
                Map.entry("plugin.name", "pgoutput"),
                Map.entry("pulsar.service.url", "pulsar://127.0.0.1:6650"),
                Map.entry("database.hostname", "postgres"),
                Map.entry("database.port", "5432"),               // defined in docker-compose.yml
                Map.entry("database.user", "postgres"),           // defined in docker-compose.yml
                Map.entry("database.password", "changeme"),       // defined in docker-compose.yml
                Map.entry("database.dbname", "entity_search"),    // defined in docker-compose.yml
                Map.entry("database.server.name", "foobar"),
                Map.entry("schema.whitelist", "public"),
                Map.entry("table.whitelist", "public.book")
                // creates topic: persistent://public/default/foobar.public.book
        );
    }


    private boolean isDebeziumRunning(PulsarAdmin admin) throws PulsarAdminException {
        List<String> sources = admin.sources().listSources(TENANT, NAMESPACE);

        if (sources.contains(SOURCE_NAME)) {
            return admin.sources().getSourceStatus(TENANT, NAMESPACE, SOURCE_NAME)
                    .numRunning > 0;
        }
        return false;
    }

    private PulsarAdmin buildAdmin() throws PulsarClientException {
        return PulsarAdmin.builder()
                .serviceHttpUrl("http://localhost:8080/")
                .build();
    }
}
