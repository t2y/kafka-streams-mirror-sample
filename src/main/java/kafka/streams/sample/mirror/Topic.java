package kafka.streams.sample.mirror;

import com.google.common.base.CaseFormat;

public enum Topic {
  MY_EVENT,
  MY_AGGREGATION;

  private final String topicName;

  Topic() {
    this.topicName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, this.name());
  }

  public String getName() {
    return this.topicName;
  }
}
