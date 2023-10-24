package test.de.westranger.optimization.common.algorithm.example.function.fitting.common;

import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.NeighbourSelector;
import java.util.List;
import java.util.Random;
import test.de.westranger.optimization.common.algorithm.example.function.fitting.common.CubicFittingAction;

public final class NormalDistributionSelector implements NeighbourSelector {
  private final Random rng;

  public NormalDistributionSelector(final Random rng) {
    this.rng = rng;
  }

  @Override
  public SearchSpaceState select(SearchSpaceState intermediateSolution,
                                 double currentTemperature) {
    final List<Action> actions = intermediateSolution.getPossibleActions();
    final Action selectedAction = actions.get(this.rng.nextInt(actions.size()));
    if (!(selectedAction instanceof final CubicFittingAction qfa)) {
      throw new IllegalArgumentException("Action must be of type CubicFittingAction");
    }
    final double newValue = this.rng.nextGaussian() * currentTemperature + qfa.value();
    final SearchSpaceState result = intermediateSolution.clone();
    result.perform(new CubicFittingAction(newValue, qfa.fp()));
    return result;
  }
}
