package test.de.westranger.optimization.common.algorithm.sa.function.fitting;

import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.NeighbourSelector;
import java.util.List;
import java.util.Random;

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
    if (!(selectedAction instanceof QubicFittingAction)) {
      throw new IllegalArgumentException("Action must be of type QubicFittingAction");
    }
    final QubicFittingAction qfa = (QubicFittingAction) selectedAction;
    final double newValue = this.rng.nextGaussian() * currentTemperature + qfa.getValue();
    final SearchSpaceState result = intermediateSolution.clone();
    result.perform((Action) new QubicFittingAction(newValue, qfa.getFp()));
    return result;
  }
}
