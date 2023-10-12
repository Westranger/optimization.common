package de.westranger.optimization.common.optimization;

public interface NeighbourSelector {
    Solution computeNeighbour(final Solution intermediateSolution, final int minTemp, final int amxTemp);
}
