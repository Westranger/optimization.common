package test.de.westranger.optimization.common.algorithm.action.planning.solver.dfs.tsp;

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
    this.score = new TSPScore(0.0);
    this.lastPerformedAction = Optional.empty();
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
      if (list.isEmpty()) {
        final double dist =
            this.vehiclePositions.get(act.getVehicleID()).distance(act.getOrder().getTo());
        score = new TSPScore(this.score.getAbsoluteScore() + dist);
      } else {
        final double dist = list.get(list.size() - 1).getTo().distance(act.getOrder().getTo());
        score = new TSPScore(this.score.getAbsoluteScore() + dist);
      }
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
    return 0;
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

}
