package examples.controllers;

import examples.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {

    private final Producer producer;

    @Autowired
    KafkaController(Producer producer) {
        this.producer = producer;
    }

    @PostMapping(value = "/produce")
    public void sendMessageToKafkaTopic(@RequestParam("key") String key, @RequestParam("value") String value) {
        this.producer.sendMessage(key, value);
    }
}
