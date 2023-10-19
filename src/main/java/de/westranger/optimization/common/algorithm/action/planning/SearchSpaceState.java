package de.westranger.optimization.common.algorithm.action.planning;

import java.util.List;

public abstract class SearchSpaceState<T extends Comparable<T>, G extends Comparable<G>>
    implements Comparable<SearchSpaceState>, Cloneable {
  public abstract T getStateRepresentation();

  public abstract List<Action> getPossibleActions();

  public abstract boolean perform(Action action);

  public abstract G getScore();

  @Override
  public abstract SearchSpaceState<T, G> clone();

  public abstract boolean isGoalState();

}
