package de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.VehicleRoute;
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

      if (vrA.route().size() < 2) {
        return Optional.empty();
      }

      final List<Order> lstA = new ArrayList<>(vrA.route());
      final int removeIdxA = this.rng.nextInt(lstA.size());
      final Order orderA = lstA.remove(removeIdxA);

      final int removeIdxB = this.rng.nextInt(lstA.size());
      final Order orderB = lstA.remove(removeIdxB);

      lstA.add(removeIdxB, orderA);
      lstA.add(removeIdxA, orderB);

      final VehicleRoute vrANew = new VehicleRoute(vrA.id(), vrA.homePosition(), lstA, 0.0);
      vrl.add(vrANew);

      score += routeEvaluator.scoreRoute(vrANew);
    } else {
      final VehicleRoute vrA = vehicles.get(0);
      final VehicleRoute vrB = vehicles.get(1);

      if (vrA.route().isEmpty() || vrB.route().isEmpty()) {
        return Optional.empty();
      }

      final List<Order> lstA = new ArrayList<>(vrA.route());
      final List<Order> lstB = new ArrayList<>(vrB.route());

      final int removeIdxA = this.rng.nextInt(vrA.route().size());
      final Order orderA = lstA.remove(removeIdxA);

      final int removeIdxB = this.rng.nextInt(vrB.route().size());
      final Order orderB = lstB.remove(removeIdxB);

      lstA.add(removeIdxA, orderB);
      lstB.add(removeIdxB, orderA);

      final VehicleRoute vrANew = new VehicleRoute(vrA.id(), vrA.homePosition(), lstA, 0.0);
      final VehicleRoute vrBNew = new VehicleRoute(vrB.id(), vrB.homePosition(), lstB, 0.0);

      vrl.add(vrANew);
      vrl.add(vrBNew);

      score += routeEvaluator.scoreRoute(vrANew);
      score += routeEvaluator.scoreRoute(vrBNew);
    }

    return Optional.of(new TSPMoveResult(score, vrl));
  }
}
