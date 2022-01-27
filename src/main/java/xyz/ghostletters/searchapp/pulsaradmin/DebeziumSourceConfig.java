package xyz.ghostletters.searchapp.pulsaradmin;

import io.quarkus.runtime.StartupEvent;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.common.io.SourceConfig;

import javax.enterprise.event.Observes;
import java.util.Map;

public class DebeziumSourceConfig {

    private static final String PULSAR_VERSION = "2.9.1";
    private static final String TENANT = "public";
    private static final String NAMESPACE = "default";
    private static final String SOURCE_NAME = "debezium-postgres-source";

    public void onStart(@Observes StartupEvent startupEvent) throws PulsarClientException, PulsarAdminException {
        PulsarAdmin admin = buildAdmin();

        if (isDebeziumRunning(admin)) {
            return; // do nothing
        }

        buildDebeziumSource(admin);
    }

    private boolean isDebeziumRunning(PulsarAdmin admin) throws PulsarAdminException {
        return admin.sources().listSources(TENANT, NAMESPACE)
                .contains(SOURCE_NAME);
    }

    private void buildDebeziumSource(PulsarAdmin admin) throws PulsarAdminException {
        SourceConfig sourceConfig = buildSourceConfig();

        admin.sources().createSourceWithUrl(sourceConfig,
                "https://downloads.apache.org/pulsar/pulsar-" + PULSAR_VERSION +
                        "/connectors/pulsar-io-debezium-postgres-" + PULSAR_VERSION + ".nar");
    }

    private SourceConfig buildSourceConfig() {
        // https://pulsar.apache.org/docs/en/io-debezium-source/#example-of-postgresql
        return SourceConfig.builder()
                .tenant(TENANT)
                .namespace(NAMESPACE)
                .name(SOURCE_NAME)
                .topicName("debezium-postgres-topic")
                .archive("/pulsar/connectors/pulsar-io-debezium-postgres-" + PULSAR_VERSION + ".nar")
                .parallelism(1)
                .configs(buildDebeziumConfig())
                .build();
    }

    private Map<String, Object> buildDebeziumConfig() {
        // https://pulsar.apache.org/docs/en/io-debezium-source/#example-of-postgresql
        return Map.ofEntries(
                Map.entry("database.server.name", "foobar"),
                Map.entry("schema.include.list", "public"),
                Map.entry("table.include.list", "public.book"),
                // creates topic: persistent://public/default/foobar.public.book
                // naming schema: persistent://<TENANT>/<NAMESPACE>/<database.server.name>.<DATABASE_SCHEMA>.<TABLE>

                Map.entry("plugin.name", "pgoutput"),             // requires Postgres 10+
                Map.entry("database.history.pulsar.service.url", "pulsar://127.0.0.1:6650"),
                Map.entry("database.hostname", "postgres"),
                Map.entry("database.port", "5432"),               // defined in docker-compose.yml
                Map.entry("database.user", "postgres"),           // defined in docker-compose.yml
                Map.entry("database.password", "changeme"),       // defined in docker-compose.yml
                Map.entry("database.dbname", "entity_search"),    // defined in docker-compose.yml
                Map.entry("poll.interval.ms", "100")              // OPTIONAL; default 1000
        );
    }

    private PulsarAdmin buildAdmin() throws PulsarClientException {
        return PulsarAdmin.builder()
                .serviceHttpUrl("http://localhost:8080")
                .build();
    }
}
