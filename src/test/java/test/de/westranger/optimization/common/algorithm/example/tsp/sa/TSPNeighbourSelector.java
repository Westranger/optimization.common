package test.de.westranger.optimization.common.algorithm.example.tsp.sa;

import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.NeighbourSelector;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.Order;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.State;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.move.TSPInsertSubrouteMove;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.move.TSPInsertSubrouteReverseMove;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.move.TSPInsertionMove;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.move.TSPMove;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.move.TSPSwapMove;

public final class TSPNeighbourSelector implements NeighbourSelector {

  private final TSPMove moveSwap;
  private final TSPMove moveInsert;
  private final TSPMove moveInsertSubroute;
  private final TSPMove moveInsertSubrouteReverse;

  private final double maxTemperature;
  private final double minTemperature;
  private final Random rng;

  public TSPNeighbourSelector(final double maxTemperature, final double minTemperature,
                              final Random rng) {

    this.maxTemperature = maxTemperature;
    this.minTemperature = minTemperature;
    this.rng = rng;
    moveSwap = new TSPSwapMove(rng, null);
    moveInsert = new TSPInsertionMove(rng, null);
    moveInsertSubroute = new TSPInsertSubrouteMove(rng, null);
    moveInsertSubrouteReverse = new TSPInsertSubrouteReverseMove(rng, null);
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

    // filter vehicle without any orders
    Map<Integer, List<Order>> vehicleCandidates = new TreeMap<>();
    for (Map.Entry<Integer, List<Order>> entry : state.getOrderMapping().entrySet()) {
      if (!entry.getValue().isEmpty()) {
        vehicleCandidates.put(entry.getKey(), entry.getValue());
      }
    }

    // now we have two lists
    final int vehicleIdA = this.rng.nextInt(vehicleCandidates.size()) + 1;
    final int vehicleIdB = this.rng.nextInt(state.getOrderMapping().size()) + 1;

    List<Order> ordersA = new ArrayList<>(state.getOrderMapping().get(vehicleIdA));

    final Map<Integer, List<Order>> newMapping = new TreeMap<>();
    if (vehicleIdA == vehicleIdB) {
      // jetzt können wir 2 opt oder 3 opt, move insert, move swap, insert subroute
      int maxRng = 2;
      if (isGenerateValidIndicesPossible(ordersA.size(), 2, 2)) {
        maxRng = 4;
      }

      int selected = rng.nextInt(maxRng);
      switch (selected) {
        case 0 -> moveInsert(ordersA, ordersA);
        case 1 -> moveSwap(ordersA, ordersA);
        case 2 -> {
          final List<Integer> indices = generateValidIndices(ordersA.size(), 2, 2, rng);
          moveInsertSubroute(ordersA, ordersA, indices.get(0), indices.get(1));
        }
        case 3 -> {
          final List<Integer> indices = generateValidIndices(ordersA.size(), 2, 2, rng);
          moveInsertSubrouteReverse(ordersA, ordersA, indices.get(0), indices.get(1));
        }
      }

      newMapping.put(vehicleIdA, ordersA);
    } else {
      List<Order> ordersB = new ArrayList<>(state.getOrderMapping().get(vehicleIdB));

      if (ordersB.isEmpty()) {
        int selected = rng.nextInt(2);
        switch (selected) {
          case 0 -> moveInsert(ordersA, ordersB);
          case 1 -> moveSwap(ordersA, ordersB);
        }
      } else {
        int maxRng = 2;
        if (isGenerateValidIndicesPossible(ordersA.size(), 2, 2)) {
          maxRng = 4;
        }

        int selected = rng.nextInt(maxRng);
        switch (selected) {
          case 0 -> moveInsert(ordersA, ordersB);
          case 1 -> moveSwap(ordersA, ordersB);
          case 2 -> {
            final List<Integer> indices = generateValidIndices(ordersA.size(), 2, 2, rng);
            moveInsertSubroute(ordersA, ordersB, indices.get(0), indices.get(1));
          }
          case 3 -> {
            final List<Integer> indices = generateValidIndices(ordersA.size(), 2, 2, rng);
            moveInsertSubrouteReverse(ordersA, ordersB, indices.get(0), indices.get(1));
          }
        }
      }
      newMapping.put(vehicleIdA, ordersA);
      newMapping.put(vehicleIdB, ordersB);
    }

    for (Map.Entry<Integer, List<Order>> entry : state.getOrderMapping().entrySet()) {
      if (!newMapping.containsKey(entry.getKey())) {
        newMapping.put(entry.getKey(), entry.getValue());
      }
    }

    return new State(new ArrayList<>(), newMapping, state.getVehiclePositions());
  }


}
