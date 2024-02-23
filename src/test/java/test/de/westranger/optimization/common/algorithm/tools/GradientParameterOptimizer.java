package test.de.westranger.optimization.common.algorithm.tools;

import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealing;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealingParameter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public final class GradientParameterOptimizer {

  private final Map<String, List<Double>> listOfParameters;

  public GradientParameterOptimizer(Map<String, List<Double>> listOfParameters) {
    this.listOfParameters = listOfParameters;
  }

  public SimulatedAnnealingParameter optimize(SimulatedAnnealing sa) {
    Map<String, Double> currentParameter = new TreeMap<>();
    Map<String, Integer> nextParamIndices = new TreeMap<>();
    Map<String, Integer> paramBitMask = new TreeMap<>();
    Map<String, Integer> currentBestIndices = new TreeMap<>();

    if (listOfParameters.size() > 32) {
      throw new IllegalStateException("cannot evaluate more than 32 different parameters");
    }

    int bitmask = 1;
    for (Map.Entry<String, List<Double>> entry : listOfParameters.entrySet()) {
      int idx = entry.getValue().size() / 2;
      //currentParamIndices.put(entry.getKey(), 0);
      //currentParamIndices.put(entry.getKey(), 0);
    }


    return null;
  }

  private Optional<Double> getConsecutiveNeighbour(final String key, final double value) {
    final List<Double> values = this.listOfParameters.get(key);

    for (int i = 0; i < values.size(); i++) {
      if (Double.compare(values.get(i), value) == 0) {
        if (i + 1 < values.size()) {
          return Optional.of(values.get(i + 1));
        }
      }
    }
    return Optional.empty();
  }

}
