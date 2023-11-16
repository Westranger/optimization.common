package test.de.westranger.optimization.common.algorithm.example.tsp.sa.move;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.Order;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.RouteEvaluator;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.VehicleRoute;

public final class TSPInsertSubrouteMove extends TSPMove {
  public TSPInsertSubrouteMove(final Random rng, final RouteEvaluator routeEvaluator) {
    super(rng, routeEvaluator);
  }

  @Override
  public Optional<TSPMoveResult> performMove(final VehicleRoute vrA, final VehicleRoute vrB) {
    if (!isGenerateValidIndicesPossible(vrA.getRoute().size(), 2, 2)) {
      return Optional.empty();
    }

    final List<Integer> indices = this.generateValidIndices(vrA.getRoute().size(), 2, 2, this.rng);
    final int min = Math.min(indices.get(0), indices.get(1));
    final int max = Math.max(indices.get(0), indices.get(1));

    final List<Order> lstA = new ArrayList<>(vrA.getRoute());
    final List<Order> lstB = new ArrayList<>(vrB.getRoute());

    List<Order> subroute = new LinkedList<>();
    for (int i = min; i <= max; i++) {
      subroute.add(lstA.remove(min));
    }

    if (lstB.isEmpty()) {
      lstB.addAll(subroute);
    } else {
      final int idxC = this.rng.nextInt(lstB.size());
      for (int i = 0; i < subroute.size(); i++) {
        lstB.add(idxC + i, subroute.get(i));
      }
    }

    final VehicleRoute vrANew = new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA);
    final VehicleRoute vrBNew = new VehicleRoute(vrB.getId(), vrB.getHomePosition(), lstB);

    double score = routeEvaluator.scoreRoute(vrANew);
    score += routeEvaluator.scoreRoute(vrBNew);

    return Optional.of(new TSPMoveResult(score, vrANew, vrBNew));
  }

}
