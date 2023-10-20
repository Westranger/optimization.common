package de.westranger.optimization.common.algorithm.action.planning.solver.dfs;

import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.ActionPlanningSolution;
import de.westranger.optimization.common.algorithm.action.planning.ActionPlanningSolver;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;

import java.util.*;

public final class DepthFirstSearchSolver<S extends Comparable<S>>
        implements ActionPlanningSolver<S> {

    private SearchSpaceState<S> initialState;
    private final boolean useBranchAndBound;
    private final Map<Integer, S> uppperBounds;

    public DepthFirstSearchSolver(final boolean useBranchAndBound) {
        this.useBranchAndBound = useBranchAndBound;
        this.uppperBounds = new TreeMap<>();
    }

    @Override
    public void setInitialState(final SearchSpaceState<S> sss) {
        this.initialState = sss;
    }

    @Override
    public Optional<List<ActionPlanningSolution<S>>> solve() {
        List<ActionPlanningSolution<S>> result = new LinkedList<>();
        PriorityQueue<TreeNode<S>> candidates = new PriorityQueue<>(new TreeNodeComparator<>());
        candidates.offer(new TreeNode<>(this.initialState, 0));

        while (!candidates.isEmpty()) {
            final TreeNode<S> currentNode = candidates.poll();

            boolean expanded = false;
            final Optional<TreeNode<S>> child = currentNode.expandNext();
            if (child.isPresent()) {
                expanded = child.isPresent();
                candidates.offer(child.get());
            }

            final S currentScore = currentNode.getState().getScore();

            if (!expanded) {
                if (currentNode.getState().isGoalState()) {
                    // we have a goal state

                    if (!this.uppperBounds.isEmpty()) {
                        final long scoreDiff = currentScore.compareTo(this.uppperBounds.get(currentNode.getLevel()));

                        if (scoreDiff < 0) { // we found a better solution
                            result.clear();
                            this.updateUpperBounds(currentNode);
                        }

                        if (scoreDiff < 0) { // we want to store the better or equal scored solution
                            final ActionPlanningSolution<S> solution =
                                    new ActionPlanningSolution<>(currentNode.getState(),
                                            computeActionList(currentNode), currentScore);
                            System.out.println("found a besser solution " + currentNode.getState().getScore());
                            result.add(solution);
                        }
                    } else {
                        this.updateUpperBounds(currentNode);
                        System.out.println("found a besser solution " + currentNode.getState().getScore());
                        final ActionPlanningSolution<S> solution =
                                new ActionPlanningSolution<>(currentNode.getState(),
                                        computeActionList(currentNode), currentScore);
                        result.add(solution);
                    }
                }
                // we still did not expand, so we need to find a parent node which can be expanded
                if (currentNode.getParent().isEmpty()) {
                    continue;
                }

                if(currentNode.getParent().isPresent()){
                    candidates.offer(currentNode.getParent().get());
                }

                currentNode.clearParent();
                expanded = true;
            } else {
                if (useBranchAndBound && !this.uppperBounds.isEmpty()) {
                    final long scoreDiff = currentScore.compareTo(this.uppperBounds.get(currentNode.getLevel()));
                    if (scoreDiff > 0 && currentNode.getParent().isPresent()) {
                        if(currentNode.getParent().isPresent()){
                            candidates.offer(currentNode.getParent().get());
                        }
                        currentNode.clearParent(); // unlink parent to enabled cleanup for GC
                    }
                }
            }
        }

        return Optional.of(Collections.unmodifiableList(result));
    }

    private List<Action> computeActionList(final TreeNode<S> node) {
        final LinkedList<Action> result = new LinkedList<>();
        Optional<TreeNode<S>> currentNode = Optional.of(node);
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

    private void updateUpperBounds(final TreeNode<S> node) {
        Optional<TreeNode<S>> currentNode = Optional.of(node);
        while (currentNode.isPresent()) {
            this.uppperBounds.put(currentNode.get().getLevel(), currentNode.get().getState().getScore());
            currentNode = currentNode.get().getParent();
        }
    }

}
