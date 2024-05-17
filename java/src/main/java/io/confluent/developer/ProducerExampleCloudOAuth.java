package io.confluent.developer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.Random;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.SECURITY_PROTOCOL_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.*;
import static org.apache.kafka.common.config.SaslConfigs.*;

public class ProducerExample {

    public static void main(final String[] args) {
        final Properties props = new Properties() {{
            // User-specific properties that you must set
            put(BOOTSTRAP_SERVERS_CONFIG, "<BOOTSTRAP SERVERS>");
            put("sasl.oauthbearer.client.id", "<OAUTH2 CLIENT ID>");
            put("sasl.oauthbearer.client.secret", "<OAUTH2 CLIENT SECRET>");
            put("sasl.oauthbearer.token.endpoint.url", "<OAUTH2 TOKEN ENDPOINT URL>");
            put("sasl.oauthbearer.scope", "<OAUTH2 SCOPE>");
            put("sasl.oauthbearer.extensions", "logicalCluster=<LOGICAL CLUSTER ID>,identityPoolId=<IDENTITY POOL ID>,");

            // Fixed properties
            put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
            put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
            put(ACKS_CONFIG, "all");
            put(SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            put(SASL_MECHANISM, "OAUTHBEARER");
            put("sasl.oauthbearer.method", "OIDC");
        }};

        final String topic = "purchases";

        String[] users = {"eabara", "jsmith", "sgarcia", "jbernard", "htanaka", "awalther"};
        String[] items = {"book", "alarm clock", "t-shirts", "gift card", "batteries"};
        try (final Producer<String, String> producer = new KafkaProducer<>(props)) {
            final Random rnd = new Random();
            final int numMessages = 10;
            for (int i = 0; i < numMessages; i++) {
                String user = users[rnd.nextInt(users.length)];
                String item = items[rnd.nextInt(items.length)];

                producer.send(
                        new ProducerRecord<>(topic, user, item),
                        (event, ex) -> {
                            if (ex != null)
                                ex.printStackTrace();
                            else
                                System.out.printf("Produced event to topic %s: key = %-10s value = %s%n", topic, user, item);
                        });
            }
            System.out.printf("%s events were produced to topic %s%n", numMessages, topic);
        }

    }
}