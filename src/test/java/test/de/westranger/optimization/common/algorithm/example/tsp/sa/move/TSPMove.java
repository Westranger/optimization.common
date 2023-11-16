package test.de.westranger.optimization.common.algorithm.example.tsp.sa.move;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.RouteEvaluator;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.VehicleRoute;

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

  protected List<Integer> generateValidIndices(final int listLength, final int numIndices,
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

  protected boolean isGenerateValidIndicesPossible(final int listLength, final int numIndices,
                                                   final int minSegmentLength) {
    return Math.ceil((double) listLength / (double) ((minSegmentLength * 2) + 1)) <= numIndices;
  }

}
