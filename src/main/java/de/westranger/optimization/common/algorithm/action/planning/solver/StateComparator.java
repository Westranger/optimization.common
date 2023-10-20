package de.westranger.optimization.common.algorithm.action.planning.solver;

import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;

import java.io.Serializable;
import java.util.Comparator;

public final class StateComparator<S extends Comparable<S>> implements Comparator<SearchSpaceState<S>>, Serializable {
    @Override
    public int compare(SearchSpaceState<S> o1, SearchSpaceState<S> o2) {
        return o1.compareTo(o2);
    }
}
