package de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public final class TSPInsertionMove extends TSPMove {

  public TSPInsertionMove(final Random rng, final RouteEvaluator re) {
    super(rng, re);
  }

  @Override
  public Optional<TSPMoveResult> performMove(final List<VehicleRoute> vehicles) {
    super.performMove(vehicles);

    final List<VehicleRoute> vrl = new ArrayList<>(vehicles.size());
    double score;

    final VehicleRoute vrA = vehicles.get(0);
    if (vehicles.size() == 1) {
      if (vrA.getRoute().size() < 2) {
        return Optional.empty();
      }

      VehicleRoute result = performMoveSingleVehicle(vrA);
      vrl.add(result);
      score = result.getScore();
    } else {
      final VehicleRoute vrB = vehicles.get(1);

      if (vrA.getRoute().isEmpty() && vrB.getRoute().isEmpty()) {
        return Optional.empty();
      }

      List<VehicleRoute> result = performMoveTwoVehicles(vrA, vrB);

      vrl.add(result.get(0));
      vrl.add(result.get(1));

      score = Double.isNaN(result.get(0).getScore()) ? 0.0 : result.get(0).getScore();
      score += Double.isNaN(result.get(1).getScore()) ? 0.0 : result.get(1).getScore();
    }

    return Optional.of(new TSPMoveResult(score, vrl));
  }

  private double removeAndInsertEmptyList(List<Order> lstA, List<Order> lstB,
                                          List<Double> distanceScoreA,
                                          List<Double> distanceScoreB, double score,
                                          List<Integer> idxToUpdateA,
                                          List<Integer> idxToUpdateB,
                                          boolean isRoundtrip) {
    final int removeIdx = rng.nextInt(lstA.size());
    final Order order = lstA.remove(removeIdx);
    final double oldRemoveScore = distanceScoreA.remove(removeIdx);

    if (distanceScoreA.size() == removeIdx) {
      score -= oldRemoveScore;
    } else {
      distanceScoreA.set(removeIdx, distanceScoreA.get(removeIdx) + oldRemoveScore);
      idxToUpdateA.add(removeIdx);
    }

    final int insertIdx = this.rng.nextInt(lstB.size() + 1);
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

  private double removeAndInsertList(List<Order> lstA, List<Order> lstB,
                                     List<Double> distanceScoreA,
                                     List<Double> distanceScoreB, double score,
                                     List<Integer> idxToUpdateA,
                                     List<Integer> idxToUpdateB,
                                     boolean isRoundtrip) {
    final int removeIdx = rng.nextInt(lstB.size());
    lstA.add(lstB.remove(removeIdx));
    final double oldRemoveScore = distanceScoreB.remove(removeIdx);

    if (distanceScoreB.size() == removeIdx) {
      score -= oldRemoveScore;
    } else {
      distanceScoreB.set(removeIdx, distanceScoreB.get(removeIdx) + oldRemoveScore);
    }
    distanceScoreA.add(0.0);

    if (!lstB.isEmpty()) {
      idxToUpdateB.add(removeIdx);
    }
    idxToUpdateA.add(0);
    if (isRoundtrip) {
      idxToUpdateA.add(1);
    }
    return score;
  }

  private void removeAndInsertSingleVehicle(List<Order> lstA,
                                            List<Double> distanceScoreA,
                                            List<Integer> idxToUpdate,
                                            boolean isRoundtrip) {
    final int removeIdx = this.rng.nextInt(lstA.size());
    final Order order = lstA.remove(removeIdx);
    final double oldRemoveScore = distanceScoreA.remove(removeIdx);
    int insertIdx = computeInsertIdxSingleVehicle(lstA, removeIdx);

    if (insertIdx == lstA.size()) {
      lstA.add(order);
      distanceScoreA.add(oldRemoveScore);

      idxToUpdate.add(insertIdx);
      idxToUpdate.add(removeIdx);

      if (isRoundtrip) {
        idxToUpdate.add(insertIdx + 1);
      }
    } else {
      lstA.add(insertIdx, order);
      distanceScoreA.add(insertIdx, oldRemoveScore);

      idxToUpdate.add(insertIdx);
      if (insertIdx < removeIdx) {
        if (distanceScoreA.size() == 2) {
          idxToUpdate.add(removeIdx);
        } else {
          idxToUpdate.add(insertIdx + 1);
          idxToUpdate.add(removeIdx + 1);
        }
      } else {
        idxToUpdate.add(insertIdx + 1);
        idxToUpdate.add(removeIdx);
      }
    }
  }

  private int computeInsertIdxSingleVehicle(List<Order> lstA, int removeIdx) {
    if (removeIdx != 0 && removeIdx != lstA.size()) {
      final boolean firstHalf = rng.nextBoolean();
      if (firstHalf) {
        return this.rng.nextInt(removeIdx);
      } else {
        return removeIdx + this.rng.nextInt(lstA.size() - removeIdx) + 1;
      }
    } else if (removeIdx == lstA.size()) {
      return this.rng.nextInt(removeIdx);
    } else {
      return removeIdx + this.rng.nextInt(lstA.size() - removeIdx) + 1;
    }
  }

  private VehicleRoute performMoveSingleVehicle(VehicleRoute vrA) {
    final List<Order> lstA = new ArrayList<>(vrA.getRoute());
    final List<Double> distanceScoreA = new ArrayList<>(vrA.getDistanceScore());
    List<Integer> idxToUpdate = new ArrayList<>(3);

    removeAndInsertSingleVehicle(lstA, distanceScoreA, idxToUpdate, vrA.isRoundtrip());

    final VehicleRoute vrANew =
        new VehicleRoute(vrA.getId(), vrA.getHomePosition(), lstA, distanceScoreA, vrA.getScore(),
            vrA.isRoundtrip());

    routeEvaluator.scoreRoutePartial(vrANew, idxToUpdate);
    return vrANew;
  }

  private List<VehicleRoute> performMoveTwoVehicles(VehicleRoute vrA, VehicleRoute vrB) {
    final List<Order> lstA = new ArrayList<>(vrA.getRoute());
    final List<Order> lstB = new ArrayList<>(vrB.getRoute());
    final List<Double> distanceScoreA = new ArrayList<>(vrA.getDistanceScore());
    final List<Double> distanceScoreB = new ArrayList<>(vrB.getDistanceScore());
    double scoreA = Double.isNaN(vrA.getScore()) ? 0.0 : vrA.getScore();
    double scoreB = Double.isNaN(vrB.getScore()) ? 0.0 : vrB.getScore();

    List<Integer> idxToUpdateA = new ArrayList<>(2);
    List<Integer> idxToUpdateB = new ArrayList<>(2);

    if (lstA.isEmpty() && !lstB.isEmpty()) {
      scoreB = removeAndInsertList(lstA, lstB, distanceScoreA, distanceScoreB,
          scoreB, idxToUpdateA, idxToUpdateB,
          vrA.isRoundtrip());
    } else if (!lstA.isEmpty() && lstB.isEmpty()) {
      scoreA = removeAndInsertList(lstB, lstA, distanceScoreB, distanceScoreA,
          scoreA, idxToUpdateB, idxToUpdateA,
          vrB.isRoundtrip());
    } else {
      boolean removeFromA = rng.nextBoolean();

      if (removeFromA) {
        scoreA =
            removeAndInsertEmptyList(lstA, lstB, distanceScoreA, distanceScoreB,
                scoreA, idxToUpdateA, idxToUpdateB,
                vrA.isRoundtrip());
      } else {
        scoreB =
            removeAndInsertEmptyList(lstB, lstA, distanceScoreB, distanceScoreA,
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

}
