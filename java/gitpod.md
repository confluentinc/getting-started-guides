# Getting Started with Apache Kafka and Java

## Introduction

In this tutorial, you will run a Java client application that produces messages to and consumes messages from an Apache Kafka® cluster in Confluent Cloud.

## Prerequisites

If you do not already have a Confluent Cloud account, sign up [here](https://www.confluent.io/confluent-cloud/tryfree/). New signups [receive $400](https://www.confluent.io/confluent-cloud-faqs/#how-can-i-get-up-to-dollar400-in-free-confluent-cloud-usage)
to spend within Confluent Cloud during their first 30 days. To avoid having to enter a credit card, navigate to [Billing & payment](https://confluent.cloud/settings/billing/payment), scroll to the bottom, and add the promo code `CONFLUENTDEV1`.
With this promo code, you will not have to enter your credit card info for 30 days or until your credits run out.

## Kafka Setup

We'll use the Confluent CLI, which is already installed in your Gitpod workspace, to create a Kafka cluster. First, login to your account by running the following command in the terminal window below. Note that, if you attempt to copy/paste the command, you may be prompted by your browser to allow this.

```noformat
confluent login --prompt
```

Next, install a CLI plugin that will create an environment to use for this guide:

```noformat
confluent plugin install confluent-cloud_kickstart
```

This plugin allows you to provision a Confluent Cloud environment, cluster, and API key in one command. It also enables Schema Registry, though we don't use it in this language guide. You may pick `aws`, `azure`, or `gcp` as the `--cloud` argument, and any supported region returned by `confluent kafka region list` as the `--region` argument. For example, to use AWS region `us-east-2`:

```noformat
confluent cloud-kickstart --name java-quickstart \
  --env java-quickstart-env \
  --cloud aws \
  --region us-east-2 \
  --output-format stdout
```

The output of this command will contain a `Kafka API key` and `Kafka API secret` that we will use in the `Configure clients` step below.

## Create Topic

A topic is an immutable, append-only log of events. Usually, a topic contains the same kind of events, e.g., in this tutorial, we create a topic for retail purchases.

Create a new topic, `purchases`, which you will use to produce and consume events:

```noformat
confluent kafka topic create purchases
```

## Get Kafka boostrap servers endpoint

Run the following command to get your cluster ID of the form `lkc-123456`:

```noformat
confluent kafka cluster list
```

Next, get your Kafka bootstrap servers endpoint by running:

```noformat
confluent kafka cluster describe <CLUSTER ID>
```

You will use the `Endpoint` value _after_ `SASL_SSL://` as the bootstrap servers endpoint. I.e., if the `Endpoint` is `SASL_SSL://pkc-123456.us-east-2.aws.confluent.cloud:9092`, then the bootstrap servers endpoint to use in the next step is `pkc-123456.us-east-2.aws.confluent.cloud:9092`.

## Configure clients

Open the `ProducerExample.java` and `ConsumerExample.java` files in the file explorer to the left by navigating to `java/src/main/java/io/confluent/developer/`. Right-click on the first and select `Open to the Side` so that you can continue to view these instructions in the panel on the left.

In both the `ProducerExample` and `ConsumerExample` classes, replace the `<BOOTSTRAP SERVERS>`, `<CLUSTER API KEY>`, and `<CLUSTER API SECRET>` placeholders with the bootstrap servers endpoint, API Key, and API secret from the previous steps.

## Compile applications

You can test the syntax before proceeding by compiling with this command in the terminal pane at the bottom of the Gitpod workspace:

```sh
gradle build
```
And you should see:

```noformat
BUILD SUCCESSFUL
```

## Produce Events

To build a JAR that we can run from the command line, first run:

```sh
gradle shadowJar
```

And you should see:

```noformat
BUILD SUCCESSFUL
```

Run the following command to execute the producer application, which will produce some random events to the `purchases` topic.

```sh
java -cp build/libs/kafka-java-getting-started-0.0.1.jar io.confluent.developer.ProducerExample
```

You should see output resembling this:

```noformat
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

```noformat
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

Once you are done exploring, delete the Confluent Cloud environment that you created at the beginning of this tutorial.

First, run the following command to get your environment ID of the form `env-123456`:

```noformat
confluent ennvironment list
```

Second, delete the environment. This deletes the cluster and API key that the `cloud-kickstart` plugin provisioned.

```noformat
confluent environment delete <ENVIRONMENT ID>
```

Next, logout of Confluent Cloud:

```noformat
confluent logout
```

Finally, delete your Gitpod workspace [here](https://gitpod.io/workspaces) by right-clicking the three dots to the right of your workspace and clicking `Delete`.
