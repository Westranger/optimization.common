package de.westranger.optimization.common.algorithm.tsp.sa;

import de.westranger.optimization.common.algorithm.tsp.common.Order;
import java.util.List;

public final class RouteEvaluator {

  private final boolean roundtrip;

  public RouteEvaluator() {
    this(false); // TODO auslagern in variable !!!
  }

  public RouteEvaluator(final boolean roundtrip) {
    this.roundtrip = roundtrip;
  }

  public double scoreRoute(final VehicleRoute vr) {

    if (vr.route().isEmpty()) {
      return 0.0;
    }

    final List<Order> route = vr.route();
    double score = vr.homePosition().distance(route.get(0).getTo());

    for (int i = 1; i < route.size(); i++) {
      score += route.get(i).getTo().distance(route.get(i - 1).getTo());
    }

    if (roundtrip) {
      score += vr.homePosition().distance(route.get(route.size() - 1).getTo());
    }

    return score;
  }

  public double estimateSwap(final int idxA, final int idxB){
    return 0.0;
  }

  public double estimateReverse(final int idxA, final int idxB){
    return 0.0;
  }




}
