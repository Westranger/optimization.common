package de.westranger.optimization.common.algorithm.action.planning;

import java.util.List;
import java.util.Optional;

public interface ActionPlanningSolver<T extends Comparable<T>, G extends Comparable<G>> {

  void setInitialState(SearchSpaceState<T, G> sss);

  Optional<List<ActionPlanningSolution<T, G>>> solve();

}
