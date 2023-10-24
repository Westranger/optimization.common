package test.de.westranger.optimization.common.algorithm.example.function.fitting.sa.aux;

import de.westranger.optimization.common.algorithm.action.planning.solver.Score;

public final class DoubleScore extends Score {

  private final double value;

  public DoubleScore(final double value) {
    this.value = value;
  }

  @Override
  public double getAbsoluteScore() {
    return this.value;
  }

  @Override
  public int compareTo(final Score score) {
    return Double.compare(this.value, score.getAbsoluteScore());
  }

  @Override
  public String toString() {
    return Double.toString(this.value);
  }

}
