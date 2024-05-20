#!/usr/bin/env python

from confluent_kafka import Consumer

if __name__ == '__main__':

    config = {
        # User-specific properties that you must set
        'bootstrap.servers':                   '<BOOTSTRAP SERVERS>',
        'sasl.oauthbearer.client.id':          '<OAUTH2 CLIENT ID>',
        'sasl.oauthbearer.client.secret':      '<OAUTH2 CLIENT SECRET>',
        'sasl.oauthbearer.token.endpoint.url': '<OAUTH2 TOKEN ENDPOINT URL>',
        'sasl.oauthbearer.scope':              '<OAUTH2 SCOPE>',
        'sasl.oauthbearer.extensions':         'logicalCluster=<LOGICAL CLUSTER ID>,identityPoolId=<IDENTITY POOL ID>',

        # Fixed properties
        'security.protocol':       'SASL_SSL',
        'sasl.mechanisms':         'OAUTHBEARER',
        'sasl.oauthbearer.method': 'OIDC',
        'group.id':                'kafka-python-getting-started',
        'auto.offset.reset':       'earliest'
    }

    # Create Consumer instance
    consumer = Consumer(config)

    # Subscribe to topic
    topic = "purchases"
    consumer.subscribe([topic])

    # Poll for new messages from Kafka and print them.
    try:
        while True:
            msg = consumer.poll(1.0)
            if msg is None:
                # Initial message consumption may take up to
                # `session.timeout.ms` for the consumer group to
                # rebalance and start consuming
                print("Waiting...")
            elif msg.error():
                print("ERROR: %s".format(msg.error()))
            else:
                # Extract the (optional) key and value, and print.
                print("Consumed event from topic {topic}: key = {key:12} value = {value:12}".format(
                    topic=msg.topic(), key=msg.key().decode('utf-8'), value=msg.value().decode('utf-8')))
    except KeyboardInterrupt:
        pass
    finally:
        # Leave group and commit final offsets
        consumer.close()
