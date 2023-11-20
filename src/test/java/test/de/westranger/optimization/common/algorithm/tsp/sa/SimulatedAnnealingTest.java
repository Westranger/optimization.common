package test.de.westranger.optimization.common.algorithm.tsp.sa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealing;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealingParameter;
import de.westranger.optimization.common.algorithm.tsp.sa.TSPNeighbourSelector;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.common.ProblemFormulation;
import de.westranger.optimization.common.algorithm.tsp.common.State;

public class SimulatedAnnealingTest {


  //@Test
  void solve194citiesTSP() {
    final InputStreamReader reader = new InputStreamReader(
        SimulatedAnnealingTest.class.getResourceAsStream("/tsp/1_vehicle_194_orders.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

    Random rng = new Random(47110815L);

    List<Order> orders = new LinkedList<>(problem.getOrders());

    Collections.shuffle(orders, rng);

    Map<Integer, List<Order>> orderMapping = new TreeMap<>();
    orderMapping.put(1, orders);

    State initialState =
        new State(new ArrayList<>(), orderMapping, problem.getVehicleStartPositions());

    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(0, 1.0, 0.99, 100000, 5, 0.9);

    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), rng);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap);

    SearchSpaceState optimizedState = sa.optimize();

    Assertions.assertEquals(9096.223933293313, optimizedState.getScore().getAbsoluteScore(), 1e-10);
    Assertions.assertEquals(73578571, sa.getTotalIterationCounter());
  }

  //@Test
  void solve29citiesTSP() {
    final InputStreamReader reader = new InputStreamReader(
        SimulatedAnnealingTest.class.getResourceAsStream("/tsp/1_vehicle_29_orders.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

    Random rng = new Random(47110815L);

    List<Order> orders = new LinkedList<>(problem.getOrders());

    Collections.shuffle(orders, rng);

    Map<Integer, List<Order>> orderMapping = new TreeMap<>();
    orderMapping.put(1, orders);

    State initialState =
        new State(new ArrayList<>(), orderMapping, problem.getVehicleStartPositions());

    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(0, 100.0, 0.7, 5000, 10, 0.9);

    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), rng);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap);

    SearchSpaceState optimizedState = sa.optimize();

    Assertions.assertEquals(25206.99729363572, optimizedState.getScore().getAbsoluteScore(), 1e-10);
    Assertions.assertEquals(72335, sa.getTotalIterationCounter());
  }
}
