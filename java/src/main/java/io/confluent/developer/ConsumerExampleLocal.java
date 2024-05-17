package io.confluent.developer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class ConsumerExample {

    public static void main(final String[] args) {
        final Properties props = new Properties() {{
            // User-specific properties that you must set
            put(BOOTSTRAP_SERVERS_CONFIG, "localhost:<PLAINTEXT PORTS>");

            // Fixed properties
            put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
            put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
            put(GROUP_ID_CONFIG, "kafka-java-getting-started");
            put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        }};

        final String topic = "purchases";

        try (final Consumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Arrays.asList(topic));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    String key = record.key();
                    String value = record.value();
                    System.out.println(
                            String.format("Consumed event from topic %s: key = %-10s value = %s", topic, key, value));
                }
            }
        }
    }

}
