package de.westranger.optimization.common.algorithm.tsp.common;

import de.westranger.optimization.common.algorithm.action.planning.solver.Score;

public final class TSPScore extends Score {

  private final double value;

  public TSPScore(final double value) {
    this.value = value;
  }

  @Override
  public double getAbsoluteScore() {
    return this.value;
  }

  @Override
  public int compareTo(Score score) {
    final double diff = this.value - score.getAbsoluteScore();
    if (diff < -1e-6) {
      return -1;
    } else if (diff > 1e-6) {
      return 1;
    }
    return 0;
  }
}
