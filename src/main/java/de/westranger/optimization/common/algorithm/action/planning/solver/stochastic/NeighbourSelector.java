package de.westranger.optimization.common.algorithm.action.planning.solver.stochastic;

import de.westranger.optimization.common.algorithm.util.Solution;

public interface NeighbourSelector {
    Solution computeNeighbour(final Solution intermediateSolution, final int minTemp, final int amxTemp);
}
