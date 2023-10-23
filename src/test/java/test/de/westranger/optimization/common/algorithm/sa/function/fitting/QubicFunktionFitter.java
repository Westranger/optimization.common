package test.de.westranger.optimization.common.algorithm.sa.function.fitting;

import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.Score;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class QubicFunktion extends SearchSpaceState {

  private double a1;
  private double a2;
  private double a3;
  private double constant;

  private double score;

  private QubicFittingAction lastPerformedAction;

  public QubicFunktion(final double a1, final double a2, final double a3, final double constant) {
    this(a1, a2, a3, constant, Double.POSITIVE_INFINITY);
  }

  public QubicFunktion() {
    this(0.0, 0.0, 0.0, 0.0, Double.POSITIVE_INFINITY);
  }

  private QubicFunktion(final double a1, final double a2, final double a3, final double constant,
                        final double score) {
    this.a1 = a1;
    this.a2 = a2;
    this.a3 = a3;
    this.constant = constant;
    this.score = score;
    lastPerformedAction = null;
  }

  private double qubic(final double x, final double a1, final double a2, final double a3,
                       final double c) {
    return a1 * Math.pow(x, 3.0) + a2 * Math.pow(x, 2.0) + a3 * x + c;
  }

  @Override
  public List<Action> getPossibleActions() {
    final List<Action> result = new ArrayList<>(4);
    result.add(
        (Action) new QubicFittingAction(this.a1, QubicFittingAction.FunctionParameter.ParamA1));
    result.add(
        (Action) new QubicFittingAction(this.a2, QubicFittingAction.FunctionParameter.ParamA2));
    result.add(
        (Action) new QubicFittingAction(this.a3, QubicFittingAction.FunctionParameter.ParamA3));
    result.add(
        (Action) new QubicFittingAction(this.constant,
            QubicFittingAction.FunctionParameter.ParamConstant));
    return Collections.unmodifiableList(result);
  }

  @Override
  public boolean perform(Action action) {
    if (!(action instanceof QubicFittingAction)) {
      throw new IllegalArgumentException("action must be of type QubicFittingAction");
    }
    final QubicFittingAction qfa = (QubicFittingAction) action;

    switch (qfa.getFp()) {
      case ParamA1 -> {
        this.a1 = qfa.getValue();
      }
      case ParamA2 -> {
        this.a2 = qfa.getValue();
      }
      case ParamA3 -> {
        this.a3 = qfa.getValue();
      }
      case ParamConstant -> {
        this.constant = qfa.getValue();
      }
    }

    double err = 0.0;
    int cnt = 0;
    for (int x = -10; x <= 10; x++) {
      err += Math.pow(
          qubic(x, 3.0, 1.5, 9.0, 0.25) - qubic(x, this.a1, this.a2, this.a3, this.constant), 2.0);
      cnt++;
    }

    err *= 1.0 / (double) cnt;

    this.score = err;
    return true;
  }

  @Override
  public Score getScore() {
    return new DoubleScore(this.score);
  }

  @Override
  public SearchSpaceState clone() {
    return new QubicFunktion(this.a1, this.a2, this.a3, this.constant, this.score);
  }

  @Override
  public Optional<Action> getLastPerformedAction() {
    if (this.lastPerformedAction != null) {
      return Optional.of((Action) this.lastPerformedAction);
    }
    return Optional.empty();
  }

  @Override
  public boolean isGoalState() {
    return false;
  }

  @Override
  public int compareTo(SearchSpaceState searchSpaceState) {
    return (new DoubleScore(this.score)).compareTo(searchSpaceState.getScore());
  }
}
