package de.westranger.optimization.common.algorithm.action.planning;

import java.util.List;
import java.util.Optional;

public interface ActionPlanningSolver<S extends Comparable<S>> {

  void setInitialState(SearchSpaceState<S> sss);

  Optional<List<ActionPlanningSolution<S>>> solve();

}
