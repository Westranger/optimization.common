package test.de.westranger.optimization.common.algorithm.example.tsp.sa;

import com.google.gson.Gson;
import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealing;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealingParameter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.Order;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.ProblemFormulation;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.State;
import test.de.westranger.optimization.common.algorithm.example.tsp.dfs.TSPNeighbourSelector;

public class SimulatedAnnealingTest {


  @Test
  void solve980citiesTSP() {
    final InputStreamReader reader = new InputStreamReader(
        SimulatedAnnealingTest.class.getResourceAsStream("/1_vehicle_980_orders.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

    Random rng = new Random(47110816L);

    List<Order> orders = new LinkedList<>();
    for(Order o:problem.getOrders()){
      orders.add(o);
    }

    Collections.shuffle(orders,rng);

    Map<Integer,List<Order>> orderMapping = new TreeMap<>();
    orderMapping.put(1,orders);

    State initialState = new State(new ArrayList<>(), orderMapping,problem.getVehicleStartPositions());

    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(100000, 10., 0.97, 1000);

    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.getTMax(), sap.getTMin(), rng);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap);

    SearchSpaceState optimizedState = sa.optimize(1e3);

    System.out.println("done");
  }

  @Test
  void solve29citiesTSP() {
    final InputStreamReader reader = new InputStreamReader(
        SimulatedAnnealingTest.class.getResourceAsStream("/1_vehicle_29_orders.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

    Random rng = new Random(47110816L);

    List<Order> orders = new LinkedList<>();
    for(Order o:problem.getOrders()){
      orders.add(o);
    }

    Collections.shuffle(orders,rng);

    Map<Integer,List<Order>> orderMapping = new TreeMap<>();
    orderMapping.put(1,orders);

    State initialState = new State(new ArrayList<>(), orderMapping,problem.getVehicleStartPositions());

    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(50000, 10, 0.98, 200);

    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.getTMax(), sap.getTMin(), rng);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap);

    SearchSpaceState optimizedState = sa.optimize(1e3);

    System.out.println("done");
  }

  @Test
  void solveSimpleTSP() {
    Map<Integer,Point2D> vehicleMapping = new TreeMap<>();
    vehicleMapping.put(1,new Point2D(100000,0));

    List<Order> orders = new LinkedList<>();
    orders.add(new Order(1,new Point2D(10000.0,0)));
    orders.add(new Order(9,new Point2D(90000.0,0)));
    orders.add(new Order(2,new Point2D(20000.0,0)));
    orders.add(new Order(8,new Point2D(80000.0,0)));
    orders.add(new Order(3,new Point2D(30000.0,0)));
    orders.add(new Order(7,new Point2D(70000.0,0)));
    orders.add(new Order(4,new Point2D(40000.0,0)));
    orders.add(new Order(6,new Point2D(60000.0,0)));
    orders.add(new Order(5,new Point2D(50000.0,0)));

    Map<Integer,List<Order>> orderMapping = new TreeMap<>();
    orderMapping.put(1,orders);

    State initialState = new State(new ArrayList<>(), orderMapping,vehicleMapping);

    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(100000, 1000, 0.7, 50);
    Random rng = new Random(47110816L);
    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.getTMax(), sap.getTMin(), rng);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap);

    SearchSpaceState optimizedState = sa.optimize(1e3);

    System.out.println("done");
  }


}
