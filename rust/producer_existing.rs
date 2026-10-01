use std::collections::HashMap;

use confluent_kafka::common::serialization::StringSerializer;
use confluent_kafka::producer::Callback;
use confluent_kafka::producer::KafkaProducer;
use confluent_kafka::producer::Producer;
use confluent_kafka::producer::ProducerConfig;
use confluent_kafka::producer::ProducerRecord;
use rand::Rng;

const TOPIC: &str = "purchases";
const USERS: [&str; 6] = ["eabara", "jsmith", "sgarcia", "jbernard", "htanaka", "awalther"];
const ITEMS: [&str; 5] = ["book", "alarm clock", "t-shirts", "gift card", "batteries"];

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let props = HashMap::from([
        // User-specific properties that you must set
        (ProducerConfig::BOOTSTRAP_SERVERS_CONFIG.to_string(), "<BOOTSTRAP SERVERS>".to_string()),
        // Fixed properties
        (ProducerConfig::ACKS_CONFIG.to_string(), "all".to_string()),
    ]);
    let config = ProducerConfig::new(&props)?;
    let producer =
        KafkaProducer::<String, String>::new(config, Box::new(StringSerializer::new()), Box::new(StringSerializer::new()))?;

    let mut rng = rand::rng();
    for _ in 0..10 {
        let key = USERS[rng.random_range(0..USERS.len())].to_string();
        let value = ITEMS[rng.random_range(0..ITEMS.len())].to_string();
        let record = ProducerRecord::with_key(TOPIC.to_string(), Some(key.clone()), Some(value.clone()));

        // Delivery report callback, invoked once the broker acks the record
        // (or the send fails).
        let callback: Callback = Box::new(move |metadata, error| {
            if let Some(error) = error {
                println!("Failed to deliver message: {error}");
            } else if let Some(metadata) = metadata {
                println!("Produced event to topic {}: key = {key:<10} value = {value}", metadata.topic());
            }
        });

        producer.send_with_callback(record, Some(callback)).await?;
    }

    // Wait for all messages to be delivered.
    producer.flush().await?;
    producer.close().await?;

    Ok(())
}
