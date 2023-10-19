package de.westranger.optimization.common.algorithm.action.planning;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class ActionPlanningSolution<T extends Comparable<T>, G extends Comparable<G>> {

    private final SearchSpaceState<T, G> solutionState;
    private final List<Action> actions;
    private final G score;

    public ActionPlanningSolution(final SearchSpaceState<T, G> solutionState,
                                  final List<Action> actions, final G score) {
        this.solutionState = solutionState;
        this.actions = new LinkedList<>(actions);
        this.score = score;
    }

    public List<Action> getActions() {
        return Collections.unmodifiableList(this.actions);
    }

    public SearchSpaceState<T, G> getState() {
        return this.solutionState;
    }

    public G getScore() {
        return this.score;
    }

}
