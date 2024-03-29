package de.westranger.optimization.common.algorithm.example.function.fitting.common;

public class CubicFunktion {
  private double a1;
  private double a2;
  private double a3;
  private double constant;

  public CubicFunktion(final double a1, final double a2, final double a3, final double constant) {
    this.a1 = a1;
    this.a2 = a2;
    this.a3 = a3;
    this.constant = constant;
  }

  public double getA1() {
    return a1;
  }

  public double getA2() {
    return a2;
  }

  public double getA3() {
    return a3;
  }

  public double getConstant() {
    return constant;
  }

  public void setA1(final double a1) {
    this.a1 = a1;
  }

  public void setA2(final double a2) {
    this.a2 = a2;
  }

  public void setA3(final double a3) {
    this.a3 = a3;
  }

  public void setConstant(double constant) {
    this.constant = constant;
  }

  public double evaluate(final double x) {
    return this.a1 * Math.pow(x, 3.0) + this.a2 * Math.pow(x, 2.0) + this.a3 * x + this.constant;
  }

  @Override
  public String toString() {
    String sb = this.a1 +
        " * x^3 + " +
        this.a2 +
        " * x^2 + " +
        this.a3 +
        " * x + " +
        this.constant;
    return sb;
  }

}
