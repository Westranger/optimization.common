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

    Random rng = new Random(47110815L);

    List<Order> orders = new LinkedList<>(problem.getOrders());
    Collections.shuffle(orders, rng);

    final VehicleRoute vr =
        new VehicleRoute(1, problem.getVehicleStartPositions().get(1), orders, true);
    final RouteEvaluator re = new RouteEvaluator();

    State initialState =
        new State(new ArrayList<>(), new ArrayList<>(), List.of(vr), re);
    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(0, 1.0, 0.96, 250000, 100, 0.9);

    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), rng);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap);

    SearchSpaceState optimizedState = sa.optimize(false);
    // {avg_score=9358.20705989015, gamma=0.9, initialAcceptanceRatio=0.9, iter=2.0250528E7, maxImprovementPerTemperature=100.0, omegaMax=250000.0, score=9358.20705989015, tMax=0.0, tMin=1.0}
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

    Random rng = new Random(47110815L);

    List<Order> orders = new LinkedList<>(problem.getOrders());

    Collections.shuffle(orders, rng);

    final VehicleRoute vr =
        new VehicleRoute(1, problem.getVehicleStartPositions().get(1), orders, true);
    final RouteEvaluator re = new RouteEvaluator();

    State initialState =
        new State(new ArrayList<>(), new ArrayList<>(), List.of(vr), re);

    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(0, 1.0E-5, 0.4, 100, 2, 0.95);

    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), rng);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap);

    SearchSpaceState optimizedState = sa.optimize(false);

    Assertions.assertEquals(27601.173774493753, optimizedState.getScore().getValue(0), 1e-6);
    Assertions.assertEquals(1440, sa.getTotalIterationCounter());
  }
}
