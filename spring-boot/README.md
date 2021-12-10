---
seo:
  title: Apache Kafka and Spring Boot - Getting Started Tutorial
  description: A quick guide to getting started with Apache Kafka and Spring Boot. Learn how to produce and consume messages from a Kafka cluster and configure your setup with examples.
hero:
  title: Getting Started with Apache Kafka and Spring Boot
  description: Step-by-step guide to building a Spring Boot client application for Kafka 
---

# Getting Started with Apache Kafka and Spring Boot

## Introduction

In this tutorial, you will run a Spring Boot client application that produces messages to and consumes messages from an Apache Kafka® cluster.

The easiest way to run Kafka is with [Confluent Cloud](https://www.confluent.io/confluent-cloud/). If you do not already have an account, [signup here](https://www.confluent.io/confluent-cloud/tryfree/). New signups [receive $400](https://www.confluent.io/confluent-cloud-faqs/#how-can-i-get-up-to-dollar400-in-free-confluent-cloud-usage) to spend within Confluent Cloud during their first 60 days.

The tutorial will walk you through setting up a local Kafka cluster if you do not already have access to one.

<div class="alert-primary">
<p>
Note: This tutorial focuses on a simple application to get you started.
If you want to build more complex applications and microservices for data in motion—with powerful features such as real-time joins, aggregations, filters, exactly-once processing, and more—check out the <a href="/learn-kafka/kafka-streams/get-started/">Kafka Streams 101 course</a>, which covers the
<a href="https://docs.confluent.io/platform/current/streams/index.html">Kafka Streams client library</a>.
</p>
</div>

## Prerequisites

This guide assumes that you already have:

[Gradle](https://gradle.org/install/) installed

[Java 11](https://www.oracle.com/java/technologies/javase-downloads.html)
installed and configured as the current java version for the environment

Later in this tutorial you will set up a new Kafka cluster or connect
to an existing one. 

If you do not have an existing cluster to use, the easiest way to run Kafka is 
with [Confluent Cloud](https://www.confluent.io/confluent-cloud/). If you do not already have an account, 
[signup here](https://www.confluent.io/confluent-cloud/tryfree/). 
New signups [receive $400](https://www.confluent.io/confluent-cloud-faqs/#how-can-i-get-up-to-dollar400-in-free-confluent-cloud-usage) 
to spend within Confluent Cloud during their first 60 days.

From within the Confluent Cloud console, creating a new cluster is just a few clicks:
<video autoplay muted playsinline poster="https://images.ctfassets.net/gt6dp23g0g38/4JMGlor4A4ad1Doa5JXkUg/7429fd45b2b96d5ce922c990a6b1df77/create-cluster-v2-preview.png">
	<source src="https://videos.ctfassets.net/gt6dp23g0g38/4RJ9frRj03WGhgmPfyfhMB/e1f5e8c719a56d7dcc5c069abd2145da/create-cluster-v2.mp4" type="video/mp4">
Your browser does not support the video tag.
</video>

If you wish instead to run a local Kafka cluster, you will need [Docker](https://docs.docker.com/get-docker/) installed.

## Create Project

Create a new directory anywhere you'd like for this project:

```sh
mkdir kafka-spring-boot-getting-started && cd kafka-spring-boot-getting-started
```

Create the following Gradle build file for the project, named
`build.gradle`:

```xml file=build.gradle
```

## Kafka Setup

We are going to need a Kafka Cluster for our client application to
operate with. This dialog can help you configure your Confluent Cloud
cluster, create a Kafka cluster for you, or help you input an existing
cluster bootstrap server to connect to.

<p>
  <div class="select-wrapper">
    <select data-context="true" name="kafka.broker">
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
configuration using the [Confluent Cloud Console](https://confluent.cloud/):
<video autoplay muted>
	<source src="https://videos.ctfassets.net/gt6dp23g0g38/n9l0LvX4FmVZSCGUuHZh3/87f6d318b2caeca0fd9c07f734d98e2f/get-cluster-bootstrap-v2.mp4" type="video/mp4">
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

Create a directory for the application resource file:

```sh
mkdir -p src/main/resources
```

Paste the following configuration data into a file located at `src/main/resources/application.yaml`

The below configuration includes the required settings for a connection
to Confluent Cloud including the bootstrap servers configuration you
provided. 

![](../media/cc-create-key.png)

When using Confluent Cloud you will be required to provide an API key
and secret authorizing your application to produce and consume. You can
use the [Cloud Console](https://confluent.cloud/) to create a key for
you.

Take note of the API key and secret and add them to the configuraiton file.
The `sasl.username` value should contain the API key, 
and the `sasl.password` value should contain the API secret.

```ini file=getting-started-cloud.properties
```

</section>

<section data-context-key="kafka.broker" data-context-value="local">

Create a directory for the application resource file:

```sh
mkdir -p src/main/resources
```

Paste the following configuration data into a file located at `src/main/resources/application.yaml`

```ini file=getting-started-local.properties
```

</section>

<section data-context-key="kafka.broker" data-context-value="other">

Create a directory for the application resource file:

```sh
mkdir -p src/main/resources
```

Paste the following configuration data into a file located at `src/main/resources/application.yaml`

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

## Build Producer

Create a directory for the Java files in this project:

```sh
mkdir -p src/main/java/examples
```

We will use `SpringBootApplication` annotation for ease of use, auto-configuration and component scanning.
Paste the following Java code into a file located at `src/main/java/examples/SpringBootWithKafkaApplication.java`

```java file=src/main/java/examples/SpringBootWithKafkaApplication.java
```

Create the Kafka Producer.
Paste the following Java code into a file located at `src/main/java/examples/Producer.java`

```java file=src/main/java/examples/Producer.java
```

You can test the code before preceding by compiling with:

```sh
gradle build
```
And you should see:

```
BUILD SUCCESSFUL
```

## Build Consumer

Create the Kafka Consumer.
Paste the following Java code into a file located at `src/main/java/examples/Consumer.java`

```java file=src/main/java/examples/Consumer.java
```

Once again, you can compile the code before preceding by with:

```sh
gradle build
```

And you should see:

```
BUILD SUCCESSFUL
```

## Produce Events

Run the following command to run the Spring Boot application for the Producer.

```sh
gradle bootRun --args='--producer'
```

You should see output that includes the lines showing produced records:

```
2021-08-27 13:09:50.287  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = awalther   value = t-shirts
2021-08-27 13:09:50.303  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = htanaka    value = t-shirts
2021-08-27 13:09:50.318  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = htanaka    value = batteries
2021-08-27 13:09:50.334  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = eabara     value = t-shirts
2021-08-27 13:09:50.349  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = htanaka    value = t-shirts
2021-08-27 13:09:50.362  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = jsmith     value = book
2021-08-27 13:09:50.377  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = awalther   value = t-shirts
2021-08-27 13:09:50.391  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = jsmith     value = batteries
2021-08-27 13:09:50.405  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = jsmith     value = gift card
2021-08-27 13:09:50.420  INFO 73259 --- [ad | producer-1] examples.Producer                        : Produced event to topic purchases: key = eabara     value = t-shirts
```

## Consume Events

Run the following command to run the Spring Boot application for the Consumer.

```sh
gradle bootRun --args='--consumer'
```

You should see output that includes the lines showing consumed records:

```
2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = awalther   value = t-shirts
2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = htanaka    value = t-shirts
2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = htanaka    value = batteries
2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = eabara     value = t-shirts
2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = htanaka    value = t-shirts
2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = jsmith     value = book
2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = awalther   value = t-shirts
2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = jsmith     value = batteries
2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = jsmith     value = gift card
2021-08-27 13:09:54.129  INFO 73259 --- [yConsumer-0-C-1] examples.Consumer                        : Consumed event from topic purchases: key = eabara     value = t-shirts
```

## Where next?

- For a Spring Boot example using Schema Registry and Avro, check out 
  [this example](https://docs.confluent.io/platform/current/tutorials/examples/clients/docs/java-springboot.html).
- Learn more in the [Introduction to Spring Boot for Confluent Cloud](https://developer.confluent.io/learn-kafka/spring/confluent-cloud/) course.
- If you want to build more complex applications and microservices—with powerful features such as real-time joins, aggregations, filters, exactly-once processing, and more—check out the [Kafka Streams 101 course](/learn-kafka/kafka-streams/get-started/).
- For information on testing in the Kafka ecosystem, check out
  [Testing Event Streaming Apps](/learn/testing-kafka).
- Interested in performance tuning of your event streaming applications?
  Check out the [Kafka Performance resources](/learn/kafka-performance/).
