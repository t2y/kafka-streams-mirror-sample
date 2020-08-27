package kafka.streams.sample.mirror.producer;

import java.nio.ByteBuffer;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.WindowedSerdes;

@Slf4j
public class RepartitionKeyPartitioner implements Partitioner {

  private final DefaultPartitioner defaultPartitioner;

  public RepartitionKeyPartitioner() {
    this.defaultPartitioner = new DefaultPartitioner();
  }

  @Override
  public void configure(Map<String, ?> configs) {}

  private byte[] convertLongToBytes(long value) {
    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
    buffer.putLong(value);
    return buffer.array();
  }

  private void showFixedPartition5(long key) {
    // when number of partitions: 5,
    // this logic come from debugging mirror to a topic,
    int fixedPartition = 0;
    if (key == 0 || key == 1 || key == 7) {
      fixedPartition = 0;
    } else if (key == 6) {
      fixedPartition = 1;
    } else if (key == 5) {
      fixedPartition = 2;
    } else if (key == 4) {
      fixedPartition = 3;
    } else if (key == 2 || key == 3) {
      fixedPartition = 4;
    }
    log.info("fixed partition5: {}", fixedPartition);
  }

  @Override
  public int partition(
      String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
    val w =
        WindowedSerdes.timeWindowedSerdeFrom(Bytes.class)
            .deserializer()
            .deserialize(topic, keyBytes);
    val k = ByteBuffer.wrap(w.key().get()).getLong();
    val defaultPartition =
        this.defaultPartitioner.partition(
            topic, k, this.convertLongToBytes(k), value, valueBytes, cluster);
    log.info("default partition: {}", defaultPartition);
    return defaultPartition;
  }

  @Override
  public void close() {
    this.defaultPartitioner.close();
  }
}
