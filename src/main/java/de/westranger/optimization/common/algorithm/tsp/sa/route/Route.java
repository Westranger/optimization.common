package de.westranger.optimization.common.algorithm.tsp.sa.route;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Deprecated
public final class Route implements Cloneable {

  private final SortedMap<Integer, List<Order>> sortedOrdersByPriority;
  private final SortedMap<Integer, List<Double>> sortedScoreDeltas;

  private double score = 0.0;

  public Route(final SortedMap<Integer, List<Order>> sortedOrdersByPriority,
               SortedMap<Integer, List<Double>> sortedScoreDeltas, final double score) {
    this.sortedOrdersByPriority = sortedOrdersByPriority;
    this.sortedScoreDeltas = sortedScoreDeltas;
    this.score = score;
  }

  public Route(final List<Order> orders) {
    this.sortedOrdersByPriority = new TreeMap<>();
    this.sortedScoreDeltas = new TreeMap<>();

    for (Order order : orders) {
      if (!sortedOrdersByPriority.containsKey(order.getPriority())) {
        this.sortedOrdersByPriority.put(order.getPriority(), new ArrayList<>());
      }
      this.sortedOrdersByPriority.get(order.getPriority()).add(order);
    }

    Point2D last = null;
    for (Map.Entry<Integer, List<Order>> entry : this.sortedOrdersByPriority.entrySet()) {
      if (!this.sortedScoreDeltas.containsKey(entry.getKey())) {
        this.sortedScoreDeltas.put(entry.getKey(), new ArrayList<>());
      }

      if (last != null) {
        this.score += entry.getValue().get(0).getTo().distance(last);
      }

      final List<Double> deltas = new ArrayList<>(entry.getValue().size() - 1);
      for (int i = 1; i < entry.getValue().size(); i++) {
        final double delta = computeScore(entry.getValue().get(i - 1), entry.getValue().get(i));
        this.score += delta;
        deltas.add(delta);
      }

      last = entry.getValue().get(entry.getValue().size() - 1).getTo();
      this.sortedScoreDeltas.put(entry.getKey(), deltas);
    }
  }

  private double computeScore(final Order orderA, final Order orderB) {
    if (orderB.getFrom().isPresent()) {
      return orderA.getTo().distance(orderB.getFrom().get());
    }
    return orderA.getTo().distance(orderB.getTo());
  }

  public SortedMap<Integer, List<Order>> getSortedOrdersByPriority() {
    return sortedOrdersByPriority;
  }

  public SortedMap<Integer, List<Double>> getSortedScoreDeltas() {
    return sortedScoreDeltas;
  }

  public double getScore() {
    return score;
  }

  @Override
  public Route clone() {
    TreeMap<Integer, List<Order>> sortedOrdersByPriority = new TreeMap<>();
    for (Map.Entry<Integer, List<Order>> entry : this.sortedOrdersByPriority.entrySet()) {
      sortedOrdersByPriority.put(entry.getKey(), new ArrayList<>(entry.getValue()));
    }

    TreeMap<Integer, List<Double>> sortedScoreDeltas = new TreeMap<>();
    for (Map.Entry<Integer, List<Double>> entry : this.sortedScoreDeltas.entrySet()) {
      sortedScoreDeltas.put(entry.getKey(), new ArrayList<>(entry.getValue()));
    }

    return new Route(sortedOrdersByPriority, sortedScoreDeltas, this.score);
  }
}
