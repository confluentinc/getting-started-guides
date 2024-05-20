package main

import (
    "fmt"
    "math/rand"
    "os"

    "github.com/confluentinc/confluent-kafka-go/kafka"
)

func main() {

    p, err := kafka.NewProducer(&kafka.ConfigMap{
        // User-specific properties that you must set
        "bootstrap.servers":                   "<BOOTSTRAP SERVERS>",
        "sasl.oauthbearer.client.id":          "<OAUTH2 CLIENT ID>",
        "sasl.oauthbearer.client.secret":      "<OAUTH2 CLIENT SECRET>",
        "sasl.oauthbearer.token.endpoint.url": "<OAUTH2 TOKEN ENDPOINT URL>",
        "sasl.oauthbearer.scope":              "<OAUTH2 SCOPE>",
        "sasl.oauthbearer.extensions":         "logicalCluster=<LOGICAL CLUSTER ID>,identityPoolId=<IDENTITY POOL ID>,",

        // Fixed properties
        "security.protocol":       "SASL_SSL",
        "sasl.mechanisms":         "OAUTHBEARER",
        "sasl.oauthbearer.method": "OIDC",
        "acks":                    "all"})

    if err != nil {
        fmt.Printf("Failed to create producer: %s", err)
        os.Exit(1)
    }

    // Go-routine to handle message delivery reports and
    // possibly other event types (errors, stats, etc)
    go func() {
        for e := range p.Events() {
            switch ev := e.(type) {
            case *kafka.Message:
                if ev.TopicPartition.Error != nil {
                    fmt.Printf("Failed to deliver message: %v\n", ev.TopicPartition)
                } else {
                    fmt.Printf("Produced event to topic %s: key = %-10s value = %s\n",
                        *ev.TopicPartition.Topic, string(ev.Key), string(ev.Value))
                }
            }
        }
    }()

    users := [...]string{"eabara", "jsmith", "sgarcia", "jbernard", "htanaka", "awalther"}
    items := [...]string{"book", "alarm clock", "t-shirts", "gift card", "batteries"}
    topic := "purchases"

    for n := 0; n < 10; n++ {
        key := users[rand.Intn(len(users))]
        data := items[rand.Intn(len(items))]
        p.Produce(&kafka.Message{
            TopicPartition: kafka.TopicPartition{Topic: &topic, Partition: kafka.PartitionAny},
            Key:            []byte(key),
            Value:          []byte(data),
        }, nil)
    }

    // Wait for all messages to be delivered
    p.Flush(15 * 1000)
    p.Close()
}
