use std::collections::HashMap;
use std::time::Duration;

use confluent_kafka::common::serialization::StringDeserializer;
use confluent_kafka::consumer::AutoOffsetResetStrategy;
use confluent_kafka::consumer::ConsumerConfig;
use confluent_kafka::consumer::GroupProtocol;
use confluent_kafka::consumer::KafkaConsumer;

const TOPIC: &str = "purchases";
const POLL_TIMEOUT: Duration = Duration::from_millis(100);

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let props = HashMap::from([
        // User-specific properties that you must set
        (ConsumerConfig::BOOTSTRAP_SERVERS_CONFIG.to_string(), "localhost:<PLAINTEXT PORTS>".to_string()),
        // Fixed properties
        (ConsumerConfig::GROUP_ID_CONFIG.to_string(), "kafka-rust-getting-started".to_string()),
        (ConsumerConfig::AUTO_OFFSET_RESET_CONFIG.to_string(), AutoOffsetResetStrategy::EARLIEST.name()),
        (ConsumerConfig::GROUP_PROTOCOL_CONFIG.to_string(), GroupProtocol::Consumer.to_string()),
    ]);
    let config = ConsumerConfig::new(&props)?;
    let mut consumer =
        KafkaConsumer::new::<String, String>(config, Box::new(StringDeserializer), Box::new(StringDeserializer))?;

    consumer.subscribe_with_topics(vec![TOPIC.to_string()]).await?;

    loop {
        tokio::select! {
            _ = tokio::signal::ctrl_c() => {
                println!("Caught signal, terminating");
                break;
            }
            result = consumer.poll(POLL_TIMEOUT) => {
                match result {
                    Ok(records) => {
                        for record in &records {
                            println!(
                                "Consumed event from topic {}: key = {:<10} value = {}",
                                record.topic(),
                                record.key().map(String::as_str).unwrap_or_default(),
                                record.value().map(String::as_str).unwrap_or_default(),
                            );
                        }
                    }
                    // Errors are informational and automatically handled by the consumer.
                    Err(e) => eprintln!("poll failed: {e}"),
                }
            }
        }
    }

    consumer.close().await?;
    Ok(())
}
