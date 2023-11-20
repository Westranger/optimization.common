package de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.VehicleRoute;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import de.westranger.optimization.common.algorithm.tsp.sa.RouteEvaluator;

public final class TSPInsertionMove extends TSPMove {

  public TSPInsertionMove(final Random rng, final RouteEvaluator re) {
    super(rng, re);
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
      final int removeIdx = this.rng.nextInt(vrA.getRoute().size());
      final Order order = lstA.remove(removeIdx);
      final int insertIdx = this.rng.nextInt(lstA.size() + 1);

      if (insertIdx == lstA.size()) {
        lstA.add(order);
      } else {
        lstA.add(insertIdx, order);
      }

      final VehicleRoute vrANew = new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA);
      vrl.add(vrANew);

      score += routeEvaluator.scoreRoute(vrANew);
    } else {
      final VehicleRoute vrA = vehicles.get(0);
      final VehicleRoute vrB = vehicles.get(1);

      if (vrA.getRoute().isEmpty() && vrB.getRoute().isEmpty()) {
        return Optional.empty();
      }


    }

    return Optional.of(new TSPMoveResult(score, vrl));
  }
}
