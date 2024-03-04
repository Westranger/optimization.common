package de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import java.util.ArrayList;
import java.util.List;
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
    final List<Integer> idxToUpdate = new ArrayList<>(2);
    double newScore = vr.getScore();

    newScore = extractSubrouteAndAdd(startIdx, endIdx, lstA, lstA, distanceScoreA, distanceScoreA,
        idxToUpdate, idxToUpdate, newScore, vr.isRoundtrip(), vr.isRoundtrip());

    final VehicleRoute vrANew =
        new VehicleRoute(vr.getId(), vr.getHomePosition(), lstA, distanceScoreA, newScore,
            vr.isRoundtrip());

    routeEvaluator.scoreRoutePartial(vrANew, idxToUpdate);

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
    final List<Integer> idxToUpdateA = new ArrayList<>(1);
    final List<Integer> idxToUpdateB = new ArrayList<>(1);

    final List<Order> srcRoute = useA ? lstA : lstB;
    final List<Order> dstRoute = useA ? lstB : lstA;
    final List<Double> srcScore = useA ? distanceScoreA : distanceScoreB;
    final List<Double> dstScore = useA ? distanceScoreB : distanceScoreA;
    final List<Integer> idxToUpdateSrc = useA ? idxToUpdateA : idxToUpdateB;
    final List<Integer> idxToUpdateDst = useA ? idxToUpdateB : idxToUpdateA;
    final boolean isSrcRoundtrip = useA ? vrA.isRoundtrip() : vrB.isRoundtrip();
    final boolean isDstRoundtrip = useA ? vrB.isRoundtrip() : vrA.isRoundtrip();

    final int startIdx = rng.nextInt(srcRoute.size() - 1);
    final int endIdx = startIdx + rng.nextInt(srcRoute.size() - startIdx - 1) + 1;

    double newSrcScore = useA ? vrA.getScore() : vrB.getScore();

    newSrcScore = extractSubrouteAndAdd(startIdx, endIdx, srcRoute, dstRoute, srcScore, dstScore,
        idxToUpdateSrc,
        idxToUpdateDst, newSrcScore, isSrcRoundtrip, isDstRoundtrip);

    double newScoreA = useA ? newSrcScore : vrA.getScore();
    double newScoreB = useA ? vrB.getScore() : newSrcScore;

    final VehicleRoute vrANew =
        new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA, distanceScoreA, newScoreA,
            vrA.isRoundtrip());
    final VehicleRoute vrBNew =
        new VehicleRoute(vrB.getId(), vrB.getHomePosition(), lstB, distanceScoreB, newScoreB,
            vrB.isRoundtrip());

    routeEvaluator.scoreRoutePartial(vrANew, idxToUpdateA);
    routeEvaluator.scoreRoutePartial(vrBNew, idxToUpdateB);

    return List.of(vrANew, vrBNew);
  }

  private double extractSubrouteAndAdd(int min, int max, List<Order> srcRoute, List<Order> dstRoute,
                                       List<Double> srcScoreLst, List<Double> dstScoreLst,
                                       List<Integer> idxToUpdateSrc, List<Integer> idxToUpdateDst,
                                       double srcScore,
                                       boolean isSrcRoundtrip, boolean isDstRoundtrip) {
    final int initialCapacity = max - min + 1;
    final List<Order> subRoute = new ArrayList<>(initialCapacity);
    final List<Double> subScore = new ArrayList<>(initialCapacity);

    double scoreSum = 0.0;
    double updatedSrcScore = srcScore;

    if (reverseSubroute) {
      for (int i = max; i >= min; i--) {
        subRoute.add(srcRoute.remove(i));

        double tmp = srcScoreLst.remove(i);
        scoreSum += tmp;
        subScore.add(0, tmp);
      }

      if (min < srcRoute.size() - 1 || isSrcRoundtrip && min == srcRoute.size() - 1) {
        idxToUpdateSrc.add(min);
      }

      if (max < srcRoute.size() || isSrcRoundtrip) {
        srcScoreLst.set(min, srcScoreLst.get(min) + scoreSum);
      } else {
        updatedSrcScore -= scoreSum;
      }
    } else {
      for (int i = min; i <= max; i++) {
        subRoute.add(srcRoute.remove(min));

        double tmp = srcScoreLst.remove(min);
        scoreSum += tmp;
        subScore.add(tmp);
      }

      if (min < srcRoute.size() - 1 || isSrcRoundtrip && min == srcRoute.size() - 1) {
        idxToUpdateSrc.add(min);
        srcScoreLst.set(min, srcScoreLst.get(min) + scoreSum);
      }
    }
    subScore.set(0, subScore.get(0) - scoreSum);

    final int insertIdx = this.rng.nextInt(dstRoute.size() + 1);
    for (int i = 0; i < subRoute.size(); i++) {
      dstRoute.add(insertIdx + i, subRoute.get(i));
      dstScoreLst.add(insertIdx + i, subScore.get(i));
    }

    idxToUpdateDst.add(insertIdx);
    if (insertIdx + subRoute.size() < dstRoute.size() || isDstRoundtrip) {
      idxToUpdateDst.add(insertIdx + subScore.size());
    }

    return updatedSrcScore;
  }
}
