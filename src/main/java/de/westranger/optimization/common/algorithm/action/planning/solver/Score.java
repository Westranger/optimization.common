package de.westranger.optimization.common.algorithm.action.planning.solver;

public abstract class Score implements Comparable<Score> {


  protected int dimensions;

  public Score(final int dimensions) {
    if (dimensions < 1) {
      throw new IllegalArgumentException("number of dimensions must eb larger then 0");
    }
    this.dimensions = dimensions;
  }

  public int getDimensions() {
    return dimensions;
  }

  public abstract int compareTo(Score score);

  public abstract boolean isInfinite();

  public abstract double getValue(final int dimensions);

  public abstract Score difference(final Score score);


}
