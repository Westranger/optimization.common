package test.de.westranger.optimization.common.util;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.util.PermutationIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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

  @Test
  void testConstructTSPProblem() {
    int idCnt = 1;
    List<Order> elements = new ArrayList<>();
    for (int x = -3; x <= 3; x++) {
      for (int y = 2; y <= 10; y++) {
        Order order = new Order(idCnt++, new Point2D(x, y), null);
        elements.add(order);
      }
    }

    Collections.shuffle(elements, new Random(47110815L));

    Point2D start = new Point2D(0.0, 0.0);

    int[] priorities = new int[] {1, 1, 2, 2, 3, 3};

    for (int a = 0; a < elements.size(); a++) {
      for (int b = a + 1; b < elements.size(); b++) {
        for (int c = b + 1; c < elements.size(); c++) {
          for (int d = c + 1; d < elements.size(); d++) {
            for (int e = d + 1; e < elements.size(); e++) {
              for (int f = e + 1; f < elements.size(); f++) {
                //for (int g = f + 1; g < elements.size(); g++) {
                //for (int h = g + 1; h < elements.size(); h++) {

                List<Order> candidates =
                    List.of(elements.get(a), elements.get(b), elements.get(c), elements.get(d),
                        elements.get(e), elements.get(f)/*, elements.get(g), elements.get(h)*/);
                extracted(start, candidates, priorities);
                //}
                //}
              }
            }
          }
        }
      }
    }

  }

  private void extracted(Point2D start, List<Order> candidates, int[] priorities) {
    // compute shortest path
    List<Order> shortestPath = computeShortestPath(start, candidates);

    long[] dueTimes = new long[priorities.length];

    double travelTime = start.distance(shortestPath.get(0).getTo());
    for (int i = 1; i < shortestPath.size(); i++) {
      travelTime += shortestPath.get(i).getTo().distance(shortestPath.get(i - 1).getTo());
      if (i % 2 == 1) {
        dueTimes[i] = (long) (travelTime * 1000);
        dueTimes[i - 1] = dueTimes[i] + 1;
      }
    }

    List<Order> newOrders = new ArrayList<>(candidates.size());
    for (int i = 0; i < candidates.size(); i++) {
      Order oOld = shortestPath.get(i);
      Order oNew =
          new Order(oOld.getId(), oOld.getTo(), null, priorities[i], 0, dueTimes[i]);
      newOrders.add(oNew);
    }

    ProblemFinderScore optimalScore = computeScore(start, newOrders, priorities);

    final List<Order> greedyPathSolution = findGreedyPathSolution(start, newOrders);
    ProblemFinderScore greedyPathScore = computeScore(start, greedyPathSolution, priorities);

    final List<Order> greedyPrioritySolution = findGreedyPrioritySolution(newOrders);
    ProblemFinderScore greedyPriorityScore =
        computeScore(start, greedyPrioritySolution, priorities);

    final List<Order> greedyDueSolution = findGreedyDueDateSolution(newOrders);
    ProblemFinderScore greedyDueScore = computeScore(start, greedyDueSolution, priorities);

    if (optimalScore.compareTo(greedyDueScore) <= 0 &&
        optimalScore.compareTo(greedyPathScore) <= 0 &&
        optimalScore.compareTo(greedyPriorityScore) <= 0) {
      System.out.println("found one ");
    }
  }


  private List<Order> findGreedyDueDateSolution(final List<Order> orders) {
    List<Order> result = new ArrayList<>(orders);
    Collections.sort(result, Comparator.comparingLong(Order::getDueTime));
    return result;
  }

  private List<Order> findGreedyPrioritySolution(final List<Order> orders) {
    List<Order> result = new ArrayList<>(orders);
    Collections.sort(result, Comparator.comparingInt(Order::getPriority));
    return result;
  }

  private List<Order> findGreedyPathSolution(final Point2D start, final List<Order> orders) {
    List<Order> tmp = new ArrayList<>(orders);
    List<Order> result = new ArrayList<>(orders);
    Point2D lastPt = start;
    while (!tmp.isEmpty()) {
      int idx = 0;
      double min = Double.POSITIVE_INFINITY;
      for (int i = 0; i < tmp.size(); i++) {
        double dst = lastPt.distance(tmp.get(i).getTo());
        if (dst < min) {
          idx = i;
          min = dst;
        }
      }
      Order rm = tmp.remove(idx);
      result.add(rm);
      Point2D closest = rm.getTo();
      lastPt = closest;
    }

    return result;
  }

  private List<Order> computeShortestPath(final Point2D start, final List<Order> orders) {
    PermutationIterator<Order> optIter =
        new PermutationIterator<>(orders, orders.size());

    double opt = Double.POSITIVE_INFINITY;
    Optional<List<Order>> optLst = Optional.empty();
    while (optIter.hasNext()) {
      List<Order> perm = optIter.next();

      double dst = start.distance(perm.get(0).getTo());
      for (int a = 1; a < perm.size(); a++) {
        dst += perm.get(a - 1).getTo().distance(perm.get(a).getTo());
      }

      if (dst < opt) {
        opt = dst;
        optLst = Optional.of(perm);
      }
    }
    return optLst.get();
  }

  private ProblemFinderScore computeScore(final Point2D start, final List<Order> orders,
                                          final int[] priorities) {
    final double dueDateScore = computeDueDateScore(start, orders);
    final double priorityScore = computePriorityScore(priorities, orders);
    final double distanceScore = computeDistanceScore(start, orders);
    return new ProblemFinderScore(distanceScore, priorityScore, dueDateScore, orders);
  }


  private double computeDueDateScore(final Point2D start, final List<Order> orders) {
    double sum = 0.0;
    double travelTime = start.distance(orders.get(0).getTo()); // speed = 1.0;

    for (int i = 0; i < orders.size(); i++) {
      sum += travelTime - orders.get(i).getDueTime();
      if (i > 0) {
        travelTime += orders.get(i - 1).getTo().distance(orders.get(i).getTo());
      }
    }

    return sum;
  }

  private double computePriorityScore(final int[] priorities, final List<Order> orders) {
    double sum = 0.0;
    for (int i = 0; i < priorities.length; i++) {
      sum += Math.abs(priorities[i] - orders.get(i).getPriority());
    }
    return sum;
  }

  private double computeDistanceScore(final Point2D start, final List<Order> orders) {
    double sum = start.distance(orders.get(0).getTo());

    for (int i = 1; i < orders.size(); i++) {
      sum += orders.get(i - 1).getTo().distance(orders.get(i).getTo());
    }
    return sum;
  }

  public final class ProblemFinderScore implements Comparable<ProblemFinderScore> {
    private final double pathScore;
    private final double priorityScore;
    private final double dueDateScore;
    private final List<Order> orders;

    public ProblemFinderScore(double pathScore, double priorityScore, double dueDateScore,
                              final List<Order> orders) {
      this.pathScore = pathScore;
      this.priorityScore = priorityScore;
      this.dueDateScore = dueDateScore;
      this.orders = orders;
    }

    public double getPathScore() {
      return pathScore;
    }

    public double getPriorityScore() {
      return priorityScore;
    }

    public double getDueDateScore() {
      return dueDateScore;
    }

    public List<Order> getOrders() {
      return orders;
    }

    @Override
    public int compareTo(ProblemFinderScore problemFinderScore) {
      double diff = problemFinderScore.getDueDateScore() - this.dueDateScore;
      if (Math.abs(diff) <= 1e-6) {
        diff = problemFinderScore.getPriorityScore() - this.priorityScore;
        if (Math.abs(diff) <= 1e-6) {
          diff = problemFinderScore.getPathScore() - this.pathScore;
          if (Math.abs(diff) <= 1e-6) {
            return 0;
          } else if (diff < 0) {
            return -1;
          }
        } else if (diff < 0) {
          return -1;
        }
      } else if (diff < 0) {
        return -1;
      }
      return 1;
    }
  }

}
















