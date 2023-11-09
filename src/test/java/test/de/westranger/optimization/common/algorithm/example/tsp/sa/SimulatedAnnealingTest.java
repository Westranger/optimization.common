package test.de.westranger.optimization.common.algorithm.example.tsp.sa;

import com.google.gson.Gson;
import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealing;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealingParameter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    List<Order> orders = new LinkedList<>(problem.getOrders());

    Collections.shuffle(orders, rng);

    Map<Integer, List<Order>> orderMapping = new TreeMap<>();
    orderMapping.put(1, orders);

    State initialState =
        new State(new ArrayList<>(), orderMapping, problem.getVehicleStartPositions());

    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(100000, .001, 0.99, 2000, 10000, 0.9);

    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), rng);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap,
        new File(
            "C:\\Users\\Marius\\IdeaProjects\\optimization.common\\src\\test\\resources\\output"));

    initialState = (State) sa.optimize();

    System.out.println("done");
  }

  @Test
  void solve29citiesTSP() {
    final InputStreamReader reader = new InputStreamReader(
        SimulatedAnnealingTest.class.getResourceAsStream("/1_vehicle_29_orders.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

    Random rng = new Random(47110816L);

    List<Order> orders = new LinkedList<>(problem.getOrders());

    Collections.shuffle(orders, rng);

    Map<Integer, List<Order>> orderMapping = new TreeMap<>();
    orderMapping.put(1, orders);

    State initialState =
        new State(new ArrayList<>(), orderMapping, problem.getVehicleStartPositions());

    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(50000, 0.1, 0.99, 200, 10000, 0.9);

    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), rng);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap,
        new File(
            "C:\\Users\\Marius\\IdeaProjects\\optimization.common\\src\\test\\resources\\output"));

    SearchSpaceState optimizedState = sa.optimize();

    System.out.println("done");
  }

  @Test
  void solveSimpleTSP() {
    Map<Integer, Point2D> vehicleMapping = new TreeMap<>();
    vehicleMapping.put(1, new Point2D(100000, 0));

    Map<Integer, List<Order>> orderMapping = new TreeMap<>();

    List<Order> orders = new LinkedList<>();
    orders.add(new Order(1, new Point2D(10000.0, 0)));
    orders.add(new Order(9, new Point2D(90000.0, 0)));
    orders.add(new Order(2, new Point2D(20000.0, 0)));
    orders.add(new Order(8, new Point2D(80000.0, 0)));
    orders.add(new Order(3, new Point2D(30000.0, 0)));
    orders.add(new Order(7, new Point2D(70000.0, 0)));
    orders.add(new Order(4, new Point2D(40000.0, 0)));
    orders.add(new Order(6, new Point2D(60000.0, 0)));
    orders.add(new Order(5, new Point2D(50000.0, 0)));

    orderMapping.put(1, orders);

    State initialState = new State(new ArrayList<>(), orderMapping, vehicleMapping);

    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(100000, 1000, 0.7, 50, 10000, 0.9);
    Random rng = new Random(47110816L);
    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), rng);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap);

    SearchSpaceState optimizedState = sa.optimize();

    State x = (State) optimizedState;
    State y = new State(x.getOrderList(), x.getOrderMapping(), x.getVehiclePositions());


    System.out.println("done");
  }

  @Test
  public void testMultipleVehicle() {

    final Map<Integer, List<Order>> mappingOrderOptimal = new TreeMap<>();

    List<Order> orders = new LinkedList<>();
    int counter = 1;
    for (double x : new double[] {2, 3, 5, 6, 7, 9, 10}) {
      List<Order> opt = new LinkedList<>();
      for (double y : new double[] {2, 3, 4, 5, 6, 7, 8, 9, 10, 11}) {

        final Order o = new Order(counter, new Point2D(x * 150, y * 100));
        orders.add(o);
        opt.add(o);
        System.out.println(
            "    { \"id\":" + counter + ", \"to\":{ \"x\":" + x * 150 + ", \"y\":" + y * 100 +
                " } },");
        counter++;
      }
      mappingOrderOptimal.put((int) x, opt);
    }

    final Map<Integer, Point2D> mapping = new TreeMap<>();
    final Map<Integer, List<Order>> mappingOrder = new TreeMap<>();

    for (int i = 1; i <= 11; i++) {
      mapping.put(i, new Point2D(i * 150, 0));
      System.out.println("    \"" + i + "\":{ \"x\":" + i * 150 + ", \"y\":0.0 }");
      mappingOrder.put(i, new ArrayList<>());
    }

    Collections.shuffle(orders, new Random(47110815));

    State state = new State(orders, mappingOrderOptimal, mapping);
    System.out.println(state.toSVG());
  }

  @Test
  void solve11Vehicle70Order() {
    final InputStreamReader reader = new InputStreamReader(
        SimulatedAnnealingTest.class.getResourceAsStream("/tsp/15_vehicle_100_orders.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

    Random rng = new Random(47110816L);

    List<Order> orders = new LinkedList<>();
    for (Order o : problem.getOrders()) {
      orders.add(o);
    }

    Collections.shuffle(orders, rng);

    Map<Integer, List<Order>> orderMapping = new TreeMap<>();

    while (!orders.isEmpty()) {
      for (Map.Entry<Integer, Point2D> entry : problem.getVehicleStartPositions().entrySet()) {
        List<Order> ord = orderMapping.get(entry.getKey());

        if (ord == null) {
          ord = new ArrayList<>();
          orderMapping.put(entry.getKey(), ord);
        }

        if (orders.isEmpty()) {
          break;
        }

        ord.add(orders.remove(0));
      }
    }

    State initialState =
        new State(new ArrayList<>(), orderMapping, problem.getVehicleStartPositions());

    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(100000, .001, 0.99, 2000, 10000, 0.9);

    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), rng);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap,
        new File(
            "C:\\Users\\Marius\\IdeaProjects\\optimization.common\\src\\test\\resources\\output"));

    initialState = (State) sa.optimize();

    System.out.println("done");
  }


  @Test
  void evaluateTemperature() {
    final InputStreamReader reader = new InputStreamReader(
        SimulatedAnnealingTest.class.getResourceAsStream("/1_vehicle_980_orders.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

    Random rng = new Random(47110816L);

    List<Order> orders = new LinkedList<>(problem.getOrders());

    Collections.shuffle(orders, rng);

    Map<Integer, List<Order>> orderMapping = new TreeMap<>();
    orderMapping.put(1, orders);

    State initialState =
        new State(new ArrayList<>(), orderMapping, problem.getVehicleStartPositions());

    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(100000, .001, 0.99, 2000, 10000, 0.9);

    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), rng);


    SearchSpaceState best = initialState;
    SearchSpaceState previous = initialState;
    SearchSpaceState current = initialState;

    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(
          "C:\\Users\\Marius\\IdeaProjects\\optimization.common\\src\\test\\resources\\eval_temp.csv"));
      bw.append("score;acceptance_rate;delta;best;counter\n");

      int bestCounter = 0;
      double temperatur = 6300;//8741.73813071313;
      for (int i = 0; i < 200000; i++) {
        SearchSpaceState tmp = ns.select(current, temperatur);

        final StringBuilder sb = new StringBuilder();

        final double score = tmp.getScore().getAbsoluteScore();
        final double acceptanceRate =
            Math.exp(-Math.abs(
                previous.getScore().getAbsoluteScore() - tmp.getScore().getAbsoluteScore()) /
                temperatur);

        sb.append(score);
        sb.append(';');
        sb.append(acceptanceRate);
        sb.append(';');
        sb.append(Math.abs(
            previous.getScore().getAbsoluteScore() - tmp.getScore().getAbsoluteScore()));
        sb.append(';');
        sb.append(best.getScore());
        sb.append(';');
        sb.append(bestCounter);
        sb.append('\n');
        bw.append(sb.toString());

        if (rng.nextDouble() < acceptanceRate) {
          previous = tmp;
        }
        if (tmp.getScore().getAbsoluteScore() < best.getScore().getAbsoluteScore()) {
          best = tmp;
          bestCounter++;
        }
      }
      bw.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }


  }

}