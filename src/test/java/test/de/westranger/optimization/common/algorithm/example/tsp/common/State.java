package test.de.westranger.optimization.common.algorithm.example.tsp.common;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;

import de.westranger.optimization.common.algorithm.action.planning.solver.Score;
import java.util.*;

public class State extends SearchSpaceState {

  private final List<Order> orderList;
  private final Map<Integer, List<Order>> orderMapping;
  private final Map<Integer, Point2D> vehiclePositions;

  private TSPScore score;

  private Optional<Action> lastPerformedAction;

  public State(final List<Order> orderList, final Map<Integer, List<Order>> orderMapping,
               final Map<Integer, Point2D> vehiclePositions) {
    this.orderList = new LinkedList<>(orderList);
    this.orderMapping = new TreeMap<>(orderMapping);
    this.vehiclePositions = new TreeMap<>(vehiclePositions);
    this.lastPerformedAction = Optional.empty();
    this.score = new TSPScore(computeFullScore());
  }

  private State(final List<Order> orderList, final Map<Integer, List<Order>> orderMapping,
                final Map<Integer, Point2D> vehiclePositions, final TSPScore score) {
    this(orderList, orderMapping, vehiclePositions);
    this.score = score;
  }

  @Override
  public List<Action> getPossibleActions() {
    final List<Action> result = new LinkedList<>();
    for (int i : this.orderMapping.keySet()) {
      for (Order order : this.orderList) {
        result.add(new TSPAction(i, order));
      }
    }
    return result;
  }

  @Override
  public boolean perform(Action action) {
    if (!(action instanceof TSPAction act)) {
      throw new IllegalArgumentException("illegal action type passed");
    }

    final boolean result = this.orderList.remove(act.getOrder());
    if (result) {
      List<Order> list = this.orderMapping.get(act.getVehicleID());
      final double dist;
      if (list.isEmpty()) {
        dist = this.vehiclePositions.get(act.getVehicleID()).distance(act.getOrder().getTo());
      } else {
        dist = list.get(list.size() - 1).getTo().distance(act.getOrder().getTo());
      }
      score = new TSPScore(this.score.getAbsoluteScore() + dist);
      list.add(act.getOrder());
    }

    this.lastPerformedAction = Optional.of(act);

    return result;
  }

  @Override
  public Score getScore() {
    return this.score;
  }

  @Override
  public int compareTo(SearchSpaceState o) {
    return this.score.compareTo(o.getScore());
  }

  @Override
  public State clone() {
    final List<Order> orderList = new ArrayList<>(this.orderList.size());
    orderList.addAll(this.orderList);

    final Map<Integer, List<Order>> orderMapping = new TreeMap<>();
    for (Map.Entry<Integer, List<Order>> entry : this.orderMapping.entrySet()) {
      List<Order> tmp = new ArrayList<>(entry.getValue().size());
      tmp.addAll(entry.getValue());
      orderMapping.put(entry.getKey(), tmp);
    }

    final Map<Integer, Point2D> vehiclePositions = new TreeMap<>(this.vehiclePositions);

    return new State(orderList, orderMapping, vehiclePositions, score);
  }

  @Override
  public Optional<Action> getLastPerformedAction() {
    return this.lastPerformedAction;
  }

  @Override
  public boolean isGoalState() {
    return this.orderList.isEmpty();
  }

  public List<Order> getOrderList() {
    return orderList;
  }

  public Map<Integer, List<Order>> getOrderMapping() {
    return orderMapping;
  }

  public Map<Integer, Point2D> getVehiclePositions() {
    return vehiclePositions;
  }

  private double computeScore(final Point2D vehicleStart, final List<Order> orders) {
    double sum = 0.0;
    Point2D prev = vehicleStart;
    for (int i = 0; i < orders.size(); i++) {
      final Point2D current = orders.get(i).getTo();
      sum += prev.distance(current);
      prev = current;
    }

    return sum;
  }

  private double computeFullScore() {
    double score = 0.0;
    for (Map.Entry<Integer, Point2D> entry : vehiclePositions.entrySet()) {
      score += computeScore(entry.getValue(), orderMapping.get(entry.getKey()));
    }
    return score;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("(state score=");
    sb.append(computeFullScore());
    sb.append(' ');
    if (this.score != null) {
      sb.append(this.score.getAbsoluteScore());
    } else {
      sb.append('x');
    }
    sb.append(')');

    return sb.toString();
  }

}
