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

In this tutorial, you will use the Confluent REST Proxy to produce and consume messages from an Apache Kafka® cluster.

As you're learning how to run your first Kafka application, we recommend using [Confluent Cloud](https://www.confluent.io/confluent-cloud/tryfree) (no credit card required to sign up) so you don't have to run your own Kafka cluster and you can focus on the client development. But if you prefer to set up a local Kafka cluster, the tutorial will walk you through those steps.


## Prerequisites

Using Windows? You'll need to download [Windows Subsystem for Linux](https://learn.microsoft.com/en-us/windows/wsl/install).

This guide assumes that you already have installed [Docker](https://docs.docker.com/get-docker/),
[Docker Compose](https://docs.docker.com/compose/install/), and [curl](https://curl.se/).

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

If you cannot use Confluent Cloud, you can use an existing Kafka cluster or run one locally using the Confluent CLI.

## Create Project

Create a new directory anywhere you’d like for this project:

```sh
mkdir kafka-restproxy-getting-started && cd kafka-restproxy-getting-started
```

## Kafka Setup

We are going to need a Kafka cluster for our client application to
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

Note the `Plaintext Ports` printed in your terminal, which you will use when configuring the client in the next step.

</section>

<section data-context-key="kafka.broker" data-context-value="existing">
  
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

Client applications access Confluent Cloud Kafka clusters using either [basic authentication](https://docs.confluent.io/cloud/current/access-management/authenticate/api-keys/api-keys.html)
or [OAuth](https://docs.confluent.io/cloud/current/access-management/authenticate/oauth/overview.html).

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

Paste the following commands into your command line terminal, substituting the API key and
secret that you just created for the `username` and `password` fields, respectively, of the `SASL_JAAS_CONFIG` 
environment variable. Note that the bootstrap server endpoint that you provided in the `Kafka Setup` step is used as 
the value of the `BOOTSTRAP_SERVERS` environment variable.

```sh file=getting-started-cloud-basic.sh
```

</section>

<section data-context-key="confluent-cloud.authentication" data-context-value="oauth">

You can use the [Confluent Cloud Console](https://confluent.cloud/) to [add an OAuth/OIDC identity provider](https://docs.confluent.io/cloud/current/access-management/authenticate/oauth/identity-providers.html)
and [create an identity pool](https://docs.confluent.io/cloud/current/access-management/authenticate/oauth/identity-pools.html) with your OAuth/OIDC identity provider.

Paste the following commands into your command line terminal.

Note that the bootstrap server endpoint that you provided in the `Kafka Setup` step is used as
the value of the `BOOTSTRAP_SERVERS` environment variable. Substitute your OAuth/OIDC-specific configuration values as follows:

* `OAUTH2 TOKEN ENDPOINT URL`: The token-issuing URL that your OAuth/OIDC provider exposes. E.g., Okta's token endpoint URL
  format is `https://<okta-domain>.okta.com/oauth2/default/v1/token`
* `OAUTH2 CLIENT ID`: The public identifier for your client. In Okta, this is a 20-character alphanumeric string.
* `OAUTH2 CLIENT SECRET`: The secret corresponding to the client ID. In Okta, this is a 64-character alphanumeric string.
* `OAUTH2 SCOPE`: The name of the scope that you created in your OAuth/OIDC provider to restrict access privileges for issued tokens.
  In Okta, you or your Okta administrator provided the scope name when configuring your authorization server. In the navigation bar of your Okta Developer account,
  you can find this by navigating to `Security > API`, clicking the authorization server name, and finding the defined scopes under the `Scopes` tab.
* `LOGICAL CLUSTER ID`: Your Confluent Cloud logical cluster ID of the form `lkc-123456`. You can view your Kafka cluster ID in
  the Confluent Cloud Console by navigating to `Cluster Settings` in the left navigation of your cluster homepage.
* `IDENTITY POOL ID`: Your Confluent Cloud identity pool ID of the form `pool-1234`. You can find this in the Confluent Cloud Console
  by navigating to `Accounts & access` in the top right menu, selecting the `Identity providers` tab, clicking your identity provider, and viewing the `Identity pools` section of the page.

```properties file=getting-started-cloud-oauth.sh
```

</section> <!--- confluent-cloud.authentication = oauth -->

</section> <!--- kafka.broker = cloud -->

<section data-context-key="kafka.broker" data-context-value="local">

Run the following command in your command line terminal, substituting the plaintext port(s) output when you started Kafka.

```sh file=getting-started-local.sh
```

</section>

<section data-context-key="kafka.broker" data-context-value="existing">

Run the following commands in your command line terminal:

```sh file=getting-started-existing.sh
```

This uses the bootstrap servers configuration you provided. If your Kafka cluster requires different
client security configuration, you may require [different settings](https://kafka.apache.org/documentation/#security).

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

## Start REST Proxy

<section data-context-key="kafka.broker" data-context-value="cloud">

First you need to start the Confluent REST Proxy locally, which you will run in Docker.

Paste the following REST proxy configuration into a new file called `rest-proxy.yml`:

<section data-context-key="confluent-cloud.authentication" data-context-value="basic" data-context-default>

```yaml
---
version: '2'
services:

  rest-proxy:
    image: confluentinc/cp-kafka-rest:7.5.0
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
      KAFKA_REST_SASL_MECHANISM: $SASL_MECHANISM
      KAFKA_REST_CLIENT_BOOTSTRAP_SERVERS: $BOOTSTRAP_SERVERS
      KAFKA_REST_CLIENT_SECURITY_PROTOCOL: "SASL_SSL"
      KAFKA_REST_CLIENT_SASL_JAAS_CONFIG: $SASL_JAAS_CONFIG
      KAFKA_REST_CLIENT_SASL_MECHANISM: $SASL_MECHANISM
```

</section>

<section data-context-key="confluent-cloud.authentication" data-context-value="oauth">

```yaml
---
version: '2'
services:

  rest-proxy:
    image: confluentinc/cp-kafka-rest:7.3.0
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
      KAFKA_REST_SASL_MECHANISM: $SASL_MECHANISM
      KAFKA_REST_SASL_OAUTHBEARER_TOKEN_ENDPOINT_URL: $SASL_OAUTHBEARER_TOKEN_ENDPOINT_URL
      KAFKA_REST_SASL_LOGIN_CALLBACK_HANDLER_CLASS: org.apache.kafka.common.security.oauthbearer.secured.OAuthBearerLoginCallbackHandler
      KAFKA_REST_CLIENT_BOOTSTRAP_SERVERS: $BOOTSTRAP_SERVERS
      KAFKA_REST_CLIENT_SECURITY_PROTOCOL: "SASL_SSL"
      KAFKA_REST_CLIENT_SASL_JAAS_CONFIG: $SASL_JAAS_CONFIG
      KAFKA_REST_CLIENT_SASL_MECHANISM: $SASL_MECHANISM
      KAFKA_REST_CLIENT_SASL_OAUTHBEARER_TOKEN_ENDPOINT_URL: $SASL_OAUTHBEARER_TOKEN_ENDPOINT_URL
      KAFKA_REST_CLIENT_SASL_LOGIN_CALLBACK_HANDLER_CLASS: org.apache.kafka.common.security.oauthbearer.secured.OAuthBearerLoginCallbackHandler

```

</section> <!--- confluent-cloud.authentication = oauth -->


Bring up the REST Proxy:

```sh
docker compose -f rest-proxy.yml up -d
```

Wait a few seconds for REST Proxy to start and verify the Docker container logs show "Server started, listening for requests"

```sh
docker compose -f rest-proxy.yml logs rest-proxy | grep "Server started, listening for requests"
```

</section> <!--- kafka.broker = cloud -->

<section data-context-key="kafka.broker" data-context-value="local">

When you started Kafka locally via `confluent local kafka start`, REST Proxy also started and is listening on port 8082.

</section>

<section data-context-key="kafka.broker" data-context-value="existing">

First you need to start the Confluent REST Proxy locally, which you will run in Docker.

Paste the following REST proxy configuration into a new file called `rest-proxy.yml`:

```yaml
---
version: '2'
services:

  rest-proxy:
    image: confluentinc/cp-kafka-rest:7.5.0
    ports:
      - 8082:8082
    hostname: rest-proxy
    container_name: rest-proxy
    environment:
      KAFKA_REST_HOST_NAME: rest-proxy
      KAFKA_REST_LISTENERS: "http://0.0.0.0:8082"
      KAFKA_REST_BOOTSTRAP_SERVERS: $BOOTSTRAP_SERVERS
```
The above Docker Compose file refers to the bootstrap servers
configuration you provided. If your Kafka cluster requires different
client security configuration, you may require [additional
settings](https://kafka.apache.org/documentation/#security).

Bring up the REST Proxy:

```sh
docker compose -f rest-proxy.yml up -d
```

Wait a few seconds for REST Proxy to start and verify the Docker container logs show "Server started, listening for requests"

```sh
docker compose -f rest-proxy.yml logs rest-proxy | grep "Server started, listening for requests"
```

</section>

## Produce Events

Run the following command to produce some random purchase events to the `purchases` topic:

```sh
curl -X POST \
     -H "Content-Type: application/vnd.kafka.json.v2+json" \
     -H "Accept: application/vnd.kafka.v2+json" \
     --data '{"records":[{"key":"jsmith","value":"alarm clock"},{"key":"htanaka","value":"batteries"},{"key":"awalther","value":"bookshelves"}]}' \
     "http://localhost:8082/topics/purchases"
```

You should see output resembling this:

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

<section data-context-key="kafka.broker" data-context-value="local">

Shut down Kafka when you are done with it:

```plaintext
confluent local kafka stop
```
</section>

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
