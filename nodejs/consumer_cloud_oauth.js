const Kafka = require('@confluentinc/kafka-javascript');
require("dotenv").config();

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
    'bootstrap.servers':                   process.env.BOOTSTRAP_SERVERS,
    'sasl.oauthbearer.client.id':          process.env.OAUTH2_CLIENT_ID,
    'sasl.oauthbearer.client.secret':      process.env.OAUTH2_CLIENT_SECRET,
    'sasl.oauthbearer.token.endpoint.url': process.env.OAUTH2_TOKEN_ENDPOINT_URL,
    'sasl.oauthbearer.scope':              process.env.OAUTH2_SCOPE,
    'sasl.oauthbearer.extensions':         `logicalCluster=${process.env.LOGICAL_CLUSTER_ID},identityPoolId=${process.env.IDENTITY_POOL_ID}`,

    // Fixed properties
    'security.protocol':       'SASL_SSL',
    'sasl.mechanisms':         'OAUTHBEARER',
    'sasl.oauthbearer.method': 'OIDC',
    'group.id':                'kafka-nodejs-getting-started'
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
