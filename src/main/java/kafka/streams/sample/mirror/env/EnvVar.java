package kafka.streams.sample.mirror.env;

import kafka.streams.sample.mirror.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnvVar {
  BOOTSTRAP_SERVERS(System.getenv().getOrDefault("BOOTSTRAP_SERVERS", Constant.BOOTSTRAP_SERVERS));

  private final String value;

  public Boolean getBoolValue() {
    return Boolean.valueOf(this.value);
  }

  public Long getLongValue() {
    return Long.valueOf(this.value);
  }
}
