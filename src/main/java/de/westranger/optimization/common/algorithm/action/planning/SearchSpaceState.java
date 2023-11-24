package de.westranger.optimization.common.algorithm.action.planning;

import de.westranger.optimization.common.algorithm.action.planning.solver.Score;
import de.westranger.optimization.common.algorithm.action.planning.solver.Visualizable;
import java.util.List;
import java.util.Optional;

public abstract class SearchSpaceState
    implements Comparable<SearchSpaceState>, Cloneable, Visualizable {

  public abstract List<Action> getPossibleActions();

  @Deprecated
  public abstract boolean perform(Action action);

  public abstract Score getScore();

  @Override
  public abstract SearchSpaceState clone();

  public abstract Optional<Action> getLastPerformedAction();

  public abstract boolean isGoalState();

}
