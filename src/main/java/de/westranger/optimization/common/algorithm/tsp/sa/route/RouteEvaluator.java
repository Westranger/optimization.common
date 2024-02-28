package de.westranger.optimization.common.algorithm.tsp.sa.route;

import de.westranger.optimization.common.algorithm.tsp.common.Order;
import java.util.List;

public final class RouteEvaluator {

  public void scoreRouteFull(final VehicleRoute vr) {
    if (vr.getRoute().isEmpty()) {
      return;
    }

    final List<Order> route = vr.getRoute();
    double dist = vr.getHomePosition().distance(route.get(0).getTo());
    vr.updateDistanceScoreAt(0, dist);
    double score = dist;
    for (int i = 1; i < route.size(); i++) {
      dist = route.get(i).getTo().distance(route.get(i - 1).getTo());
      vr.updateDistanceScoreAt(i, dist);
      score += dist;
    }

    if (vr.isRoundtrip()) {
      dist = vr.getHomePosition().distance(route.get(route.size() - 1).getTo());
      vr.updateDistanceScoreAt(route.size(), dist);
      score += dist;
    }
    vr.setScore(score);
  }

  /**
   * This method takes list of edge indices for which the edge score should be updated
   *
   * @param vr
   * @param idx edge indices in the distanceScore List from these indices the score between two orders.
   * @return
   */
  public void scoreRoutePartial(final VehicleRoute vr, List<Integer> idx) {
    if (vr.getRoute().isEmpty()) {
      return;
    }

    if ((vr.isRoundtrip() && idx.size() > vr.getTripLocationCount() + 1) ||
        (!vr.isRoundtrip() && idx.size() > vr.getTripLocationCount())) {
      throw new IllegalArgumentException("there are more indices to update than edges in the trip");
    }

    double score = vr.getScore();
    for (int i : idx) {
      score -= vr.getDistanceScoreAt(i);
      if (i == 0) {
        final double dist = vr.getHomePosition().distance(vr.getLocationAt(i).getTo());
        vr.updateDistanceScoreAt(0, dist);
        score += dist;
      } else if (i == vr.getTripLocationCount()) {
        final double dist = vr.getHomePosition().distance(vr.getLocationAt(i - 1).getTo());
        vr.updateDistanceScoreAt(i, dist);
        score += dist;
      } else {
        final double dist = vr.getLocationAt(i - 1).getTo().distance(vr.getLocationAt(i).getTo());
        vr.updateDistanceScoreAt(i, dist);
        score += dist;
      }
    }
    vr.setScore(score);
  }
}
