package de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public final class TSPSwapMove extends TSPMove {

  public TSPSwapMove(final Random rng, final RouteEvaluator routeEvaluator) {
    super(rng, routeEvaluator);
  }

  @Override
  public Optional<TSPMoveResult> performMove(final List<VehicleRoute> vehicles) {
    super.performMove(vehicles);

    final List<VehicleRoute> vrl = new ArrayList<>(vehicles.size());
    double score = 0.0;

    final VehicleRoute vrA = vehicles.get(0);
    if (vehicles.size() == 1) {
      if (vrA.getRoute().size() < 2) {
        return Optional.empty();
      }

      VehicleRoute result = swapSingleVehicle(vrA);
      vrl.add(result);

      score += result.getScore();
    } else {
      final VehicleRoute vrB = vehicles.get(1);

      if (vrA.getRoute().isEmpty() || vrB.getRoute().isEmpty()) {
        return Optional.empty();
      }

      List<VehicleRoute> result = swapTwoVehicle(vrA, vrB);
      vrl.add(result.get(0));
      vrl.add(result.get(1));

      score = result.get(0).getScore();
      score += result.get(1).getScore();
    }

    return Optional.of(new TSPMoveResult(score, vrl));
  }

  private VehicleRoute swapSingleVehicle(VehicleRoute vrA) {
    final List<Order> lstA = new ArrayList<>(vrA.getRoute());
    final List<Double> distanceScoreA = new ArrayList<>(vrA.getDistanceScore());

    final int removeIdxA = this.rng.nextInt(lstA.size());
    final Order orderA = lstA.remove(removeIdxA);
    int removeIdxB = this.rng.nextInt(lstA.size());
    final Order orderB = lstA.remove(removeIdxB);

    lstA.add(removeIdxB, orderA);
    lstA.add(removeIdxA, orderB);

    if (removeIdxB >= removeIdxA) {
      removeIdxB += 1;
    }

    List<Integer> idxToUpdate = new ArrayList<>(2);
    computeUpdateIndices(lstA, idxToUpdate, removeIdxA, removeIdxB, vrA.isRoundtrip());

    final VehicleRoute vrANew =
        new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA, distanceScoreA, vrA.getScore(),
            vrA.isRoundtrip());

    routeEvaluator.scoreRoutePartial(vrANew, idxToUpdate);
    return vrANew;
  }

  private List<VehicleRoute> swapTwoVehicle(VehicleRoute vrA, VehicleRoute vrB) {
    final List<Order> lstA = new ArrayList<>(vrA.getRoute());
    final List<Order> lstB = new ArrayList<>(vrB.getRoute());
    final List<Double> distanceScoreA = new ArrayList<>(vrA.getDistanceScore());
    final List<Double> distanceScoreB = new ArrayList<>(vrB.getDistanceScore());

    final int removeIdxA = this.rng.nextInt(vrA.getRoute().size());
    final Order orderA = lstA.remove(removeIdxA);

    final int removeIdxB = this.rng.nextInt(vrB.getRoute().size());
    final Order orderB = lstB.remove(removeIdxB);

    lstA.add(removeIdxA, orderB);
    lstB.add(removeIdxB, orderA);

    List<Integer> idxToUpdateA = new ArrayList<>(1);
    List<Integer> idxToUpdateB = new ArrayList<>(1);

    computeUpdateIndex(lstA, idxToUpdateA, removeIdxA, vrA.isRoundtrip());
    computeUpdateIndex(lstB, idxToUpdateB, removeIdxB, vrB.isRoundtrip());

    final VehicleRoute vrANew =
        new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA, distanceScoreA, vrA.getScore(),
            vrA.isRoundtrip());
    final VehicleRoute vrBNew =
        new VehicleRoute(vrB.getId(), vrB.getHomePosition(), lstB, distanceScoreB, vrB.getScore(),
            vrB.isRoundtrip());

    routeEvaluator.scoreRoutePartial(vrANew, idxToUpdateA);
    routeEvaluator.scoreRoutePartial(vrBNew, idxToUpdateB);
    return List.of(vrANew, vrBNew);
  }

  private void computeUpdateIndices(List<Order> orderLst, List<Integer> updateEdgeIdxLst, int idxA,
                                    int idxB,
                                    boolean isRoundtrip) {
    final int idxMin = Math.min(idxA, idxB);
    final int idxMax = Math.max(idxA, idxB);

    updateEdgeIdxLst.add(idxMin);
    if (idxMax - idxMin != 1) {
      updateEdgeIdxLst.add(idxMin + 1);
    }

    computeUpdateIndex(orderLst, updateEdgeIdxLst, idxMax, isRoundtrip);
  }

  private void computeUpdateIndex(List<Order> orderLst, List<Integer> updateEdgeIdxLst, int idx,
                                  boolean isRoundtrip) {
    updateEdgeIdxLst.add(idx);
    if (idx < orderLst.size() - 1 || isRoundtrip) {
      updateEdgeIdxLst.add(idx + 1);
    }
  }

}
