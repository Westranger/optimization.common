package test.de.westranger.optimization.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.westranger.optimization.common.util.CombinationIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class CombinationIteratorTest {
  @Test
  public void testPermutations() {
    LinkedHashMap<String, List<Double>> input = new LinkedHashMap<>();
    input.put("A", Arrays.asList(1.0, 2.0, 3.0));
    input.put("B", Arrays.asList(1.0, 2.0, 3.0, 4.0));
    input.put("C", Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));

    CombinationIterator iterator = new CombinationIterator(input);

    List<Map<String, Double>> expectedPermutations = new ArrayList<>();
    for (double a : input.get("A")) {
      for (double b : input.get("B")) {
        for (double c : input.get("C")) {
          Map<String, Double> permutation = new HashMap<>();
          permutation.put("A", a);
          permutation.put("B", b);
          permutation.put("C", c);
          expectedPermutations.add(permutation);
        }
      }
    }

    for (Map<String, Double> expected : expectedPermutations) {
      assertTrue(iterator.hasNext());
      Map<String, Double> result = iterator.next();
      assertEquals(expected, result);
    }

    assertFalse(iterator.hasNext());
  }
}
