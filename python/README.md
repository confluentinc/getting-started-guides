---
seo:
  title: Apache Kafka and Python - Getting Started Tutorial 
  description: How to run a Kafka client application written in Python that produces to and consumes messages from a Kafka cluster, complete with step-by-step instructions and examples. 
hero:
  title: Getting Started with Apache Kafka and Python
  description: Step-by-step guide to building a Python client application for Kafka 
---

# Getting Started with Apache Kafka and Python

## Introduction

In this tutorial, you will build Python client applications which produce and consume messages from an Apache Kafka® cluster. 

As you're learning how to run your first Kafka application, we recommend using [Confluent Cloud](https://www.confluent.io/confluent-cloud/tryfree) so that you don't have to run your own Kafka cluster and can focus on the client development. If you do not already have an account, be sure to [sign up](https://www.confluent.io/confluent-cloud/tryfree/). New signups [receive $400](https://www.confluent.io/confluent-cloud-faqs/#how-can-i-get-up-to-dollar400-in-free-confluent-cloud-usage) to spend within Confluent Cloud during their first 30 days. To avoid having to enter a credit card, navigate to [Billing & payment](https://confluent.cloud/settings/billing/payment), scroll to the bottom, and add the promo code `CONFLUENTDEV1`. With this promo code, you will not have to enter your credit card info for 30 days or until your credits run out.

If you prefer to set up a local Kafka cluster, the tutorial will walk you through those steps as well.

## Prerequisites

Using Windows? You'll need to download [Windows Subsystem for Linux](https://learn.microsoft.com/en-us/windows/wsl/install).

This guide assumes that you already have [Python 3](https://www.python.org/downloads/) installed. The example was last tested against Python 3.12.1.

The instructions use [`virtualenv`](https://virtualenv.pypa.io/en/latest/installation.html) but you may use other virtual environment managers like [`venv`](https://docs.python.org/3/library/venv.html) if you prefer.

## Create Project

Create a new directory anywhere you'd like for this project:

```sh
mkdir kafka-python-getting-started && cd kafka-python-getting-started
```

Create and activate a Python virtual environment to give yourself a
clean, isolated workspace. You may also use [`venv`](https://docs.python.org/3/library/venv.html) if you prefer.

```sh
virtualenv env

source env/bin/activate
```

Install the Apache Kafka Python client library:

```sh
pip install confluent-kafka
```

Note: this guide was last tested using version `2.4.0` of the client.

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

Let's create the Python producer application by pasting the following code into a file `producer.py`.

<section data-context-key="kafka.broker" data-context-value="cloud" data-context-default>
<section data-context-key="confluent-cloud.authentication" data-context-value="basic" data-context-default>

```python file=producer_cloud_basic.py
```

</section>
<section data-context-key="confluent-cloud.authentication" data-context-value="oauth">

```python file=producer_cloud_oauth.py
```

</section>
</section>
<section data-context-key="kafka.broker" data-context-value="local">

```python file=producer_local.py
```

</section>
<section data-context-key="kafka.broker" data-context-value="existing">

```python file=producer_existing.py
```

</section>

Fill in the appropriate `bootstrap.servers` value and any additional security configuration needed inline where the client configuration `config` object is created.

## Build Consumer

Next, create the Python consumer application by pasting the following code into a file `consumer.py`.

<section data-context-key="kafka.broker" data-context-value="cloud" data-context-default>
<section data-context-key="confluent-cloud.authentication" data-context-value="basic" data-context-default>

```python file=consumer_cloud_basic.py
```

</section>
<section data-context-key="confluent-cloud.authentication" data-context-value="oauth">

```python file=consumer_cloud_oauth.py
```

</section>
</section>
<section data-context-key="kafka.broker" data-context-value="local">

```python file=consumer_local.py
```

</section>
<section data-context-key="kafka.broker" data-context-value="existing">

```python file=consumer_existing.py
```

</section>

Again, fill in the appropriate `bootstrap.servers` value and any additional security configuration needed inline where the client configuration `config` object is created.

## Produce Events

Make the producer script executable, and run it:

```sh
chmod u+x producer.py

./producer.py
```

You should see output resembling this:

```
Produced event to topic purchases: key = jsmith     value = batteries
Produced event to topic purchases: key = jsmith     value = book
Produced event to topic purchases: key = jbernard   value = book
Produced event to topic purchases: key = eabara     value = alarm clock
Produced event to topic purchases: key = htanaka    value = t-shirts
Produced event to topic purchases: key = jsmith     value = book
Produced event to topic purchases: key = jbernard   value = book
Produced event to topic purchases: key = awalther   value = batteries
Produced event to topic purchases: key = eabara     value = alarm clock
Produced event to topic purchases: key = htanaka    value = batteries
```

## Consume Events

Make the consumer script executable and run it:

```sh
chmod u+x consumer.py

./consumer.py
```

You should see output resembling this:

```
Consumed event from topic purchases: key = sgarcia    value = t-shirts
Consumed event from topic purchases: key = htanaka    value = alarm clock
Consumed event from topic purchases: key = awalther   value = book
Consumed event from topic purchases: key = sgarcia    value = gift card
Consumed event from topic purchases: key = eabara     value = t-shirts
Consumed event from topic purchases: key = eabara     value = t-shirts
Consumed event from topic purchases: key = jsmith     value = t-shirts
Consumed event from topic purchases: key = htanaka    value = batteries
Consumed event from topic purchases: key = htanaka    value = book
Consumed event from topic purchases: key = sgarcia    value = book
Waiting...
Waiting...
Waiting...
```

Rerun the producer to see more events, or feel free to modify the code as necessary to create more or different events.

Once you are done with the consumer, enter `Ctrl-C` to terminate the consumer application.

<section data-context-key="kafka.broker" data-context-value="local">

Shut down Kafka when you are done with it:

```plaintext
confluent local kafka stop
```

And exit the virtual environment:

```plaintext
deactivate
```

</section>

## Where next?


- To delve deeper into producers, consumers, schema registry and cluster administration try our [Apache Kafka for Python Developers](https://developer.confluent.io/courses/kafka-python/intro/) course.
- If you're interested in writing Apache Kafka microservices in Python, follow this step-by-step [blog post](https://www.confluent.io/en-gb/blog/event-driven-microservices-with-python-and-kafka/). 
- For the Python client API, check out the
  [confluent_kafka documentation](https://docs.confluent.io/platform/current/clients/confluent-kafka-python/html/index.html).
- For information on testing in the Kafka ecosystem, check out
  [Testing Event Streaming Apps](/learn/testing-kafka).
- If you're interested in using streaming SQL for data creation,
  processing, and querying in your applications, check out the
  [ksqlDB 101 course](/learn-kafka/ksqldb/intro/).
- Interested in performance tuning of your event streaming applications?
  Check out the [Kafka Performance resources](/learn/kafka-performance/).
