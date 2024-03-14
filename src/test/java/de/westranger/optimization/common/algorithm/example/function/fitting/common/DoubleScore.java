package de.westranger.optimization.common.algorithm.example.function.fitting.common;

import de.westranger.optimization.common.algorithm.action.planning.solver.Score;

public final class DoubleScore extends Score {

  private final double value;

  public DoubleScore(final double value) {
    super(1);
    this.value = value;
  }

  @Override
  public int compareTo(final Score score) {
    if (score instanceof DoubleScore) {
      DoubleScore ds = (DoubleScore) score;
      return Double.compare(this.value, ds.getValue(1));
    } else {
      throw new IllegalArgumentException("provided Score is not of instance DoubleScore");
    }
  }

  @Override
  public boolean isInfinite() {
    return Double.isInfinite(this.value);
  }

  @Override
  public double getValue(int dimensions) {
    return this.value;
  }

  @Override
  public Score difference(Score score) {
    if (score instanceof DoubleScore) {
      DoubleScore ds = (DoubleScore) score;
      return new DoubleScore(this.value - score.getValue(0));
    } else {
      throw new IllegalArgumentException("provided Score is not of instance DoubleScore");
    }
  }

  @Override
  public String toString() {
    return Double.toString(this.value);
  }

}
