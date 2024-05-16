package main

import (
    "fmt"
    "os"
    "os/signal"
    "syscall"
    "time"

    "github.com/confluentinc/confluent-kafka-go/kafka"
)

func main() {

    c, err := kafka.NewConsumer(&kafka.ConfigMap{
        // User-specific properties that you must set
        "bootstrap.servers":                   "<BOOTSTRAP SERVERS>",
        "sasl.oauthbearer.client.id":          "<OAUTH2 CLIENT ID>",
        "sasl.oauthbearer.client.secret":      "<OAUTH2 CLIENT SECRET>",
        "sasl.oauthbearer.token.endpoint.url": "<OAUTH2 TOKEN ENDPOINT URL>",
        "sasl.oauthbearer.scope":              "<OAUTH2 SCOPE>",
        "sasl.oauthbearer.extensions":         "logicalCluster=<LOGICAL CLUSTER ID>,identityPoolId=<IDENTITY POOL ID>,",

        // Fixed properties
        "security.protocol":                   "SASL_SSL",
        "sasl.mechanisms":                     "OAUTHBEARER",
        "sasl.oauthbearer.method":             "OIDC",
        "group.id":                            "kafka-go-getting-started",
        "auto.offset.reset":                   "earliest"})

    if err != nil {
        fmt.Printf("Failed to create consumer: %s", err)
        os.Exit(1)
    }

    topic := "purchases"
    err = c.SubscribeTopics([]string{topic}, nil)
    // Set up a channel for handling Ctrl-C, etc
    sigchan := make(chan os.Signal, 1)
    signal.Notify(sigchan, syscall.SIGINT, syscall.SIGTERM)

    // Process messages
    run := true
    for run {
        select {
        case sig := <-sigchan:
            fmt.Printf("Caught signal %v: terminating\n", sig)
            run = false
        default:
            ev, err := c.ReadMessage(100 * time.Millisecond)
            if err != nil {
                // Errors are informational and automatically handled by the consumer
                continue
            }
            fmt.Printf("Consumed event from topic %s: key = %-10s value = %s\n",
                *ev.TopicPartition.Topic, string(ev.Key), string(ev.Value))
        }
    }

    c.Close()

}
