package de.westranger.optimization.common.algorithm.tsp.sa;

import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.NeighbourSelector;
import de.westranger.optimization.common.algorithm.tsp.common.State;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPInsertSubrouteMove;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPInsertionMove;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPMove;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPMoveResult;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPSwapMove;
import java.util.ArrayList;
import java.util.LinkedList;
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

    // now we have two lists
    final int vehicleIdA = this.rng.nextInt(state.getOrderMapping().size()) + 1;
    final int vehicleIdB = this.rng.nextInt(state.getOrderMapping().size()) + 1;

    final Map<Integer, VehicleRoute> newMapping = new TreeMap<>();

    final List<VehicleRoute> vrl = new LinkedList<>();
    if (vehicleIdA == vehicleIdB) {
      final VehicleRoute vrA = state.getOrderMapping().get(vehicleIdA);
      vrl.add(vrA);
    } else {
      final VehicleRoute vrA = state.getOrderMapping().get(vehicleIdA);
      final VehicleRoute vrB = state.getOrderMapping().get(vehicleIdB);
      vrl.add(vrA);
      vrl.add(vrB);
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
      finalResult = moveSRRResult;
    }

    if (finalResult.isEmpty()) {
      throw new IllegalStateException("none of the moves came up with an result");
    }

    for (VehicleRoute vr : finalResult.get().vehicles()) {
      newMapping.put(vr.id(), vr);
    }

    for (Map.Entry<Integer, VehicleRoute> entry : state.getOrderMapping().entrySet()) {
      if (!newMapping.containsKey(entry.getKey())) {
        newMapping.put(entry.getKey(), entry.getValue());
      }
    }

    return new State(new ArrayList<>(), newMapping, state.getRouteEval());
  }


}
