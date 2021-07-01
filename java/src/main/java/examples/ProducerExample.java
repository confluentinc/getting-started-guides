package examples;

import org.apache.kafka.clients.producer.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ProducerExample {

    public static void main(final String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Please provide the configuration file path as a command line argument");
            System.exit(1);
        }

        // Load producer configuration settings from a local file
        final Properties props = loadConfig(args[0]);
        final String topic = "purchases";

        String[] users = { "eabara", "jsmith", "sgarcia", "jbernard", "htanaka", "awalther" };
        String[] items = { "book", "alarm clock", "t-shirts", "gift card", "batteries" };
        Producer<String, String> producer = new KafkaProducer<>(props);

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
                      System.out.printf("Produced event to topic %s: key = %-10s value = %s%n", topic, user, item);
              });
        }

        producer.flush();
        System.out.printf("%s events were produced to topic %s%n", numMessages, topic);
        producer.close();
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