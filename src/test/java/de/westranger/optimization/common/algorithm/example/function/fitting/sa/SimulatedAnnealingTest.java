package de.westranger.optimization.common.algorithm.example.function.fitting.sa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.NeighbourSelector;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealing;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealingParameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import de.westranger.optimization.common.algorithm.example.function.fitting.common.CubicFittingAction;
import de.westranger.optimization.common.algorithm.example.function.fitting.common.CubicFunktion;
import de.westranger.optimization.common.algorithm.example.function.fitting.common.CubicFunktionFitter;
import de.westranger.optimization.common.algorithm.example.function.fitting.common.DataPoint;
import de.westranger.optimization.common.algorithm.example.function.fitting.common.NormalDistributionSelector;

class SimulatedAnnealingTest {

  @Test
  void learnCubicFunction() {
    CubicFunktion qf = new CubicFunktion(3.0, 1.5, 9.0, 0.25);
    List<DataPoint> data = new LinkedList<>();
    for (int x = -10; x <= 10; x++) {
      data.add(new DataPoint(x, qf.evaluate(x)));
    }

    final long seed = 47110815L;
    final SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(10000, 0.001, 0.9, 100, 20000, 0.9, 0.9);
    final Random rng = new Random(seed);
    final CubicFunktion tbf = new CubicFunktion(10000.0, 10000.0, 10000.0, 10000.0);
    final CubicFunktionFitter initial = new CubicFunktionFitter(tbf, data);
    final NeighbourSelector ns = new NormalDistributionSelector(rng);

    final SimulatedAnnealing sa = new SimulatedAnnealing(initial, ns, seed, sap);
    final SearchSpaceState optimizedResult = sa.optimize(false);

    assertNotNull(optimizedResult);
    assertTrue(optimizedResult instanceof CubicFunktionFitter);
    final CubicFunktionFitter qff = (CubicFunktionFitter) optimizedResult;
    final List<Action> actions = qff.getPossibleActions();
    assertEquals(4, actions.size());
    for (int i = 0; i < 4; i++) {
      assertTrue(actions.get(i) instanceof CubicFittingAction);
    }

    assertEquals(0.003682319440971411, qff.getScore().getValue(0), 1e-4);

    CubicFittingAction qfa = (CubicFittingAction) actions.get(0);
    assertEquals(CubicFittingAction.FunctionParameter.ParamA1, qfa.fp());
    assertEquals(qf.getA1(), qfa.value(), 1e-3);

    qfa = (CubicFittingAction) actions.get(1);
    assertEquals(CubicFittingAction.FunctionParameter.ParamA2, qfa.fp());
    assertEquals(qf.getA2(), qfa.value(), 1e-2);

    qfa = (CubicFittingAction) actions.get(2);
    assertEquals(CubicFittingAction.FunctionParameter.ParamA3, qfa.fp());
    assertEquals(qf.getA3(), qfa.value(), 1e-2);

    qfa = (CubicFittingAction) actions.get(3);
    assertEquals(CubicFittingAction.FunctionParameter.ParamConstant, qfa.fp());
    //assertEquals(qf.getConstant(), qfa.value(), 1e-2);

    //assertEquals(15200, sa.getTotalIterationCounter());
  }

}
