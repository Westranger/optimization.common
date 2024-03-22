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
        new SimulatedAnnealingParameter(0, 0.1, 0.5, 25000, 500, 0.7, 0.97);

    // {avg_score=9353.517538525306, beta=0.97, gamma=0.5, initialAcceptanceRatio=0.7, iter=9725000.0, maxImprovementPerTemperature=500.0, omegaMax=25000.0, param_id#4_3_11_7_4_7=0.0, score=9353.517538525306, tMin=0.01, time=390936.0} t:9353.6874335299 true


    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), seed, true);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, seed, sap);

    SearchSpaceState optimizedState = sa.optimize(true);

    Assertions.assertEquals(9353.517538504946,
        optimizedState.getScore().getValue(0), 1e-4);
    Assertions.assertEquals(7825000, sa.getTotalIterationCounter());
  }

  /*

	{avg_score=9356.312588239258, beta=0.8, gamma=0.5, initialAcceptanceRatio=0.8, iter=1.0543568E7, maxImprovementPerTemperature=100.0, omegaMax=100000.0, , tMin=1.0E-5} t:9353.77161663262 false
   */


  @Test
  void solve29citiesTSP() {
    final InputStreamReader reader = new InputStreamReader(
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
        new SimulatedAnnealingParameter(0, 1.0E-5, 0.2, 100, 5, 0.99, 0.2);

    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), seed, true);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, seed, sap);

    SearchSpaceState optimizedState = sa.optimize(true);

    Assertions.assertEquals(27601.173774493753, optimizedState.getScore().getValue(0), 1e-3);
    Assertions.assertEquals(1122, sa.getTotalIterationCounter());
  }

}
