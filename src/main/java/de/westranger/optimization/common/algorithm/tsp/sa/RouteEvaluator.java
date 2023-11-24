package de.westranger.optimization.common.algorithm.tsp.sa;

import de.westranger.optimization.common.algorithm.tsp.common.Order;
import java.util.List;

public final class RouteEvaluator {

  private final boolean roundtrip;

  public RouteEvaluator() {
    this(true);
  }

  public RouteEvaluator(final boolean roundtrip) {
    this.roundtrip = roundtrip;
  }

  public double scoreRoute(final VehicleRoute vr) {

    if (vr.getRoute().isEmpty()) {
      return 0.0;
    }

    final List<Order> route = vr.getRoute();
    double score = vr.getHomePosition().distance(route.get(0).getTo());

    for (int i = 1; i < route.size(); i++) {
      score += route.get(i).getTo().distance(route.get(i - 1).getTo());
    }

    if (roundtrip) {
      score += vr.getHomePosition().distance(route.get(route.size() - 1).getTo());
    }

    return score;
  }

}
