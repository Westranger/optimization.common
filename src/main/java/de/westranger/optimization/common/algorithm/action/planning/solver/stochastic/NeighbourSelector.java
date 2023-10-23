package de.westranger.optimization.common.algorithm.action.planning.solver.stochastic;

import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;

public interface NeighbourSelector {
  SearchSpaceState select(final SearchSpaceState intermediateSolution,
                             final double currentTemperature);
}
