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

In this tutorial, you will run a Node.js client application that produces messages to and consumes messages from an Apache Kafka® cluster. 

As you're learning how to run your first Kafka application, we recommend using [Confluent Cloud](https://www.confluent.io/confluent-cloud/tryfree) (no credit card required to sign up) so you don't have to run your own Kafka cluster and you can focus on the client development. But if you prefer to setup a local Kafka cluster, the tutorial will walk you through those steps.


## Prerequisites

This guide assumes that you have [node.js](https://nodejs.org/en/download/) installed (tested with `16.3.1`).

Later in this tutorial you will set up a new Kafka cluster or connect
to an existing one. 

If you do not have an existing cluster to use, the easiest way to run Kafka is 
with [Confluent Cloud](https://www.confluent.io/confluent-cloud/). If you do not already have an account, 
be sure to [sign up](https://www.confluent.io/confluent-cloud/tryfree/). 
New signups [receive $400](https://www.confluent.io/confluent-cloud-faqs/#how-can-i-get-up-to-dollar400-in-free-confluent-cloud-usage) 
to spend within Confluent Cloud during their first 60 days.

From within the Confluent Cloud Console, creating a new cluster is just a few clicks:
<video autoplay muted playsinline poster="https://images.ctfassets.net/gt6dp23g0g38/4JMGlor4A4ad1Doa5JXkUg/bcd6f6fafd5c694af33e91562fd160c0/create-cluster-preview.png" loop>
	<source src="https://videos.ctfassets.net/gt6dp23g0g38/6zFaUcKTgj5pCKCZWb0zXP/6b25ae63eae25756441a572c2bbcffb6/create-cluster.mp4" type="video/mp4">
Your browser does not support the video tag.
</video>

If you cannot use Confluent Cloud, you can use an existing Kafka cluster or run one locally using [Docker](https://docs.docker.com/get-docker/).

## Create Project

Create a new directory anywhere you’d like for this project:

```sh
mkdir kafka-nodejs-getting-started && cd kafka-nodejs-getting-started
```

<div class="alert-primary">
<p>
Note: Users of macOS 10.13 (High Sierra) and later should read <a href="https://github.com/Blizzard/node-rdkafka/blob/master/README.md#mac-os-high-sierra--mojave">node-rdkafka additional configuration instructions</a> related to OpenSSL before running `npm install`.
</p>
</div>

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
  <label>Kafka location</label>
  <div class="select-wrapper">
    <select data-context="true" name="kafka.broker">
      <option value="cloud">Confluent Cloud</option>
      <option value="local">Local</option>
      <option value="other">Other</option>
    </select>
  </div>
</p>

<section data-context-key="kafka.broker" data-context-value="cloud" data-context-default>

After you sign up for [Confluent Cloud](https://www.confluent.io/confluent-cloud/tryfree/)
and provision your Kafka cluster,
paste your Confluent Cloud bootstrap server setting below and the
tutorial will fill in the appropriate configuration for
you.

<p>
  <label for="kafka-broker-server">Bootstrap Server</label>
  <input id="kafka-broker-server" data-context="true" name="kafka.broker.server" placeholder="cluster-id.region.provider.confluent.cloud:9092" />
</p>

You can obtain your Confluent Cloud Kafka cluster bootstrap server
configuration using the [Confluent Cloud Console](https://confluent.cloud/):
<video autoplay muted playsinline poster="https://images.ctfassets.net/gt6dp23g0g38/nrZ31F1vVHVWKpQpBYzi1/a435b23ed68d82c4a39fa0b4472b7b71/get-cluster-bootstrap-preview.pn://images.ctfassets.net/gt6dp23g0g38/nrZ31F1vVHVWKpQpBYzi1/dd72c752e9ed2724edc30a7f9eb77ccb/get-cluster-bootstrap-preview.png" loop>
	<source src="https://videos.ctfassets.net/gt6dp23g0g38/n9l0LvX4FmVZSCGUuHZh3/b53a03f62bb92c2ce71a7c4a23953292/get-cluster-bootstrap.mp4" type="video/mp4">
Your browser does not support the video tag.
</video>

</section>

<section data-context-key="kafka.broker" data-context-value="local">
  
Paste the following file into a `docker-compose.yml` file:

```yaml file=../docker-compose.yml
```

Now start the Kafka broker with the new `docker compose` command (see the [Docker
documentation for more information](https://docs.docker.com/compose/cli-command/#new-docker-compose-command)).

```sh
docker compose up -d
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

<section data-context-key="kafka.broker" data-context-default>
  Please go back to the Kafka Setup section and select a broker type.
</section>

<section data-context-key="kafka.broker" data-context-value="cloud">

When using Confluent Cloud you will be required to provide an API key
and secret authorizing your application to produce and consume. You can
use the [Confluent Cloud Console](https://confluent.cloud/) to create a key for
you by navigating to the `API Keys` section under `Cluster Overview`.

![](../media/cc-create-key.png)

Copy and paste the following configuration data into a file named `getting-started.properties`, substituting the API key and
secret that you just created for the `sasl.username` and `sasl.password` values, respectively. Note that bootstrap
server endpoint that you provided in the `Kafka Setup` step is used as the value corresponding to `bootstrap.servers`.

```properties file=getting-started-cloud.properties
```
</section>

<section data-context-key="kafka.broker" data-context-value="local">

Paste the following configuration data into a file named `getting-started.properties`:

```properties file=getting-started-local.properties
```

</section>

<section data-context-key="kafka.broker" data-context-value="other">

Paste the following configuration data into a file named `getting-started.properties`.

The below configuration file includes the bootstrap servers
configuration you provided. If your Kafka Cluster requires different
client security configuration, you may require [different
settings](https://kafka.apache.org/documentation/#security).

```properties file=getting-started-other.properties
```

</section>

## Create Topic

Events in Kafka are organized and durably stored in named topics. Topics
have parameters that determine the performance and durability guarantees
of the events that flow through them.

Create a new topic, `purchases`, which we will use to produce and consume
events.

<section data-context-key="kafka.broker" data-context-value="cloud" data-context-default="true">

![](../media/cc-create-topic.png)

When using Confluent Cloud, you can use the [Confluent Cloud
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
