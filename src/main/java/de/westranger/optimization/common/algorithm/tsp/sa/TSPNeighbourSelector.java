package de.westranger.optimization.common.algorithm.tsp.sa;

import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.NeighbourSelector;
import de.westranger.optimization.common.algorithm.tsp.common.State;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPInsertSubrouteMove;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPInsertionMove;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPMove;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPMoveResult;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPSwapMove;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import de.westranger.optimization.common.util.SampleStatistics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;

public final class TSPNeighbourSelector implements NeighbourSelector {

  private final TSPMove moveSwap;
  private final TSPMove moveInsert;
  private final TSPMove moveInsertSubroute;
  private final TSPMove moveInsertSubrouteReverse;
  private final TSPMove moveTwoOpt;

  private SampleStatistics<Integer> samplingStatsVehicleID;
  private SampleStatistics<Integer> samplingStatsNumVehicle;
  private SampleStatistics<String> samplingStatsFirstVehicle;

  private final double maxTemperature;
  private final double minTemperature;
  private final Random rng;

  public TSPNeighbourSelector(final double maxTemperature, final double minTemperature,
                              final long seed, boolean collectStatistics) {
    this.maxTemperature = maxTemperature;
    this.minTemperature = minTemperature;
    this.rng = new Random(seed);
    final RouteEvaluator re = new RouteEvaluator();
    this.moveSwap = new TSPSwapMove(new Random(seed), re);
    this.moveInsert = new TSPInsertionMove(new Random(seed), re, collectStatistics);
    this.moveInsertSubroute = new TSPInsertSubrouteMove(new Random(seed), re, false, false);
    this.moveInsertSubrouteReverse = new TSPInsertSubrouteMove(new Random(seed), re, true, false);
    this.moveTwoOpt = new TSPInsertSubrouteMove(new Random(seed), re, true, true);
    this.samplingStatsVehicleID = new SampleStatistics<>(collectStatistics);
    this.samplingStatsNumVehicle = new SampleStatistics<>(collectStatistics);
    this.samplingStatsFirstVehicle = new SampleStatistics<>(collectStatistics);
  }


  @Override
  public SearchSpaceState select(SearchSpaceState intermediateSolution,
                                 double currentTemperature) {

    if (!(intermediateSolution instanceof State state)) {
      throw new IllegalArgumentException("passed SearchSpaceState is not of type State");
    }

    if (!state.getOrderList().isEmpty()) {
      throw new IllegalStateException("there are still order which are not assigned to vehicles");
    }

    /*
     * TODO mann kann den swap ind insert teil auch so machen das bei 2 fahrzeugen nicht immer
     * zwischen den Fahrzeugen geswapped oder remove uns geinserted wird, sonder auch das man in
     * dem jeweiligen vehicle swapped oder remove und inserted, das erhöht die variabilität beim
     * lösungsfinden (update: ich weiss nicht ob es mehr bring 2 fahrzeige jeweils einzeln zu
     * modifizieren oder einfach nur zu warten bis der zufallsgenerator ein fahrzeug auswählt. Es
     * kann schon sein das wenn 2 fahrzeuge selektiert werden und man sich entscheidet jedes einzel
     * zu modifizieren, das man schneller zu einer lösung kommt
     */

    final Map<Integer, VehicleRoute> vehicles = new TreeMap<>(state.getVehicles());
    final List<VehicleRoute> vrl = sampleVehicles(vehicles);

    final Optional<TSPMoveResult> moveInsertResult = this.moveInsert.performMove(vrl);
    final Optional<TSPMoveResult> moveSwapResult = this.moveSwap.performMove(vrl);
    final Optional<TSPMoveResult> moveSRResult = this.moveInsertSubroute.performMove(vrl);
    final Optional<TSPMoveResult> moveSRRResult = this.moveInsertSubrouteReverse.performMove(vrl);
    final Optional<TSPMoveResult> moveTwoOptResult = this.moveTwoOpt.performMove(vrl);

    Optional<TSPMoveResult> finalResult = Optional.empty();
    double min = Double.POSITIVE_INFINITY;

    if (moveInsertResult.isPresent() && moveInsertResult.get().score() < min) {
      min = moveInsertResult.get().score();
      finalResult = moveInsertResult;
    }

    if (moveSwapResult.isPresent() && moveSwapResult.get().score() < min) {
      min = moveSwapResult.get().score();
      finalResult = moveSwapResult;
    }

    if (moveSRResult.isPresent() && moveSRResult.get().score() < min) {
      min = moveSRResult.get().score();
      finalResult = moveSRResult;
    }

    if (moveSRRResult.isPresent() && moveSRRResult.get().score() < min) {
      min = moveSRRResult.get().score();
      finalResult = moveSRRResult;
    }

    if (moveTwoOptResult.isPresent() && moveTwoOptResult.get().score() < min) {
      min = moveTwoOptResult.get().score();
      finalResult = moveTwoOptResult;
    }

    double score = intermediateSolution.getScore().getValue(0);
    for (VehicleRoute vr : vrl) {
      if (!vr.getRoute().isEmpty()) {
        score -= vr.getScore();
      }
    }

    if (vrl.get(0).getId() == 2 && vrl.size() == 2 && vrl.get(1).getId() == 3 //&&
      /*vrl.get(0).getRoute().size() == 1*/) {
      //System.out.println("got it");
    }

    if (finalResult.isPresent()) {
      for (VehicleRoute vr : finalResult.get().vehicles()) {
        vehicles.put(vr.getId(), vr);
        score += vr.getScore();
      }
    } else {
      for (VehicleRoute vr : vrl) {
        vehicles.put(vr.getId(), vr);
        score += vr.getScore();
      }
    }

    return new State(new ArrayList<>(), vehicles, state.getRouteEval(),
        score);
  }

  private List<VehicleRoute> sampleVehicles(Map<Integer, VehicleRoute> vehicles) {
    List<Integer> keys = new ArrayList<>(vehicles.size());
    List<VehicleRoute> vrl = new ArrayList<>(1);

    for (int key : vehicles.keySet()) {
      keys.add(key);
    }

    final int maxSamplingIter = 10000;
    for (int i = 0; i < maxSamplingIter; i++) {
      final int vehicleIdA = this.rng.nextInt(keys.size());
      final int vehicleIdB = this.rng.nextInt(keys.size());
      final boolean single = this.rng.nextBoolean();

      this.samplingStatsNumVehicle.add(single ? 1 : 2);
      VehicleRoute vrA = vehicles.get(keys.get(vehicleIdA));
      VehicleRoute vrB = vehicles.get(keys.get(vehicleIdB));

      this.samplingStatsVehicleID.add(vrA.getId());
      this.samplingStatsVehicleID.add(vrB.getId());

      if (!vrA.getRoute().isEmpty() && vrB.getRoute().isEmpty()) {
        vrl.add(vrA);
        if (!single) {
          vrl.add(vrB);
        }
        break;
      }
      if (vrA.getRoute().isEmpty() && !vrB.getRoute().isEmpty()) {
        vrl.add(vrB);
        if (!single) {
          vrl.add(vrA);
        }
        break;
      } else if (!vrA.getRoute().isEmpty() && !vrB.getRoute().isEmpty()) {
        if (vehicleIdA == vehicleIdB) {
          vrl.add(vrA);
        } else if (single) {
          if (this.rng.nextBoolean()) {
            samplingStatsFirstVehicle.add("A");
            vrl.add(vrA);
          } else {
            vrl.add(vrB);
            samplingStatsFirstVehicle.add("B");
          }
        } else {
          if (this.rng.nextBoolean()) {
            samplingStatsFirstVehicle.add("A");
            vrl.add(vrA);
            vrl.add(vrB);
          } else {
            samplingStatsFirstVehicle.add("B");
            vrl.add(vrB);
            vrl.add(vrA);
          }
        }
        break;
      }
    }

    if (vrl.isEmpty()) {
      throw new IllegalStateException(
          "was not able to sample to non empty vehicles after " + maxSamplingIter + " iterations");
    }
    return vrl;
  }

  public SampleStatistics getSamplingStatsVehicleID() {
    return this.samplingStatsVehicleID;
  }

  public SampleStatistics getSamplingStatsNumVehicle() {
    return this.samplingStatsNumVehicle;
  }

  public SampleStatistics getSamplingStatsFirstVehicle() {
    return this.samplingStatsFirstVehicle;
  }

}
