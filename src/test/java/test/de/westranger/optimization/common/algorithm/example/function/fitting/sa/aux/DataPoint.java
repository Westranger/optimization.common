package test.de.westranger.optimization.common.algorithm.example.function.fitting.sa.aux;

public record DataPoint(double x, double y) {

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
