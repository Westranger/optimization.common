package test.de.westranger.optimization.common.util;

import de.westranger.optimization.common.util.PermutationIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PermutationIteratorTest {

  @Test
  void testPermutation3elemLen2() {
    List<String> elements = Arrays.asList("a", "b", "c");
    int permutationLength = 2;
    PermutationIterator<String> iterator = new PermutationIterator<>(elements, permutationLength);

    List<List<String>> expectedPermutations = new ArrayList<>();
    expectedPermutations.add(List.of("a", "b"));
    expectedPermutations.add(List.of("a", "c"));
    expectedPermutations.add(List.of("b", "a"));
    expectedPermutations.add(List.of("b", "c"));
    expectedPermutations.add(List.of("c", "a"));
    expectedPermutations.add(List.of("c", "b"));

    List<List<String>> actualPermutations = new ArrayList<>();

    while (iterator.hasNext()) {
      actualPermutations.add(iterator.next());
    }

    Assertions.assertEquals(expectedPermutations, actualPermutations);
  }

  @Test
  void testPermutation3elemLen3() {
    List<String> elements = Arrays.asList("a", "b", "c");
    int permutationLength = 3;
    PermutationIterator<String> iterator = new PermutationIterator<>(elements, permutationLength);

    List<List<String>> expectedPermutations = new ArrayList<>();
    expectedPermutations.add(List.of("a", "b", "c"));
    expectedPermutations.add(List.of("a", "c", "b"));
    expectedPermutations.add(List.of("b", "a", "c"));
    expectedPermutations.add(List.of("b", "c", "a"));
    expectedPermutations.add(List.of("c", "a", "b"));
    expectedPermutations.add(List.of("c", "b", "a"));

    List<List<String>> actualPermutations = new ArrayList<>();

    while (iterator.hasNext()) {
      actualPermutations.add(iterator.next());
    }

    Assertions.assertEquals(expectedPermutations, actualPermutations);
  }

  @Test
  void testPermutation4elemLen3() {
    List<String> elements = Arrays.asList("a", "b", "c", "d");
    int permutationLength = 3;
    PermutationIterator<String> iterator = new PermutationIterator<>(elements, permutationLength);

    List<List<String>> expectedPermutations = new ArrayList<>();
    expectedPermutations.add(List.of("a", "b", "c"));
    expectedPermutations.add(List.of("a", "b", "d"));
    expectedPermutations.add(List.of("a", "c", "b"));
    expectedPermutations.add(List.of("a", "c", "d"));
    expectedPermutations.add(List.of("a", "d", "b"));
    expectedPermutations.add(List.of("a", "d", "c"));
    expectedPermutations.add(List.of("b", "a", "c"));
    expectedPermutations.add(List.of("b", "a", "d"));
    expectedPermutations.add(List.of("b", "c", "a"));
    expectedPermutations.add(List.of("b", "c", "d"));
    expectedPermutations.add(List.of("b", "d", "a"));
    expectedPermutations.add(List.of("b", "d", "c"));
    expectedPermutations.add(List.of("c", "a", "b"));
    expectedPermutations.add(List.of("c", "a", "d"));
    expectedPermutations.add(List.of("c", "b", "a"));
    expectedPermutations.add(List.of("c", "b", "d"));
    expectedPermutations.add(List.of("c", "d", "a"));
    expectedPermutations.add(List.of("c", "d", "b"));
    expectedPermutations.add(List.of("d", "a", "b"));
    expectedPermutations.add(List.of("d", "a", "c"));
    expectedPermutations.add(List.of("d", "b", "a"));
    expectedPermutations.add(List.of("d", "b", "c"));
    expectedPermutations.add(List.of("d", "c", "a"));
    expectedPermutations.add(List.of("d", "c", "b"));

    List<List<String>> actualPermutations = new ArrayList<>();

    while (iterator.hasNext()) {
      actualPermutations.add(iterator.next());
    }

    Assertions.assertEquals(expectedPermutations, actualPermutations);
  }
}
















