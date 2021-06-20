package xyz.ghostletters.searchapp.pulsar.event;

// not strictly needed for showcase
public abstract class AbstractChangeEvent {

    private Source source;

    private String op;

    private Long ts_ms;
}
