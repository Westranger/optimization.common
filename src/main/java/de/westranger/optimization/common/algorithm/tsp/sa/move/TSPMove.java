package de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.optimization.common.algorithm.tsp.sa.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.VehicleRoute;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class TSPMove {

  protected final Random rng;
  protected final RouteEvaluator routeEvaluator;

  public TSPMove(final Random rng, final RouteEvaluator routeEvaluator) {
    this.rng = rng;
    this.routeEvaluator = routeEvaluator;
  }

  public Optional<TSPMoveResult> performMove(final List<VehicleRoute> vehicles) {
    if (vehicles.isEmpty() || vehicles.size() > 2) {
      throw new IllegalArgumentException(
          "list of vehicles must not be empty or must not contain more than two vehicles");
    }
    return Optional.empty();
  }

  public List<Integer> generateValidCuts(final int listLength, final int numIndices,
                                         final int minSegmentLength, final Random rng) {
    if (listLength <= 0) {
      throw new IllegalArgumentException("invalid listLength");
    }

    if (minSegmentLength <= 0) {
      throw new IllegalArgumentException("invalid minSegmentLength");
    }

    if (listLength - minSegmentLength < 1) {
      throw new IllegalArgumentException("amount of indices to be generated must be larger than 1");
    }

    List<Integer> candidates = new LinkedList<>();
    List<Integer> result = new ArrayList<>(listLength - minSegmentLength);
    for (int i = minSegmentLength; i <= listLength - minSegmentLength; i++) {
      candidates.add(i);
    }

    for (int idxCount = 0; idxCount < numIndices; idxCount++) {
      List<Integer> toBeRemoved = new LinkedList<>();

      if (candidates.isEmpty()) {
        throw new IllegalStateException("could not draw enough candidates");
      }

      final int idx = rng.nextInt(candidates.size());
      final int value = candidates.get(idx);
      result.add(value);
      for (int i = value - minSegmentLength; i <= value + minSegmentLength - 1; i++) {
        toBeRemoved.add(i);
      }
      candidates.removeAll(toBeRemoved);
    }

    return result;
  }

  public boolean isGenerateValidCutsAlwaysPossible(final int listLength, final int numIndices,
                                                   final int minSegmentLength) {
    double delta = listLength - 2 * minSegmentLength;
    if (delta < 0.0) {
      return false;
    }
    final double frac = Math.floor(delta / (double) minSegmentLength);
    return frac + 1 >= numIndices;
  }

}
