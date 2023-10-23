package de.westranger.optimization.common.algorithm.action.planning.solver;

import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;

import java.io.Serializable;
import java.util.Comparator;

public final class StateComparator implements Comparator<SearchSpaceState>, Serializable {
  @Override
  public int compare(SearchSpaceState o1, SearchSpaceState o2) {
    return o1.compareTo(o2);
  }
}
