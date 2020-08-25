package kafka.streams.sample.mirror.producer;

import java.util.Map;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

@Slf4j
public class RandomPartitioner implements Partitioner {

  @Override
  public void configure(Map<String, ?> configs) {}

  @Override
  public int partition(
      String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
    val partitionInfo = cluster.availablePartitionsForTopic("my-event");
    val partition = new Random().nextInt(partitionInfo.size());
    log.info("random partition: {}/{}", partition, partitionInfo.size());
    return partition;
  }

  @Override
  public void close() {}
}
