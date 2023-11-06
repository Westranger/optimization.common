package test.de.westranger.optimization.common.algorithm.example.function.fitting.common;

import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.Score;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class CubicFunktionFitter extends SearchSpaceState {

  private final CubicFunktion func;

  private double score;

  private final CubicFittingAction lastPerformedAction;

  private final List<DataPoint> data;

  public CubicFunktionFitter(final CubicFunktion targetFunction, final List<DataPoint> data) {
    this(targetFunction, Double.POSITIVE_INFINITY, data);
    this.score = computeMSE();
  }

  private CubicFunktionFitter(final CubicFunktion targetFunction, final double score,
                              final List<DataPoint> data) {
    this.func = targetFunction;
    this.score = score;
    this.lastPerformedAction = null;
    this.data = data;
  }


  @Override
  public List<Action> getPossibleActions() {
    final List<Action> result = new ArrayList<>(4);
    result.add(new CubicFittingAction(this.func.getA1(),
        CubicFittingAction.FunctionParameter.ParamA1));
    result.add(new CubicFittingAction(this.func.getA2(),
        CubicFittingAction.FunctionParameter.ParamA2));
    result.add(new CubicFittingAction(this.func.getA3(),
        CubicFittingAction.FunctionParameter.ParamA3));
    result.add(new CubicFittingAction(this.func.getConstant(),
        CubicFittingAction.FunctionParameter.ParamConstant));
    return Collections.unmodifiableList(result);
  }

  @Override
  public boolean perform(Action action) {
    if (!(action instanceof final CubicFittingAction qfa)) {
      throw new IllegalArgumentException("action must be of type CubicFittingAction");
    }

    switch (qfa.fp()) {
      case ParamA1 -> this.func.setA1(qfa.value());
      case ParamA2 -> this.func.setA2(qfa.value());
      case ParamA3 -> this.func.setA3(qfa.value());
      case ParamConstant -> this.func.setConstant(qfa.value());
    }

    this.score = computeMSE();
    return true;
  }

  @Override
  public Score getScore() {
    return new DoubleScore(this.score);
  }

  @Override
  public SearchSpaceState clone() {
    return new CubicFunktionFitter(
        new CubicFunktion(this.func.getA1(), this.func.getA2(), this.func.getA3(),
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

  @Override
  public String toSVG() {
    return null;
  }

  private double computeMSE() {
    double mse = 0.0;
    for (DataPoint dp : this.data) {
      mse += Math.pow(dp.y() - this.func.evaluate(dp.x()), 2.0);
    }
    mse *= 1.0 / (double) this.data.size();

    return mse;
  }
}
