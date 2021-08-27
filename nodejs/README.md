---
seo:
  title: Apache Kafka and Node.js - Getting Started Tutorial 
  description: How to develop your first Kafka client application in Node.js, which produces and consumes messages from a Kafka cluster, complete with configuration instructions. 
hero:
  title: Getting Started with Apache Kafka and Node.js
  description: Step-by-step guide to building a Node.js client application for Kafka 
---

# Getting Started with Apache Kafka and Node.js

## Introduction

In this tutorial, you will run a Node.js client application that produces
messages to and consumes messages from an Apache Kafka® cluster. The
tutorial will walk you through setting up a Kafka cluster if you
do not already have access to one.

## Prerequisites

This guide assumes that you have [node.js](https://nodejs.org/en/download/) installed (tested with LTS version `14.17.3`).

Later in this tutorial you will set up a new Kafka cluster or connect
to an existing one. If you wish to run a local Kafka cluster, you will
also need [Docker](https://docs.docker.com/get-docker/) installed
(this tutorial uses the new `docker compose` command, see the [Docker
documentation for more information](https://docs.docker.com/compose/cli-command/#new-docker-compose-command)).

## Create Project

Create a new directory anywhere you’d like for this project:

```sh
mkdir kafka-nodejs-getting-started && cd kafka-nodejs-getting-started
```

Then install the required libraries:

```sh
npm i node-rdkafka
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
  <input id="kafka-broker-server" data-context="true" name="kafka.broker.server" placeholder="cluster-id.region.provider.confluent.cloiud:9092" />
</p>

Paste your Confluent Cloud bootstrap server setting above and the
tutorial will fill in the appropriate configuration for
you.

You can obtain your Confluent Cloud Kafka cluster bootstrap server
configuration using the [Confluent Cloud UI](https://confluent.cloud/).

![](../media/cc-cluster-settings.png)

</section>

<section data-context-key="kafka.broker" data-context-value="local">
  
Paste the following file into a `docker-compose.yml` file:

```yaml file=../docker-compose.yml
```

Now start the Kafka broker with: `docker compose up -d`

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
use the [Cloud UI](https://confluent.cloud/) to create a key for
you.

Take note of the API key and secret and add them to the configuraiton file.
The `sasl.username` value should contain the API key, 
and the `sasl.password` value should contain the API secret.

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
UI](https://confluent.cloud/) to create a topic. Create a topic
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

## Build Producer

First, we are going to create a utility script which we will use from the Producer and Consumer applications we build next. This utility script will contain functions to help parse the configuration file we created previously. Copy the following code into a file named `util.js`:

```javascript file=util.js
```

Next, we are going to create the producer application by pasting the following code into a file named `producer.js`:

```javascript file=producer.js
```

## Build Consumer

To create the consumer application, paste the following JavaScript code into a file named `consumer.js`:

```javascript file=consumer.js
```

## Produce Events

In order to run the producer, use the node command, passing in the configuration file created above:

```sh
node producer.js getting-started.properties
```

You should see output that resembles:
```sh
Produced event to topic purchases: key = jsmith        value = alarm clock
Produced event to topic purchases: key = htanaka       value = book
Produced event to topic purchases: key = eabara        value = batteries
Produced event to topic purchases: key = htanaka       value = t-shirts
Produced event to topic purchases: key = htanaka       value = t-shirts
Produced event to topic purchases: key = htanaka       value = gift card
Produced event to topic purchases: key = sgarcia       value = gift card
Produced event to topic purchases: key = jbernard   value = gift card
Produced event to topic purchases: key = awalther   value = alarm clock
Produced event to topic purchases: key = htanaka       value = book
```

## Consume Events

From another terminal, run the following command to run the consumer application which will read the events from the purchases topic and write the information to the terminal.

```sh
node consumer.js getting-started.properties 
```

The consumer application will start and print any events it has not yet consumed and then wait for more events to arrive. On startup of the consumer, you should see output that resembles the below. Once you are done with the consumer, press ctrl-c to terminate the consumer application.

```sh
Consumed event from topic purchases: key = jsmith     value = alarm clock
Consumed event from topic purchases: key = htanaka    value = book
Consumed event from topic purchases: key = eabara     value = batteries
Consumed event from topic purchases: key = htanaka    value = t-shirts
Consumed event from topic purchases: key = htanaka    value = t-shirts
Consumed event from topic purchases: key = htanaka    value = gift card
Consumed event from topic purchases: key = sgarcia    value = gift card
Consumed event from topic purchases: key = jbernard    value = gift card
Consumed event from topic purchases: key = awalther    value = alarm clock
Consumed event from topic purchases: key = htanaka    value = book
```

Re-run the producer to see more events, or feel free to modify the code as necessary to create more or different events.

## Where next?

- [Get started with KafkaJS](https://www.confluent.io/blog/getting-started-with-kafkajs)
- [Learn more about KafkaJS on the Streaming Audio podcast](https://developer.confluent.io/podcast/powering-microservices-using-apache-kafka-on-nodejs-with-kafkajs-at-klarna-ft-tommy-brunn/)
- [Try the Node.js code example for Apache Kafka](https://docs.confluent.io/platform/current/tutorials/examples/clients/docs/nodejs.html)
- For information on testing in the Kafka ecosystem, check out
  [Testing Event Streaming Apps](/learn/testing-kafka).
- If you're interested in using streaming SQL for data creation,
  processing, and querying in your applications, check out the
  [ksqlDB 101 course](/learn-kafka/ksqldb/intro/).
- Interested in performance tuning of your event streaming applications?
  Check out the [Kafka Performance resources](/learn/kafka-performance/).
