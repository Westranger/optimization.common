package de.westranger.optimization.common.algorithm.tsp.common;

import de.westranger.optimization.common.algorithm.action.planning.solver.Score;

public final class TSPScore extends Score {

  private final double valueDistance;

  public TSPScore(final double valueDistance) {
    super(1);
    this.valueDistance = valueDistance;
  }

  @Override
  public int compareTo(Score score) {
    if (score instanceof TSPScore) {
      TSPScore tsp = (TSPScore) score;
      final double diff = this.valueDistance - tsp.getValue(0);
      if (diff < -1e-6) {
        return 1;
      } else if (diff > 1e-6) {
        return -1;
      }
      return 0;
    } else {
      throw new IllegalArgumentException("provided parameter must be of type TSPScore");
    }
  }

  @Override
  public boolean isInfinite() {
    return Double.isInfinite(this.valueDistance);
  }

  @Override
  public double getValue(int index) {
    if (index >= this.dimensions) {
      throw new IndexOutOfBoundsException("");
    }
    return this.valueDistance;
  }

  @Override
  public Score difference(Score score) {
    if (score instanceof TSPScore) {
      TSPScore tsp = (TSPScore) score;
      final double diff = this.valueDistance - score.getValue(0);
      return new TSPScore(diff);
    } else {
      throw new IllegalArgumentException("provided score is not of type TSPScore");
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Score(dist:");
    sb.append(this.valueDistance);
    sb.append(")");
    return sb.toString();
  }
}
