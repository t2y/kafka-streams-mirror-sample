package kafka.streams.sample.mirror;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import java.util.Properties;
import kafka.streams.sample.mirror.env.EnvVar;
import lombok.Getter;
import lombok.ToString;
import lombok.val;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

@Getter
@ToString
public class EventStreamsConfig {

  private final Properties props;

  private String makeStateDirPath() {
    return "/tmp/kafka-streams-" + EnvVar.BOOTSTRAP_SERVERS.getValue();
  }

  private Properties createProperties() {
    val p = new Properties();
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "event-mirror-streams");
    p.put(StreamsConfig.STATE_DIR_CONFIG, this.makeStateDirPath());
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, EnvVar.BOOTSTRAP_SERVERS.getValue());
    p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    p.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 2);
    p.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 3000);
    p.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, "3");
    p.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, false);
    p.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
    p.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, Constant.SCHEMA_REGISTRY_URL);
    p.put(StreamsConfig.mainConsumerPrefix(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG), "earliest");
    return p;
  }

  void setBootstrapServer(String servers) {
    this.props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
  }

  public EventStreamsConfig() {
    this.props = this.createProperties();
  }
}
