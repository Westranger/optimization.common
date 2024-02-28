package de.westranger.optimization.common.util;

import java.util.LinkedList;
import java.util.List;

public class ProgressTracker {
  private final long maxValue;
  private long lastTime;
  private long lastCurrent;
  private double averageDelta;
  private double progressPercentage;
  private final List<Long> deltasTime = new LinkedList<>();
  private final List<Long> deltasValue = new LinkedList<>();

  public ProgressTracker(long maxValue) {
    this.maxValue = maxValue;
    this.lastTime = System.currentTimeMillis();
  }

  public void nextRound(long currentValue) {
    final long currentTime = System.currentTimeMillis();
    final long deltaTime = currentTime - lastTime;
    final long deltaValue = currentValue - lastCurrent;

    if (deltaValue < 0) {
      throw new IllegalArgumentException("progress is negative this is not allowed");
    }

    lastTime = currentTime;
    lastCurrent = currentValue;

    deltasTime.add(deltaTime);
    deltasValue.add(deltaValue);

    averageDelta = 0.0;
    for (int i = 0; i < deltasTime.size(); i++) {
      averageDelta += (double) deltasTime.get(i) / (double) deltasValue.get(i);
    }
    averageDelta /= deltasTime.size();

    // Aktualisierung des Fortschritts
    progressPercentage = ((double) currentValue / maxValue) * 100;
  }

  public double getProgressPercentage() {
    return progressPercentage;
  }

  public double getAverageDelta() {
    return averageDelta;
  }

  public long getEstimatedCompletionDate() {
    long remainingValues = maxValue - lastCurrent;
    long estimatedTimeInMillis = (long) (averageDelta * remainingValues);
    return System.currentTimeMillis() + estimatedTimeInMillis;
  }
}
