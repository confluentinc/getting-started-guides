package main

import (
    "bufio"
    "fmt"
    "os"
    "strings"

    "github.com/confluentinc/confluent-kafka-go/kafka"
)

func ReadConfig(configFile string) kafka.ConfigMap {

    m := make(map[string]kafka.ConfigValue)

    file, err := os.Open(configFile)
    if err != nil {
        fmt.Fprintf(os.Stderr, "Failed to open file: %s", err)
        os.Exit(1)
    }
    defer file.Close()

    scanner := bufio.NewScanner(file)
    for scanner.Scan() {
        line := strings.TrimSpace(scanner.Text())
        if !strings.HasPrefix(line, "#") && len(line) != 0 {
            before, after, found := strings.Cut(line, "=")
            if found {
                parameter := strings.TrimSpace(before)
                value := strings.TrimSpace(after)
                m[parameter] = value
            }
        }
    }

    if err := scanner.Err(); err != nil {
        fmt.Printf("Failed to read file: %s", err)
        os.Exit(1)
    }

    return m

}
