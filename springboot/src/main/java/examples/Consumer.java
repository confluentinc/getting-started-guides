package examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

import java.io.IOException;

@Service
public class Consumer {

    private final Logger logger = LoggerFactory.getLogger(Producer.class);

    @KafkaListener(topics = "purchases", groupId = "spring-boot")
    public void listen(String value,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key) {
        //logger.info(String.format("Consumed event from topic %s: key = %-10s value = %s", record.topic(), record.key(), record.value()));
        logger.info(String.format("Consumed event from topic %s: key = %-10s value = %s%n", topic, key, value));
    }
}
