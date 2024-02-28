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
  public Optional<TSPMoveResult> performMove(final List<VehicleRoute> vehicles) {
    super.performMove(vehicles);

    final List<VehicleRoute> vrl = new ArrayList<>(vehicles.size());
    double score = 0.0;

    if (vehicles.size() == 1) {
      final VehicleRoute vrA = vehicles.get(0);

      if (vrA.getRoute().size() < 4) {
        return Optional.empty();
      }

      final VehicleRoute vrANew = insertSubrouteOneVehicle(vrA);
      vrl.add(vrANew);

      routeEvaluator.scoreRouteFull(vrANew);
      score += vrANew.getScore();
    } else {
      final VehicleRoute vrA = vehicles.get(0);
      final VehicleRoute vrB = vehicles.get(1);

      final boolean cutRouteAPossible = vrA.getRoute().size() >= 4;
      final boolean cutRouteBPossible = vrB.getRoute().size() >= 4;

      if (!cutRouteAPossible && !cutRouteBPossible) {
        return Optional.empty();
      }

      vrl.addAll(insertSubrouteTwoVehicles(vrA, vrB, cutRouteAPossible, cutRouteBPossible));

      routeEvaluator.scoreRouteFull(vrl.get(0));
      routeEvaluator.scoreRouteFull(vrl.get(1));

      score += vrl.get(0).getScore();
      score += vrl.get(1).getScore();
    }

    return Optional.of(new TSPMoveResult(score, vrl));
  }

  private VehicleRoute insertSubrouteOneVehicle(VehicleRoute vrA) {
    final int startIdx = rng.nextInt(vrA.getRoute().size() - 1);
    final int endIdx = startIdx + rng.nextInt(vrA.getRoute().size() - startIdx - 1) + 1;
    final List<Order> lstA = new ArrayList<>(vrA.getRoute());
    final List<Double> distanceScoreA = new ArrayList<>(vrA.getDistanceScore());

    extractSubrouteAndAdd(startIdx, endIdx, lstA, lstA, distanceScoreA, distanceScoreA);

    final VehicleRoute vrANew =
        new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA, distanceScoreA, 0.0,
            vrA.isRoundtrip());
    return vrANew;
  }

  private List<VehicleRoute> insertSubrouteTwoVehicles(VehicleRoute vrA, VehicleRoute vrB,
                                                       final boolean cutRouteAPossible,
                                                       final boolean cutRouteBPossible) {
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
        new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA, distanceScoreA, 0.0,
            vrA.isRoundtrip());
    final VehicleRoute vrBNew =
        new VehicleRoute(vrB.getId(), vrB.getHomePosition(), lstB, distanceScoreB, 0.0,
            vrB.isRoundtrip());
    return List.of(vrANew, vrBNew);
  }

  private void extractSubrouteAndAdd(int min, int max, List<Order> srcRoute,
                                     List<Order> dstRoute, List<Double> srcScore,
                                     List<Double> dstScore) {
    final List<Order> subRoute = new ArrayList<>(max - min + 1);
    final List<Double> subScore = new ArrayList<>(max - min + 1);

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
