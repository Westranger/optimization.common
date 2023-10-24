package test.de.westranger.optimization.common.algorithm.example.tsp.dfs.aux;

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

    List<Order> orders = state.getOrderMapping().get(1);

    final double ratio = (currentTemperature - minTemperature) / maxTemperature;
    switch (rng.nextInt(2)) {
      case 0 -> {
        // insert move
        final int removeIdx = this.rng.nextInt(orders.size());
        final Order order = orders.remove(removeIdx);
        final int stddev = (int) ((orders.size() / 2) * ratio);
        final double nexD = this.rng.nextDouble();
        int insertIdx =
            (int) triangularDistribution(stddev - removeIdx, removeIdx, stddev + removeIdx);

        if (insertIdx < 0) {
          insertIdx = 0;
        } else if (insertIdx >= orders.size()) {
          insertIdx = orders.size() - 1;
        }

        orders.add(insertIdx, order);
      }
      case 1 -> {
        // swap move
        final int removeIdxA = this.rng.nextInt(orders.size());
        final Order orderA = orders.remove(removeIdxA);
        final int stddev = (int) ((orders.size() / 2) * ratio);
        int removeIdxB =
            (int) triangularDistribution(stddev - removeIdxA, removeIdxA, stddev + removeIdxA);

        if (removeIdxB < 0) {
          removeIdxB = 0;
        } else if (removeIdxB >= orders.size()) {
          removeIdxB = orders.size() - 1;
        }

        final Order orderB = orders.remove(removeIdxB);
        orders.add(removeIdxB, orderA);
        orders.add(removeIdxA, orderB);
      }
      /*
      case 2 -> {
        // 2-opt move

      }
      case 3 -> {
        // reverse move

      }
      */

    }

    final List<Order> newOrders = new ArrayList<>(orders.size());
    newOrders.addAll(orders);

    final Map<Integer, List<Order>> newMapping = new TreeMap<>();
    newMapping.put(1, newOrders);

    return new State(new ArrayList<>(), newMapping, state.getVehiclePositions());
  }

  public double triangularDistribution(double a, double b, double c) {
    double F = (c - a) / (b - a);
    double rand = this.rng.nextDouble();
    if (rand < F) {
      return a + Math.sqrt(rand * (b - a) * (c - a));
    } else {
      return b - Math.sqrt((1 - rand) * (b - a) * (b - c));
    }
  }
}
