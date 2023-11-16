package test.de.westranger.optimization.common.algorithm.example.tsp.sa.move;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.Order;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.RouteEvaluator;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.VehicleRoute;

public final class TSPSwapMove extends TSPMove {

  public TSPSwapMove(final Random rng, final RouteEvaluator routeEvaluator) {
    super(rng, routeEvaluator);
  }

  @Override
  public Optional<TSPMoveResult> performMove(VehicleRoute vrA, VehicleRoute vrB) {
    if (vrA.getId() == vrB.getId() || vrB.getRoute().isEmpty()) {
      return Optional.empty();
    }

    final int removeIdxA = this.rng.nextInt(vrA.getRoute().size());
    final List<Order> lstA = new ArrayList<>(vrA.getRoute());

    final Order orderA = lstA.remove(removeIdxA);
    final int removeIdxB = this.rng.nextInt(vrB.getRoute().size());
    final List<Order> lstB = new ArrayList<>(vrA.getRoute());
    final Order orderB = lstB.remove(removeIdxB);

    lstB.add(removeIdxB, orderA);
    lstA.add(removeIdxA, orderB);

    final VehicleRoute vrANew = new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA);
    final VehicleRoute vrBNew = new VehicleRoute(vrB.getId(), vrB.getHomePosition(), lstB);

    double score = routeEvaluator.scoreRoute(vrANew);
    score += routeEvaluator.scoreRoute(vrBNew);

    return Optional.of(new TSPMoveResult(score, vrANew, vrBNew));
  }
}
