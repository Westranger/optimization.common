package de.westranger.optimization.common.algorithm.action.planning.solver;

public abstract class Score implements Comparable<Score> {

  public abstract double getAbsoluteScore();

}
