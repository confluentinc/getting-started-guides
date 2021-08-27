package examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import examples.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

@RestController
@SpringBootApplication
public class SpringBootWithKafkaApplication {

    private final Producer producer;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWithKafkaApplication.class, args);
    }

    @Autowired
    SpringBootWithKafkaApplication(Producer producer) {
        this.producer = producer;
    }

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @PostMapping(value = "/produce")
    public void sendMessageToKafkaTopic(@RequestParam("key") String key, @RequestParam("value") String value) {
        this.producer.sendMessage(key, value);
    }

    @GetMapping(path = "/start-consume")
    public String start() {
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer("myConsumer");
        listenerContainer.start();
        return "Started consumer";
    }

    @GetMapping(path = "/stop-consume")
    public String stop() {
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer("myConsumer");
        listenerContainer.stop();
        return "Stopped consumer";
    }

}
