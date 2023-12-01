package test.de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.VehicleRoute;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPInsertionMove;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPMove;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPMoveResult;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.de.westranger.optimization.common.algorithm.tools.util.CustomRandom;

class TSPInsertionMoveTest {

  private Random rng;

  @BeforeEach
  public void setup() {
    rng = new Random(47110815);
  }

  @Test
  public void testNoVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertionMove(rng, re);

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> move.performMove(new LinkedList<>()));
  }

  @Test
  public void testThreeVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertionMove(rng, re);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>());
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), new LinkedList<>());
    VehicleRoute vrC = new VehicleRoute(3, new Point2D(3.0, 3.0), new LinkedList<>());

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> move.performMove(List.of(vrA, vrB, vrC)));
  }

  @Test
  public void test1VehicleNoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertionMove(rng, re);
    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>());

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test2VehiclesNoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertionMove(rng, re);
    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>());
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), new LinkedList<>());

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test1Vehicle2OrdersA() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0});
    TSPMove move = new TSPInsertionMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));
    Order orderB = new Order(2, new Point2D(2.0, 2.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB));

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).route().size());

    Assertions.assertEquals(2, result.get().vehicles().get(0).route().get(0).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).route().get(1).getId());
  }

  @Test
  public void test1Vehicle2OrdersB() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0});
    TSPMove move = new TSPInsertionMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));
    Order orderB = new Order(2, new Point2D(2.0, 2.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB));

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).route().size());

    Assertions.assertEquals(2, result.get().vehicles().get(0).route().get(0).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).route().get(1).getId());
  }

  @Test
  public void test1Vehicle3OrdersA() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0}, new boolean[] {true});
    TSPMove move = new TSPInsertionMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));
    Order orderB = new Order(2, new Point2D(2.0, 2.0));
    Order orderC = new Order(3, new Point2D(3.0, 3.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC));

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(3, result.get().vehicles().get(0).route().size());

    Assertions.assertEquals(2, result.get().vehicles().get(0).route().get(0).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).route().get(1).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).route().get(2).getId());
  }

  @Test
  public void test1Vehicle3OrdersB() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertionMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));
    Order orderB = new Order(2, new Point2D(2.0, 2.0));
    Order orderC = new Order(3, new Point2D(3.0, 3.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC));

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(3, result.get().vehicles().get(0).route().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).route().get(0).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).route().get(1).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).route().get(2).getId());
  }

  @Test
  public void test2VehicleANoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0});
    TSPMove move = new TSPInsertionMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));
    Order orderB = new Order(2, new Point2D(2.0, 2.0));
    Order orderC = new Order(3, new Point2D(3.0, 3.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>());
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), List.of(orderA, orderB, orderC));

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(1, result.get().vehicles().get(0).route().size());
    Assertions.assertEquals(2, result.get().vehicles().get(1).route().size());

    Assertions.assertEquals(2, result.get().vehicles().get(0).route().get(0).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(1).route().get(0).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(1).route().get(1).getId());
  }

  @Test
  public void test2VehicleBNoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0});
    TSPMove move = new TSPInsertionMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));
    Order orderB = new Order(2, new Point2D(2.0, 2.0));
    Order orderC = new Order(3, new Point2D(3.0, 3.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), List.of(orderA, orderB, orderC));
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), new LinkedList<>());

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).route().size());
    Assertions.assertEquals(1, result.get().vehicles().get(1).route().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).route().get(0).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).route().get(1).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(1).route().get(0).getId());
  }

  @Test
  public void test2Vehicle6OrdersA() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 3}, new boolean[] {true});
    TSPMove move = new TSPInsertionMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));
    Order orderB = new Order(2, new Point2D(2.0, 2.0));
    Order orderC = new Order(3, new Point2D(3.0, 3.0));
    Order orderD = new Order(4, new Point2D(4.0, 4.0));
    Order orderE = new Order(5, new Point2D(5.0, 5.0));
    Order orderF = new Order(6, new Point2D(6.0, 6.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), List.of(orderA, orderB, orderC));
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), List.of(orderD, orderE, orderF));

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).route().size());
    Assertions.assertEquals(4, result.get().vehicles().get(1).route().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).route().get(0).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).route().get(1).getId());

    Assertions.assertEquals(4, result.get().vehicles().get(1).route().get(0).getId());
    Assertions.assertEquals(5, result.get().vehicles().get(1).route().get(1).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).route().get(2).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(1).route().get(3).getId());
  }

  @Test
  public void test2Vehicle6OrdersB() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0}, new boolean[] {true});
    TSPMove move = new TSPInsertionMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));
    Order orderB = new Order(2, new Point2D(2.0, 2.0));
    Order orderC = new Order(3, new Point2D(3.0, 3.0));
    Order orderD = new Order(4, new Point2D(4.0, 4.0));
    Order orderE = new Order(5, new Point2D(5.0, 5.0));
    Order orderF = new Order(6, new Point2D(6.0, 6.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), List.of(orderA, orderB, orderC));
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), List.of(orderD, orderE, orderF));

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).route().size());
    Assertions.assertEquals(4, result.get().vehicles().get(1).route().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).route().get(0).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).route().get(1).getId());

    Assertions.assertEquals(2, result.get().vehicles().get(1).route().get(0).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(1).route().get(1).getId());
    Assertions.assertEquals(5, result.get().vehicles().get(1).route().get(2).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).route().get(3).getId());
  }

  @Test
  public void test2Vehicle6OrdersC() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 3}, new boolean[] {false});
    TSPMove move = new TSPInsertionMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));
    Order orderB = new Order(2, new Point2D(2.0, 2.0));
    Order orderC = new Order(3, new Point2D(3.0, 3.0));
    Order orderD = new Order(4, new Point2D(4.0, 4.0));
    Order orderE = new Order(5, new Point2D(5.0, 5.0));
    Order orderF = new Order(6, new Point2D(6.0, 6.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), List.of(orderA, orderB, orderC));
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), List.of(orderD, orderE, orderF));

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(4, result.get().vehicles().get(0).route().size());
    Assertions.assertEquals(2, result.get().vehicles().get(1).route().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).route().get(0).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).route().get(1).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).route().get(2).getId());
    Assertions.assertEquals(5, result.get().vehicles().get(0).route().get(3).getId());

    Assertions.assertEquals(4, result.get().vehicles().get(1).route().get(0).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).route().get(1).getId());
  }

  @Test
  public void test2Vehicle6OrdersD() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertionMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));
    Order orderB = new Order(2, new Point2D(2.0, 2.0));
    Order orderC = new Order(3, new Point2D(3.0, 3.0));
    Order orderD = new Order(4, new Point2D(4.0, 4.0));
    Order orderE = new Order(5, new Point2D(5.0, 5.0));
    Order orderF = new Order(6, new Point2D(6.0, 6.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), List.of(orderA, orderB, orderC));
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), List.of(orderD, orderE, orderF));

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(4, result.get().vehicles().get(0).route().size());
    Assertions.assertEquals(2, result.get().vehicles().get(1).route().size());

    Assertions.assertEquals(5, result.get().vehicles().get(0).route().get(0).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).route().get(1).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).route().get(2).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).route().get(3).getId());

    Assertions.assertEquals(4, result.get().vehicles().get(1).route().get(0).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).route().get(1).getId());
  }

  @Test
  public void testGenerateValidIndexA() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertionMove(rng, re);

    Assertions.assertTrue(move.isGenerateValidCutsAlwaysPossible(2, 1, 1));

    List<Integer> integers = move.generateValidCuts(2, 1, 1, rng);
    Assertions.assertEquals(List.of(1), integers);
  }

  @Test
  public void testGenerateValidIndexB() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertionMove(rng, re);

    Assertions.assertTrue(move.isGenerateValidCutsAlwaysPossible(7, 2, 2));

    List<Integer> integers = move.generateValidCuts(6, 2, 2, rng);
    Assertions.assertEquals(List.of(2, 4), integers);
  }

  @Test
  public void testGenerateValidIndexNotPossible() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertionMove(rng, re);

    Assertions.assertFalse(move.isGenerateValidCutsAlwaysPossible(2, 3, 1));
    Assertions.assertFalse(move.isGenerateValidCutsAlwaysPossible(6, 2, 4));
    Assertions.assertFalse(move.isGenerateValidCutsAlwaysPossible(5, 2, 2));
    Assertions.assertFalse(move.isGenerateValidCutsAlwaysPossible(6, 2, 3));
  }

  @Test
  public void testGenerateValidPossible() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertionMove(rng, re);

    Assertions.assertTrue(move.isGenerateValidCutsAlwaysPossible(6, 2, 2));
  }
}