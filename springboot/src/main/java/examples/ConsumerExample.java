package examples.springboot.kafka;

import org.apache.kafka.clients.consumer.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootApplication
public class ConsumerExample {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerExample.class, args);
    }

    @KafkaListener(topics = "purchases", groupId = "springboot")
    public void consume(final ConsumerRecord<String, String> consumerRecord) {
      log.info("received {} {}", consumerRecord.key(), consumerRecord.value());
    }
}
