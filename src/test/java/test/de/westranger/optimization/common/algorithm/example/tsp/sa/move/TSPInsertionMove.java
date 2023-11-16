package test.de.westranger.optimization.common.algorithm.example.tsp.sa.move;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.Order;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.RouteEvaluator;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.VehicleRoute;

public final class TSPInsertionMove extends TSPMove {

  public TSPInsertionMove(final Random rng, final RouteEvaluator re) {
    super(rng, re);
  }

  @Override
  public Optional<TSPMoveResult> performMove(final List<VehicleRoute> vehicles) {
    super.performMove(vehicles);

    final int removeIdx = this.rng.nextInt(vrA.getRoute().size());
    final List<Order> lstA = new ArrayList<>(vrA.getRoute());
    final List<Order> lstB = new ArrayList<>(vrB.getRoute());
    final Order order = lstA.remove(removeIdx);
    final int insertIdx = lstB.isEmpty() ? 0 : this.rng.nextInt(lstB.size());
    lstB.add(insertIdx, order);

    final VehicleRoute vrANew = new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA);
    final VehicleRoute vrBNew = new VehicleRoute(vrB.getId(), vrB.getHomePosition(), lstB);

    double score = routeEvaluator.scoreRoute(vrANew);
    score += routeEvaluator.scoreRoute(vrBNew);

    return Optional.of(new TSPMoveResult(score, vrANew, vrBNew));
  }
}
