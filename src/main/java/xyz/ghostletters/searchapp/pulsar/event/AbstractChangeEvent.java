package xyz.ghostletters.searchapp.pulsar.event;

public abstract class AbstractChangeEvent {

    private Source source;

    private String op;

    private Long ts_ms;
}
