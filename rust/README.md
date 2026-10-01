---
seo:
  title: Apache Kafka and Rust - Getting Started Tutorial 
  description: How to run a Kafka client application written in Rust that produces to and consumes messages from a Kafka cluster, complete with step-by-step instructions and examples. 
hero:
  title: Getting Started with Apache Kafka and Rust
  description: Step-by-step guide to building a Rust client application for Kafka 
---

# Getting Started with Apache Kafka and Rust

## Introduction

In this tutorial, you will build Rust client applications that produce and consume messages from an Apache Kafka® cluster. 

As you're learning how to run your first Kafka application, we recommend using [Confluent Cloud](https://www.confluent.io/confluent-cloud/tryfree) so that you don't have to run your own Kafka cluster and can focus on the client development. If you do not already have an account, be sure to [sign up](https://www.confluent.io/confluent-cloud/tryfree/). New signups [receive $400](https://www.confluent.io/confluent-cloud-faqs/#how-can-i-get-up-to-dollar400-in-free-confluent-cloud-usage) to spend within Confluent Cloud during their first 30 days. To avoid having to enter a credit card, navigate to [Billing & payment](https://confluent.cloud/settings/billing/payment), scroll to the bottom, and add the promo code `CONFLUENTDEV1`. With this promo code, you will not have to enter your credit card info for 30 days or until your credits run out.

If you already have a Kafka cluster or prefer to set up a new one locally, the tutorial will walk you through those steps as well.

<div class="alert-primary">
<p>
Note: The Rust client, available in the <a href="https://github.com/confluentinc/kafka-clients">confluentinc/kafka-clients</a> repository, is an early preview and is subject to change. If you run into an issue, please <a href="https://github.com/confluentinc/kafka-clients/issues">open a GitHub issue</a>. For general feedback, start a <a href="https://github.com/confluentinc/kafka-clients/discussions">GitHub discussion</a>.
</p>
</div>

## Prerequisites

Using Windows? You'll need to download [Windows Subsystem for Linux](https://learn.microsoft.com/en-us/windows/wsl/install).

This guide assumes that you already have [Rust and Cargo](https://www.rust-lang.org/tools/install) installed via `rustup`. The example was last tested against Rust 1.95.

## Create Project

Create a new directory anywhere you'd like for this project and initialize a new Cargo project:

```sh
mkdir kafka-rust-getting-started && cd kafka-rust-getting-started

cargo init
```

Add the Kafka client and its runtime dependencies to your `Cargo.toml`:

```toml
[dependencies]
confluent-kafka = { git = "https://github.com/confluentinc/kafka-clients" }
tokio = { version = "1", features = ["rt-multi-thread", "macros", "signal"] }
rand = "0.9"
```

The producer and consumer below each run as their own binary, so remove the generated `src/main.rs` and create a `src/bin` directory instead:

```sh
rm src/main.rs

mkdir src/bin
```

## Kafka Setup

You'll need a Kafka cluster for your client application to connect to.
This dialog can help you configure a Confluent Cloud cluster, create a
local Kafka cluster, or connect to an existing cluster's bootstrap server.

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

Use the [Confluent CLI](https://docs.confluent.io/confluent-cli/current/overview.html) to create a Confluent Cloud environment and Kafka cluster. Install the CLI if you don't already have it:

```plaintext
brew install confluentinc/tap/cli
```

If you don't use Homebrew, you can use a [different installation method](https://docs.confluent.io/confluent-cli/current/install.html).

Log in to Confluent Cloud:

```plaintext
confluent login
```

Install the `confluent-quickstart` CLI plugin, then use it to provision the environment and cluster:

```plaintext
confluent plugin install confluent-quickstart

confluent quickstart \
  --environment-name kafka-getting-started-env \
  --kafka-cluster-name kafka-getting-started-cluster \
  --cloud aws \
  --region us-east-1
```

The example above provisions the cluster in AWS's `us-east-1` region. To use a different cloud provider (`gcp` or `azure`) or region, pass different values for `--cloud` and `--region`. You can find the regions supported by a given cloud provider by running:

```plaintext
confluent kafka region list --cloud <CLOUD>
```

Next, note your Confluent Cloud Kafka cluster's bootstrap server endpoint, as you will need it to configure the producer and consumer clients in upcoming steps. Describe your cluster:

```plaintext
confluent kafka cluster describe
```

Note the `Endpoint` field, which will look something like `SASL_SSL://pkc-abcdef.us-east-1.aws.confluent.cloud:9092`. Only the `pkc-...` portion onward (everything after `SASL_SSL://`) is the bootstrap server endpoint. Save it for later.

Next, create an API key that the producer and consumer client applications will use to access Confluent Cloud with [basic authentication](https://docs.confluent.io/cloud/current/access-management/authenticate/api-keys/api-keys.html).

Get the ID of your Kafka cluster:

```plaintext
confluent kafka cluster list
```

Then create an API key and secret for it, substituting your cluster ID for `<KAFKA_CLUSTER_ID>`:

```plaintext
confluent api-key create --resource <KAFKA_CLUSTER_ID>
```

Note the API key and secret as you will use them when configuring the producer and consumer clients in upcoming steps.

</section>

<section data-context-key="kafka.broker" data-context-value="local">

This guide runs Kafka in Docker via the Confluent CLI.

First, install and start [Docker Desktop](https://docs.docker.com/desktop/) or [Docker Engine](https://docs.docker.com/engine/install/) if you don't already have it. Verify that Docker is set up properly by ensuring that no errors are output when you run `docker info` in your terminal.

Install the Confluent CLI if you don't already have it. In your terminal:

```plaintext
brew install confluentinc/tap/cli
```

If you don't use Homebrew, you can use a [different installation method](https://docs.confluent.io/confluent-cli/current/install.html).

This guide requires version 4.0.0 or later of the Confluent CLI. If you have an older version, run `confluent update` to get the latest release (or `brew upgrade confluentinc/tap/cli` if you installed the CLI with Homebrew).

Now start the Kafka broker:

```plaintext
confluent local kafka start
```

Note the `Plaintext Ports` printed in your terminal, which you will need to configure the producer and consumer clients in upcoming steps.

</section>

<section data-context-key="kafka.broker" data-context-value="existing">

Note your Kafka cluster bootstrap server endpoint as you will need it to configure the producer and consumer clients in upcoming steps.

</section>

## Create Topic

A topic is an immutable, append-only log of events. Usually, a topic is composed of the same kind of events. For example, in this guide, you create a topic for retail purchases.

Create a new topic, `purchases`, which you will use to produce and consume events.

<section data-context-key="kafka.broker" data-context-value="cloud" data-context-default="true">

```plaintext
confluent kafka topic create purchases --partitions 1
```

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

Let's create the Rust producer application by pasting the following code into a file `src/bin/producer.rs`.

<section data-context-key="kafka.broker" data-context-value="cloud" data-context-default>

```rust file=producer_cloud_basic.rs
```

</section>
<section data-context-key="kafka.broker" data-context-value="local">

```rust file=producer_local.rs
```

</section>
<section data-context-key="kafka.broker" data-context-value="existing">

```rust file=producer_existing.rs
```

</section>

Fill in the appropriate `bootstrap.servers` value and any additional security configuration needed inline where the `props` map is created.

## Build Consumer

Next, create the Rust consumer application by pasting the following code into a file `src/bin/consumer.rs`.

<section data-context-key="kafka.broker" data-context-value="cloud" data-context-default>

```rust file=consumer_cloud_basic.rs
```

</section>
<section data-context-key="kafka.broker" data-context-value="local">

```rust file=consumer_local.rs
```

</section>
<section data-context-key="kafka.broker" data-context-value="existing">

```rust file=consumer_existing.rs
```

</section>

Again, fill in the appropriate `bootstrap.servers` value and any additional security configuration needed inline where the `props` map is created.

## Build Binaries

Build the producer and consumer binaries:

```sh
cargo build
```

## Produce Events

Run the producer:

```sh
cargo run --bin producer
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

From another terminal, in the same project directory, run the consumer:

```sh
cargo run --bin consumer
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
```

Rerun the producer to see more events, or feel free to modify the code as necessary to create more or different events.

Enter `Ctrl-C` to terminate the consumer application.

## Clean Up

<section data-context-key="kafka.broker" data-context-value="cloud" data-context-default="true">

When you are finished, delete the `kafka-getting-started-env` environment. First, get its environment ID (in the form `env-123456`):

```plaintext
confluent environment list
```

Delete the environment, including all resources created for this language guide:

```plaintext
confluent environment delete <ENVIRONMENT ID>
```

</section>

<section data-context-key="kafka.broker" data-context-value="local">

Shut down Kafka when you are done with it:

```plaintext
confluent local kafka stop
```

</section>

<section data-context-key="kafka.broker" data-context-value="existing">

If you created any temporary resources on your existing cluster for this guide, such as the `purchases` topic, clean them up now.

</section>

## Where next?

- For the RFCs behind this client's design and additional examples, check out
  the [kafka-clients GitHub repository](https://github.com/confluentinc/kafka-clients).
- Interested in performance tuning of your event streaming applications?
  Check out the [Kafka Performance resources](/learn/kafka-performance/).
