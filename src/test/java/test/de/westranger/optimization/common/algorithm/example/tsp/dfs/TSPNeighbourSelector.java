package test.de.westranger.optimization.common.algorithm.example.tsp.dfs;

import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.NeighbourSelector;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.Order;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.State;

import java.util.*;

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

    final int vehicleIdA = this.rng.nextInt(state.getOrderMapping().size()) + 1;
    final int vehicleIdB = this.rng.nextInt(state.getOrderMapping().size()) + 1;

    List<Order> ordersA;
    List<Order> ordersB;

    if (vehicleIdA == vehicleIdB) {
      ordersA = new ArrayList<>(state.getOrderMapping().get(vehicleIdA));
      ordersB = ordersA;
    } else {
      ordersA = new ArrayList<>(state.getOrderMapping().get(vehicleIdA));
      ordersB = new ArrayList<>(state.getOrderMapping().get(vehicleIdB));
    }


    if (ordersA.isEmpty() && ordersB.isEmpty()) {
      return state;
    } else if (ordersA.isEmpty() && !ordersB.isEmpty()) {
      List<Order> swap = ordersA;
      ordersA = ordersB;
      ordersB = swap;
    }

    final double ratio = (currentTemperature - minTemperature) / maxTemperature;
    int selected = rng.nextInt(4);
    switch (selected) {
      case 0 -> {
        // insert move
        final int removeIdx = this.rng.nextInt(ordersA.size());
        final Order order = ordersA.remove(removeIdx);
        final int insertIdx = ordersB.isEmpty() ? 0 : this.rng.nextInt(ordersB.size());

        ordersB.add(insertIdx, order);
      }
      case 1 -> {
        // swap move
        if (ordersA == ordersB || ordersB.isEmpty()) {
          break;
        }
        final int removeIdxA = this.rng.nextInt(ordersA.size());
        final Order orderA = ordersA.remove(removeIdxA);
        final int removeIdxB = this.rng.nextInt(ordersB.size());
        final Order orderB = ordersB.remove(removeIdxB);

        ordersB.add(removeIdxB, orderA);
        ordersA.add(removeIdxA, orderB);
      }
      case 2 -> {
        // reverse Move / 2-opt move
        final int idxA = this.rng.nextInt(ordersA.size());
        final int idxB = this.rng.nextInt(ordersA.size());

        final int min = Math.min(idxA, idxB);
        final int max = Math.max(idxA, idxB);
        final int deltaHalf = (max - min) / 2;

        for (int i = min; i <= min + deltaHalf; i++) {
          Order swap = ordersA.get(i);
          ordersA.set(i, ordersA.get(max - i));
          ordersA.set(max - i, swap);
        }
      }
      case 3 -> {
        // insert subroute#
        final int idxA = this.rng.nextInt(ordersA.size());
        final int idxB = this.rng.nextInt(ordersA.size());

        final int min = Math.min(idxA, idxB);
        final int max = Math.max(idxA, idxB);

        List<Order> subroute = new LinkedList<>();
        for (int i = min; i <= max; i++) {
          subroute.add(ordersA.remove(min));
        }

        if (ordersB.isEmpty()) {
          ordersB.addAll(subroute);
        } else {
          final int idxC = this.rng.nextInt(ordersB.size());
          for (int i = 0; i < subroute.size(); i++) {
            ordersB.add(idxC + i, subroute.get(i));
          }
        }
      }
    }


    final Map<Integer, List<Order>> newMapping = new TreeMap<>();
    int count = 0;
    for (Map.Entry<Integer, List<Order>> entry : state.getOrderMapping().entrySet()) {
      if (entry.getKey() == vehicleIdA) {
        newMapping.put(vehicleIdA, ordersA);
        count += ordersA.size();
      } else if (entry.getKey() == vehicleIdB) {
        newMapping.put(vehicleIdB, ordersB);
        count += ordersB.size();
      } else {
        count += entry.getValue().size();
        newMapping.put(entry.getKey(), entry.getValue());
      }
    }

    return new State(new ArrayList<>(), newMapping, state.getVehiclePositions());
  }
}
