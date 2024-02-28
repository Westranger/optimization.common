package de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import de.westranger.optimization.common.algorithm.tools.util.CustomRandom;

public class TSPInsertSubrouteMoveTest {
  private Random rng;

  @BeforeEach
  public void setup() {
    rng = new Random(47110815);
  }

  @Test
  public void testNoVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> move.performMove(new LinkedList<>()));
  }

  @Test
  public void testThreeVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

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
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);
    VehicleRoute
        vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>(), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test2VehiclesNoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);
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
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertFalse(result.isPresent());
  }

  @Test
  public void test1Vehicle4OrdersInsertAtBegin() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 0});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().size());

    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(3).getId());
  }

  @Test
  public void test1Vehicle4OrdersInsertAtEnd() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 2});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(3).getId());
  }

  @Test
  public void test1Vehicle4OrdersInsertAtMiddle() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0, 1});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().size());

    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(3).getId());
  }

  @Test
  public void test1Vehicle4OrdersInsertAtBeginReverse() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 0});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, true);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().size());

    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(3).getId());
  }

  @Test
  public void test1Vehicle4OrdersInsertAtEndReverse() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 2});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, true);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(3).getId());
  }

  @Test
  public void test1Vehicle4OrdersInsertAtMiddleReverse() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0, 1});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, true);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().size());

    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(3).getId());
  }

  @Test
  public void test2Vehicle8OrdersInsertVehicleAAtBegin() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 0}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);
    Order orderG = new Order(7, new Point2D(7.0, 7.0), null);
    Order orderH = new Order(8, new Point2D(8.0, 8.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD), false);
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0),
        List.of(orderE, orderF, orderG, orderH), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(1).getId());

    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(1).getRoute().get(1).getId());
    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(2).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().get(3).getId());
    Assertions.assertEquals(7, result.get().vehicles().get(1).getRoute().get(4).getId());
    Assertions.assertEquals(8, result.get().vehicles().get(1).getRoute().get(5).getId());
  }

  @Test
  public void test2Vehicle8OrdersInsertVehicleAAtEnd() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 4}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);
    Order orderG = new Order(7, new Point2D(7.0, 7.0), null);
    Order orderH = new Order(8, new Point2D(8.0, 8.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD), false);
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0),
        List.of(orderE, orderF, orderG, orderH), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(1).getId());

    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().get(1).getId());
    Assertions.assertEquals(7, result.get().vehicles().get(1).getRoute().get(2).getId());
    Assertions.assertEquals(8, result.get().vehicles().get(1).getRoute().get(3).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().get(4).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(1).getRoute().get(5).getId());
  }

  @Test
  public void test2Vehicle8OrdersInsertVehicleAAtMiddle() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 2}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);
    Order orderG = new Order(7, new Point2D(7.0, 7.0), null);
    Order orderH = new Order(8, new Point2D(8.0, 8.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD), false);
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0),
        List.of(orderE, orderF, orderG, orderH), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(1).getId());

    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().get(1).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().get(2).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(1).getRoute().get(3).getId());
    Assertions.assertEquals(7, result.get().vehicles().get(1).getRoute().get(4).getId());
    Assertions.assertEquals(8, result.get().vehicles().get(1).getRoute().get(5).getId());
  }

  @Test
  public void test2Vehicle8OrdersInsertVehicleBAtBegin() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);
    Order orderG = new Order(7, new Point2D(7.0, 7.0), null);
    Order orderH = new Order(8, new Point2D(8.0, 8.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD), false);
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0),
        List.of(orderE, orderF, orderG, orderH), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(7, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(3).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(4).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(5).getId());

    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(8, result.get().vehicles().get(1).getRoute().get(1).getId());
  }

  @Test
  public void test2Vehicle8OrdersInsertVehicleBAtEnd() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 4}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);
    Order orderG = new Order(7, new Point2D(7.0, 7.0), null);
    Order orderH = new Order(8, new Point2D(8.0, 8.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD), false);
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0),
        List.of(orderE, orderF, orderG, orderH), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(3).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().get(4).getId());
    Assertions.assertEquals(7, result.get().vehicles().get(0).getRoute().get(5).getId());

    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(8, result.get().vehicles().get(1).getRoute().get(1).getId());
  }

  @Test
  public void test2Vehicle8OrdersInsertVehicleBAtMiddle() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 2}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);
    Order orderG = new Order(7, new Point2D(7.0, 7.0), null);
    Order orderH = new Order(8, new Point2D(8.0, 8.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD), false);
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0),
        List.of(orderE, orderF, orderG, orderH), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(7, result.get().vehicles().get(0).getRoute().get(3).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(4).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(5).getId());

    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(8, result.get().vehicles().get(1).getRoute().get(1).getId());
  }

  @Test
  public void test2Vehicle6OrdersInsertVehicleAAtBegin() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 0}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD), false);
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0),
        List.of(orderE, orderF), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(1).getId());

    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(1).getRoute().get(1).getId());
    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(2).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().get(3).getId());
  }

  @Test
  public void test2Vehicle6OrdersInsertVehicleAAtEnd() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 2}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD), false);
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0),
        List.of(orderE, orderF), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(1).getId());

    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().get(1).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().get(2).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(1).getRoute().get(3).getId());
  }

  @Test
  public void test2Vehicle6OrdersInsertVehicleAAtMiddle() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 1}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD), false);
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0),
        List.of(orderE, orderF), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(1).getId());

    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().get(1).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(1).getRoute().get(2).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().get(3).getId());
  }

  @Test
  public void test2Vehicle6OrdersInsertVehicleBAtBegin() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);
    Order orderG = new Order(7, new Point2D(7.0, 7.0), null);
    Order orderH = new Order(8, new Point2D(8.0, 8.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB), false);
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0),
        List.of(orderE, orderF, orderG, orderH), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(7, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(3).getId());

    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(8, result.get().vehicles().get(1).getRoute().get(1).getId());
  }

  @Test
  public void test2Vehicle6OrdersInsertVehicleBAtEnd() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 2}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);
    Order orderG = new Order(7, new Point2D(7.0, 7.0), null);
    Order orderH = new Order(8, new Point2D(8.0, 8.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB), false);
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0),
        List.of(orderE, orderF, orderG, orderH), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(7, result.get().vehicles().get(0).getRoute().get(3).getId());

    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(8, result.get().vehicles().get(1).getRoute().get(1).getId());
  }

  @Test
  public void test2Vehicle6OrdersInsertVehicleBAtMiddle() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0, 1}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false);

    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);
    Order orderG = new Order(7, new Point2D(7.0, 7.0), null);
    Order orderH = new Order(8, new Point2D(8.0, 8.0), null);

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB), false);
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0),
        List.of(orderE, orderF, orderG, orderH), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(7, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(3).getId());

    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(8, result.get().vehicles().get(1).getRoute().get(1).getId());
  }

}
