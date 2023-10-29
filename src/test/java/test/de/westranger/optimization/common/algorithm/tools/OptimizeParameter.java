package test.de.westranger.optimization.common.algorithm.tools;

import com.google.gson.Gson;
import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.NeighbourSelector;
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
import test.de.westranger.optimization.common.algorithm.example.function.fitting.common.DataPoint;
import test.de.westranger.optimization.common.algorithm.example.function.fitting.common.NormalDistributionSelector;
import test.de.westranger.optimization.common.algorithm.example.function.fitting.common.CubicFunktion;
import test.de.westranger.optimization.common.algorithm.example.function.fitting.common.CubicFunktionFitter;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.Order;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.ProblemFormulation;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.State;
import test.de.westranger.optimization.common.algorithm.example.tsp.dfs.TSPNeighbourSelector;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.SimulatedAnnealingTest;

public class OptimizeParameter {

  public static void main(String[] args) {

    double bestScore = Double.POSITIVE_INFINITY;

    for (double tmax : new double[] {100000, 50000, 30000, 20000, 15000, 10000, 7500, 5000, 2500,
        1000, 100}) {
      for (double tmin : new double[] {1000, 100, 10, 1, 0.1, 0.01, 0.001}) {
        for (double gamma : new double[] {0.7, 0.8, 0.85, 0.9, 0.95, 0.96, 0.97, 0.98,
            0.99,999}) {
          for (double omega : new double[] {50, 100, 150, 200, 300, 400, 500}) {
            if (tmin >= tmax) {
              continue;
            }

            double sum = 0.0;
            double iter = 0.0;

            for (long i = 0; i < 10; i++) {
              final long seed = 47110815 + i * 10000;

              final SimulatedAnnealingParameter sap =
                  new SimulatedAnnealingParameter(tmax, tmin, gamma, omega);
              final Random rng = new Random(seed);
              final SimulatedAnnealing sa = szenarioB(sap, rng);
              final SearchSpaceState optimizedResult = sa.optimize(1e-3);

              iter += sa.getTotalIterationCounter();
              sum += optimizedResult.getScore().getAbsoluteScore();
            }

            sum /= 10.0;
            iter /= 10.0;

            if (
                bestScore > sum * iter) {
              System.out.println(
                  "found a better solution " + iter + " with a score of " +
                      sum + " param: " + tmax + " " +
                      tmin + " " + gamma + " " + omega + " " + sum * iter
              );
              bestScore = sum *iter;
            }
          }
        }
      }
    }

    System.out.println("done");
  }

  private static SimulatedAnnealing szenarioA(final SimulatedAnnealingParameter sap,
                                             final Random rng) {
    Map<Integer, Point2D> vehicleMapping = new TreeMap<>();
    vehicleMapping.put(1, new Point2D(100000, 0));

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

    Map<Integer, List<Order>> orderMapping = new TreeMap<>();
    orderMapping.put(1, orders);

    State initialState = new State(new ArrayList<>(), orderMapping, vehicleMapping);
    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.getTMax(), sap.getTMin(), rng);
    return new SimulatedAnnealing(initialState, ns, rng, sap);
  }

  private static SimulatedAnnealing szenarioB(final SimulatedAnnealingParameter sap,
                                             final Random rng) {
    final InputStreamReader reader = new InputStreamReader(
        SimulatedAnnealingTest.class.getResourceAsStream("/1_vehicle_194_orders.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

    List<Order> orders = new LinkedList<>();
    for(Order o:problem.getOrders()){
      orders.add(o);
    }

    Collections.shuffle(orders,rng);

    Map<Integer,List<Order>> orderMapping = new TreeMap<>();
    orderMapping.put(1,orders);

    State initialState = new State(new ArrayList<>(), orderMapping,problem.getVehicleStartPositions());
    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.getTMax(), sap.getTMin(), rng);

    return new SimulatedAnnealing(initialState, ns, rng, sap);
  }

}
