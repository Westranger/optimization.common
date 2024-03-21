package de.westranger.optimization.common.algorithm.tools.util;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealing;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealingParameter;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.common.ProblemFormulation;
import de.westranger.optimization.common.algorithm.tsp.common.State;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.TSPNeighbourSelector;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
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
  private final boolean idRoundtrip;

  private final String paramId;

  public TSPCallable(final ProblemFormulation pf, final Map<String, Double> param, int numTries,
                     boolean idRoundtrip, String paramId) {
    this.pf = pf;
    this.param = param;
    this.numTries = numTries;
    this.idRoundtrip = idRoundtrip;
    this.paramId = paramId;
  }

  public TSPCallable(final ProblemFormulation pf, final Map<String, Double> param, int numTries,
                     boolean idRoundtrip) {
    this(pf, param, numTries, idRoundtrip, "");
  }

  @Override
  public Map<String, Double> call() throws Exception {

    double iter = 0.0;
    double sum = 0.0;
    long time = 0;

    for (long i = 0; i < this.numTries; i++) {
      final long seed = 47110815L + i * 10000L;
      final SimulatedAnnealingParameter sap =
          new SimulatedAnnealingParameter(0.0, this.param.get("tMin"),
              this.param.get("gamma"), this.param.get("omegaMax"),
              this.param.get("maxImprovementPerTemperature"),
              this.param.get("initialAcceptanceRatio"), this.param.get("beta"));
      final SimulatedAnnealing sa = initializeSA(sap, seed);
      long start = System.currentTimeMillis();
      final SearchSpaceState optimizedResult = sa.optimize(false);
      long end = System.currentTimeMillis();
      time += end - start;

      iter += sa.getTotalIterationCounter();
      sum += optimizedResult.getScore().getValue(0);
    }

    sum /= this.numTries;
    iter /= this.numTries;
    time /= this.numTries;

    Map<String, Double> result = new TreeMap<>(this.param);
    result.put("score", sum);
    result.put("iter", iter);
    result.put("avg_score", sum);
    result.put("time", (double) time);
    result.put("param_id#" + this.paramId, 0.0);

    return result;
  }

  private SimulatedAnnealing initializeSA(final SimulatedAnnealingParameter sap,
                                          final long seed) {
    List<Order> orders = new LinkedList<>(this.pf.getOrders());
    Random rng = new Random(seed);
    Collections.shuffle(orders, rng);

    List<Integer> keys = new LinkedList<>();

    Map<Integer, VehicleRoute> vehicles = new TreeMap<>();
    Map<Integer, List<Order>> orderMap = new TreeMap<>();

    for (Map.Entry<Integer, Point2D> entry : this.pf.getVehicleStartPositions()
        .entrySet()) {
      orderMap.put(entry.getKey(), new LinkedList<>());
      keys.add(entry.getKey());
    }

    while (!orders.isEmpty()) {
      int key = keys.get(rng.nextInt(keys.size()));
      Order order = orders.remove(rng.nextInt(orders.size()));
      orderMap.get(key).add(order);
    }

    for (Map.Entry<Integer, Point2D> entry : this.pf.getVehicleStartPositions().entrySet()) {
      final VehicleRoute vr =
          new VehicleRoute(entry.getKey(), entry.getValue(), orderMap.get(entry.getKey()),
              this.idRoundtrip);
      vehicles.put(vr.getId(), vr);
    }

    RouteEvaluator re = new RouteEvaluator();
    State initialState =
        new State(new ArrayList<>(), vehicles, re);
    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), seed, false);

    return new SimulatedAnnealing(initialState, ns, seed, sap);
  }
}
