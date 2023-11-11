package test.de.westranger.optimization.common.algorithm.example.tsp.dfs;

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

  private boolean isGenerateValidIndicesPossible(final int listLength, final int numIndices,
                                                 final int minSegmentLength) {
    return Math.ceil((double) listLength / (double) ((minSegmentLength * 2) + 1)) <= numIndices;
  }

  private List<Integer> generateValidIndices(final int listLength, final int numIndices,
                                             final int minSegmentLength, final Random rng) {
    if (listLength <= 0) {
      throw new IllegalArgumentException("invalid listLength");
    }

    if (minSegmentLength <= 0) {
      throw new IllegalArgumentException("invalid minSegmentLength");
    }

    List<Integer> candidates = new LinkedList<>();
    List<Integer> result = new ArrayList<>();
    for (int i = minSegmentLength - 1; i < listLength - minSegmentLength; i++) {
      candidates.add(i);
    }

    for (int idxCount = 0; idxCount < numIndices; idxCount++) {
      List<Integer> toBeRemoved = new ArrayList<>((minSegmentLength * 2) + 1);

      if (candidates.isEmpty()) {
        throw new IllegalStateException("could not draw enough candidates");
      }

      final int idx = rng.nextInt(candidates.size());
      result.add(idx);
      for (int i = idx - minSegmentLength; i <= idx + minSegmentLength; i++) {
        toBeRemoved.add(i);
      }
      candidates.removeAll(toBeRemoved);
    }

    return result;
  }

  private void moveInsert(List<Order> ordersA, List<Order> ordersB) {
    final int removeIdx = this.rng.nextInt(ordersA.size());
    final Order order = ordersA.remove(removeIdx);
    final int insertIdx = ordersB.isEmpty() ? 0 : this.rng.nextInt(ordersB.size());

    ordersB.add(insertIdx, order);
  }

  private void moveInsertSubroute(List<Order> ordersA, List<Order> ordersB, final int idxStart,
                                  final int idxEnd) {
    final int min = Math.min(idxStart, idxEnd);
    final int max = Math.max(idxStart, idxEnd);

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

  private void moveInsertSubrouteReverse(List<Order> ordersA, List<Order> ordersB,
                                         final int idxStart,
                                         final int idxEnd) {
    final int min = Math.min(idxStart, idxEnd);
    final int max = Math.max(idxStart, idxEnd);

    List<Order> subroute = new LinkedList<>();
    for (int i = min; i <= max; i++) {
      subroute.add(ordersA.remove(max - i - min));
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


  private void moveSwap(List<Order> ordersA, List<Order> ordersB) {
    if (ordersA == ordersB || ordersB.isEmpty()) {
      return;
    }
    final int removeIdxA = this.rng.nextInt(ordersA.size());
    final Order orderA = ordersA.remove(removeIdxA);
    final int removeIdxB = this.rng.nextInt(ordersB.size());
    final Order orderB = ordersB.remove(removeIdxB);

    ordersB.add(removeIdxB, orderA);
    ordersA.add(removeIdxA, orderB);
  }
}
