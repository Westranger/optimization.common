package de.westranger.optimization.common.algorithm.tsp.common;

import de.westranger.geometry.common.simple.Point2D;
import java.util.Optional;

public final class Order {
  private final int id;
  private final Point2D to;
  private final Point2D from;
  private final int priority;
  private final Integer material;

  private final long dueTime;

  public Order(final int id, final Point2D to, final Point2D from, final int priority,
               final int material, final long dueTime) {
    this.id = id;
    this.to = to;
    this.from = from;
    this.priority = priority;
    this.material = material;
    this.dueTime = dueTime;
  }

  public Order(final int id, final Point2D to, final Point2D from) {
    this.id = id;
    this.to = to;
    this.from = from;
    this.priority = 0;
    this.material = null;
    this.dueTime = Long.MAX_VALUE;
  }

  public int getId() {
    return id;
  }

  public Point2D getTo() {
    return to;
  }

  public Optional<Point2D> getFrom() {
    if (this.from != null) {
      return Optional.of(this.from);
    }
    return Optional.empty();
  }

  public int getPriority() {
    return this.priority;
  }

  public Optional<Integer> getMaterial() {
    if (this.material != null) {
      return Optional.of(this.material);
    }
    return Optional.empty();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Order order)) {
      return false;
    }
    return id == order.getId();
  }

  public long getDueTime() {
    return dueTime;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("(id=");
    sb.append(this.id);

    if (this.from != null) {
      sb.append(" from=");
      sb.append(this.from);
    }

    sb.append(" to=");
    sb.append(this.to);
    sb.append(" prio=");
    sb.append(this.priority);

    if (this.material != null) {
      sb.append(" mat=");
      sb.append(this.material);
    }
    sb.append(')');

    return sb.toString();
  }

}
