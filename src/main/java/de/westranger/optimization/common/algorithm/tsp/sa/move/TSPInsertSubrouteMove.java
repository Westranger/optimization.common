package de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.VehicleRoute;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public final class TSPInsertSubrouteMove extends TSPMove {
  public TSPInsertSubrouteMove(final Random rng, final RouteEvaluator routeEvaluator) {
    super(rng, routeEvaluator);
  }

  @Override
  public Optional<TSPMoveResult> performMove(final List<VehicleRoute> vehicles) {
    super.performMove(vehicles);

    final VehicleRoute vrA = vehicles.get(0);

    if (!super.isGenerateValidIndicesAlwaysPossible(vrA.getRoute().size(), 2, 2)) {
      return Optional.empty();
    }

    final List<Integer> indices = this.generateValidIndices(vrA.getRoute().size(), 2, 2, this.rng);
    final int min = Math.min(indices.get(0), indices.get(1));
    final int max = Math.max(indices.get(0), indices.get(1));

    final List<VehicleRoute> vrl = new ArrayList<>(vehicles.size());
    double score = 0.0;

    final List<Order> lstA = new ArrayList<>(vrA.getRoute());
    final List<Order> subroute = new LinkedList<>();
    for (int i = min; i <= max; i++) {
      subroute.add(lstA.remove(min));
    }

    if (vehicles.size() == 1) {
      final int idxC = this.rng.nextInt(lstA.size());
      for (int i = 0; i < subroute.size(); i++) {
        lstA.add(idxC + i, subroute.get(i));
      }

      final VehicleRoute vrANew = new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA);
      vrl.add(vrA);

      score += routeEvaluator.scoreRoute(vrANew);
    } else {
      final VehicleRoute vrB = vehicles.get(1);
      final List<Order> lstB = new ArrayList<>(vrB.getRoute());

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
      vrl.add(vrA);
      vrl.add(vrB);

      score += routeEvaluator.scoreRoute(vrANew);
      score += routeEvaluator.scoreRoute(vrBNew);
    }

    return Optional.of(new TSPMoveResult(score, vrl));
  }

}
