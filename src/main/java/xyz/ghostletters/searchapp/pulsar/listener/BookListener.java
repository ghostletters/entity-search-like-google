package xyz.ghostletters.searchapp.pulsar.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClientException;
import xyz.ghostletters.searchapp.pulsar.event.BookChangeEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class BookListener {

    private static final String topicName = "persistent://public/default/foobar.public.book";

    @Inject
    ObjectMapper objectMapper;

    private Consumer consumer;

    MessageListener messageListener = (consumer, message) -> {
        try {
            String data = new String(message.getData());
            System.out.println("Message received: " + data);

            BookChangeEvent bookChangeEvent = objectMapper.readValue(data, BookChangeEvent.class);
            System.out.println(bookChangeEvent.getAfter().getTitle());

            consumer.acknowledge(message);
        } catch (PulsarClientException | JsonProcessingException e) {
            consumer.negativeAcknowledge(message);
            e.printStackTrace();
        }
    };

    public void onStart(@Observes StartupEvent startupEvent) throws PulsarClientException {
        consumer = PulsarClientProvider.buildConsumer(topicName, messageListener);
    }

    public void onStop(@Observes ShutdownEvent shutdownEvent) throws PulsarClientException {
        consumer.close();
    }
}
