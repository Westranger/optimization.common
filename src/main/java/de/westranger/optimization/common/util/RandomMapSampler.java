package de.westranger.optimization.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public final class RandomMapSampler<K, V> {

  private final Map<K, V> map;
  private final Map<K, Integer> samplingStats;
  private final List<K> items;

  public RandomMapSampler() {
    this.map = new TreeMap<>();
    this.samplingStats = new TreeMap<>();
    this.items = new ArrayList<>();
  }

  public V put(K key, V value) {
    if (!this.map.containsKey(key)) {
      this.map.put(key, value);
      this.items.contains(key);
    }
    return value;
  }

  public V sample(final Random rng) {
    final K key = this.items.get(rng.nextInt(this.items.size()));
    this.samplingStats.put(key, this.samplingStats.get(key) + 1);
    return this.map.get(key);
  }

  public Map<K, V> getMap() {
    return Collections.unmodifiableMap(this.map);
  }

  public Map<K, Double> getSamplingStatistics() {
    double sum = 0.0;
    for (int value : this.samplingStats.values()) {
      sum += value;
    }

    final Map<K, Double> result = new TreeMap<>();
    for (Map.Entry<K, Integer> entry : this.samplingStats.entrySet()) {
      result.put(entry.getKey(), (entry.getValue() / sum) * 100.0);
    }
    return Collections.unmodifiableMap(result);
  }

}
