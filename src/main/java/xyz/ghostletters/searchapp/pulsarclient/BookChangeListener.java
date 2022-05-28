package xyz.ghostletters.searchapp.pulsarclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClientException;
import xyz.ghostletters.searchapp.elastic.BookSearchService;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class BookChangeListener {

    private static final String topicName = "persistent://public/default/foobar.public.book";

    @Inject
    ObjectMapper objectMapper;

    @Inject
    BookSearchService bookSearchService;

    private Consumer pulsarConsumer;

    private MessageListener messageListener = ((consumer, msg) -> {
        String jsonData = new String(msg.getData());
        System.out.println(jsonData);

        try {
            BookEvent bookEvent = objectMapper.readValue(jsonData, BookEvent.class);
            bookSearchService.addToIndex(bookEvent.after());

            consumer.acknowledge(msg.getMessageId());
        } catch (PulsarClientException | JsonProcessingException e) {
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
