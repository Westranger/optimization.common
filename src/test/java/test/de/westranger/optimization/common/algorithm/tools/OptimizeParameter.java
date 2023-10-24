package test.de.westranger.optimization.common.algorithm.tools;

import com.google.gson.Gson;
import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.NeighbourSelector;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealing;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealingParameter;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import test.de.westranger.optimization.common.algorithm.example.function.fitting.sa.aux.DataPoint;
import test.de.westranger.optimization.common.algorithm.example.function.fitting.sa.aux.NormalDistributionSelector;
import test.de.westranger.optimization.common.algorithm.example.function.fitting.sa.aux.CubicFunktion;
import test.de.westranger.optimization.common.algorithm.example.function.fitting.common.CubicFunktionFitter;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.Order;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.ProblemFormulation;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.State;
import test.de.westranger.optimization.common.algorithm.example.tsp.dfs.aux.TSPNeighbourSelector;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.SimulatedAnnealingTest;

public class OptimizeParameter {

  public static void main(String[] args) {

    double bestScore = Double.POSITIVE_INFINITY;

    for (double tmax : new double[] {100000, 50000, 30000, 20000, 15000, 10000, 7500, 5000, 2500,
        1000}) {
      for (double tmin : new double[] {100, 10, 1, 0.1, 0.01,0.001}) {
        for (double gamma : new double[] {0.8, 0.85, 0.9, 0.95, 0.96, 0.97, 0.98,
            0.99}) {
          for (double omega : new double[] {100, 125, 150, 175, 200}) {
            if (tmin >= tmax) {
              continue;
            }

            double sum = 0.0;
            double iter = 0.0;

            for (long i = 0; i < 10; i++) {
              final long seed = 47110815 + i * 10000;

              final SimulatedAnnealingParameter sap =
                  new SimulatedAnnealingParameter(tmax, tmin, gamma, omega);
              final InputStreamReader reader = new InputStreamReader(
                  SimulatedAnnealingTest.class.getResourceAsStream("/tsp/1_vehicle_29_cities.json"));

              final Gson gson = new Gson();
              final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

              //System.out.println("worked");

              // create initial solution
              Map<Integer, List<Order>> orderMapping = new TreeMap<>();
              Random rng = new Random(47110815L);

              LinkedList<Order> orders = new LinkedList<>(problem.getOrders());
              for (Map.Entry<Integer, Point2D> entry : problem.getVehicleStartPositions().entrySet()) {
                orderMapping.put(entry.getKey(), new LinkedList<>());

                int cnt = 0;
                while (!orders.isEmpty()) {
                  orderMapping.get(cnt + 1).add(orders.removeFirst());
                  cnt = (cnt + 1) % problem.getVehicleStartPositions().size();
                }

                Collections.shuffle(orderMapping.get(entry.getKey()),rng);
              }

              State initialState = new State(orders, orderMapping, problem.getVehicleStartPositions());


              TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.getTMax(), sap.getTMin(), rng);

              SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap);

              SearchSpaceState optimizedState = sa.optimize(1e3);

              iter += sa.getTotalIterationCounter();
              sum += optimizedState.getScore().getAbsoluteScore();
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
              bestScore = sum * iter;
            }
          }
        }
      }
    }

    System.out.println("done");
  }

}
