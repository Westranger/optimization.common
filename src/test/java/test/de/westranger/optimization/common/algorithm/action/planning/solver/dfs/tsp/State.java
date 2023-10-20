package test.de.westranger.optimization.common.algorithm.action.planning.solver.dfs.tsp;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;

import java.util.*;

public class State extends SearchSpaceState<Long> {

    private final List<Order> orderList;
    private final Map<Integer, List<Order>> orderMapping;
    private final Map<Integer, Point2D> vehiclePositions;

    private Long score;

    private Optional<Action> lastPerformedAction;

    public State(final List<Order> orderList, final Map<Integer, List<Order>> orderMapping,
                 final Map<Integer, Point2D> vehiclePositions) {
        this.orderList = new LinkedList<>(orderList);
        this.orderMapping = new TreeMap<>(orderMapping);
        this.vehiclePositions = new TreeMap<>(vehiclePositions);
        this.score = 0L;
        this.lastPerformedAction = Optional.empty();
    }

    private State(final List<Order> orderList, final Map<Integer, List<Order>> orderMapping,
                  final Map<Integer, Point2D> vehiclePositions, final Long score) {
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
                score +=
                        (long) (this.vehiclePositions.get(act.getVehicleID()).distance(act.getOrder().getTo()) *
                                1000L);
            } else {
                score +=
                        (long) (list.get(list.size() - 1).getTo().distance(act.getOrder().getTo()) *
                                1000L);
            }
            list.add(act.getOrder());
        }

        this.lastPerformedAction = Optional.of(act);

        return result;
    }

    @Override
    public Long getScore() {
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
