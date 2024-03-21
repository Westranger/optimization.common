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

public class TSPInsertSubrouteMoveTest extends MoveTestBase {

  @Test
  public void testNoVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> move.performMove(new LinkedList<>()));
  }

  @Test
  public void testThreeVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

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
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);
    VehicleRoute
        vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>(), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test2VehiclesNoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);
    VehicleRoute
        vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>(), false);
    VehicleRoute
        vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), new LinkedList<>(), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test1VehicleNotEnoughOrders() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 2});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test1Vehicle4OrdersRemoveMiddleInsertBegin() {
    Random rng = new CustomRandom(new int[] {1, 1, 0});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4), List.of(2, 3, 1, 4), false);
  }

  @Test
  public void test1Vehicle4OrdersRemoveMiddleInsertEnd() {
    Random rng = new CustomRandom(new int[] {1, 1, 2});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4), List.of(1, 4, 2, 3), false);
  }

  @Test
  public void test1Vehicle4OrdersRemoveMiddleInsertMiddle() {
    Random rng = new CustomRandom(new int[] {0, 0, 1});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4), List.of(3, 1, 2, 4), false);
  }

  @Test
  public void test1Vehicle4OrdersRemoveMiddleInsertBeginReverse() {
    Random rng = new CustomRandom(new int[] {2, 2, 0});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, true, false, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4), List.of(4, 3, 1, 2), false);
  }

  @Test
  public void test1Vehicle4OrdersRemoveMiddleInsertEndReverse() {
    Random rng = new CustomRandom(new int[] {2, 2, 2});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, true, false, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4), List.of(1, 2, 4, 3), false);
  }

  @Test
  public void test1Vehicle4OrdersRemoveMiddleInsertAlmostEndRoundtrip() {
    Random rng = new CustomRandom(new int[] {1, 2, 2});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4, 5, 6), List.of(1, 5, 2, 3, 4, 6), true);
  }

  @Test
  public void test1Vehicle4OrdersRemoveMiddleInsertMiddleReverse() {
    Random rng = new CustomRandom(new int[] {1, 1, 1});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, true, false, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4), List.of(1, 3, 2, 4), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveMiddleInsertVehicleABegin() {
    Random rng = new CustomRandom(new int[] {1, 0, 0}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 4), List.of(2, 3, 5, 6, 7, 8), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveMiddleInsertVehicleAEnd() {
    Random rng = new CustomRandom(new int[] {1, 0, 4}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 4), List.of(5, 6, 7, 8, 2, 3), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveMiddleInsertVehicleAMiddle() {
    Random rng = new CustomRandom(new int[] {1, 0, 2}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 4), List.of(5, 6, 2, 3, 7, 8), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveMiddleInsertVehicleBBegin() {
    Random rng = new CustomRandom(new int[] {1, 0, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(6, 7, 1, 2, 3, 4), List.of(5, 8), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveMiddleInsertVehicleBEnd() {
    Random rng = new CustomRandom(new int[] {1, 0, 4}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 2, 3, 4, 6, 7), List.of(5, 8), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveMiddleInsertVehicleBMiddle() {
    Random rng = new CustomRandom(new int[] {1, 0, 2}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 2, 6, 7, 3, 4), List.of(5, 8), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveEndInsertVehicleBMiddle() {
    Random rng = new CustomRandom(new int[] {2, 0, 2}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 2, 7, 8, 3, 4), List.of(5, 6), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveEndInsertVehicleBMiddleReverse() {
    Random rng = new CustomRandom(new int[] {2, 0, 2}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, true, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 2, 8, 7, 3, 4), List.of(5, 6), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveBeginInsertVehicleBMiddleReverse() {
    Random rng = new CustomRandom(new int[] {0, 0, 2}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, true, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 2, 6, 5, 3, 4), List.of(7, 8), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveMiddleInsertVehicleABegin() {
    Random rng = new CustomRandom(new int[] {1, 0, 0}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6),
        List.of(1, 4), List.of(2, 3, 5, 6), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveMiddleInsertVehicleAEnd() {
    Random rng = new CustomRandom(new int[] {1, 0, 2}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6),
        List.of(1, 4), List.of(5, 6, 2, 3), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveMiddleInsertVehicleAMiddle() {
    Random rng = new CustomRandom(new int[] {1, 0, 1}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6),
        List.of(1, 4), List.of(5, 2, 3, 6), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveMiddleInsertVehicleBBegin() {
    Random rng = new CustomRandom(new int[] {1, 0, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2), List.of(5, 6, 7, 8),
        List.of(6, 7, 1, 2), List.of(5, 8), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveMiddleInsertVehicleBEnd() {
    Random rng = new CustomRandom(new int[] {1, 0, 2}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2), List.of(5, 6, 7, 8),
        List.of(1, 2, 6, 7), List.of(5, 8), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveMiddleInsertVehicleBMiddle() {
    Random rng = new CustomRandom(new int[] {1, 0, 1}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2), List.of(5, 6, 7, 8),
        List.of(1, 6, 7, 2), List.of(5, 8), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveMiddleInsertVehicleBEmpty() {
    Random rng = new CustomRandom(new int[] {2, 0, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4, 5), List.of(),
        List.of(1, 2, 5), List.of(3, 4), false);
  }


  @Test
  public void testValidateSamplingOneVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false, true);

    VehicleRoute vrA =
        new VehicleRoute(1, this.homeA, createList(List.of(1, 2, 3, 4, 5, 6, 7, 8)),
            false);

    for (int i = 0; i < 1e7; i++) {
      move.performMove(List.of(vrA));
    }

    Map<String, SampleStatistics> stats = move.getSamplingStatistics().get();
    assertEquals("Stats(num_keys=8 num_obs=10.000.000 avg_perc=12,5% std_dev_perc=0,38%)",
        stats.get("move_subroute_start_idx").toString());
    assertEquals("Stats(num_keys=8 num_obs=10.000.000 avg_perc=12,5% std_dev_perc=0,38%)",
        stats.get("move_swap_end_idx").toString());
  }

}
