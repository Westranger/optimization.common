package de.westranger.optimization.common.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CombinationSearcherTest {

  @Test
  void computeNextParameterList() {

    LinkedHashMap<String, List<Double>> values = new LinkedHashMap();
    values.put("a", Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
    values.put("b", Arrays.asList(1.1, 2.1, 3.1, 4.1, 5.1, 6.1, 7.1));
    values.put("c", Arrays.asList(1.05, 2.05, 3.05));

    Map<String, Integer> initIdx = Map.of("a", 0, "b", 0, "c", 0);

    CombinationSearcher cs =
        new CombinationSearcher(values, initIdx, Double.POSITIVE_INFINITY, Integer.MAX_VALUE,5);


    while (cs.computeNextParameterList()) {
      Map<String, Map<String, Double>> map = cs.getCandidates(3);
      for (Map.Entry<String, Map<String, Double>> mapEntry : map.entrySet()) {
        double sum = 0.0;
        for (Map.Entry<String, Double> tmp : mapEntry.getValue().entrySet()) {
          sum += tmp.getValue();
        }
        cs.provideScore(mapEntry.getKey(), sum, 1);
      }
    }
    System.out.println("done");
  }
}