package de.westranger.optimization.common.algorithm.action.planning.solver.stochastic;

public record SimulatedAnnealingParameter(double tMax, double tMin, double gamma,
                                          double omegaMax,
                                          double maxImprovementPerTemperature,
                                          double initialAcceptanceRatio) {
}
