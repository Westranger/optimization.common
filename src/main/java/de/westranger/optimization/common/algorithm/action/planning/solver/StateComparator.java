package de.westranger.optimization.common.algorithm.action.planning.solver;

import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;

import java.io.Serializable;
import java.util.Comparator;

public final class StateComparator<T extends Comparable<T>, G extends Comparable<G>> implements Comparator<SearchSpaceState<T, G>>, Serializable {
    @Override
    public int compare(SearchSpaceState<T, G> o1, SearchSpaceState<T, G> o2) {
        return o1.compareTo(o2);
    }
}
