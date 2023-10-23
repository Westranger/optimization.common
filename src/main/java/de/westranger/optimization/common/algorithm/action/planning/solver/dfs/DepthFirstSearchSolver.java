package de.westranger.optimization.common.algorithm.action.planning.solver.dfs;

import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.ActionPlanningSolution;
import de.westranger.optimization.common.algorithm.action.planning.ActionPlanningSolver;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;

import de.westranger.optimization.common.algorithm.action.planning.solver.Score;
import java.util.*;

public final class DepthFirstSearchSolver
    implements ActionPlanningSolver {

  private SearchSpaceState initialState;
  private final boolean useBranchAndBound;
  private Optional<Score> uppperBound;

  public DepthFirstSearchSolver(final boolean useBranchAndBound) {
    this.useBranchAndBound = useBranchAndBound;
    this.uppperBound = Optional.empty();
  }

  @Override
  public void setInitialState(final SearchSpaceState sss) {
    this.initialState = sss;
  }

  @Override
  public Optional<List<ActionPlanningSolution>> solve() {
    List<ActionPlanningSolution> result = new LinkedList<>();
    LinkedList<TreeNode> candidates = new LinkedList<>();
    candidates.offer(new TreeNode(this.initialState, 0));

    while (!candidates.isEmpty()) {
      final TreeNode currentNode = candidates.poll();
      final Score currentScore = currentNode.getState().getScore();

      if (useBranchAndBound && this.uppperBound.isPresent()) {
        final long scoreDiff = currentScore.compareTo(this.uppperBound.get());
        if (scoreDiff >= 0) {
          currentNode.clearParent(); // unlink parent to enabled cleanup for GC
          continue;
        }
      }

      if (currentNode.getState().isGoalState()) {
        // we have a goal state

        if (!this.uppperBound.isEmpty()) {
          final long scoreDiff = currentScore.compareTo(this.uppperBound.get());

          if (scoreDiff < 0) { // we found a better solution
            result.clear();
            this.uppperBound = Optional.of(currentNode.getState().getScore());
          }

          if (scoreDiff < 0) { // we want to store the better or equal scored solution
            final ActionPlanningSolution solution =
                new ActionPlanningSolution(currentNode.getState(),
                    computeActionList(currentNode), currentScore);
            System.out.println("found a besser solution " + currentNode.getState().getScore());
            result.add(solution);
          }
        } else {
          this.uppperBound = Optional.of(currentNode.getState().getScore());
          System.out.println("found a initial solution " + currentNode.getState().getScore());
          final ActionPlanningSolution solution =
              new ActionPlanningSolution(currentNode.getState(),
                  computeActionList(currentNode), currentScore);
          result.add(solution);
        }
      } else {
        boolean expanded = true;
        while (expanded) {
          final Optional<TreeNode> child = currentNode.expandNext();
          expanded = child.isPresent();
          if (expanded) {
            candidates.offer(child.get());
          }
        }

        Collections.sort(candidates, new TreeNodeComparator());
      }
    }

    return Optional.of(Collections.unmodifiableList(result));
  }

  private List<Action> computeActionList(final TreeNode node) {
    final LinkedList<Action> result = new LinkedList<>();
    Optional<TreeNode> currentNode = Optional.of(node);
    while (currentNode.isPresent()) {
      if (currentNode.get().getState().getLastPerformedAction().isEmpty()) {
        if (!result.isEmpty() && currentNode.get().getParent().isPresent()) {
          throw new IllegalStateException(
              "there is no action available in the current state, this should not be possible");
        }
      } else {
        result.addFirst(currentNode.get().getState().getLastPerformedAction().get());
      }
      currentNode = currentNode.get().getParent();
    }

    return Collections.unmodifiableList(result);
  }

}
