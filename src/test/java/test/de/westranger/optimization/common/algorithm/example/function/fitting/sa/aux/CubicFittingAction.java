package test.de.westranger.optimization.common.algorithm.example.function.fitting.sa.aux;


import de.westranger.optimization.common.algorithm.action.planning.Action;

public record CubicFittingAction(double value, FunctionParameter fp)
    implements Action {
  public enum FunctionParameter {
    ParamA1, ParamA2, ParamA3, ParamConstant
  }

}
