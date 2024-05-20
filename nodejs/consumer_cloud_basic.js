const Kafka = require('node-rdkafka');


function createConsumer(config, onData) {
  const consumer = new Kafka.KafkaConsumer(config, {'auto.offset.reset': 'earliest'});

  return new Promise((resolve, reject) => {
    consumer
     .on('ready', () => resolve(consumer))
     .on('data', onData);

    consumer.connect();
  });
};


async function consumerExample() {
  const config = {
    // User-specific properties that you must set
    'bootstrap.servers': '<BOOTSTRAP SERVERS>',
    'sasl.username':     '<CLUSTER API KEY>',
    'sasl.password':     '<CLUSTER API SECRET>',

    // Fixed properties
    'security.protocol': 'SASL_SSL',
    'sasl.mechanisms':   'PLAIN',
    'group.id':          'kafka-nodejs-getting-started'
  }

  let topic = "purchases";

  const consumer = await createConsumer(config, ({key, value}) => {
    let k = key.toString().padEnd(10, ' ');
    console.log(`Consumed event from topic ${topic}: key = ${k} value = ${value}`);
  });

  consumer.subscribe([topic]);
  consumer.consume();

  process.on('SIGINT', () => {
    console.log('\nDisconnecting consumer ...');
    consumer.disconnect();
  });
}

consumerExample()
  .catch((err) => {
    console.error(`Something went wrong:\n${err}`);
    process.exit(1);
  });
