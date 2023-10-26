package test.de.westranger.optimization.common.algorithm.example.tsp.dfs;

import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.NeighbourSelector;
import java.sql.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.Order;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.State;

public final class TSPNeighbourSelector implements NeighbourSelector {

  private final double maxTemperature;
  private final double minTemperature;
  private final Random rng;

  public TSPNeighbourSelector(final double maxTemperature, final double minTemperature,
                              final Random rng) {

    this.maxTemperature = maxTemperature;
    this.minTemperature = minTemperature;
    this.rng = rng;
  }

  @Override
  public SearchSpaceState select(SearchSpaceState intermediateSolution,
                                 double currentTemperature) {

    if (!(intermediateSolution instanceof State state)) {
      throw new IllegalArgumentException("passed SearchSpaceState is not of type State");
    }

    if (!state.getOrderList().isEmpty()) {
      throw new IllegalStateException("there are still order which are not assigned to vehicles");
    }

    if (state.getVehiclePositions().size() != 1) {
      throw new IllegalArgumentException("current implementation can only handle a single vehicle");
    }

    List<Order> orders = new ArrayList<>(state.getOrderMapping().get(1));

    final double ratio = (currentTemperature - minTemperature) / maxTemperature;
    switch (rng.nextInt(4)) {
      case 0 -> {
        // insert move
        final int removeIdx = this.rng.nextInt(orders.size());
        final Order order = orders.remove(removeIdx);
        final int insertIdx = this.rng.nextInt(orders.size());

        orders.add(insertIdx, order);
      }
      case 1 -> {
        // swap move
        final int removeIdxA = this.rng.nextInt(orders.size());
        final Order orderA = orders.remove(removeIdxA);
        final int removeIdxB = this.rng.nextInt(orders.size());
        final Order orderB = orders.remove(removeIdxB);

        orders.add(removeIdxB, orderA);
        orders.add(removeIdxA, orderB);
      }
      case 2 -> {
        // reverse Move / 2-opt move
        final int idxA = this.rng.nextInt(orders.size());
        final int idxB = this.rng.nextInt(orders.size());

        final int min = Math.min(idxA, idxB);
        final int max = Math.max(idxA, idxB);
        final int deltaHalf = (max - min) / 2;

        for (int i = min; i <= min + deltaHalf; i++) {
          Order swap = orders.get(i);
          orders.set(i, orders.get(max - i));
          orders.set(max - i, swap);
        }
      }
      case 3 -> {
        // insert subroute
        final int idxA = this.rng.nextInt(orders.size());
        final int idxB = this.rng.nextInt(orders.size());

        final int min = Math.min(idxA, idxB);
        final int max = Math.max(idxA, idxB);

        List<Order> subroute = new LinkedList<>();
        for (int i = min; i <= max; i++) {
          subroute.add(orders.remove(min));
        }

        if (orders.isEmpty()) {
          orders.addAll(subroute);
        } else {
          final int idxC = this.rng.nextInt(orders.size());
          for (int i = 0; i < subroute.size(); i++) {
            orders.add(idxC + i, subroute.get(i));
          }
        }
      }
    }


    final Map<Integer, List<Order>> newMapping = new TreeMap<>();
    newMapping.put(1, orders);

    return new State(new ArrayList<>(), newMapping, state.getVehiclePositions());
  }
}
