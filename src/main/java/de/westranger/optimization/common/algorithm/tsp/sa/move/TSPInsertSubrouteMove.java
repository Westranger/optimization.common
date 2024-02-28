package de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public final class TSPInsertSubrouteMove extends TSPMove {

  private final boolean reverseSubroute;

  public TSPInsertSubrouteMove(final Random rng, final RouteEvaluator routeEvaluator,
                               final boolean reverseSubroute) {
    super(rng, routeEvaluator);
    this.reverseSubroute = reverseSubroute;
  }

  @Override
  protected boolean checkOneVehicleOrderListLength(List<Order> orders) {
    return orders.size() >= 4;
  }

  @Override
  protected boolean checkTwoVehiclesOrdersListLength(List<Order> ordersA, List<Order> ordersB) {
    final boolean cutRouteAPossible = ordersA.size() >= 4;
    final boolean cutRouteBPossible = ordersB.size() >= 4;
    return cutRouteAPossible || cutRouteBPossible;
  }

  @Override
  protected VehicleRoute performMoveSingleVehicle(VehicleRoute vr) {
    final int startIdx = rng.nextInt(vr.getRoute().size() - 1);
    final int endIdx = startIdx + rng.nextInt(vr.getRoute().size() - startIdx - 1) + 1;
    final List<Order> lstA = new ArrayList<>(vr.getRoute());
    final List<Double> distanceScoreA = new ArrayList<>(vr.getDistanceScore());

    extractSubrouteAndAdd(startIdx, endIdx, lstA, lstA, distanceScoreA, distanceScoreA);

    final VehicleRoute vrANew =
        new VehicleRoute(vr.getId(), vr.getHomePosition(), lstA, distanceScoreA, vr.getScore(),
            vr.isRoundtrip());

    routeEvaluator.scoreRouteFull(vrANew);

    return vrANew;
  }

  @Override
  protected List<VehicleRoute> performMoveTwoVehicles(VehicleRoute vrA,
                                                      VehicleRoute vrB) {
    final boolean cutRouteAPossible = vrA.getRoute().size() >= 4;
    final boolean cutRouteBPossible = vrB.getRoute().size() >= 4;

    boolean useA = false;
    if (cutRouteAPossible && cutRouteBPossible) {
      useA = rng.nextBoolean();
    } else if (cutRouteAPossible) {
      useA = true;
    }

    final List<Order> lstA = new ArrayList<>(vrA.getRoute());
    final List<Order> lstB = new ArrayList<>(vrB.getRoute());
    final List<Double> distanceScoreA = new ArrayList<>(vrA.getDistanceScore());
    final List<Double> distanceScoreB = new ArrayList<>(vrB.getDistanceScore());

    List<Order> srcRoute;
    List<Order> dstRoute;
    List<Double> srcScore;
    List<Double> dstScore;

    if (useA) {
      srcRoute = lstA;
      dstRoute = lstB;
      srcScore = distanceScoreA;
      dstScore = distanceScoreB;
    } else {
      srcRoute = lstB;
      dstRoute = lstA;
      srcScore = distanceScoreB;
      dstScore = distanceScoreA;
    }

    final int startIdx = rng.nextInt(srcRoute.size() - 1);
    final int endIdx = startIdx + rng.nextInt(srcRoute.size() - startIdx - 1) + 1;

    extractSubrouteAndAdd(startIdx, endIdx, srcRoute, dstRoute, srcScore, dstScore);

    final VehicleRoute vrANew =
        new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA, distanceScoreA, vrA.getScore(),
            vrA.isRoundtrip());
    final VehicleRoute vrBNew =
        new VehicleRoute(vrB.getId(), vrB.getHomePosition(), lstB, distanceScoreB, vrB.getScore(),
            vrB.isRoundtrip());

    routeEvaluator.scoreRouteFull(vrANew);
    routeEvaluator.scoreRouteFull(vrANew);

    return List.of(vrANew, vrBNew);
  }

  private void extractSubrouteAndAdd(int min, int max, List<Order> srcRoute,
                                     List<Order> dstRoute, List<Double> srcScore,
                                     List<Double> dstScore) {
    final int initialCapacity = max - min + 1;
    final List<Order> subRoute = new ArrayList<>(initialCapacity);
    final List<Double> subScore = new ArrayList<>(initialCapacity);

    if (reverseSubroute) {
      for (int i = max; i >= min; i--) {
        subRoute.add(srcRoute.remove(i));
        subScore.add(srcScore.remove(i));
      }
    } else {
      for (int i = min; i <= max; i++) {
        subRoute.add(srcRoute.remove(min));
        subScore.add(srcScore.remove(min));
      }
    }

    final int insertIdx = this.rng.nextInt(dstRoute.size() + 1);
    for (int i = 0; i < subRoute.size(); i++) {
      dstRoute.add(insertIdx + i, subRoute.get(i));
      dstScore.add(insertIdx + i, subScore.get(i));
    }
  }
}
