package test.de.westranger.optimization.common.algorithm.tools;

import com.google.gson.Gson;
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
import org.junit.jupiter.api.Assertions;
import test.de.westranger.optimization.common.algorithm.tsp.sa.SimulatedAnnealingTest;

public class Benchmark {
  public static void main(String[] args) {
    final InputStreamReader reader = new InputStreamReader(
        SimulatedAnnealingTest.class.getResourceAsStream("/tsp/1_vehicle_194_orders.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

    Random rng = new Random(47110815L);

    List<Order> orders = new LinkedList<>(problem.getOrders());
    Collections.shuffle(orders, rng);

    final VehicleRoute vr = new VehicleRoute(1, problem.getVehicleStartPositions().get(1), orders);
    final RouteEvaluator re = new RouteEvaluator();

    State initialState =
        new State(new ArrayList<>(), Map.of(vr.getId(), vr), re);
    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(0, 1.0, 0.96, 250000, 100, 0.9);

    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), rng);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap);

    SearchSpaceState optimizedState = sa.optimize();

    Assertions.assertEquals(9353.678079851821, optimizedState.getScore().getAbsoluteScore(), 1e-10);
    Assertions.assertEquals(5.2500528E7, sa.getTotalIterationCounter());
  }

}
