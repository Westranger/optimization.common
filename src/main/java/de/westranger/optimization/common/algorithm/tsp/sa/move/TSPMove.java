package de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import java.util.ArrayList;
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

    final List<VehicleRoute> vrl = new ArrayList<>(vehicles.size());
    double score;

    final VehicleRoute vrA = vehicles.get(0);
    if (vehicles.size() == 1) {
      if (!checkOneVehicleOrderListLength(vrA.getRoute())) {
        return Optional.empty();
      }

      VehicleRoute result = performMoveSingleVehicle(vrA);
      vrl.add(result);
      score = result.getScore();
    } else {
      final VehicleRoute vrB = vehicles.get(1);

      if (!checkTwoVehiclesOrdersListLength(vrA.getRoute(), vrB.getRoute())) {
        return Optional.empty();
      }

      final List<VehicleRoute> result = performMoveTwoVehicles(vrA, vrB);
      vrl.addAll(result);

      score = Double.isNaN(result.get(0).getScore()) ? 0.0 : result.get(0).getScore();
      score += Double.isNaN(result.get(1).getScore()) ? 0.0 : result.get(1).getScore();
    }

    return Optional.of(new TSPMoveResult(score, vrl));
  }

  protected abstract boolean checkOneVehicleOrderListLength(List<Order> orders);

  protected abstract boolean checkTwoVehiclesOrdersListLength(List<Order> ordersA,
                                                              List<Order> ordersB);

  protected abstract VehicleRoute performMoveSingleVehicle(final VehicleRoute vr);

  protected abstract List<VehicleRoute> performMoveTwoVehicles(final VehicleRoute vrA,
                                                               final VehicleRoute vrB);

}
