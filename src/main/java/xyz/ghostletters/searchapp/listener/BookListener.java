package xyz.ghostletters.searchapp.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClientException;
import xyz.ghostletters.searchapp.event.BookChangeEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class BookListener {

    private static final String topicName = "persistent://public/default/foobar.public.book";

    @Inject
    ObjectMapper objectMapper;

    private Consumer consumer;

    MessageListener messageListener = ((consumer, msg) -> {
        String jsonData = new String(msg.getData());
        System.out.println(jsonData);

        try {
            BookChangeEvent bookChangeEvent = objectMapper.readValue(jsonData, BookChangeEvent.class);

            System.out.println(bookChangeEvent.getAfter().getTitle());

            consumer.acknowledge(msg.getMessageId());
        } catch (PulsarClientException | JsonProcessingException e) {
            e.printStackTrace();
            consumer.negativeAcknowledge(msg.getMessageId());
        }
    });

    public void onStart(@Observes StartupEvent startupEvent) throws PulsarClientException {
        this.consumer = PulsarClientProvider.buildConsumer(topicName, messageListener);
    }

    public void onStop(@Observes ShutdownEvent shutdownEvent) throws PulsarClientException {
        consumer.close();
    }
}
