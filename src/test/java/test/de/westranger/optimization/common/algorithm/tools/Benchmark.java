package test.de.westranger.optimization.common.algorithm.tools;

import com.google.gson.Gson;
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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.Assertions;
import test.de.westranger.optimization.common.algorithm.tsp.sa.SimulatedAnnealingTest;

public class Benchmark {
  public static void main(String[] args) {
    final InputStreamReader reader = new InputStreamReader(
        SimulatedAnnealingTest.class.getResourceAsStream("/tsp/vrp_problem_3_39_PDE.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

    Random rng = new Random(47110815L);

    List<Order> orders = new LinkedList<>(problem.getOrders());
    Collections.shuffle(orders, rng);

    Map<Integer, VehicleRoute> orderMapping = new TreeMap<>();

    for (Map.Entry<Integer, Point2D> entry : problem.getVehicleStartPositions()
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

    final RouteEvaluator re = new RouteEvaluator();
    State initialState =
        new State(new ArrayList<>(), orderMapping, re);
    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(0, 1.0, 0.96, 250000, 100, 0.9);

    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), rng);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap);

    SearchSpaceState optimizedState = sa.optimize();

    Assertions.assertEquals(9353.678079851821, optimizedState.getScore().getAbsoluteScore(), 1e-10);
    Assertions.assertEquals(5.2500528E7, sa.getTotalIterationCounter());
  }

}
