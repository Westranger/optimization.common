package de.westranger.optimization.common.algorithm.tsp.sa.move;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tools.util.CustomRandom;
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

class TSPInsertionMoveTest extends MoveTestBase {

  @Test
  public void testNoVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertionMove(rng, re, false);

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> move.performMove(new LinkedList<>()));
  }

  @Test
  public void testThreeVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertionMove(rng, re, false);

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
    TSPMove move = new TSPInsertionMove(rng, re, false);
    VehicleRoute
        vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>(), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test2VehiclesNoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertionMove(rng, re, false);
    VehicleRoute
        vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>(), false);
    VehicleRoute
        vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), new LinkedList<>(), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test1Vehicle2OrdersA() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveOneVehicle(move, List.of(1, 2), List.of(2, 1), false);
  }

  @Test
  public void test1Vehicle2OrdersARoundtrip() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveOneVehicle(move, List.of(1, 2), List.of(2, 1), true);
  }

  @Test
  public void test1Vehicle4Orders() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 1});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4), List.of(2, 3, 1, 4), false);
  }

  @Test
  public void test1Vehicle4OrdersRemoveEndInsertMiddle() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {3, 2});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4), List.of(1, 2, 4, 3), false);
  }

  @Test
  public void test1Vehicle4OrdersRemoveEndInsertMiddleRoundtrip() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {3, 2});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4), List.of(1, 2, 4, 3), true);
  }

  @Test
  public void test1Vehicle2OrdersB() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveOneVehicle(move, List.of(1, 2), List.of(2, 1), false);
  }

  @Test
  public void test1Vehicle3OrdersA() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3), List.of(2, 1, 3), false);
  }

  @Test
  public void test1Vehicle3OrdersB() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 1});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3), List.of(1, 3, 2), false);
  }

  @Test
  public void test2VehicleANoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(), List.of(1, 2, 3),
        List.of(2), List.of(1, 3), false);
  }

  @Test
  public void test2VehicleBNoOrdersRemoveLastFromA() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(1), List.of(),
        List.of(), List.of(1), false);
  }

  @Test
  public void test2VehicleBNoOrdersRemoveLastFromARoundtrip() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(1), List.of(),
        List.of(), List.of(1), true);
  }

  @Test
  public void test2VehicleANoOrdersRemoveLastFromB() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(), List.of(1),
        List.of(1), List.of(), false);
  }

  @Test
  public void test2VehicleBNoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3), List.of(),
        List.of(1, 3), List.of(2), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveAMiddleInsertBEnd() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 3}, new boolean[] {true});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3), List.of(4, 5, 6),
        List.of(1, 3), List.of(4, 5, 6, 2), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveAMiddleInsertBEndRoundtrip() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 3}, new boolean[] {true});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3), List.of(4, 5, 6),
        List.of(1, 3), List.of(4, 5, 6, 2), true);
  }


  @Test
  public void test2Vehicle6OrdersRemoveAMiddleInsertBBegin() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0}, new boolean[] {true});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3), List.of(4, 5, 6),
        List.of(1, 3), List.of(2, 4, 5, 6), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveAEndInsertBBegin() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {2, 0}, new boolean[] {true});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3), List.of(4, 5, 6),
        List.of(1, 2), List.of(3, 4, 5, 6), false);
  }


  @Test
  public void test2Vehicle6OrdersRemoveABeginInsertBBegin() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0}, new boolean[] {true});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(1), List.of(4, 5, 6),
        List.of(), List.of(1, 4, 5, 6), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveBMiddleInsertAEnd() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 3}, new boolean[] {false});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3), List.of(4, 5, 6),
        List.of(1, 2, 3, 5), List.of(4, 6), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveBMiddleInsertABegin() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3), List.of(4, 5, 6),
        List.of(5, 1, 2, 3), List.of(4, 6), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveBEndInsertABegin() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {2, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3), List.of(4, 5, 6),
        List.of(6, 1, 2, 3), List.of(4, 5), false);
  }


  @Test
  public void test2Vehicle6OrdersRemoveBEndInsertABeginVariation() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertionMove(rng, re, false);

    evaluateMoveTwoVehicles(move, List.of(), List.of(4, 5),
        List.of(5), List.of(4), false);
  }

  @Test
  public void testValidateSamplingOneVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertionMove(rng, re, true);

    VehicleRoute vrA =
        new VehicleRoute(1, this.homeA, createList(List.of(1, 2, 3, 4, 5, 6, 7, 8)),
            false);

    for (int i = 0; i < 1e7; i++) {
      move.performMove(List.of(vrA));
    }

    Map<String, SampleStatistics> stats = move.getSamplingStatistics().get();
    assertEquals("Stats(num_keys=8 num_obs=10.000.000 avg_perc=12,5% std_dev_perc=0,38%)",
        stats.get("move_insert_iidx").toString());
    assertEquals("Stats(num_keys=8 num_obs=10.000.000 avg_perc=12,5% std_dev_perc=0,38%)",
        stats.get("move_insert_ridx").toString());
  }

  @Test
  public void testValidateSamplingTwoVehicles() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertionMove(rng, re, true);

    VehicleRoute vrA =
        new VehicleRoute(1, this.homeA, createList(List.of(1, 2, 3, 4, 5, 6, 7, 8)),
            false);

    VehicleRoute vrB =
        new VehicleRoute(2, this.homeB, createList(List.of(1, 2, 3, 4, 5, 6, 7, 8)),
            false);

    for (int i = 0; i < 1e7; i++) {
      move.performMove(List.of(vrA, vrB));
    }

    Map<String, SampleStatistics> stats = move.getSamplingStatistics().get();
    assertEquals("Stats(num_keys=9 num_obs=10.000.000 avg_perc=11,11% std_dev_perc=0,35%)",
        stats.get("move_insert_iidx").toString());
    assertEquals("Stats(num_keys=8 num_obs=10.000.000 avg_perc=12,5% std_dev_perc=0,38%)",
        stats.get("move_insert_ridx").toString());
    assertEquals("Stats(num_keys=2 num_obs=10.000.000 avg_perc=50% std_dev_perc=1%)",
        stats.get("move_insert_switch_vehicle").toString());
  }
}