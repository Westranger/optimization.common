package test.de.westranger.optimization.common.util;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.util.PermutationIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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


  private double optMaster = Double.NEGATIVE_INFINITY;

  //@Test
  void testConstructTSPProblem() {
    List<Point2D> elements = new ArrayList<>();
    for (int x = -3; x <= 3; x++) {
      for (int y = 2; y <= 10; y++) {
        elements.add(new Point2D(x, y));
      }
    }

    Point2D start = new Point2D(0.0, 0.0);

    for (int a = 0; a < elements.size(); a++) {
      for (int b = a + 1; b < elements.size(); b++) {
        for (int c = b + 1; c < elements.size(); c++) {
          for (int d = c + 1; d < elements.size(); d++) {
            for (int e = d + 1; e < elements.size(); e++) {
              for (int f = e + 1; f < elements.size(); f++) {
                List<Point2D> candidates =
                    List.of(elements.get(a), elements.get(b), elements.get(c), elements.get(d),
                        elements.get(e), elements.get(f));
                extracted(start, candidates);

              }
            }
          }
        }
      }
    }

  }

  private void extracted(Point2D start, List<Point2D> candidates) {
    List<Point2D> tmp = new ArrayList<>(candidates);
    // greedy
    Point2D lastPt = start;
    double greedySum = 0.0;
    while (!tmp.isEmpty()) {
      int idx = 0;
      double min = Double.POSITIVE_INFINITY;
      for (int i = 0; i < tmp.size(); i++) {
        double dst = lastPt.distance(tmp.get(i));
        if (dst < min) {
          idx = i;
          min = dst;
        }
      }
      greedySum += min;
      Point2D closest = tmp.remove(idx);
      lastPt = closest;
    }

    // now optimal
    PermutationIterator<Point2D> optIter =
        new PermutationIterator<>(candidates, candidates.size());

    double opt = Double.POSITIVE_INFINITY;
    Optional<List<Point2D>> optLst = Optional.empty();
    while (optIter.hasNext()) {
      List<Point2D> perm = optIter.next();

      double dst = start.distance(perm.get(0));
      for (int a = 1; a < perm.size(); a++) {
        dst += perm.get(a - 1).distance(perm.get(a));
      }

      if (dst < opt) {
        opt = dst;
        optLst = Optional.of(perm);
      }
    }

    if (opt < greedySum && optMaster < greedySum - opt) {
      optMaster = greedySum - opt;

      System.out.println(
          "found one " + optLst.get() + " " + greedySum + " " + opt + " " + (greedySum - opt));
    }
  }


}
















