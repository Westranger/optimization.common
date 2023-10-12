package de.westranger.optimization.common.optimization.stochastic;

import de.westranger.optimization.common.optimization.Solution;

public interface NeighbourSelector {
    Solution computeNeighbour(final Solution intermediateSolution, final int minTemp, final int amxTemp);
}
