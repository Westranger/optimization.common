package de.westranger.optimization.common.algorithm.tsp.sa.move;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tools.util.CustomRandom;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import de.westranger.optimization.common.util.SampleStatistics;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TSPSwapMoveTest extends MoveTestBase {

  @Test
  public void testNoVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re, false);

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> move.performMove(new LinkedList<>()));
  }

  @Test
  public void testThreeVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re, false);

    VehicleRoute
        vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>(), false);
    VehicleRoute
        vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), new LinkedList<>(), false);
    VehicleRoute
        vrC = new VehicleRoute(3, new Point2D(3.0, 3.0), new LinkedList<>(), false);

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> move.performMove(List.of(vrA, vrB, vrC)));
  }

  @Test
  public void test1VehicleNoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re, false);
    VehicleRoute
        vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>(), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test2VehiclesNoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re, false);
    VehicleRoute
        vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>(), false);
    VehicleRoute
        vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), new LinkedList<>(), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test2VehicleOneOrderA() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), List.of(orderA), false);
    VehicleRoute
        vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), new LinkedList<>(), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test2VehicleOneOrderB() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re, false);

    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);

    VehicleRoute
        vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>(), false);
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), List.of(orderB), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test2Vehicle2Orders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(1), List.of(2),
        List.of(2), List.of(1), false);
  }

  @Test
  public void test2Vehicle6Orders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3), List.of(4, 5, 6),
        List.of(5, 2, 3), List.of(4, 1, 6), false);
  }

  @Test
  public void test2Vehicle6OrdersDifferentLengths() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 3});
    TSPMove move = new TSPSwapMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2), List.of(3, 4, 5, 6),
        List.of(6, 2), List.of(3, 4, 5, 1), false);
  }

  @Test
  public void test1Vehicle6OrdersSameIndex() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0});
    TSPMove move = new TSPSwapMove(rng, re, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4, 5, 6), List.of(2, 1, 3, 4, 5, 6), false);
  }

  @Test
  public void test1Vehicle6OrdersA() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {2, 4});
    TSPMove move = new TSPSwapMove(rng, re, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4, 5, 6), List.of(1, 2, 6, 4, 5, 3), false);
  }

  @Test
  public void test1Vehicle6OrdersB() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {4, 2});
    TSPMove move = new TSPSwapMove(rng, re, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4, 5, 6), List.of(1, 2, 5, 4, 3, 6), false);
  }

  @Test
  public void testValidateSamplingOneVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re, true);

    VehicleRoute vrA =
        new VehicleRoute(1, this.homeA, createList(List.of(1, 2, 3, 4, 5, 6, 7, 8)),
            false);

    for (int i = 0; i < 1e7; i++) {
      move.performMove(List.of(vrA));
    }

    Map<String, SampleStatistics> stats = move.getSamplingStatistics().get();
    assertEquals("Stats(num_keys=8 num_obs=10.000.000 avg_perc=12,5% std_dev_perc=0,38%)",
        stats.get("move_swap_ridxa").toString());
    assertEquals("Stats(num_keys=7 num_obs=10.000.000 avg_perc=14,29% std_dev_perc=0,41%)",
        stats.get("move_swap_ridxb").toString());
  }

  @Test
  public void testValidateSamplingTwoVehicles() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re, true);

    VehicleRoute vrA =
        new VehicleRoute(1, this.homeA, createList(List.of(1, 2, 3, 4, 5, 6, 7, 8)),
            false);

    VehicleRoute vrB =
        new VehicleRoute(2, this.homeB, createList(List.of(1, 2, 3, 4, 5, 6)),
            false);

    for (int i = 0; i < 1e7; i++) {
      move.performMove(List.of(vrA, vrB));
    }

    Map<String, SampleStatistics> stats = move.getSamplingStatistics().get();
    assertEquals("Stats(num_keys=8 num_obs=10.000.000 avg_perc=12,5% std_dev_perc=0,38%)",
        stats.get("move_swap_ridxa").toString());
    assertEquals("Stats(num_keys=6 num_obs=10.000.000 avg_perc=16,67% std_dev_perc=0,45%)",
        stats.get("move_swap_ridxb").toString());
  }

}