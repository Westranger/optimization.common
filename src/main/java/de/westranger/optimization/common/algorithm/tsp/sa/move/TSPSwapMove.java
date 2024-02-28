package de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public final class TSPSwapMove extends TSPMove {

  public TSPSwapMove(final Random rng, final RouteEvaluator routeEvaluator) {
    super(rng, routeEvaluator);
  }

  @Override
  public Optional<TSPMoveResult> performMove(final List<VehicleRoute> vehicles) {
    super.performMove(vehicles);

    final List<VehicleRoute> vrl = new ArrayList<>(vehicles.size());
    double score = 0.0;

    if (vehicles.size() == 1) {
      final VehicleRoute vrA = vehicles.get(0);

      if (vrA.getRoute().size() < 2) {
        return Optional.empty();
      }

      final List<Order> lstA = new ArrayList<>(vrA.getRoute());
      final List<Double> distanceScoreA = new ArrayList<>(vrA.getDistanceScore());

      final int removeIdxA = this.rng.nextInt(lstA.size());
      final Order orderA = lstA.remove(removeIdxA);

      final int removeIdxB = this.rng.nextInt(lstA.size());
      final Order orderB = lstA.remove(removeIdxB);

      lstA.add(removeIdxB, orderA);
      lstA.add(removeIdxA, orderB);

      final VehicleRoute vrANew =
          new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA, distanceScoreA, 0.0,
              vrA.isRoundtrip());
      vrl.add(vrANew);

      routeEvaluator.scoreRouteFull(vrANew);
      score += vrANew.getScore();
    } else {
      final VehicleRoute vrA = vehicles.get(0);
      final VehicleRoute vrB = vehicles.get(1);

      if (vrA.getRoute().isEmpty() || vrB.getRoute().isEmpty()) {
        return Optional.empty();
      }

      final List<Order> lstA = new ArrayList<>(vrA.getRoute());
      final List<Order> lstB = new ArrayList<>(vrB.getRoute());
      final List<Double> distanceScoreA = new ArrayList<>(vrA.getDistanceScore());
      final List<Double> distanceScoreB = new ArrayList<>(vrB.getDistanceScore());

      final int removeIdxA = this.rng.nextInt(vrA.getRoute().size());
      final Order orderA = lstA.remove(removeIdxA);

      final int removeIdxB = this.rng.nextInt(vrB.getRoute().size());
      final Order orderB = lstB.remove(removeIdxB);

      lstA.add(removeIdxA, orderB);
      lstB.add(removeIdxB, orderA);

      final VehicleRoute vrANew =
          new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA, distanceScoreA, 0.0,
              vrA.isRoundtrip());
      final VehicleRoute vrBNew =
          new VehicleRoute(vrB.getId(), vrB.getHomePosition(), lstB, distanceScoreB, 0.0,
              vrB.isRoundtrip());

      vrl.add(vrANew);
      vrl.add(vrBNew);

      routeEvaluator.scoreRouteFull(vrANew);
      routeEvaluator.scoreRouteFull(vrBNew);

      score += vrANew.getScore();
      score += vrBNew.getScore();
    }

    return Optional.of(new TSPMoveResult(score, vrl));
  }
}
