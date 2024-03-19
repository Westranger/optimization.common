package de.westranger.optimization.common.util;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

public final class SampleStatistics<T> {

  private final DecimalFormat dfFloat;
  private final DecimalFormat dfInt;
  private final Map<T, Integer> hist;
  private long sum;
  private boolean doCollect;

  public SampleStatistics(boolean doCollect) {
    this.doCollect = doCollect;
    this.hist = new TreeMap<>();
    this.dfFloat = new DecimalFormat("#.##");
    this.dfInt = new DecimalFormat("#,###");
  }

  public void add(T key) {
    if (this.doCollect) {
      if (!hist.containsKey(key)) {
        this.hist.put(key, 1);
      } else {
        this.hist.put(key, this.hist.get(key) + 1);
      }
      this.sum += 1;
    }
  }

  @Override
  public String toString() {
    if (doCollect) {
      final StringBuilder sb = new StringBuilder();
      sb.append("Stats(");
      sb.append("num_keys=");
      sb.append(this.dfInt.format(this.hist.size()));
      sb.append(" num_obs=");
      sb.append(this.dfInt.format(this.sum));

      double avg = (double) this.sum / (double) this.hist.size();
      avg /= this.sum;
      avg *= 100.0;

      double variance = 0.0;
      for (Map.Entry<T, Integer> entry : this.hist.entrySet()) {
        final double perc = ((double) entry.getValue() / (double) this.sum) * 100.0;
        variance += (perc - avg) * (perc - avg);
      }
      variance += 1.0 / (this.hist.size() - 1.0);
      final double stdDev = Math.sqrt(variance);

      sb.append(" avg_perc=");
      sb.append(this.dfFloat.format(avg));

      sb.append("% std_dev_perc=");
      sb.append(this.dfFloat.format(stdDev));
      sb.append("%)");

      return sb.toString();
    }
    return "Stats( --- no statistics were collected --- )";
  }


}
