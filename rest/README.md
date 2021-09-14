---
seo:
  title: Apache Kafka and REST - Getting Started Tutorial 
  description: How to develop your first Kafka client application against the Confluent REST Proxy, which produces and consumes messages from a Kafka cluster, complete with configuration instructions. 
hero:
  title: Getting Started with Apache Kafka and Confluent REST Proxy
  description: Step-by-step guide to building a client application for Kafka using the Confluent REST Proxy
---

# Getting Started with Apache Kafka and REST Proxy

## Introduction

In this tutorial, you will use the Confluent REST Proxy to produce and consume messages from an Apache Kafka® cluster. The tutorial will walk you through setting up a Kafka cluster if you do not already have access to one.

## Prerequisites

This guide assumes that you already have:

[Docker](https://docs.docker.com/get-docker/) installed

[Docker Compose](https://docs.docker.com/compose/install/) installed

[curl](https://curl.se/) installed

Later in this tutorial you will set up a new Kafka cluster or connect
to an existing one. The simplest way to get started is to create
your cluster in [Confluent Cloud](https://www.confluent.io/confluent-cloud/tryfree/).
If you wish instead to run a local Kafka cluster, you will
use Docker.

## Create Project

Create a new directory anywhere you’d like for this project:

```sh
mkdir kafka-restproxy-getting-started && cd kafka-restproxy-getting-started
```

## Kafka Setup

We are going to need a Kafka Cluster for our client application to
operate with. This dialog can help you configure your Confluent Cloud
cluster, create a Kafka cluster for you, or help you input an existing
cluster bootstrap server to connect to.

<p>
  <div class="select-wrapper">
    <select data-context="true" name="kafka.broker">
      <option value="">Select Kafka Broker</option>
      <option value="cloud">Confluent Cloud</option>
      <option value="local">Local</option>
      <option value="other">Other</option>
    </select>
  </div>
</p>

<section data-context-key="kafka.broker" data-context-value="cloud">

<p>
  <label for="kafka-broker-server">Bootstrap Server</label>
  <input id="kafka-broker-server" data-context="true" name="kafka.broker.server" placeholder="cluster-id.region.provider.confluent.cloud:9092" />
</p>

After you sign up for [Confluent Cloud](https://www.confluent.io/confluent-cloud/tryfree/)
and provision your Kafka cluster,
paste your Confluent Cloud bootstrap server setting above and the
tutorial will fill in the appropriate configuration for
you.

You can obtain your Confluent Cloud Kafka cluster bootstrap server
configuration using the [Confluent Cloud Console](https://confluent.cloud/).

![](../media/cc-cluster-settings.png)

</section>

<section data-context-key="kafka.broker" data-context-value="local">
  
Paste the following file into a `docker-compose.yml` file:

```yaml file=../docker-compose.yml
```

Now start the Kafka broker with the new `docker compose` command (see the [Docker
documentation for more information](https://docs.docker.com/compose/cli-command/#new-docker-compose-command)).

```docker compose up -d
```

</section>

<section data-context-key="kafka.broker" data-context-value="other">
  
<p>
  <label for="kafka-broker-server">Bootstrap Server</label>
  <input id="kafka-broker-server" data-context="true" name="kafka.broker.server" placeholder="broker:9092" />
</p>

Paste your Kafka cluster bootstrap server URL above and the tutorial will
fill it into the appropriate configuration for you.

</section>

## Configuration

Paste the following configuration data into a file at `getting-started.properties`

<section data-context-key="kafka.broker" data-context-value="cloud">

The below configuration includes the required settings for a connection
to Confluent Cloud including the bootstrap servers configuration you
provided. 

![](../media/cc-create-key.png)

When using Confluent Cloud you will be required to provide an API key
and secret authorizing your application to produce and consume. You can
use the [Cloud Console](https://confluent.cloud/) to create a key for
you.

Take note of the API key and secret and add them to the configuraiton file 
in the `username` and `password` fields of the `SASL_JAAS_CONFIG` value.

```ini file=getting-started-cloud.properties
```

</section>

<section data-context-key="kafka.broker" data-context-value="local">

```ini file=getting-started-local.properties
```

</section>

<section data-context-key="kafka.broker" data-context-value="other">

The below configuration file includes the bootstrap servers
configuration you provided. If your Kafka Cluster requires different
client security configuration, you may require [different
settings](https://kafka.apache.org/documentation/#security).

```ini file=getting-started-other.properties
```

</section>

## Create Topic

Events in Kafka are organized and durably stored in named topics. Topics
have parameters that determine the performance and durability guarantees
of the events that flow through them.

Create a new topic, `purchases`, which we will use to produce and consume
events.

<section data-context-key="kafka.broker" data-context-value="cloud">

![](../media/cc-create-topic.png)

When using Confluent Cloud, you can use the [Cloud
Console](https://confluent.cloud/) to create a topic. Create a topic
with 1 partition and defaults for the remaining settings.

</section>

<section data-context-key="kafka.broker" data-context-value="local">

We'll use the `kafka-topics` command located inside the local running
Kafka broker:

```sh file=../create-topic.sh
```
</section>

<section data-context-key="kafka.broker" data-context-value="other">

Depending on your available Kafka cluster, you have multiple options
for creating a topic. You may have access to [Confluent Control
Center](https://docs.confluent.io/platform/current/control-center/index.html),
where you can [create a topic with a
UI](https://docs.confluent.io/platform/current/control-center/topics/create.html). You
may have already installed a Kafka distribution, in which case you can
use the [kafka-topics command](https://kafka.apache.org/documentation/#basic_ops_add_topic).
Note that, if your cluster is centrally managed, you may need to
request the creation of a topic from your operations team.

</section>

## Start REST Proxy

First you need to start the Confluent REST Proxy locally, which you will run in Docker.

Paste the following REST proxy configuration into a new file called `rest-proxy.yml`: 


<section data-context-key="kafka.broker" data-context-value="cloud">

```yaml
---
version: '2'
services:

  rest-proxy:
    image: confluentinc/cp-kafka-rest:6.1.1
    ports:
      - 8082:8082
    hostname: rest-proxy
    container_name: rest-proxy
    environment:
      KAFKA_REST_HOST_NAME: rest-proxy
      KAFKA_REST_LISTENERS: "http://0.0.0.0:8082"
      KAFKA_REST_BOOTSTRAP_SERVERS: $BOOTSTRAP_SERVERS
      KAFKA_REST_SECURITY_PROTOCOL: "$SECURITY_PROTOCOL"
      KAFKA_REST_SASL_JAAS_CONFIG: $SASL_JAAS_CONFIG
      KAFKA_REST_SASL_MECHANISM: "PLAIN"
      KAFKA_REST_CLIENT_BOOTSTRAP_SERVERS: $BOOTSTRAP_SERVERS
      KAFKA_REST_CLIENT_SECURITY_PROTOCOL: "SASL_SSL"
      KAFKA_REST_CLIENT_SASL_JAAS_CONFIG: $SASL_JAAS_CONFIG
      KAFKA_REST_CLIENT_SASL_MECHANISM: "PLAIN"
```

</section>

<section data-context-key="kafka.broker" data-context-value="local">

```yaml
---
version: '2'
services:

  rest-proxy:
    image: confluentinc/cp-kafka-rest:6.1.1
    ports:
      - 8082:8082
    hostname: rest-proxy
    container_name: rest-proxy
    environment:
      KAFKA_REST_HOST_NAME: rest-proxy
      KAFKA_REST_LISTENERS: "http://0.0.0.0:8082"
      KAFKA_REST_BOOTSTRAP_SERVERS: $BOOTSTRAP_SERVERS
```

</section>
<section data-context-key="kafka.broker" data-context-value="other">

```yaml
---
version: '2'
services:

  rest-proxy:
    image: confluentinc/cp-kafka-rest:6.1.1
    ports:
      - 8082:8082
    hostname: rest-proxy
    container_name: rest-proxy
    environment:
      KAFKA_REST_HOST_NAME: rest-proxy
      KAFKA_REST_LISTENERS: "http://0.0.0.0:8082"
      KAFKA_REST_BOOTSTRAP_SERVERS: $BOOTSTRAP_SERVERS
```
The above docker compose file refers to the bootstrap servers
configuration you provided. If your Kafka Cluster requires different
client security configuration, you may require [additional
settings](https://kafka.apache.org/documentation/#security).

</section>

Source the Kafka cluster properties file into your environment:

```sh
source getting-started.properties
```

Bring up the REST Proxy:

```sh
docker-compose -f rest-proxy.yml up -d
```

Wait a few seconds for REST Proxy to start and verify the Docker container logs show "Server started, listening for requests"

```sh
docker-compose -f rest-proxy.yml logs rest-proxy | grep "Server started, listening for requests"
```

## Produce Events

Run the following command to produce some random purchase events to the `purchases` topic:

```sh
curl -X POST \
     -H "Content-Type: application/vnd.kafka.json.v2+json" \
     -H "Accept: application/vnd.kafka.v2+json" \
     --data '{"records":[{"key":"jsmith","value":"alarm clock"},{"key":"htanaka","value":"batteries"},{"key":"awalther","value":"bookshelves"}]}' \
     "http://localhost:8082/topics/purchases"
```

You should see output that resembles:

```sh
{"offsets":[{"partition":0,"offset":0,"error_code":null,"error":null},{"partition":0,"offset":1,"error_code":null,"error":null},{"partition":0,"offset":2,"error_code":null,"error":null}],"key_schema_id":null,"value_schema_id":null}  
```

## Consume Events

Run the following commands to run a consumer with the REST Proxy, which will read the events from the purchases topic and write the information to the terminal.

Create a consumer, starting at the beginning of the topic's log:

```sh
curl -X POST \
     -H "Content-Type: application/vnd.kafka.v2+json" \
     --data '{"name": "ci1", "format": "json", "auto.offset.reset": "earliest"}' \
     http://localhost:8082/consumers/cg1
```

Subscribe to the topic purchases:

```sh
curl -X POST \
     -H "Content-Type: application/vnd.kafka.v2+json" \
     --data '{"topics":["purchases"]}' \
     http://localhost:8082/consumers/cg1/instances/ci1/subscription 
```

Consume some data using the base URL in the first response:
(Note that you must issue this command twice due to https://github.com/confluentinc/kafka-rest/issues/432)

```sh
curl -X GET \
     -H "Accept: application/vnd.kafka.json.v2+json" \
     http://localhost:8082/consumers/cg1/instances/ci1/records 

sleep 10

curl -X GET \
     -H "Accept: application/vnd.kafka.json.v2+json" \
     http://localhost:8082/consumers/cg1/instances/ci1/records 
```

Verify that you see the following output returned from the REST Proxy:
```sh
[{"topic":"purchases","key":"jsmith","value":"alarm clock","partition":0,"offset":0},{"topic":"purchases","key":"htanaka","value":"batteries","partition":0,"offset":1},{"topic":"purchases","key":"awalther","value":"bookshelves","partition":0,"offset":2}] 
```

Close the consumer with a DELETE to make it leave the group and clean up its resources:
```sh
curl -X DELETE \
     -H "Content-Type: application/vnd.kafka.v2+json" \
     http://localhost:8082/consumers/cg1/instances/ci1 
```

## Where next?

- For the REST Proxy API, check out the
  [REST Proxy documentation](https://docs.confluent.io/platform/current/kafka-rest/api.html).
- For information on testing in the Kafka ecosystem, check out
  [Testing Event Streaming Apps](/learn/testing-kafka).
- If you're interested in using streaming SQL for data creation,
  processing, and querying in your applications, check out the
  [ksqlDB 101 course](/learn-kafka/ksqldb/intro/).
- Interested in performance tuning of your event streaming applications?
  Check out the [Kafka Performance resources](/learn/kafka-performance/).
