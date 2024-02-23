package de.westranger.optimization.common.algorithm.tsp.sa;

import de.westranger.optimization.common.algorithm.tsp.common.Order;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class RouteEvaluator {

  private final boolean roundtrip;

  public RouteEvaluator() {
    this(false); // TODO auslagern in variable !!!
  }

  public RouteEvaluator(final boolean roundtrip) {
    this.roundtrip = roundtrip;
  }

  public double scoreRouteFull(final VehicleRoute vr) {
    final int len = vr.isRoundtrip() ? vr.route().size() + 1 : vr.route().size();
    final List<Integer> idxList = IntStream.rangeClosed(0, len)
        .boxed()
        .collect(Collectors.toList());
    return this.scoreRoutePartial(vr, idxList);
  }

  /**
   * @param vr
   * @param idx indices in the distanceScore List from these indices the position of the orders will be computed.
   * @return
   */
  public double scoreRoutePartial(final VehicleRoute vr, List<Integer> idx) {

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

  public double estimateSwap(final int idxA, final int idxB) {
    return 0.0;
  }

  public double estimateReverse(final int idxA, final int idxB) {
    return 0.0;
  }


}
