package de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.VehicleRoute;
import java.util.ArrayList;
import java.util.LinkedList;
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

      score += routeEvaluator.scoreRoute(vrANew);
    } else {
      final VehicleRoute vrA = vehicles.get(0);
      final VehicleRoute vrB = vehicles.get(1);

      final boolean cutRouteAPossible = vrA.getRoute().size() >= 4;
      final boolean cutRouteBPossible = vrB.getRoute().size() >= 4;

      if (!cutRouteAPossible && !cutRouteBPossible) {
        return Optional.empty();
      }

      vrl.addAll(insertSubrouteTwoVehicle(vrA, vrB, cutRouteAPossible, cutRouteBPossible));

      score += routeEvaluator.scoreRoute(vrl.get(0));
      score += routeEvaluator.scoreRoute(vrl.get(1));
    }

    return Optional.of(new TSPMoveResult(score, vrl));
  }

  private VehicleRoute insertSubrouteOneVehicle(VehicleRoute vrA) {
    final int startIdx = rng.nextInt(vrA.getRoute().size() - 1);
    final int endIdx = startIdx + rng.nextInt(vrA.getRoute().size() - startIdx - 1) + 1;
    final List<Order> lstA = new ArrayList<>(vrA.getRoute());
    final List<Order> subroute = new LinkedList<>();

    extractSubrouteAndAdd(startIdx, endIdx, subroute, lstA, lstA);

    final VehicleRoute vrANew = new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA);
    return vrANew;
  }

  private List<VehicleRoute> insertSubrouteTwoVehicle(VehicleRoute vrA, VehicleRoute vrB,
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
    final List<Order> subroute = new LinkedList<>();

    List<Order> srcRoute;
    List<Order> dstRoute;
    if (useA) {
      srcRoute = lstA;
      dstRoute = lstB;
    } else {
      srcRoute = lstB;
      dstRoute = lstA;
    }

    final int startIdx = rng.nextInt(srcRoute.size() - 1);
    final int endIdx = startIdx + rng.nextInt(srcRoute.size() - startIdx - 1) + 1;

    extractSubrouteAndAdd(startIdx, endIdx, subroute, srcRoute, dstRoute);

    final VehicleRoute vrANew = new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA);
    final VehicleRoute vrBNew = new VehicleRoute(vrB.getId(), vrB.getHomePosition(), lstB);
    return List.of(vrANew, vrBNew);
  }

  private void extractSubrouteAndAdd(int min, int max, List<Order> subroute, List<Order> srcRoute,
                                     List<Order> dstRoute) {
    if (reverseSubroute) {
      for (int i = max; i >= min; i--) {
        subroute.add(srcRoute.remove(i));
      }
    } else {
      for (int i = min; i <= max; i++) {
        subroute.add(srcRoute.remove(min));
      }
    }

    final int insertIdx = this.rng.nextInt(dstRoute.size() + 1);
    for (int i = 0; i < subroute.size(); i++) {
      dstRoute.add(insertIdx + i, subroute.get(i));
    }
  }
}
