package de.westranger.optimization.common.algorithm.action.planning.solver.dfs;

import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;

import java.util.List;
import java.util.Optional;

public final class TreeNode<S extends Comparable<S>> {
    private final SearchSpaceState<S> state;
    private Optional<TreeNode<S>> parent;
    private final List<Action> actions;
    private Optional<TreeNode<S>> child;
    private int currentIdx;
    private int level;


    public TreeNode(final TreeNode<S> parent, final SearchSpaceState<S> state, final int level) {
        if (parent == null) {
            this.parent = Optional.empty();
        } else {
            this.parent = Optional.of(parent);
        }
        this.state = state;
        this.child = Optional.empty();
        this.actions = state.getPossibleActions();
        this.currentIdx = 0;
        this.level = level;
    }

    public TreeNode(final SearchSpaceState<S> state, final int level) {
        this(null, state, level);
    }

    public Optional<TreeNode<S>> expandNext() {
        if (this.currentIdx < this.actions.size()) {

            SearchSpaceState<S> nextState = this.state.clone();
            boolean actionPossible = nextState.perform(this.actions.get(this.currentIdx));

            if (actionPossible) {
                this.currentIdx++;
                this.child = Optional.of(new TreeNode<>(this, nextState, this.level + 1));
                return this.child;
            }
        }
        return Optional.empty();
    }

    public Optional<TreeNode<S>> getParent() {
        return this.parent;
    }

    public void clearParent() {
        this.parent = Optional.empty();
    }

    public SearchSpaceState<S> getState() {
        return this.state;
    }

    public int getLevel() {
        return level;
    }
}
