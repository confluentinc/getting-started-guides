---
seo:
  title: Apache Kafka and Java - Getting Started Tutorial 
  description: How to run a Kafka client application written in Java that produces to and consumes messages from a Kafka cluster, with step-by-step setup instructions and examples. 
hero:
  title: Getting Started with Apache Kafka and Java
  description: Step-by-step guide to building a Java client application for Kafka 
---

# Getting Started with Apache Kafka and Java

## Introduction

<div class="alert-success">
  <p>
    <b>New!</b> Open the language guide in Gitpod for an in-browser experience by clicking this button:
    <span style="width:100%">
      <a style="display:flex;flex-direction:column;width:100px;height:50px;justify-content:center;margin: 0 auto;background-color:#f9f9f9;border:1px solid #dcdcde;padding:10px;border-radius:10px;" target="_blank" href="https://gitpod.io/#https://github.com/confluentinc/getting-started-guides/blob/gitpod-integration">
        <img alt="Gitpod" src="//images.ctfassets.net/gt6dp23g0g38/6ZSSfl01xAF2sq5UHvfmxI/21d7a5982af2272f5e05e8c83eaa8058/logo-light-theme.png"/>
      </a>
    </span>
    Otherwise, continue reading to run through the guide locally on your machine.
  </p>
</div>

In this tutorial, you will run a Java client application that produces messages to and consumes messages from an Apache Kafka® cluster. 

As you're learning how to run your first Kafka application, we recommend using [Confluent Cloud](https://www.confluent.io/confluent-cloud/tryfree) so that you don't have to run your own Kafka cluster and can focus on the client development. If you do not already have an account, be sure to [sign up](https://www.confluent.io/confluent-cloud/tryfree/). New signups [receive $400](https://www.confluent.io/confluent-cloud-faqs/#how-can-i-get-up-to-dollar400-in-free-confluent-cloud-usage) to spend within Confluent Cloud during their first 30 days. To avoid having to enter a credit card, navigate to [Billing & payment](https://confluent.cloud/settings/billing/payment), scroll to the bottom, and add the promo code `CONFLUENTDEV1`. With this promo code, you will not have to enter your credit card info for 30 days or until your credits run out.

If you prefer to set up a local Kafka cluster, the tutorial will walk you through those steps as well.

<div class="alert-primary">
<p>
Note: This tutorial focuses on a simple application to get you started. For more in-depth information to get you started you can look at <a href="https://docs.confluent.io/kafka-clients/java/current/overview.html">Kafka clients documentation.</a>
If you want to build more complex applications and microservices for data in motion—with powerful features such as real-time joins, aggregations, filters, exactly-once processing, and more—check out the <a href="/learn-kafka/kafka-streams/get-started/">Kafka Streams 101 course</a>, which covers the
<a href="https://docs.confluent.io/platform/current/streams/index.html">Kafka Streams client library</a>.
</p>
</div>

## Prerequisites

Using Windows? You'll need to download [Windows Subsystem for Linux](https://learn.microsoft.com/en-us/windows/wsl/install).

This guide assumes that you already have:

- [Gradle](https://gradle.org/install/) installed.
- [Java 11](https://openjdk.org/install/) installed and configured as the current Java version for the environment.
  Verify that `java -version` outputs version 11 and ensure that the `JAVA_HOME` environment variable is set to the Java
  installation directory containing `bin`.

## Create Project

Create a new directory anywhere you'd like for this project:

```sh
mkdir kafka-java-getting-started && cd kafka-java-getting-started
```

Create the following Gradle build file for the project, named
`build.gradle`:

```gradle file=build.gradle
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
      <option value="existing">I have a cluster already!</option>
    </select>
  </div>
</p>

<section data-context-key="kafka.broker" data-context-value="cloud" data-context-default>

From within the Confluent Cloud Console, creating a new cluster is just a few clicks:
<video autoplay muted playsinline poster="https://images.ctfassets.net/gt6dp23g0g38/4JMGlor4A4ad1Doa5JXkUg/bcd6f6fafd5c694af33e91562fd160c0/create-cluster-preview.png" loop>
	<source src="https://videos.ctfassets.net/gt6dp23g0g38/6zFaUcKTgj5pCKCZWb0zXP/6b25ae63eae25756441a572c2bbcffb6/create-cluster.mp4" type="video/mp4">
Your browser does not support the video tag.
</video>

Next, note your Confluent Cloud bootstrap server as we will need it to configure the producer and consumer clients in upcoming steps. You can obtain your Confluent Cloud Kafka cluster bootstrap server configuration using the [Confluent Cloud Console](https://confluent.cloud/):
<video autoplay muted playsinline poster="https://images.ctfassets.net/gt6dp23g0g38/nrZ31F1vVHVWKpQpBYzi1/a435b23ed68d82c4a39fa0b4472b7b71/get-cluster-bootstrap-preview.png" loop>
	<source src="https://videos.ctfassets.net/gt6dp23g0g38/n9l0LvX4FmVZSCGUuHZh3/b53a03f62bb92c2ce71a7c4a23953292/get-cluster-bootstrap.mp4" type="video/mp4">
Your browser does not support the video tag.
</video>

Next, choose the authentication mechanism that the producer and consumer client applications will use to access Confluent Cloud: either [basic authentication](https://docs.confluent.io/cloud/current/access-management/authenticate/api-keys/api-keys.html) or [OAuth](https://docs.confluent.io/cloud/current/access-management/authenticate/oauth/overview.html).

Basic authentication is quicker to implement since you only need to create an API key in Confluent Cloud, whereas OAuth requires that you have an OAuth provider, as well as an OAuth application created within it for use with Confluent Cloud, in order to proceed.

Select your authentication mechanism:

<p>
  <label>Authentication mechanism</label>
  <div class="select-wrapper">
    <select data-context="true" name="confluent-cloud.authentication">
      <option value="basic">Basic</option>
      <option value="oauth">OAuth</option>
    </select>
  </div>
</p>

<section data-context-key="confluent-cloud.authentication" data-context-value="basic" data-context-default>

You can use the [Confluent Cloud Console](https://confluent.cloud/) to create a key for
you by navigating to the `API Keys` section under `Cluster Overview`.

![](../media/cc-create-key.png)

Note the API key and secret as we will use them when configuring the producer and consumer clients in upcoming steps.

</section> <!--- confluent-cloud.authentication = basic -->

<section data-context-key="confluent-cloud.authentication" data-context-value="oauth">

You can use the [Confluent Cloud Console](https://confluent.cloud/) to [add an OAuth/OIDC identity provider](https://docs.confluent.io/cloud/current/access-management/authenticate/oauth/identity-providers.html)
and [create an identity pool](https://docs.confluent.io/cloud/current/access-management/authenticate/oauth/identity-pools.html) with your OAuth/OIDC identity provider.

Note the following OAuth/OIDC-specific configuration values, which we will use to configure the producer and consumer clients in upcoming steps:

* `OAUTH2 CLIENT ID`: The public identifier for your client. In Okta, this is a 20-character alphanumeric string.
* `OAUTH2 CLIENT SECRET`: The secret corresponding to the client ID. In Okta, this is a 64-character alphanumeric string.
* `OAUTH2 TOKEN ENDPOINT URL`: The token-issuing URL that your OAuth/OIDC provider exposes. E.g., Okta's token endpoint URL
  format is `https://<okta-domain>.okta.com/oauth2/default/v1/token`
* `OAUTH2 SCOPE`: The name of the scope that you created in your OAuth/OIDC provider to restrict access privileges for issued tokens.
  In Okta, you or your Okta administrator provided the scope name when configuring your authorization server. In the navigation bar of your Okta Developer account,
  you can find this by navigating to `Security > API`, clicking the authorization server name, and finding the defined scopes under the `Scopes` tab.
* `LOGICAL CLUSTER ID`: Your Confluent Cloud logical cluster ID of the form `lkc-123456`. You can view your Kafka cluster ID in
  the Confluent Cloud Console by navigating to `Cluster Settings` in the left navigation of your cluster homepage.
* `IDENTITY POOL ID`: Your Confluent Cloud identity pool ID of the form `pool-1234`. You can find this in the Confluent Cloud Console
  by navigating to `Accounts & access` in the top right menu, selecting the `Identity providers` tab, clicking your identity provider, and viewing the `Identity pools` section of the page.

</section> <!--- confluent-cloud.authentication = oauth -->

</section>

<section data-context-key="kafka.broker" data-context-value="local">

This guide runs Kafka in Docker via the Confluent CLI.

First, install and start [Docker Desktop](https://docs.docker.com/desktop/) or [Docker Engine](https://docs.docker.com/engine/install/) if you don't already have it. Verify that Docker is set up properly by ensuring that no errors are output when you run `docker info` in your terminal.

Install the Confluent CLI if you don't already have it. In your terminal:

```plaintext
brew install confluentinc/tap/cli
```

If you don't use Homebrew, you can use a [different installation method](https://docs.confluent.io/confluent-cli/current/install.html).

This guide requires version 3.34.1 or later of the Confluent CLI. If you have an older version, run `confluent update` to get the latest release (or `brew upgrade confluentinc/tap/cli` if you installed the CLI with Homebrew).

Now start the Kafka broker:

```plaintext
confluent local kafka start
```

Note the `Plaintext Ports` printed in your terminal, which you will need to configure the producer and consumer clients in upcoming steps.

</section>

<section data-context-key="kafka.broker" data-context-value="existing">

Note your Kafka cluster bootstrap server URL as you will need it to configure the producer and consumer clients in upcoming steps.

</section>

## Create Topic

A topic is an immutable, append-only log of events. Usually, a topic is comprised of the same kind of events, e.g., in this guide we create a topic for retail purchases.

Create a new topic, `purchases`, which you will use to produce and consume events.

<section data-context-key="kafka.broker" data-context-value="cloud" data-context-default="true">

![](../media/cc-create-topic.png)

When using Confluent Cloud, you can use the [Confluent Cloud
Console](https://confluent.cloud/) to create a topic. Create a topic
with 1 partition and defaults for the remaining settings.

</section>

<section data-context-key="kafka.broker" data-context-value="local">

```plaintext
confluent local kafka topic create purchases
```
</section>

<section data-context-key="kafka.broker" data-context-value="existing">

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
mkdir -p src/main/java/io/confluent/developer
```

Let's create the Java producer application by pasting the following code into a file located at `src/main/java/io/confluent/developer/ProducerExample.java`.

<section data-context-key="kafka.broker" data-context-value="cloud" data-context-default>
<section data-context-key="confluent-cloud.authentication" data-context-value="basic" data-context-default>

```java file=src/main/java/io/confluent/developer/ProducerExampleCloudBasic.java
```

</section>
<section data-context-key="confluent-cloud.authentication" data-context-value="oauth">

```java file=src/main/java/io/confluent/developer/ProducerExampleCloudOAuth.java
```

</section>
</section>
<section data-context-key="kafka.broker" data-context-value="local">

```java file=src/main/java/io/confluent/developer/ProducerExampleLocal.java
```

</section>
<section data-context-key="kafka.broker" data-context-value="existing">

```java file=src/main/java/io/confluent/developer/ProducerExampleExisting.java
```

</section>

Fill in the appropriate `BOOTSTRAP_SERVERS_CONFIG` value and any additional security configuration needed inline where the client configuration `Properties` object is instantiated.

You can test the syntax before preceding by compiling with:

```sh
gradle build
```
And you should see:

```
BUILD SUCCESSFUL
```

## Build Consumer

Next, create the Java consumer application by pasting the following code into a file located at `src/main/java/io/confluent/developer/ConsumerExample.java`.

<section data-context-key="kafka.broker" data-context-value="cloud" data-context-default>
<section data-context-key="confluent-cloud.authentication" data-context-value="basic" data-context-default>

```java file=src/main/java/io/confluent/developer/ConsumerExampleCloudBasic.java
```

</section>
<section data-context-key="confluent-cloud.authentication" data-context-value="oauth">

```java file=src/main/java/io/confluent/developer/ConsumerExampleCloudOAuth.java
```

</section>
</section>
<section data-context-key="kafka.broker" data-context-value="local">

```java file=src/main/java/io/confluent/developer/ConsumerExampleLocal.java
```

</section>
<section data-context-key="kafka.broker" data-context-value="existing">

```java file=src/main/java/io/confluent/developer/ConsumerExampleExisting.java
```

</section>

Fill in the appropriate `BOOTSTRAP_SERVERS_CONFIG` value and any additional security configuration needed inline where the client configuration `Properties` object is instantiated.

Once again, you can compile the code before preceding by with:

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

From another terminal, run the following command to run the consumer
application, which will read the events from the `purchases` topic and write
the information to the terminal.

```sh
java -cp build/libs/kafka-java-getting-started-0.0.1.jar io.confluent.developer.ConsumerExample
```

The consumer application will start and print any events it has not
yet consumed and then wait for more events to arrive. On startup of
the consumer, you should see output resembling this:

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

<section data-context-key="kafka.broker" data-context-value="local">

Shut down Kafka when you are done with it:

```plaintext
confluent local kafka stop
```
</section>

## Where next?

- For the Java client API, check out the
  [Java documentation](https://docs.confluent.io/platform/current/clients/javadocs/javadoc/index.html).
- The [Kafka clients documentation](https://docs.confluent.io/kafka-clients/java/current/overview.html). 
- Kafka Streams allows you to build advanced Java applications that include powerful features such as real-time joins, aggregations, filters, and exactly-once processing. The [Kafka Streams 101 course](/learn-kafka/kafka-streams/get-started/) provides an in-depth approach including videos and exercises. For quick hands-on approach, see the [How to build your first Apache Kafka Streams application](https://kafka-tutorials.confluent.io/creating-first-apache-kafka-streams-application/confluent.html) tutorial.
- For information on testing in the Kafka ecosystem, check out
  [Testing Event Streaming Apps](/learn/testing-kafka).
- Interested in performance tuning of your event streaming applications?
  Check out the [Kafka Performance resources](/learn/kafka-performance/).
