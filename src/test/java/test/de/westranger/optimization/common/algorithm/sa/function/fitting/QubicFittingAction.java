package test.de.westranger.optimization.common.algorithm.sa.function.fitting;


import de.westranger.optimization.common.algorithm.action.planning.Action;

public class QubicFittingAction implements Action {
  public enum FunctionParameter {
    ParamA1, ParamA2, ParamA3, ParamConstant;
  }

  private final double value;
  private final FunctionParameter fp;

  public QubicFittingAction(double value, FunctionParameter fp) {
    this.value = value;
    this.fp = fp;
  }

  public double getValue() {
    return value;
  }

  public FunctionParameter getFp() {
    return fp;
  }
}
