package examples;

import org.apache.kafka.clients.producer.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ProducerExample {

    private final KafkaTemplate<String, String> producer;
    private final String topic = "purchase";

    public void produce() {
        String[] users = { "eabara", "jsmith", "sgarcia", "jbernard", "htanaka", "awalther" };
        String[] items = { "book", "alarm clock", "t-shirts", "gift card", "batteries" };

        final Long numMessages = 10L;
        for (Long i = 0L; i < numMessages; i++) {
            Random rnd = new Random();
            String user = users[rnd.nextInt(users.length)];
            String item = items[rnd.nextInt(items.length)];

            ListenableFuture<SendResult<String, String>> future = producer.send(topic, user, item);
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onSuccess(SendResult<String, String> result) {
                    log.info("Produced event to topic %s: key = %-10s value = %s%n", topic, user, item);
                }
                @Override
                public void onFailure(Throwable ex) {
                    ex.printStackTrace();
                }
            });
        }

        producer.flush();
        log.info("%s events were produced to topic %s%n", numMessages, topic);
    }


    /**
     * We'll reuse this function to load properties from the Consumer as well
     */
    public static Properties loadConfig(final String configFile) throws IOException {
        if (!Files.exists(Paths.get(configFile))) {
            throw new IOException(configFile + " not found.");
        }
        final Properties cfg = new Properties();
        try (InputStream inputStream = new FileInputStream(configFile)) {
            cfg.load(inputStream);
        }
        return cfg;
    }
}
