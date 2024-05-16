using Confluent.Kafka;
using System;
using System.Threading;

class Consumer {

    static void Main(string[] args)
    {
        var config = new ConsumerConfig
        {
            // User-specific properties that you must set
            BootstrapServers = "<BOOTSTRAP SERVERS>",
            SaslUsername     = "<CLUSTER API KEY>",
            SaslPassword     = "<CLUSTER API SECRET>"

            // Fixed properties
            SecurityProtocol = SecurityProtocol.SaslSsl
            SaslMechanism    = SaslMechanism.Plain
            GroupId = "kafka-dotnet-getting-started",
            AutoOffsetReset = AutoOffsetReset.Earliest
        };

        const string topic = "purchases";

        CancellationTokenSource cts = new CancellationTokenSource();
        Console.CancelKeyPress += (_, e) => {
            e.Cancel = true; // prevent the process from terminating.
            cts.Cancel();
        };

        using (var consumer = new ConsumerBuilder<string, string>(config).Build())
        {
            consumer.Subscribe(topic);
            try {
                while (true) {
                    var cr = consumer.Consume(cts.Token);
                    Console.WriteLine($"Consumed event from topic {topic}: key = {cr.Message.Key,-10} value = {cr.Message.Value}");
                }
            }
            catch (OperationCanceledException) {
                // Ctrl-C was pressed.
            }
            finally{
                consumer.Close();
            }
        }
    }
} 