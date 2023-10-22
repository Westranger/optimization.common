package de.westranger.optimization.common.algorithm.action.planning.solver.dfs;

import java.util.Comparator;

public final class TreeNodeComparator<S extends Comparable<S>> implements Comparator<TreeNode<S>> {
    @Override
    public int compare(final TreeNode<S> o1, final TreeNode<S> o2) {
        final int lvlDiff = o2.getLevel() - o1.getLevel();
        final int scoreDiff = o1.getState().getScore().compareTo(o2.getState().getScore());
        return lvlDiff * 10 + scoreDiff;
    }
}
