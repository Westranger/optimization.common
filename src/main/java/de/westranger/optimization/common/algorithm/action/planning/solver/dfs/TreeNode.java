package de.westranger.optimization.common.algorithm.action.planning.solver.dfs;

import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;

import java.util.List;
import java.util.Optional;

public final class TreeNode {
  private final SearchSpaceState state;
  private TreeNode parent;
  private final List<Action> actions;
  private TreeNode child;
  private int currentIdx;
  private final int level;


  public TreeNode(final TreeNode parent, final SearchSpaceState state, final int level) {
    this.parent = parent;
    this.state = state;
    this.child = null;
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
        this.child = new TreeNode(this, nextState, this.level + 1);
        return Optional.of(this.child);
      }
    }
    return Optional.empty();
  }

  public Optional<TreeNode> getParent() {
    if (this.parent == null) {
      return Optional.empty();
    }
    return Optional.of(this.parent);
  }

  public void clearParent() {
    this.parent = null;
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
