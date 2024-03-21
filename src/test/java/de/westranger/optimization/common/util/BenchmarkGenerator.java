package de.westranger.optimization.common.util;

import com.google.gson.Gson;
import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.common.ProblemFormulation;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class BenchmarkGenerator {

  public static void main(String[] args) {
    for (boolean usePriorities : new boolean[] {true, false}) {
      for (boolean useDueDates : new boolean[] {true, false}) {
        for (boolean emptyVehicles : new boolean[] {true, false}) {
          for (int numVehicles : new int[] {1, 3, 5, 11, 15, 25, 50}) {
            for (int orderPerVehicle : new int[] {3, 5, 7, 11, 13, 15}) {
              final StringBuilder sb = new StringBuilder();
              sb.append("vrp_problem_");
              sb.append(numVehicles);
              sb.append('_');
              if (emptyVehicles) {
                sb.append((int) Math.round((numVehicles * orderPerVehicle) / 2.0));
              } else {
                sb.append(numVehicles * orderPerVehicle);
              }

              sb.append('_');

              if (usePriorities) {
                sb.append('P');
              } else {
                sb.append('_');
              }

              if (useDueDates) {
                sb.append('D');
              } else {
                sb.append('_');
              }

              if (emptyVehicles) {
                sb.append('E');
              } else {
                sb.append('_');
              }

              sb.append(".json");

              generateSolution(usePriorities, useDueDates, emptyVehicles, orderPerVehicle,
                  numVehicles, sb.toString());
            }
          }
        }
      }
    }
  }

  private static void generateSolution(final boolean usePriorities, final boolean useDueDates,
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

      if ((emptyVehicles && i % 2 != 1) || !emptyVehicles) {
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
          dueTimes[dueTimes.length - 1] = (long) travelTime;
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
