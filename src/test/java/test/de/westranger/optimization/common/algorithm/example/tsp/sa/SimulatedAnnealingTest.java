package test.de.westranger.optimization.common.algorithm.example.tsp.sa;

import com.google.gson.Gson;
import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealing;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealingParameter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.Order;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.ProblemFormulation;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.State;
import test.de.westranger.optimization.common.algorithm.example.tsp.dfs.aux.TSPNeighbourSelector;

public class SimulatedAnnealingTest {


  @Test
  void solve29citiesTSP() {
    final InputStreamReader reader = new InputStreamReader(
        SimulatedAnnealingTest.class.getResourceAsStream("/tsp/1_vehicle_29_cities.json"));

    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

    //System.out.println("worked");

    // create initial solution
    Map<Integer, List<Order>> orderMapping = new TreeMap<>();

    LinkedList<Order> orders = new LinkedList<>(problem.getOrders());
    for (Map.Entry<Integer, Point2D> entry : problem.getVehicleStartPositions().entrySet()) {
      orderMapping.put(entry.getKey(), new LinkedList<>());

      int cnt = 0;
      while (!orders.isEmpty()) {
        orderMapping.get(cnt + 1).add(orders.removeFirst());
        cnt = (cnt + 1) % problem.getVehicleStartPositions().size();
      }
    }

    State initialState = new State(orders, orderMapping, problem.getVehicleStartPositions());

    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(10000.0, 0.1, 0.95, 200);
    Random rng = new Random(47110815L);
    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.getTMax(), sap.getTMin(), rng);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap);

    SearchSpaceState optimizedState = sa.optimize(1e3);

    System.out.println("done");
  }

}
