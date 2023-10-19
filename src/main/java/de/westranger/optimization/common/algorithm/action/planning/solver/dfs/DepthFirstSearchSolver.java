package de.westranger.optimization.common.algorithm.action.planning.solver.dfs;

import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.ActionPlanningSolution;
import de.westranger.optimization.common.algorithm.action.planning.ActionPlanningSolver;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;

import java.util.*;

public class DepthFirstSearchSolver<T extends Comparable<T>, G extends Comparable<G>>
        implements ActionPlanningSolver<T, G> {

    private SearchSpaceState<T, G> initialState;
    private final boolean useBranchAndBound;
    private final Map<Integer, G> uppperBounds;

    public DepthFirstSearchSolver(final boolean useBranchAndBound) {
        this.useBranchAndBound = useBranchAndBound;
        this.uppperBounds = new TreeMap<>();
    }

    @Override
    public void setInitialState(final SearchSpaceState<T, G> sss) {
        this.initialState = sss;
    }

    @Override
    public Optional<List<ActionPlanningSolution<T, G>>> solve() {
        TreeNode<T, G> currentNode = new TreeNode<>(this.initialState, 0);
        Optional<TreeNode<T, G>> child;

        List<ActionPlanningSolution<T, G>> result = new LinkedList<>();

        //int depthCounter = 0;
        boolean expanded;
        do {

            child = currentNode.expandNext();
            expanded = child.isPresent();

            final G currentScore = currentNode.getState().getScore();

            if (!expanded) {
                if (currentNode.getState().isGoalState()) {
                    // we have a goal state

                    if (!this.uppperBounds.isEmpty()) {
                        final long scoreDiff = currentScore.compareTo(this.uppperBounds.get(currentNode.getLevel()));

                        if (scoreDiff < 0) { // we found a better solution
                            result.clear();
                            this.updateUpperBounds(currentNode);
                        }

                        if (scoreDiff <= 0) { // we want to store the better or equal scored solution
                            final ActionPlanningSolution<T, G> solution =
                                    new ActionPlanningSolution<>(currentNode.getState(),
                                            computeActionList(currentNode), currentScore);
                            result.add(solution);
                        }
                    } else {
                        this.updateUpperBounds(currentNode);
                        final ActionPlanningSolution<T, G> solution =
                                new ActionPlanningSolution<>(currentNode.getState(),
                                        computeActionList(currentNode), currentScore);
                        result.add(solution);
                    }
                }
                // we still did not expand, so we need to find a parent node which can be expanded
                if (currentNode.getParent().isEmpty()) {
                    continue;
                }

                child = currentNode.getParent();
                currentNode.clearParent();
                expanded = true;
            } else {
                if (useBranchAndBound && !this.uppperBounds.isEmpty()) {
                    final long scoreDiff = currentScore.compareTo(this.uppperBounds.get(currentNode.getLevel()));
                    if (scoreDiff > 0 && currentNode.getParent().isPresent()) {
                        child = currentNode.getParent();
                        currentNode.clearParent(); // unlink parent to enabled cleanup for GC
                    }
                }
            }
            currentNode = child.get();
        } while (expanded);

        return Optional.of(Collections.unmodifiableList(result));
    }

    private List<Action> computeActionList(final TreeNode<T, G> node) {
        final LinkedList<Action> result = new LinkedList<>();
        Optional<TreeNode<T, G>> currentNode = Optional.of(node);
        while (currentNode.isPresent()) {
            if (currentNode.get().getCurrentAction().isEmpty()) {
                if (!result.isEmpty()) {
                    throw new IllegalStateException(
                            "there is no action available in the current state, this should not be possible");
                }
            } else {
                result.addFirst(currentNode.get().getCurrentAction().get());
            }
            currentNode = currentNode.get().getParent();
        }

        return Collections.unmodifiableList(result);
    }

    private void updateUpperBounds(final TreeNode<T, G> node) {
        Optional<TreeNode<T, G>> currentNode = Optional.of(node);
        while (currentNode.isPresent()) {
            this.uppperBounds.put(currentNode.get().getLevel(), currentNode.get().getState().getScore());
            currentNode = currentNode.get().getParent();
        }
    }

}
