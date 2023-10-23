package de.westranger.optimization.common.algorithm.action.planning.solver.dfs;

import java.io.Serializable;
import java.util.Comparator;

public final class TreeNodeComparator implements Comparator<TreeNode>, Serializable {
  @Override
  public int compare(final TreeNode o1, final TreeNode o2) {
    final int lvlDiff = o2.getLevel() - o1.getLevel();
    final int scoreDiff = o1.getState().getScore().compareTo(o2.getState().getScore());
    return lvlDiff * 10 + scoreDiff;
  }
}
