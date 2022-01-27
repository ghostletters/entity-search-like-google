package xyz.ghostletters.searchapp.pulsarclient;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClientException;

import javax.enterprise.event.Observes;

public class BookChangeListener {

    private static final String topicName = "persistent://public/default/foobar.public.book";

    private Consumer pulsarConsumer;

    private MessageListener messageListener = ((consumer, msg) -> {
        String jsonData = new String(msg.getData());
        System.out.println(jsonData);

        try {
            consumer.acknowledge(msg.getMessageId());
        } catch (PulsarClientException e) {
            e.printStackTrace();
            consumer.negativeAcknowledge(msg.getMessageId());
        }
    });

    public void onStart(@Observes StartupEvent startupEvent) throws PulsarClientException {
        pulsarConsumer = PulsarConsumerBuilder.from(topicName, messageListener);
    }

    public void onStop(@Observes ShutdownEvent shutdownEvent) throws PulsarClientException {
        pulsarConsumer.close();
    }
}
