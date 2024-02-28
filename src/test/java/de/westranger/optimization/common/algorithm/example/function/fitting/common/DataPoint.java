package de.westranger.optimization.common.algorithm.example.function.fitting.common;

public record DataPoint(double x, double y) {

  @Override
  public String toString() {
    String sb = "(" +
        this.x +
        " , " +
        this.y +
        ')';
    return sb;
  }
}
