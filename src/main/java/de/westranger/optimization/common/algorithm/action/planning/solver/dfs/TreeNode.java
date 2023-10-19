package de.westranger.optimization.common.algorithm.action.planning.solver.dfs;

import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;

import java.util.List;
import java.util.Optional;

public final class TreeNode<T extends Comparable<T>, G extends Comparable<G>> {
    private final SearchSpaceState<T, G> state;
    private Optional<TreeNode<T, G>> parent;
    private final List<Action> actions;
    private Optional<TreeNode<T, G>> child;
    private int currentIdx;
    private Optional<Action> currentAction;
    private int level;


    public TreeNode(final TreeNode<T, G> parent, final SearchSpaceState<T, G> state, final int level) {
        if (parent == null) {
            this.parent = Optional.empty();
        } else {
            this.parent = Optional.of(parent);
        }
        this.state = state;
        this.child = Optional.empty();
        this.actions = state.getPossibleActions();
        this.currentIdx = 0;
        this.currentAction = Optional.empty();
        this.level = level;
    }

    public TreeNode(final SearchSpaceState<T, G> state, final int level) {
        this(null, state, level);
    }

    public Optional<TreeNode<T, G>> expandNext() {
        if (this.currentIdx < this.actions.size()) {

            SearchSpaceState<T, G> nextState = this.state.clone();
            boolean actionPossible = nextState.perform(this.actions.get(this.currentIdx));

            if (actionPossible) {
                this.currentAction = Optional.of(this.actions.get(this.currentIdx));
                this.currentIdx++;
                this.child = Optional.of(new TreeNode<>(this, nextState, this.level + 1));
                return this.child;
            }
        }
        return Optional.empty();
    }

    public Optional<Action> getCurrentAction() {
        return this.currentAction;
    }

    public Optional<TreeNode<T, G>> getParent() {
        return this.parent;
    }

    public void clearParent() {
        this.parent = Optional.empty();
    }

    public SearchSpaceState<T, G> getState() {
        return this.state;
    }

    public int getLevel() {
        return level;
    }
}
