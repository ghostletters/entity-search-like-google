package xyz.ghostletters.searchapp.pulsar.listener;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class PulsarClientProvider {

    public static Consumer buildConsumer(String topicName, MessageListener<byte[]> messageListener) throws PulsarClientException {
        return buildClient().newConsumer()
                .topic(topicName)
                .subscriptionName("whatever")
                .messageListener(messageListener)
                .subscribe();
    }

    private static PulsarClient buildClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl("pulsar://localhost:6650")
                .build();
    }
}
