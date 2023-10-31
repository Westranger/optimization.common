package de.westranger.optimization.common.algorithm.evolutionary;

import java.util.LinkedList;

public final class Gene<T> extends LinkedList<T> {

  public double getScore() {
    return score;
  }

  public void setScore(final double score) {
    this.score = score;
  }

  private double score;
  
}
