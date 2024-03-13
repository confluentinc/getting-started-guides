package examples;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    private static final String TOPIC = "purchases";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String key, String value) {
           CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, key, value);

           future.whenComplete((result, ex) -> {
               if (ex == null) {
                   logger.info(String.format("Produced event to topic %s: key = %-10s value = %s", result.getRecordMetadata().topic(), key, value));
               } else {
                   ex.printStackTrace(System.out);
               }
           });
             
    }

}
