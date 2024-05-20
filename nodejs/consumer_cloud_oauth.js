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
    'bootstrap.servers':                   '<BOOTSTRAP SERVERS>',
    'sasl.oauthbearer.client.id':          '<OAUTH2 CLIENT ID>',
    'sasl.oauthbearer.client.secret':      '<OAUTH2 CLIENT SECRET>',
    'sasl.oauthbearer.token.endpoint.url': '<OAUTH2 TOKEN ENDPOINT URL>',
    'sasl.oauthbearer.scope':              '<OAUTH2 SCOPE>',
    'sasl.oauthbearer.extensions':         'logicalCluster=<LOGICAL CLUSTER ID>,identityPoolId=<IDENTITY POOL ID>',

    // Fixed properties
    'security.protocol':       'SASL_SSL',
    'sasl.mechanisms':         'OAUTHBEARER',
    'sasl.oauthbearer.method': 'OIDC',
    'group.id':                'kafka-nodejs-getting-started',
    'auto.offset.reset':       'earliest'
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
