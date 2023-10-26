package test.de.westranger.optimization.common.algorithm.example.tsp.common;

import de.westranger.geometry.common.simple.Point2D;

public final class Order {
  private final int id;
  private final Point2D to;
  private final Point2D from;

  public Order(final int id, final Point2D to) {
    this(id, to, null);
  }

  public Order(final int id, final Point2D to, final Point2D from) {
    this.id = id;
    this.to = to;
    this.from = from;
  }

  public int getId() {
    return id;
  }

  public Point2D getTo() {
    return to;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Order order)) {
      return false;
    }
    return id == order.getId();
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append('(');
    sb.append(this.id);
    sb.append(',');
    sb.append(this.to);
    sb.append(')');
    return sb.toString();
  }

}
