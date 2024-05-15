

https://gitpod.io/#https://github.com/confluentinc/getting-started-guides/blob/gitpod-poc


# Getting Started with Apache Kafka and Java

## Introduction

In this tutorial, you will run a Java client application that produces messages to and consumes messages from an Apache Kafka® cluster in Confluent Cloud.

## Prerequisites

If you do not already have a Confluent Cloud account, sign up [here](https://www.confluent.io/confluent-cloud/tryfree/). New signups [receive $400](https://www.confluent.io/confluent-cloud-faqs/#how-can-i-get-up-to-dollar400-in-free-confluent-cloud-usage)
to spend within Confluent Cloud during their first 30 days. To avoid having to enter a credit card, navigate to [Billing & payment](https://confluent.cloud/settings/billing/payment), scroll to the bottom, and add the promo code `CONFLUENTDEV1`.
With this promo code, you will not have to enter your credit card info for 30 days or until your credits run out.

## Kafka Setup

We'll use the Confluent CLI, which is already installed in your Gitpod workspace, to create a Kafka cluster. First, login to your account:

```plaintext
confluent login --prompt
```

Next, create a cluster in the `default` environment named `kafka-java-gettin-started`. You may pick `aws`, `azure`, or `gcp` as the `--cloud` argument, and any supported region returned by `confluent kafka region list` as the `--region` argument. For example, to create the cluster in AWS region `us-east-2`:

```plaintext
confluent kafka cluster create kafka-java-gettin-started --cloud aws --region us-east-2
```

Note the ID of the form `lkc-123456` in the command output, and set it as the active cluster so that we won't need to specify it in future commands:

```plaintext
confluent kafka cluster use <CLUSTER ID>
```

## Create Topic

A topic is an immutable, append-only log of events. Usually, a topic is comprised of the same kind of events, e.g., in this guide we create a topic for retail purchases.

Create a new topic, `purchases`, which you will use to produce and consume events:

```plaintext
confluent kafka topic create purchases
```

## Generate Credentials

Using the cluster ID of the form `lkc-123456` from before, create an API key that our Java client applications will use to authenticate to Confluent Cloud:

```plaintext
confluent api-key create --resource <CLUSTER ID>
```

Also, get the bootstrap servers endpoint by running:

```plaintext
confluent kafka cluster describe <CLUSTER ID>
```

You will use the `Endpoint` value _after_ `SASL_SSL://` as the bootstrap servers endpoint. I.e., if the `Endpoint` is `SASL_SSL://pkc-123456.us-east-2.aws.confluent.cloud:9092`, then the bootstrap servers endpoint to use in the next step is `pkc-123456.us-east-2.aws.confluent.cloud:9092`.

## Configure clients

In both the `ProducerExample` an `ConsumerExample` classes in the pane to the left, replace the `<BOOTSTRAP SERVERS>`, `<CLUSTER API KEY>`, and `<CLUSTER API SECRET>` placeholders with the bootstrap servers endpoint, API Key, and API secret that you gathered in the previous step.
Fill in the appropriate `BOOTSTRAP_SERVERS_CONFIG` value and any additional security configuration needed inline where the client configuration `Properties` object is instantiated.

## Compile applications

You can test the syntax before preceding by compiling with:

```sh
gradle build
```
And you should see:

```
BUILD SUCCESSFUL
```

## Produce Events

To build a JAR that we can run from the command line, first run:

```sh
gradle shadowJar
```

And you should see:

```
BUILD SUCCESSFUL
```

Run the following command to execute the producer application, which will produce some random events to the `purchases` topic.

```sh
java -cp build/libs/kafka-java-getting-started-0.0.1.jar io.confluent.developer.ProducerExample
```

You should see output resembling this:

```
Produced event to topic purchases: key = awalther   value = t-shirts
Produced event to topic purchases: key = htanaka    value = t-shirts
Produced event to topic purchases: key = htanaka    value = batteries
Produced event to topic purchases: key = eabara     value = t-shirts
Produced event to topic purchases: key = htanaka    value = t-shirts
Produced event to topic purchases: key = jsmith     value = book
Produced event to topic purchases: key = awalther   value = t-shirts
Produced event to topic purchases: key = jsmith     value = batteries
Produced event to topic purchases: key = jsmith     value = gift card
Produced event to topic purchases: key = eabara     value = t-shirts
10 events were produced to topic purchases
```

## Consume Events

Next, run the following command to run the consumer application, which will read the events from the `purchases` topic and write the information to the terminal.

```sh
java -cp build/libs/kafka-java-getting-started-0.0.1.jar io.confluent.developer.ConsumerExample
```

The consumer application will start and print any events it has not yet consumed and then wait for more events to arrive. On startup of the consumer, you should see output resembling this:

```
Consumed event from topic purchases: key = awalther   value = t-shirts
Consumed event from topic purchases: key = htanaka    value = t-shirts
Consumed event from topic purchases: key = htanaka    value = batteries
Consumed event from topic purchases: key = eabara     value = t-shirts
Consumed event from topic purchases: key = htanaka    value = t-shirts
Consumed event from topic purchases: key = jsmith     value = book
Consumed event from topic purchases: key = awalther   value = t-shirts
Consumed event from topic purchases: key = jsmith     value = batteries
Consumed event from topic purchases: key = jsmith     value = gift card
Consumed event from topic purchases: key = eabara     value = t-shirts
```

Rerun the producer to see more events, or feel free to modify the code as necessary to create more or different events.

Once you are done with the consumer, enter `Ctrl-C` to terminate the consumer application.

## Clean up

TODO