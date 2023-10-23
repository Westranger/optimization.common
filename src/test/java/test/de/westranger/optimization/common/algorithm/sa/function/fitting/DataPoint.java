package test.de.westranger.optimization.common.algorithm.sa.function.fitting;

public final class DataPoint {
  private final double x;
  private final double y;

  public DataPoint(final double x, final double y) {
    this.x = x;
    this.y = y;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append('(');
    sb.append(this.x);
    sb.append(" , ");
    sb.append(this.y);
    sb.append(')');
    return sb.toString();
  }
}
