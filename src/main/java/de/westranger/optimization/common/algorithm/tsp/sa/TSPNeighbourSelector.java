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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public final class TSPNeighbourSelector implements NeighbourSelector {

  private final TSPMove moveSwap;
  private final TSPMove moveInsert;
  private final TSPMove moveInsertSubroute;
  private final TSPMove moveInsertSubrouteReverse;

  private final double maxTemperature;
  private final double minTemperature;
  private final Random rng;

  public TSPNeighbourSelector(final double maxTemperature, final double minTemperature,
                              final Random rng) {
    this.maxTemperature = maxTemperature;
    this.minTemperature = minTemperature;
    this.rng = rng;
    final RouteEvaluator re = new RouteEvaluator();
    moveSwap = new TSPSwapMove(rng, re);
    moveInsert = new TSPInsertionMove(rng, re);
    moveInsertSubroute = new TSPInsertSubrouteMove(rng, re, false);
    moveInsertSubrouteReverse = new TSPInsertSubrouteMove(rng, re, true);
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

    final List<VehicleRoute> vrl = new LinkedList<>();
    final List<VehicleRoute> emptyVehicles = new ArrayList(state.getEmptyVehicles());
    final List<VehicleRoute> nonEmptyVehicles = new ArrayList(state.getNonEmptyVehicles());

    if (state.getEmptyVehicles().isEmpty()) {
      final int vehicleIdA = this.rng.nextInt(nonEmptyVehicles.size());
      final int vehicleIdB = this.rng.nextInt(nonEmptyVehicles.size() - 1);

      if (vehicleIdA == vehicleIdB) {
        vrl.add(nonEmptyVehicles.remove(vehicleIdA));
      } else {
        vrl.add(nonEmptyVehicles.remove(vehicleIdA));
        vrl.add(nonEmptyVehicles.remove(vehicleIdB));
      }
    } else {
      final int vehicleIdA = this.rng.nextInt(state.getNonEmptyVehicles().size());
      final int vehicleIdB = this.rng.nextInt(state.getEmptyVehicles().size());

      vrl.add(nonEmptyVehicles.remove(vehicleIdA));
      vrl.add(emptyVehicles.remove(vehicleIdB));
    }

    final Optional<TSPMoveResult> moveInsertResult = this.moveInsert.performMove(vrl);
    final Optional<TSPMoveResult> moveSwapResult = this.moveSwap.performMove(vrl);
    final Optional<TSPMoveResult> moveSRResult = this.moveInsertSubroute.performMove(vrl);
    final Optional<TSPMoveResult> moveSRRResult = this.moveInsertSubrouteReverse.performMove(vrl);

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

    double score = intermediateSolution.getScore().getValue(0);
    for (VehicleRoute vr : vrl) {
      if (!vr.getRoute().isEmpty()) {
        score -= vr.getScore();
      }
    }

    if (finalResult.isPresent()) {
      for (VehicleRoute vr : finalResult.get().vehicles()) {
        if (vr.getRoute().isEmpty()) {
          emptyVehicles.add(vr);
        } else {
          nonEmptyVehicles.add(vr);
          score += vr.getScore();
        }
      }
    } else {
      for (VehicleRoute vr : vrl) {
        if (vr.getRoute().isEmpty()) {
          emptyVehicles.add(vr);
        } else {
          nonEmptyVehicles.add(vr);
          score += vr.getScore();
        }
      }
    }

    return new State(new ArrayList<>(), emptyVehicles, nonEmptyVehicles, state.getRouteEval(), score);
  }


}
