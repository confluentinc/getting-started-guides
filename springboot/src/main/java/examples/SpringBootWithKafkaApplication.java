package examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import examples.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping(value = "/produce")
    public void sendMessageToKafkaTopic(@RequestParam("key") String key, @RequestParam("value") String value) {
        this.producer.sendMessage(key, value);
    }

}
