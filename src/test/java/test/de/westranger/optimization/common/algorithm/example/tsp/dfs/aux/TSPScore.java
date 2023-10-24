package test.de.westranger.optimization.common.algorithm.example.tsp.dfs.aux;

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
    return Double.compare(this.value, score.getAbsoluteScore());
  }
}
