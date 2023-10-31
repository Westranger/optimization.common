package de.westranger.optimization.common.algorithm.action.planning;

import de.westranger.optimization.common.algorithm.action.planning.solver.Score;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class ActionPlanningSolution {

  private final SearchSpaceState solutionState;
  private final List<Action> actions;
  private final Score score;

  public ActionPlanningSolution(final SearchSpaceState solutionState,
                                final List<Action> actions, final Score score) {
    this.solutionState = solutionState;
    this.actions = new LinkedList<>(actions);
    this.score = score;
  }

  public List<Action> getActions() {
    return Collections.unmodifiableList(this.actions);
  }

  public SearchSpaceState getState() {
    return this.solutionState;
  }

  public Score getScore() {
    return this.score;
  }

}
