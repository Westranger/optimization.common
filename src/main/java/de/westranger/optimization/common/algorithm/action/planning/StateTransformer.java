package de.westranger.optimization.common.algorithm.action.planning;

public interface StateTransformer<S extends Comparable<S>, R extends Comparable<R>> {
    R fromState(SearchSpaceState<S> sss);

    SearchSpaceState<S> fromRepresentation(R repr);
}
