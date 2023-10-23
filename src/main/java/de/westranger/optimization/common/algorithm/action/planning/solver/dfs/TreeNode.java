package de.westranger.optimization.common.algorithm.action.planning.solver.dfs;

import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;

import java.util.List;
import java.util.Optional;

public final class TreeNode {
    private final SearchSpaceState state;
    private Optional<TreeNode> parent;
    private final List<Action> actions;
    private Optional<TreeNode> child;
    private int currentIdx;
    private int level;


    public TreeNode(final TreeNode parent, final SearchSpaceState state, final int level) {
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

    public TreeNode(final SearchSpaceState state, final int level) {
        this(null, state, level);
    }

    public Optional<TreeNode> expandNext() {
        if (this.currentIdx < this.actions.size()) {

            SearchSpaceState nextState = this.state.clone();
            boolean actionPossible = nextState.perform(this.actions.get(this.currentIdx));

            if (actionPossible) {
                this.currentIdx++;
                this.child = Optional.of(new TreeNode(this, nextState, this.level + 1));
                return this.child;
            }
        }
        return Optional.empty();
    }

    public Optional<TreeNode> getParent() {
        return this.parent;
    }

    public void clearParent() {
        this.parent = Optional.empty();
    }

    public SearchSpaceState getState() {
        return this.state;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return this.level + " " + this.state.getScore();
    }
}
