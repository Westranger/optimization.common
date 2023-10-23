package test.de.westranger.optimization.common.algorithm.sa.function.fitting;

import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.NeighbourSelector;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealing;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealingParameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class OptimizeParameter {

  public static void main(String[] args) {
    QubicFunktion qf = new QubicFunktion(3.0, 1.5, 9.0, 0.25);
    List<DataPoint> data = new LinkedList<>();
    for (int x = -10; x <= 10; x++) {
      data.add(new DataPoint(x, qf.evaluate(x)));
    }

    double bestScore = Double.POSITIVE_INFINITY;

    for (double tmax : new double[] {100000, 50000, 30000, 20000, 15000, 10000, 7500, 5000, 2500,
        1000, 100, 10, 1, 0.1, 0.01}) {
      for (double tmin : new double[] {100000, 10000, 1000, 100, 10, 1, 0.1, 0.01}) {
        for (double gamma : new double[] {0.8, 0.85, 0.9, 0.95, 0.96, 0.965, 0.97, 0.975, 0.98,
            0.99}) {
          for (double omega : new double[] {10, 25, 50, 75, 100, 125, 150, 175, 200}) {
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
              final QubicFunktion tbf = new QubicFunktion(10000.0, 10000.0, 10000.0, 10000.0);
              final QubicFunktionFitter initial = new QubicFunktionFitter(tbf, data);
              final NeighbourSelector ns = new NormalDistributionSelector(rng);

              final SimulatedAnnealing sa = new SimulatedAnnealing(initial, ns, rng, sap);
              final SearchSpaceState optimizedResult = sa.optimize(1e-3);

              double score = (optimizedResult.getScore().getAbsoluteScore() *
                  (double) sa.getTotalIterationCounter());

              iter += sa.getTotalIterationCounter();
              sum += score;
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
