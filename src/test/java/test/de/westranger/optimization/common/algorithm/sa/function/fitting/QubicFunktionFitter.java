package test.de.westranger.optimization.common.algorithm.sa.function.fitting;

import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.Score;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class QubicFunktionFitter extends SearchSpaceState {

  private QubicFunktion func;

  private double score;

  private QubicFittingAction lastPerformedAction;

  private List<DataPoint> data;

  public QubicFunktionFitter(final QubicFunktion targetFunction, final List<DataPoint> data) {
    this(targetFunction, Double.POSITIVE_INFINITY, data);
  }

  private QubicFunktionFitter(final QubicFunktion targetFunction, final double score,
                              final List<DataPoint> data) {
    this.func = targetFunction;
    this.score = score;
    this.lastPerformedAction = null;
    this.data = data;
  }


  @Override
  public List<Action> getPossibleActions() {
    final List<Action> result = new ArrayList<>(4);
    result.add(new QubicFittingAction(this.func.getA1(),
        QubicFittingAction.FunctionParameter.ParamA1));
    result.add(new QubicFittingAction(this.func.getA2(),
        QubicFittingAction.FunctionParameter.ParamA2));
    result.add(new QubicFittingAction(this.func.getA3(),
        QubicFittingAction.FunctionParameter.ParamA3));
    result.add(new QubicFittingAction(this.func.getConstant(),
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
        this.func.setA1(qfa.getValue());
      }
      case ParamA2 -> {
        this.func.setA2(qfa.getValue());
      }
      case ParamA3 -> {
        this.func.setA3(qfa.getValue());
      }
      case ParamConstant -> {
        this.func.setConstant(qfa.getValue());
      }
    }

    double mse = 0.0;
    for (DataPoint dp : this.data) {
      mse += Math.pow(dp.getY() - this.func.evaluate(dp.getX()), 2.0);
    }
    mse *= 1.0 / (double) this.data.size();

    this.score = mse;
    return true;
  }

  @Override
  public Score getScore() {
    return new DoubleScore(this.score);
  }

  @Override
  public SearchSpaceState clone() {
    return new QubicFunktionFitter(
        new QubicFunktion(this.func.getA1(), this.func.getA2(), this.func.getA3(),
            this.func.getConstant()), this.score, this.data);
  }

  @Override
  public Optional<Action> getLastPerformedAction() {
    if (this.lastPerformedAction != null) {
      return Optional.of(this.lastPerformedAction);
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
