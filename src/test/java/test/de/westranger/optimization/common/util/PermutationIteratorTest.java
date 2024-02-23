package test.de.westranger.optimization.common.util;

import com.google.gson.Gson;
import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.common.ProblemFormulation;
import de.westranger.optimization.common.util.PermutationIterator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
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

    Point2D start = new Point2D(0.0, 0.0);
    Point2D end = new Point2D(0.0, 12.0);

    int[] priorities = new int[] {1, 1, 2, 2, 3, 3, 4, 4, 5, 5};

    Order o1 = new Order(10, new Point2D(2, 1), null);
    Order o2 = new Order(1, new Point2D(0, 2), null);

    Order o3 = new Order(2, new Point2D(-2, 3), null);
    Order o4 = new Order(3, new Point2D(0, 4), null);

    Order o5 = new Order(4, new Point2D(2, 5), null);
    Order o6 = new Order(5, new Point2D(0, 6), null);

    Order o7 = new Order(6, new Point2D(-2, 7), null);
    Order o8 = new Order(7, new Point2D(0, 8), null);

    Order o9 = new Order(8, new Point2D(2, 9), null);
    Order o10 = new Order(9, new Point2D(0, 10), null);


    List<Order> candidates =
        List.of(o2, o1, o4, o3, o6, o5, o8, o7, o10, o9);
    extracted(start, end, candidates, priorities);



    /*

    for (int x1 = -3; x1 <= 3; x1++) {
      Order o2 = new Order(2, new Point2D(x1, 2), null);
      for (int x2 = -3; x2 <= 3; x2++) {
        Order o4 = new Order(4, new Point2D(x2, 4), null);
        for (int x3 = -3; x3 <= 3; x3++) {
          Order o6 = new Order(6, new Point2D(x3, 6), null);
          for (int x4 = -3; x4 <= 3; x4++) {
            Order o8 = new Order(8, new Point2D(x4, 8), null);
            for (int x5 = -3; x5 <= 3; x5++) {
              Order o10 = new Order(10, new Point2D(x5, 10), null);

              Order o3 = new Order(3, new Point2D(0, 3), null);
              Order o5 = new Order(5, new Point2D(0, 5), null);
              Order o7 = new Order(7, new Point2D(0, 7), null);
              Order o9 = new Order(9, new Point2D(0, 9), null);

              List<Order> candidates =
                  List.of(o2, o3, o4, o5, o6, o7, o8, o9);
              extracted(start, end, candidates, priorities);
            }
          }
        }
      }
    }
*/


    /*
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
    Point2D end = new Point2D(0.0, 12.0);

    int[] priorities = new int[] {1, 1, 2, 2, 3, 3, 4, 4};

    for (int a = 0; a < elements.size(); a++) {
      for (int b = a + 1; b < elements.size(); b++) {
        for (int c = b + 1; c < elements.size(); c++) {
          for (int d = c + 1; d < elements.size(); d++) {
            for (int e = d + 1; e < elements.size(); e++) {
              for (int f = e + 1; f < elements.size(); f++) {
                for (int g = f + 1; g < elements.size(); g++) {
                  for (int h = g + 1; h < elements.size(); h++) {

                    List<Order> candidates =
                        List.of(elements.get(a), elements.get(b), elements.get(c), elements.get(d),
                            elements.get(e), elements.get(f), elements.get(g), elements.get(h));
                    extracted(start, end, candidates, priorities);
                  }
                }
              }
            }
          }
        }
      }
    }

*/

  }

  private ProblemFinderScore masterScore = new ProblemFinderScore(0.0, 0.0, 0.0, null);
  private int fileCnt = 0;

  private void extracted(Point2D start, Point2D end, List<Order> candidates, int[] priorities) {
   /* boolean accept =
        acceptSolution(start, start, candidates.get(0).getTo(), candidates.get(1).getTo(),
            candidates.get(2).getTo());

    if (!accept) {
      return;
    }

    //System.out.println("pass 0");
    for (int i = 0; i < candidates.size() - 3; i += 1) {
      accept =
          acceptSolution(start, candidates.get(0).getTo(), candidates.get(1).getTo(),
              candidates.get(2).getTo(), candidates.get(3).getTo());

      if (!accept) {
        return;
      }
      //System.out.println("pass " + (i - 1));
    }*/

    //System.out.println("GOT ONE");

    // compute shortest path
    List<Order> shortestPath = computeShortestPath(start, end, candidates);

    long[] dueTimes = new long[priorities.length];

    double travelTime = start.distance(shortestPath.get(0).getTo());
    for (int i = 1; i < shortestPath.size(); i++) {
      travelTime += shortestPath.get(i).getTo().distance(shortestPath.get(i - 1).getTo());
      if (i % 2 == 1) {
        dueTimes[i] = (long) (travelTime * 1000);
        dueTimes[i - 1] = dueTimes[i] + 1;
      }
    }

    for (int i = 0; i < shortestPath.size(); i++) {
      shortestPath.set(i,
          new Order(shortestPath.get(i).getId(), shortestPath.get(i).getTo(), null,
              priorities[i], 0, dueTimes[i]));
    }


    List<Order> newOrders = new ArrayList<>(candidates.size());
    for (Order oOld : candidates) {
      // find idx in candidates
      int idx = -1;
      for (int j = 0; j < shortestPath.size(); j++) {
        if (shortestPath.get(j).getTo().distance(oOld.getTo()) <= 1e-6) {
          idx = j;
          break;
        }
      }

      // assemble new order
      Order oNew =
          new Order(oOld.getId(), oOld.getTo(), null, priorities[idx], 0, dueTimes[idx]);
      newOrders.add(oNew);
    }

    ProblemFinderScore optimalScore = computeScore(start, end, shortestPath, priorities);

    final List<Order> greedyPathSolution = findGreedyPathSolution(start, newOrders);
    ProblemFinderScore greedyPathScore = computeScore(start, end, greedyPathSolution, priorities);

    final List<Order> greedyPrioritySolution = findGreedyPrioritySolution(newOrders);
    ProblemFinderScore greedyPriorityScore =
        computeScore(start, end, greedyPrioritySolution, priorities);

    final List<Order> greedyDueSolution = findGreedyDueDateSolution(newOrders);
    ProblemFinderScore greedyDueScore = computeScore(start, end, greedyDueSolution, priorities);

//    if (optimalScore.compareTo(greedyDueScore) > 0 &&
    //   optimalScore.compareTo(greedyPathScore) > 0 &&
    //   optimalScore.compareTo(greedyPriorityScore) > 0) {
    //System.out.println("found one ");

    double diffPath = greedyPathScore.getPathScore() - optimalScore.getPathScore() +
        greedyDueScore.getPathScore() - optimalScore.getPathScore() /*+
          greedyPriorityScore.getPathScore() - optimalScore.getPathScore()*/;
    diffPath /= 2.0;

    double diffPrio = greedyDueScore.getPriorityScore() - optimalScore.getPriorityScore() +
        greedyPathScore.getPriorityScore() - optimalScore.getPriorityScore() +
        greedyPriorityScore.getPriorityScore() - optimalScore.getPriorityScore();
    diffPrio = 0.0;

    double diffDueDate = /*greedyDueScore.getDueDateScore() - optimalScore.getDueDateScore()*/ +
        greedyPathScore.getDueDateScore() - optimalScore.getDueDateScore()/* +
          greedyPriorityScore.getDueDateScore() - optimalScore.getDueDateScore()*/;
    diffDueDate /= 2.0;


    ProblemFinderScore tmp =
        new ProblemFinderScore(diffPath, diffPrio, diffDueDate, optimalScore.getOrders());

    //if (tmp.compareTo(masterScore) < 0) {
    masterScore = tmp;
    System.out.println(
        "found one " + optimalScore.getOrders() + " " + diffPath + " " + diffPrio + " " + " " +
            diffDueDate);

    String svgContent = SvgPlotter.plotProblemFinderScores(
        new ProblemFinderScore[] {optimalScore, greedyPathScore, greedyPriorityScore,
            greedyDueScore}, start, end);

    try (BufferedWriter writer = new BufferedWriter(
        new FileWriter("./img_" + (fileCnt++) + ".svg"))) {
      writer.write(svgContent);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    //}
    //}
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

  private List<Order> findGreedyPathSolution(final Point2D start,
                                             final List<Order> orders) {
    List<Order> tmp = new ArrayList<>(orders);
    List<Order> result = new ArrayList<>(orders.size());
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

  private List<Order> computeShortestPath(final Point2D start, final Point2D end,
                                          final List<Order> orders) {
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
      dst += end.distance(perm.get(perm.size() - 1).getTo());

      if (dst < opt) {
        opt = dst;
        optLst = Optional.of(perm);
      }
    }
    return optLst.get();
  }

  private ProblemFinderScore computeScore(final Point2D start, final Point2D end,
                                          final List<Order> orders,
                                          final int[] priorities) {
    final double dueDateScore = computeDueDateScore(start, orders);
    final double priorityScore = computePriorityScore(priorities, orders);
    final double distanceScore = computeDistanceScore(start, end, orders);
    return new ProblemFinderScore(distanceScore, priorityScore, dueDateScore, orders);
  }


  private double computeDueDateScore(final Point2D start, final List<Order> orders) {
    double sum = 0.0;
    double travelTime = start.distance(orders.get(0).getTo()) * 1000.0; // speed = 1.0;

    for (int i = 0; i < orders.size(); i++) {
      if (i > 0) {
        travelTime += orders.get(i - 1).getTo().distance(orders.get(i).getTo()) * 1000.0;
      }
      sum += Math.floor(travelTime) - orders.get(i).getDueTime();
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

  private double computeDistanceScore(final Point2D start, final Point2D end,
                                      final List<Order> orders) {
    double sum = start.distance(orders.get(0).getTo());

    for (int i = 1; i < orders.size(); i++) {
      sum += orders.get(i - 1).getTo().distance(orders.get(i).getTo());
    }
    sum += end.distance(orders.get(orders.size() - 1).getTo());

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
          } else if (this.pathScore < problemFinderScore.getPathScore()) {
            return -1;
          }
        } else if (this.priorityScore < problemFinderScore.getPriorityScore()) {
          return -1;
        }
      } else if (this.dueDateScore < problemFinderScore.getDueDateScore()) {
        return -1;
      }
      return 1;
    }
  }


  public boolean acceptSolution(Point2D pOrigin, Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
    final double distA = p0.distance(p2);
    final double distB = p0.distance(p1);
    final double distC = p1.distance(p2);
    final double distD = p2.distance(p3);
    final double distE = p1.distance(p3);
    final double distF = pOrigin.distance(p3);

    return distC < distE && distE > distD && distA + distC + distC > distB + distC &&
        distA > distB && distF > distA;
  }

  // ###############################################################################################
  // ###############################################################################################
  // ###############################################################################################
  // ###############################################################################################
  // ###############################################################################################


  @Test

  void testConstructTSPProblemList() {
    for (boolean usePriorities : new boolean[] {true, false}) {
      for (boolean useDueDates : new boolean[] {true, false}) {
        for (boolean emptyVehicles : new boolean[] {true, false}) {
          for (int numVehicles : new int[] {1, 3, 5, 10, 15, 25, 50}) {
            for (int orderPerVehicle : new int[] {3, 5, 7, 11, 13}) {
              final StringBuilder sb = new StringBuilder();
              sb.append("vrp_problem_");
              sb.append(numVehicles);
              sb.append('_');
              sb.append(numVehicles * orderPerVehicle);
              sb.append('_');

              if (usePriorities) {
                sb.append('P');
              }
              if (useDueDates) {
                sb.append('D');
              }
              if (emptyVehicles) {
                sb.append('E');
              }
              sb.append(".json");

              generateSolution(usePriorities, useDueDates, emptyVehicles, orderPerVehicle, numVehicles, sb.toString());
            }
          }
        }
      }
    }
  }

  private void generateSolution(final boolean usePriorities, final boolean useDueDates,
                                final boolean emptyVehicles, final int orderPerVehicle,
                                final int numVehicles, final String fileName) {
    int xStep = 2 + 2 + 3;
    double xBase = 0;
    double yBase = 2;
    double yDeltaPerVehicle = 0.1;

    int oderIdCounter = 1;

    Map<Integer, Point2D> vehicleStartPositions = new TreeMap<>();
    final Map<Integer, List<Order>> finalOrderMapping = new TreeMap<>();
    List<Order> orderAll = new LinkedList<>();

    double score = 0.0;

    for (int i = 0; i < numVehicles; i++) {
      // compute coordinates
      Point2D start = new Point2D(xBase, 0.0);

      List<Point2D> points = new ArrayList<>(orderPerVehicle);
      long[] dueTimes = new long[orderPerVehicle];
      int[] priorities = new int[orderPerVehicle];

      if (emptyVehicles && i % 2 != 1) {
        boolean toggle = false;
        for (int y = 0; y < orderPerVehicle; y++) {
          double xValue;
          double yValue;
          if (y % 2 == 0) {
            yValue = y + yBase + (i * yDeltaPerVehicle);
            if (!toggle) {
              xValue = xBase - 2;
            } else {
              xValue = xBase + 2;
            }
            toggle = !toggle;
          } else {
            yValue = y + yBase + (i * yDeltaPerVehicle);
            xValue = xBase;
          }
          points.add(new Point2D(xValue, yValue));
        }

        // create priorities
        if (usePriorities) {
          int prio = 0;
          for (int j = 0; j < priorities.length; j++) {
            if (j % 2 == 0) {
              prio++;
            }
            priorities[j] = prio;
          }
        }

        // compute due times

          double travelTime = start.distance(points.get(0));
          score += start.distance(points.get(0));
          for (int j = 1; j < points.size(); j++) {
            travelTime += points.get(j).distance(points.get(j - 1));
            score += points.get(j).distance(points.get(j - 1));
            if (useDueDates && j % 2 == 1) {
              dueTimes[j] = (long) (travelTime * 1000);
              dueTimes[j - 1] = dueTimes[j] + 1;
            }
          }
        if (useDueDates && dueTimes.length < points.size()) {
          dueTimes[dueTimes.length-1] = (long) travelTime;
        }
      }

      vehicleStartPositions.put(i + 1, start);

      List<Order> order = new LinkedList<>();
      for (int j = 0; j < points.size(); j++) {
        final Order ord =
            new Order(oderIdCounter++, points.get(j), points.get(j), priorities[j], 0, dueTimes[j]);
        order.add(ord);
      }
      finalOrderMapping.put(i + 1, order);
      orderAll.addAll(order);

      // generate Problem formulation


      // make point 2D Pist
      if (emptyVehicles) {
        xBase += xStep;
      } else {
        xBase += 2 * xStep;
      }
    }

    Random rng = new Random(47110815L);

    Collections.shuffle(orderAll, rng);
    ProblemFormulation pf =
        new ProblemFormulation(orderAll, vehicleStartPositions, finalOrderMapping, score);

    Gson gson = new Gson();

    try (FileWriter writer = new FileWriter(fileName)) {
      writer.write(gson.toJson(pf));
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}
