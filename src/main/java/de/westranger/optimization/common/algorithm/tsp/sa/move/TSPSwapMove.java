package de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import de.westranger.optimization.common.util.SampleStatistics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public final class TSPSwapMove extends TSPMove {

  private SampleStatistics<Integer> statsRemoveIdxA;
  private SampleStatistics<Integer> statsRemoveIdxB;

  public TSPSwapMove(final Random rng, final RouteEvaluator routeEvaluator,
                     boolean collectStatistics) {
    super(rng, routeEvaluator, collectStatistics);
    this.statsRemoveIdxA = new SampleStatistics<>(collectStatistics);
    this.statsRemoveIdxB = new SampleStatistics<>(collectStatistics);
  }

  @Override
  protected boolean checkOneVehicleOrderListLength(List<Order> orders) {
    return orders.size() >= 2;
  }

  @Override
  protected boolean checkTwoVehiclesOrdersListLength(List<Order> ordersA, List<Order> ordersB) {
    return !ordersA.isEmpty() && !ordersB.isEmpty();
  }

  @Override
  protected VehicleRoute performMoveSingleVehicle(VehicleRoute vr) {
    final List<Order> lstA = new ArrayList<>(vr.getRoute());
    final List<Double> distanceScoreA = new ArrayList<>(vr.getDistanceScore());

    final int removeIdxA = this.rng.nextInt(lstA.size());
    final Order orderA = lstA.remove(removeIdxA);
    int removeIdxB = this.rng.nextInt(lstA.size());
    final Order orderB = lstA.remove(removeIdxB);

    statsRemoveIdxA.add(removeIdxA);
    statsRemoveIdxB.add(removeIdxB);

    lstA.add(removeIdxB, orderA);
    lstA.add(removeIdxA, orderB);

    if (removeIdxB >= removeIdxA) {
      removeIdxB += 1;
    }

    List<Integer> idxToUpdate = new ArrayList<>(2);
    computeUpdateIndices(lstA, idxToUpdate, removeIdxA, removeIdxB, vr.isRoundtrip());

    final VehicleRoute vrANew =
        new VehicleRoute(vr.getId(), vr.getHomePosition(), lstA, distanceScoreA, vr.getScore(),
            vr.isRoundtrip());

    routeEvaluator.scoreRoutePartial(vrANew, idxToUpdate);
    return vrANew;
  }

  @Override
  protected List<VehicleRoute> performMoveTwoVehicles(VehicleRoute vrA,
                                                      VehicleRoute vrB) {
    final List<Order> lstA = new ArrayList<>(vrA.getRoute());
    final List<Order> lstB = new ArrayList<>(vrB.getRoute());
    final List<Double> distanceScoreA = new ArrayList<>(vrA.getDistanceScore());
    final List<Double> distanceScoreB = new ArrayList<>(vrB.getDistanceScore());

    final int removeIdxA = this.rng.nextInt(vrA.getRoute().size());
    final Order orderA = lstA.remove(removeIdxA);

    final int removeIdxB = this.rng.nextInt(vrB.getRoute().size());
    final Order orderB = lstB.remove(removeIdxB);

    statsRemoveIdxA.add(removeIdxA);
    statsRemoveIdxB.add(removeIdxB);

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

  @Override
  public Optional<Map<String, SampleStatistics>> getSamplingStatistics() {
    if (collectStatistics) {
      return Optional.of(
          Map.of("move_swap_ridxa", this.statsRemoveIdxA, "move_swap_ridxb", this.statsRemoveIdxB));
    }
    return Optional.empty();
  }

  private void computeUpdateIndices(List<Order> orderLst, List<Integer> updateEdgeIdxLst,
                                    int idxA, int idxB, boolean isRoundtrip) {
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
