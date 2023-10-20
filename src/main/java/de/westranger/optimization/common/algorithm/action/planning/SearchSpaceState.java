package de.westranger.optimization.common.algorithm.action.planning;

import java.util.List;
import java.util.Optional;

public abstract class SearchSpaceState<S extends Comparable<S>>
        implements Comparable<SearchSpaceState>, Cloneable {

    public abstract List<Action> getPossibleActions();

    public abstract boolean perform(Action action);

    public abstract S getScore();

    @Override
    public abstract SearchSpaceState<S> clone();

    public abstract Optional<Action> getLastPerformedAction();

    public abstract boolean isGoalState();

}
