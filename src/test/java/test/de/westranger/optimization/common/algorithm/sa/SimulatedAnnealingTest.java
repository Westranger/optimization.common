package test.de.westranger.optimization.common.algorithm.sa;

import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.NeighbourSelector;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealing;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealingParameter;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;

import java.util.Random;
import test.de.westranger.optimization.common.algorithm.sa.function.fitting.DataPoint;
import test.de.westranger.optimization.common.algorithm.sa.function.fitting.NormalDistributionSelector;
import test.de.westranger.optimization.common.algorithm.sa.function.fitting.QubicFittingAction;
import test.de.westranger.optimization.common.algorithm.sa.function.fitting.QubicFunktion;
import test.de.westranger.optimization.common.algorithm.sa.function.fitting.QubicFunktionFitter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatedAnnealingTest {

  @Test
  void learnQubicFunction() {
    QubicFunktion qf = new QubicFunktion(3.0, 1.5, 9.0, 0.25);
    List<DataPoint> data = new LinkedList<>();
    for (int x = -10; x <= 10; x++) {
      data.add(new DataPoint(x, qf.evaluate(x)));
    }

    final SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(10000.0, 0.01, 0.97, 150);
    final Random rng = new Random(47110815);
    final QubicFunktion tbf = new QubicFunktion(10000.0, 10000.0, 10000.0, 10000.0);
    final QubicFunktionFitter initial = new QubicFunktionFitter(tbf, data);
    final NeighbourSelector ns = new NormalDistributionSelector(rng);

    final SimulatedAnnealing sa = new SimulatedAnnealing(initial, ns, rng, sap);
    final SearchSpaceState optimizedResult = sa.optimize(1e-3);

    assertNotNull(optimizedResult);
    assertTrue(optimizedResult instanceof QubicFunktionFitter);
    final QubicFunktionFitter qff = (QubicFunktionFitter) optimizedResult;
    final List<Action> actions = qff.getPossibleActions();
    assertEquals(4, actions.size());
    for (int i = 0; i < 4; i++) {
      assertTrue(actions.get(i) instanceof QubicFittingAction);
    }

    assertEquals(6.363217904078274E-4, qff.getScore().getAbsoluteScore(), 1e-3);

    QubicFittingAction qfa = (QubicFittingAction) actions.get(0);
    assertEquals(QubicFittingAction.FunctionParameter.ParamA1, qfa.getFp());
    assertEquals(qf.getA1(), qfa.getValue(), 1e-3);

    qfa = (QubicFittingAction) actions.get(1);
    assertEquals(QubicFittingAction.FunctionParameter.ParamA2, qfa.getFp());
    assertEquals(qf.getA2(), qfa.getValue(), 1e-3);

    qfa = (QubicFittingAction) actions.get(2);
    assertEquals(QubicFittingAction.FunctionParameter.ParamA3, qfa.getFp());
    assertEquals(qf.getA3(), qfa.getValue(), 1e-2);

    qfa = (QubicFittingAction) actions.get(3);
    assertEquals(QubicFittingAction.FunctionParameter.ParamConstant, qfa.getFp());
    assertEquals(qf.getConstant(), qfa.getValue(), 1e-3);

    assertEquals(90051, sa.getTotalIterationCounter());
  }

}
