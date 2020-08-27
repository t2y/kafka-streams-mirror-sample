package kafka.streams.sample.mirror.producer;

import java.nio.ByteBuffer;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.WindowedSerdes;

@Slf4j
public class FixedPartitioner implements Partitioner {

  @Override
  public void configure(Map<String, ?> configs) {}

  @Override
  public int partition(
      String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
    val w =
        WindowedSerdes.timeWindowedSerdeFrom(Bytes.class)
            .deserializer()
            .deserialize(topic, keyBytes);
    val k = ByteBuffer.wrap(w.key().get()).getLong();
    if (k == 0 || k == 1 || k == 7) {
      return 0;
    } else if (k == 6) {
      return 1;
    } else if (k == 5) {
      return 2;
    } else if (k == 4) {
      return 3;
    } else if (k == 2 || k == 3) {
      return 4;
    } else {
      return 0;
    }
  }

  @Override
  public void close() {}
}
