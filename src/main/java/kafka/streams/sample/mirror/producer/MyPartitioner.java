package kafka.streams.sample.mirror.producer;

import java.util.Map;
import kafka.streams.sample.mirror.serde.MySerdes;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.serialization.Serdes;

@Slf4j
public class MyPartitioner implements Partitioner {

  private final DefaultPartitioner defaultPartitioner;

  public MyPartitioner() {
    this.defaultPartitioner = new DefaultPartitioner();
  }

  @Override
  public void configure(Map<String, ?> configs) {}

  @Override
  public int partition(
      String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
    log.info("key: {}", Serdes.String().deserializer().deserialize(topic, keyBytes));
    log.info("value: {}", MySerdes.EVENT_SERDE.deserializer().deserialize(topic, valueBytes));
    val defaultPartition =
        this.defaultPartitioner.partition(topic, key, keyBytes, value, valueBytes, cluster);
    log.info("default partition: {}", defaultPartition);
    return defaultPartition;
  }

  @Override
  public void close() {
    this.defaultPartitioner.close();
  }
}
