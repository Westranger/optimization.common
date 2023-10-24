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
          for (double omega : new double[] {100, 200, 300, 400, 500}) {
            if (tmin >= tmax) {
              continue;
            }

            double sum = 0.0;
            double iter = 0.0;

            for (long i = 0; i < 10; i++) {
              final long seed = 47110815 + i * 10000;

              final SimulatedAnnealingParameter sap =
                  new SimulatedAnnealingParameter(tmax, tmin, gamma, omega);

              CubicFunktion qf = new CubicFunktion(3.0, 1.5, 9.0, 0.25);
              List<DataPoint> data = new LinkedList<>();
              for (int x = -10; x <= 10; x++) {
                data.add(new DataPoint(x, qf.evaluate(x)));
              }

              final Random rng = new Random(47110815);
              final CubicFunktion tbf = new CubicFunktion(10000.0, 10000.0, 10000.0, 10000.0);
              final CubicFunktionFitter initial = new CubicFunktionFitter(tbf, data);
              final NeighbourSelector ns = new NormalDistributionSelector(rng);

              final SimulatedAnnealing sa = new SimulatedAnnealing(initial, ns, rng, sap);
              final SearchSpaceState optimizedResult = sa.optimize(1e-6);

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
              bestScore = sum *  iter;
            }
          }
        }
      }
    }

    System.out.println("done");
  }

}
