package de.westranger.optimization.common.algorithm.tools.util;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealing;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealingParameter;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.common.ProblemFormulation;
import de.westranger.optimization.common.algorithm.tsp.common.State;
import de.westranger.optimization.common.algorithm.tsp.sa.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.TSPNeighbourSelector;
import de.westranger.optimization.common.algorithm.tsp.sa.VehicleRoute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.Callable;

public final class TSPCallable implements Callable<Map<String, Double>> {

  private final ProblemFormulation pf;

  private final Map<String, Double> param;
  private final int numTries;

  public TSPCallable(final ProblemFormulation pf, final Map<String, Double> param, int numTries) {
    this.pf = pf;
    this.param = param;
    this.numTries = numTries;
  }

  @Override
  public Map<String, Double> call() throws Exception {

    double iter = 0.0;
    double sum = 0.0;
    for (long i = 0; i < this.numTries; i++) {
      final long seed = 47110815 + i * 10000;
      final SimulatedAnnealingParameter sap =
          new SimulatedAnnealingParameter(this.param.get("tMax"), this.param.get("tMin"),
              this.param.get("gamma"), this.param.get("omegaMax"),
              this.param.get("maxImprovementPerTemperature"),
              this.param.get("initialAcceptanceRatio"));
      final Random rng = new Random(seed);
      final SimulatedAnnealing sa = initializeSA(sap, rng);
      final SearchSpaceState optimizedResult = sa.optimize();

      iter += sa.getTotalIterationCounter();
      sum += optimizedResult.getScore().getAbsoluteScore();
    }

    sum /= this.numTries;
    iter /= this.numTries;

    Map<String, Double> result = new TreeMap<>(this.param);
    result.put("score", sum);
    result.put("iter", iter);
    result.put("avg_score", sum);
    return result;
  }

  private SimulatedAnnealing initializeSA(final SimulatedAnnealingParameter sap,
                                          final Random rng) {
    List<Order> orders = new LinkedList<>(this.pf.getOrders());

    Collections.shuffle(orders, rng);

    Map<Integer, VehicleRoute> orderMapping = new TreeMap<>();
    for (Map.Entry<Integer, Point2D> entry : this.pf.getVehicleStartPositions()
        .entrySet()) {
      if (entry.getKey() == 1) {
        final VehicleRoute vr = new VehicleRoute(entry.getKey(), entry.getValue(), orders, 0.0);
        orderMapping.put(entry.getKey(), vr);
      } else {
        final VehicleRoute vr =
            new VehicleRoute(entry.getKey(), entry.getValue(), new ArrayList<>(), 0.0);
        orderMapping.put(entry.getKey(), vr);
      }
    }

    RouteEvaluator re = new RouteEvaluator();
    State initialState =
        new State(new ArrayList<>(), orderMapping, re);
    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), rng);

    return new SimulatedAnnealing(initialState, ns, rng, sap);
  }

  public Map<String, Double> getParam() {
    return param;
  }

}
