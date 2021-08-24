package examples;

import org.apache.kafka.clients.consumer.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ConsumerExample {

    @KafkaListener(topics = "purchases")
    public void consume(final ConsumerRecord<String, String> consumerRecord) {
      log.info("received {} {}", consumerRecord.key(), consumerRecord.value());
    }

}
