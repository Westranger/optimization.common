package de.westranger.optimization.common.algorithm.action.planning;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class ActionPlanningSolution<S extends Comparable<S>> {

    private final SearchSpaceState<S> solutionState;
    private final List<Action> actions;
    private final S score;

    public ActionPlanningSolution(final SearchSpaceState<S> solutionState,
                                  final List<Action> actions, final S score) {
        this.solutionState = solutionState;
        this.actions = new LinkedList<>(actions);
        this.score = score;
    }

    public List<Action> getActions() {
        return Collections.unmodifiableList(this.actions);
    }

    public SearchSpaceState<S> getState() {
        return this.solutionState;
    }

    public S getScore() {
        return this.score;
    }

}
