package examples;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.clients.admin.NewTopic;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Component
public class ProducerExample {

    private final KafkaTemplate<String, String> producer;
    private final NewTopic topic;

    public void produce() {
        String[] users = { "eabara", "jsmith", "sgarcia", "jbernard", "htanaka", "awalther" };
        String[] items = { "book", "alarm clock", "t-shirts", "gift card", "batteries" };

        final Long numMessages = 10L;
        for (Long i = 0L; i < numMessages; i++) {
            Random rnd = new Random();
            String user = users[rnd.nextInt(users.length)];
            String item = items[rnd.nextInt(items.length)];

            producer.send(
              new ProducerRecord<>(topic, user, item),
              (event, ex) -> {
                  if (ex != null)
                      ex.printStackTrace();
                  else
                      log.info("Produced event to topic %s: key = %-10s value = %s%n", topic, user, item);
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
