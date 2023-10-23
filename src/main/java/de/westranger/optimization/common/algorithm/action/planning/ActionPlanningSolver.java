package de.westranger.optimization.common.algorithm.action.planning;

import java.util.List;
import java.util.Optional;

public interface ActionPlanningSolver {

  void setInitialState(SearchSpaceState sss);

  Optional<List<ActionPlanningSolution>> solve();

}
