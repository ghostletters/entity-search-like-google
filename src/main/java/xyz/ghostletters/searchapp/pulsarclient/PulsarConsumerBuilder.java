package xyz.ghostletters.searchapp.pulsarclient;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class PulsarConsumerBuilder {
    public static Consumer from(String topicName, MessageListener messageListener) throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl("pulsar://localhost:6650")
                .build()

                .newConsumer()
                .topic(topicName)
                .messageListener(messageListener)
                .subscriptionName("whatever")
                .subscribe();
    }
}
