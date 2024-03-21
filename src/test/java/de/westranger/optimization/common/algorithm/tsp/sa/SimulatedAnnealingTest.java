package de.westranger.optimization.common.algorithm.tsp.sa;

import com.google.gson.Gson;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealing;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealingParameter;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.common.ProblemFormulation;
import de.westranger.optimization.common.algorithm.tsp.common.State;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimulatedAnnealingTest {


  @Test
  void solve194citiesTSP() {
    final InputStreamReader reader = new InputStreamReader(
        SimulatedAnnealingTest.class.getResourceAsStream("/tsp/1_vehicle_194_orders.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

    final long seed = 47110815L;
    Random rng = new Random(seed);

    List<Order> orders = new LinkedList<>(problem.getOrders());
    Collections.shuffle(orders, rng);

    final VehicleRoute vr =
        new VehicleRoute(1, problem.getVehicleStartPositions().get(1), orders, true);
    final RouteEvaluator re = new RouteEvaluator();

    State initialState =
        new State(new ArrayList<>(), Map.of(1, vr), re);
    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(0, 1.0, 0.96, 250000, 100, 0.9);

    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), seed, true);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, seed, sap);

    SearchSpaceState optimizedState = sa.optimize(true);
    //{avg_score=9360.77913529154, gamma=0.95, initialAcceptanceRatio=0.5, iter=1.375E7, maxImprovementPerTemperature=250.0, omegaMax=50000.0, param_id#4_9_6_6_8=0.0, score=9360.77913529154, tMin=0.1} t:9353.77161663262 false

    Assertions.assertEquals(9353.678079851821, optimizedState.getScore().getValue(0), 1e-10);
    Assertions.assertEquals(5.2500528E7, sa.getTotalIterationCounter());
  }

  @Test
  void solve29citiesTSP() {
    final InputStreamReader reader = new InputStreamReader(
        //SimulatedAnnealingTest.class.getResourceAsStream("/tsp/1vehicle_8_orders_rectangle.json"));
        SimulatedAnnealingTest.class.getResourceAsStream("/tsp/1_vehicle_29_orders.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

    final long seed = 47110815L;
    Random rng = new Random(seed);

    List<Order> orders = new LinkedList<>(problem.getOrders());

    Collections.shuffle(orders, rng);

    final VehicleRoute vr =
        new VehicleRoute(1, problem.getVehicleStartPositions().get(1), orders, true);
    final RouteEvaluator re = new RouteEvaluator();

    State initialState =
        new State(new ArrayList<>(), Map.of(1, vr), re);

    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(0, 1.0E-5, 0.2, 500, 2, 0.95);

    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), seed, true);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, seed, sap);

    SearchSpaceState optimizedState = sa.optimize(true);

    Assertions.assertEquals(27601.173774493753, optimizedState.getScore().getValue(0), 1e-6);
    Assertions.assertEquals(1318, sa.getTotalIterationCounter());
  }
}
