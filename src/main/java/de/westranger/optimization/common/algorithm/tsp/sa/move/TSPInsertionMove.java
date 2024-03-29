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

public final class TSPInsertionMove extends TSPMove {

  private SampleStatistics<Integer> statsRemoveIdx;
  private SampleStatistics<Integer> statsInsertIdx;
  private SampleStatistics<Boolean> statsSwitchVehicle;

  public TSPInsertionMove(final Random rng, final RouteEvaluator re,
                          final boolean collectStatistics) {
    super(rng, re, collectStatistics);
    this.statsRemoveIdx = new SampleStatistics<>(collectStatistics);
    this.statsInsertIdx = new SampleStatistics<>(collectStatistics);
    this.statsSwitchVehicle = new SampleStatistics<>(collectStatistics);
  }

  @Override
  protected boolean checkOneVehicleOrderListLength(final List<Order> orders) {
    return orders.size() >= 2;
  }

  @Override
  protected boolean checkTwoVehiclesOrdersListLength(final List<Order> ordersA,
                                                     final List<Order> ordersB) {
    return !(ordersA.isEmpty() && ordersB.isEmpty());
  }

  private double removeAndInsertList(final List<Order> lstA, final List<Order> lstB,
                                     final List<Double> distanceScoreA,
                                     final List<Double> distanceScoreB, double score,
                                     final List<Integer> idxToUpdateA,
                                     final List<Integer> idxToUpdateB,
                                     final boolean isRoundtrip) {
    final int removeIdx = rng.nextInt(lstA.size());
    statsRemoveIdx.add(removeIdx);
    final Order order = lstA.remove(removeIdx);
    final double oldRemoveScore = distanceScoreA.remove(removeIdx);

    if (distanceScoreA.size() == removeIdx) {
      score -= oldRemoveScore;
    } else {
      distanceScoreA.set(removeIdx, distanceScoreA.get(removeIdx) + oldRemoveScore);
      idxToUpdateA.add(removeIdx);
    }

    final int insertIdx = this.rng.nextInt(lstB.size() + 1);
    statsInsertIdx.add(insertIdx);
    if (insertIdx == lstB.size()) {
      lstB.add(order);
      distanceScoreB.add(0.0);

      idxToUpdateB.add(lstB.size() - 1);
      if (isRoundtrip) {
        idxToUpdateB.add(lstB.size());
      }
    } else {
      lstB.add(insertIdx, order);
      distanceScoreB.add(insertIdx, 0.0);

      idxToUpdateB.addAll(List.of(insertIdx, insertIdx + 1));
    }
    return score;
  }

  private double removeAndInsertEmptyList(final List<Order> lstA, final List<Order> lstB,
                                          final List<Double> distanceScoreA,
                                          final List<Double> distanceScoreB, double score,
                                          final List<Integer> idxToUpdateA,
                                          final List<Integer> idxToUpdateB,
                                          final boolean isRoundtrip) {
    final int removeIdx = rng.nextInt(lstB.size());
    statsRemoveIdx.add(removeIdx);
    lstA.add(lstB.remove(removeIdx));
    final double oldRemoveScore = distanceScoreB.remove(removeIdx);

    if (distanceScoreB.size() == removeIdx) {
      score -= oldRemoveScore;
    } else {
      distanceScoreB.set(removeIdx, distanceScoreB.get(removeIdx) + oldRemoveScore);
    }
    distanceScoreA.add(0.0);

    if (!lstB.isEmpty() && removeIdx != distanceScoreB.size()) {
      idxToUpdateB.add(removeIdx);
    }
    idxToUpdateA.add(0);
    if (isRoundtrip) {
      idxToUpdateA.add(1);
    }
    return score;
  }

  private void removeAndInsertSingleVehicle(final List<Order> lstA,
                                            final List<Double> distanceScoreA,
                                            final List<Integer> idxToUpdate,
                                            final boolean isRoundtrip) {
    final int removeIdx = this.rng.nextInt(lstA.size());
    final int insertIdx = computeInsertIdxSingleVehicle(lstA, removeIdx);

    statsRemoveIdx.add(removeIdx);
    statsInsertIdx.add(insertIdx);

    final Order order = lstA.remove(removeIdx);
    final double oldRemoveScore = distanceScoreA.remove(removeIdx);

    if (removeIdx != lstA.size() || isRoundtrip) {
      idxToUpdate.add(removeIdx);
    }

    lstA.add(insertIdx, order);
    distanceScoreA.add(insertIdx, oldRemoveScore);

    if (insertIdx < removeIdx) {
      for (int i = 0; i < idxToUpdate.size(); i++) {
        idxToUpdate.set(i, idxToUpdate.get(i) + 1);
      }

      idxToUpdate.add(insertIdx);
      idxToUpdate.add(insertIdx + 1);
    } else {
      if (insertIdx == lstA.size() - 1) {
        idxToUpdate.add(insertIdx);

        if (isRoundtrip) {
          idxToUpdate.add(insertIdx + 1);
        }
      } else {
        idxToUpdate.add(insertIdx);
        idxToUpdate.add(insertIdx + 1);
      }
    }
  }


  private int computeInsertIdxSingleVehicle(final List<Order> lstA, final int removeIdx) {
    int insertIdx = rng.nextInt(lstA.size() - 1);
    if (removeIdx <= insertIdx) {
      insertIdx += 1;
    }
    return insertIdx;
  }

  @Override
  protected VehicleRoute performMoveSingleVehicle(final VehicleRoute vrA) {
    final List<Order> lstA = new ArrayList<>(vrA.getRoute());
    final List<Double> distanceScoreA = new ArrayList<>(vrA.getDistanceScore());
    final List<Integer> idxToUpdate = new ArrayList<>(3);

    removeAndInsertSingleVehicle(lstA, distanceScoreA, idxToUpdate, vrA.isRoundtrip());

    final VehicleRoute vrANew =
        new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA, distanceScoreA, vrA.getScore(),
            vrA.isRoundtrip());

    routeEvaluator.scoreRoutePartial(vrANew, idxToUpdate);
    return vrANew;
  }

  @Override
  protected List<VehicleRoute> performMoveTwoVehicles(VehicleRoute vrA, VehicleRoute vrB) {
    final List<Order> lstA = new ArrayList<>(vrA.getRoute());
    final List<Order> lstB = new ArrayList<>(vrB.getRoute());
    final List<Double> distanceScoreA = new ArrayList<>(vrA.getDistanceScore());
    final List<Double> distanceScoreB = new ArrayList<>(vrB.getDistanceScore());
    double scoreA = Double.isNaN(vrA.getScore()) ? 0.0 : vrA.getScore();
    double scoreB = Double.isNaN(vrB.getScore()) ? 0.0 : vrB.getScore();

    final List<Integer> idxToUpdateA = new ArrayList<>(2);
    final List<Integer> idxToUpdateB = new ArrayList<>(2);

    if (lstA.isEmpty() && !lstB.isEmpty()) {
      scoreB = removeAndInsertEmptyList(lstA, lstB, distanceScoreA, distanceScoreB,
          scoreB, idxToUpdateA, idxToUpdateB,
          vrA.isRoundtrip());
    } else if (!lstA.isEmpty() && lstB.isEmpty()) {
      scoreA = removeAndInsertEmptyList(lstB, lstA, distanceScoreB, distanceScoreA,
          scoreA, idxToUpdateB, idxToUpdateA,
          vrB.isRoundtrip());
    } else {
      boolean removeFromA = rng.nextBoolean();
      statsSwitchVehicle.add(removeFromA);

      if (removeFromA) {
        scoreA =
            removeAndInsertList(lstA, lstB, distanceScoreA, distanceScoreB,
                scoreA, idxToUpdateA, idxToUpdateB,
                vrA.isRoundtrip());
      } else {
        scoreB =
            removeAndInsertList(lstB, lstA, distanceScoreB, distanceScoreA,
                scoreB, idxToUpdateB, idxToUpdateA,
                vrB.isRoundtrip());
      }
    }

    if (lstA.isEmpty()) {
      scoreA = Double.NaN;
    }

    if (lstB.isEmpty()) {
      scoreB = Double.NaN;
    }

    final VehicleRoute vrANew =
        new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA, distanceScoreA, scoreA,
            vrA.isRoundtrip());
    final VehicleRoute vrBNew =
        new VehicleRoute(vrB.getId(), vrB.getHomePosition(), lstB, distanceScoreB, scoreB,
            vrB.isRoundtrip());

    routeEvaluator.scoreRoutePartial(vrANew, idxToUpdateA);
    routeEvaluator.scoreRoutePartial(vrBNew, idxToUpdateB);

    return List.of(vrANew, vrBNew);
  }

  @Override
  public Optional<Map<String, SampleStatistics>>getSamplingStatistics()

  {
    if (this.collectStatistics) {
      return Optional.of(
          Map.of("move_insert_iidx", this.statsInsertIdx, "move_insert_ridx", this.statsRemoveIdx,
              "move_insert_switch_vehicle", this.statsSwitchVehicle));
    }
    return Optional.empty();
  }

}
