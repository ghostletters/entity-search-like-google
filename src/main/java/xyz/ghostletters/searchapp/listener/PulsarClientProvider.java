package xyz.ghostletters.searchapp.listener;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class PulsarClientProvider {

    public static Consumer buildConsumer(String topic, MessageListener<byte[]> messageListener) throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl("pulsar://localhost:6650")
                .build()

                .newConsumer()
                .topic(topic)
                .subscriptionName("whatever")
                .messageListener(messageListener)
                .subscribe();
    }
}
