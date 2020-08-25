# kafka-streams-mirror-sample

Kafka Streams sample application to confirm mirroring behavior

## How to build/run

Start kafka broker and schema registry servers on localhost.

```bash
$ export BOOTSTRAP_SERVERS="localhost:9092"
```

### Run kafka streams applicatikon

```bash
$ ./gradlew run
```

```
INFO  kafka.streams.sample.mirror.Main - Bootstrap servers: localhost:9092
Topologies:
   Sub-topology: 0
    Source: my-event (topics: [my-event])
      --> KSTREAM-KEY-SELECT-0000000001
    Processor: KSTREAM-KEY-SELECT-0000000001 (stores: [])
      --> chunk-num-aggregation-repartition-filter
      <-- my-event
    Processor: chunk-num-aggregation-repartition-filter (stores: [])
      --> chunk-num-aggregation-repartition-sink
      <-- KSTREAM-KEY-SELECT-0000000001
    Sink: chunk-num-aggregation-repartition-sink (topic: chunk-num-aggregation-repartition)
      <-- chunk-num-aggregation-repartition-filter

  Sub-topology: 1
    Source: chunk-num-aggregation-repartition-source (topics: [chunk-num-aggregation-repartition])
      --> KSTREAM-AGGREGATE-0000000002
    Processor: KSTREAM-AGGREGATE-0000000002 (stores: [chunk-num-aggregation])
      --> KTABLE-SUPPRESS-0000000006
      <-- chunk-num-aggregation-repartition-source
    Processor: KTABLE-SUPPRESS-0000000006 (stores: [KTABLE-SUPPRESS-STATE-STORE-0000000007])
      --> KTABLE-TOSTREAM-0000000008
      <-- KSTREAM-AGGREGATE-0000000002
    Processor: KTABLE-TOSTREAM-0000000008 (stores: [])
      --> KSTREAM-KEY-SELECT-0000000009
      <-- KTABLE-SUPPRESS-0000000006
    Processor: KSTREAM-KEY-SELECT-0000000009 (stores: [])
      --> KSTREAM-KEY-SELECT-0000000010
      <-- KTABLE-TOSTREAM-0000000008
    Processor: KSTREAM-KEY-SELECT-0000000010 (stores: [])
      --> user-id-aggregation-repartition-filter
      <-- KSTREAM-KEY-SELECT-0000000009
    Processor: user-id-aggregation-repartition-filter (stores: [])
      --> user-id-aggregation-repartition-sink
      <-- KSTREAM-KEY-SELECT-0000000010
    Sink: user-id-aggregation-repartition-sink (topic: user-id-aggregation-repartition)
      <-- user-id-aggregation-repartition-filter

  Sub-topology: 2
    Source: user-id-aggregation-repartition-source (topics: [user-id-aggregation-repartition])
      --> KSTREAM-AGGREGATE-0000000011
    Processor: KSTREAM-AGGREGATE-0000000011 (stores: [user-id-aggregation])
      --> KTABLE-TOSTREAM-0000000015
      <-- user-id-aggregation-repartition-source
    Processor: KTABLE-TOSTREAM-0000000015 (stores: [])
      --> KSTREAM-KEY-SELECT-0000000016
      <-- KSTREAM-AGGREGATE-0000000011
    Processor: KSTREAM-KEY-SELECT-0000000016 (stores: [])
      --> KSTREAM-PRINTER-0000000017, KSTREAM-SINK-0000000018
      <-- KTABLE-TOSTREAM-0000000015
    Processor: KSTREAM-PRINTER-0000000017 (stores: [])
      --> none
      <-- KSTREAM-KEY-SELECT-0000000016
    Sink: KSTREAM-SINK-0000000018 (topic: my-aggregation)
      <-- KSTREAM-KEY-SELECT-0000000016
```

### Run Producer

```bash
$ ./gradlew runEventProducer
...
INFO  k.s.s.mirror.producer.EventProducer - Bootstrap servers: localhost:9092
INFO  k.s.s.mirror.producer.EventProducer - sent event: {"user_id": 3, "custom_id": 955, "action": "some", "type": "VIEW", "created_at": 2020-08-25T08:09:19.872Z}
INFO  k.s.s.mirror.producer.EventProducer - sent event: {"user_id": 0, "custom_id": 671, "action": "some", "type": "VIEW", "created_at": 2020-08-25T08:09:21.130Z}
INFO  k.s.s.mirror.producer.EventProducer - sent event: {"user_id": 0, "custom_id": 309, "action": "some", "type": "VIEW", "created_at": 2020-08-25T08:09:22.151Z}
...
```

### Run Consumer

```bash
$ ./gradlew runAggregationConsumer
...
INFO  k.s.s.m.consumer.AggregationConsumer - Bootstrap servers: localhost:9092
INFO  k.s.s.m.consumer.AggregationConsumer - key: 1, value: 356
INFO  k.s.s.m.consumer.AggregationConsumer - key: 5, value: 2300
INFO  k.s.s.m.consumer.AggregationConsumer - key: 2, value: 1960
...
```

## Reference

* https://kafka.apache.org/documentation/#basic_ops_mirror_maker
* https://www.confluent.io/blog/data-reprocessing-with-kafka-streams-resetting-a-streams-application/
