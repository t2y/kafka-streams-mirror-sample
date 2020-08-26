package kafka.streams.sample.mirror;

import java.util.concurrent.CountDownLatch;
import kafka.streams.sample.mirror.env.EnvVar;
import kafka.streams.sample.mirror.stream.EventStreams;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;

@Slf4j
public class Main {

  public static void main(String[] args) {
    val config = new EventStreamsConfig();
    val eventStreams = new EventStreams(config);
    val topology = eventStreams.createTopology();
    val bootstrapServers = config.getProps().get(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG);
    log.info("Bootstrap servers: {}", bootstrapServers);
    log.info("\n{}", topology.describe());

    val streams = new KafkaStreams(topology, config.getProps());
    if (EnvVar.STATE_CLEANUP.getBoolValue()) {
      log.info("Clean up local state store");
      streams.cleanUp();
    }

    val latch = new CountDownLatch(1);
    // attach shutdown handler to catch control-c
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread("streams-shutdown-hook") {
              @Override
              public void run() {
                streams.close();
                latch.countDown();
              }
            });

    try {
      streams.start();
      latch.await();
    } catch (Exception e) {
      System.exit(1);
    }
    System.exit(0);
  }
}
