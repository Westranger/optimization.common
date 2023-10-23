package test.de.westranger.optimization.common.algorithm;

import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.util.Function;
import de.westranger.optimization.common.algorithm.util.Solution;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.NeighbourSelector;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealing;
import java.util.List;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulatedAnnealingTest {


  class NormalDistributionSelector implements NeighbourSelector {
    private final double sigma;
    private final Random rng;

    public NormalDistributionSelector(final double sigma, final Random rng) {
      this.sigma = sigma;
      this.rng = rng;
    }

    @Override
    public SearchSpaceState select(SearchSpaceState intermediateSolution,
                                   double currentTemperature) {
      final List<Action> actions = intermediateSolution.getPossibleActions();
      final Action selectedAction = actions.get(this.rng.nextInt(actions.size()));
      final SearchSpaceState result = intermediateSolution.clone();
      result.perform(selectedAction);
      return result;
    }
  }

  class QubicFunction implements Function {

    private double qubic(final double x, final double a1, final double a2, final double a3,
                         final double c) {
      return a1 * Math.pow(x, 3.0) + a2 * Math.pow(x, 2.0) + a3 * x + c;
    }

    @Override
    public double evaluate(Solution solution) {
      final DoubleSolution ds = (DoubleSolution) solution;
      final double a1 = ds.get()[0];
      final double a2 = ds.get()[0];
      final double a3 = ds.get()[0];
      final double c = ds.get()[0];

      double err = 0.0;
      int cnt = 0;
      for (int x = -10; x <= 10; x++) {
        err += Math.pow(qubic(x, 3.0, 1.5, 9.0, 0.25) - qubic(x, a1, a2, a3, c), 2.0);
        cnt++;
      }

      err *= 1.0 / (double) cnt;

      return err;
    }
  }

  @Test
  void optimizeFunction() {
    final DoubleSolution initial = new DoubleSolution(new double[] {0.0, 0.0, 0.0, 0.0});
    final NeighbourSelector ns = new NormalDistributionSelector(2.0);
    final Function func = new QubicFunction();
    final SimulatedAnnealing sa = new SimulatedAnnealing(initial, ns, func, 2000);
    final DoubleSolution finalSolution = (DoubleSolution) sa.optimize(1e-10);
    //assertEquals(3.0, finalSolution.get()[0], 1e-1);
    //assertEquals(1.5, finalSolution.get()[1], 1e-2);
    //assertEquals(9.0, finalSolution.get()[2], 1e-1);
    //assertEquals(0.25, finalSolution.get()[3], 1e-3);
  }

}
