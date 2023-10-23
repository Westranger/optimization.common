package de.westranger.optimization.common.algorithm.action.planning.solver.stochastic;

public class SimulatedAnnealingParameter {
  private final double tMax;// = 100000.0;
  private final double tMin;// = 0.1;
  private final double gamma;// = 0.95;
  private final double omegaMax;// = 200;

  public SimulatedAnnealingParameter(final double tMax, final double tMin, final double gamma,
                                     final double omegaMax) {
    this.tMax = tMax;
    this.tMin = tMin;
    this.gamma = gamma;
    this.omegaMax = omegaMax;
  }

  public double getTMax() {
    return tMax;
  }

  public double getTMin() {
    return tMin;
  }

  public double getGamma() {
    return gamma;
  }

  public double getOmegaMax() {
    return omegaMax;
  }
}
