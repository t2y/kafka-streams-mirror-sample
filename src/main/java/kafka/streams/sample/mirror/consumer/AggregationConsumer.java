package kafka.streams.sample.mirror.consumer;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import kafka.streams.sample.mirror.Constant;
import kafka.streams.sample.mirror.Topic;
import kafka.streams.sample.mirror.env.EnvVar;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;

@Slf4j
public class AggregationConsumer {

  private final Properties props;

  private Properties createProperties() {
    val p = new Properties();
    p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, EnvVar.BOOTSTRAP_SERVERS.getValue());
    p.put(ConsumerConfig.GROUP_ID_CONFIG, "aggregation-consumer");
    p.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
    p.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
    p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
    p.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
    p.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, Constant.SCHEMA_REGISTRY_URL);
    log.info("Bootstrap servers: {}", EnvVar.BOOTSTRAP_SERVERS.getValue());
    return p;
  }

  public AggregationConsumer() {
    this.props = this.createProperties();
  }

  private static final Duration DURATION_SEC = Duration.ofSeconds(5);

  public void pool() {
    try (val consumer = new KafkaConsumer<Long, Long>(this.props)) {
      consumer.subscribe(Collections.singletonList(Topic.MY_AGGREGATION.getName()));
      while (true) {
        val records = consumer.poll(DURATION_SEC);
        for (val record : records) {
          log.info("key: {}, value: {}", record.key(), record.value());
        }
        consumer.commitSync();
      }
    }
  }

  public static void main(String[] args) {
    val consumer = new AggregationConsumer();
    consumer.pool();
  }
}
