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

class TSPSwapMoveTest {

  private Random rng;

  @BeforeEach
  public void setup() {
    rng = new Random(47110815);
  }

  @Test
  public void testNoVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re);

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> move.performMove(new LinkedList<>()));
  }

  @Test
  public void testThreeVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re);

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
    TSPMove move = new TSPSwapMove(rng, re);
    VehicleRoute
        vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>(), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test2VehiclesNoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re);
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
    TSPMove move = new TSPSwapMove(rng, re);

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
    TSPMove move = new TSPSwapMove(rng, re);

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
    TSPMove move = new TSPSwapMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Point2D homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA, List.of(orderA), false);
    VehicleRoute vrB = new VehicleRoute(2, homeB, List.of(orderB), false);

    re.scoreRouteFull(vrA);
    re.scoreRouteFull(vrB);

    double expectedResultA = homeA.distance(orderA.getTo());
    double expectedResultB = homeB.distance(orderB.getTo());

    Assertions.assertEquals(expectedResultA, vrA.getScore(), 1e-10);
    Assertions.assertEquals(expectedResultB, vrB.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(1, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(0).getId());

    expectedResultA = homeA.distance(orderB.getTo());
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
    expectedResultB = homeB.distance(orderA.getTo());
    Assertions.assertEquals(expectedResultB, result.get().vehicles().get(1).getScore(), 1e-10);
  }

  @Test
  public void test2Vehicle6Orders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Point2D homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);

    VehicleRoute vrA =
        new VehicleRoute(1, homeA, List.of(orderA, orderB, orderC), false);
    VehicleRoute vrB =
        new VehicleRoute(2, homeB, List.of(orderD, orderE, orderF), false);

    re.scoreRouteFull(vrA);
    re.scoreRouteFull(vrB);

    double expectedResultA =
        homeA.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo()) +
            orderB.getTo().distance(orderC.getTo());
    double expectedResultB =
        homeB.distance(orderD.getTo()) + orderD.getTo().distance(orderE.getTo()) +
            orderE.getTo().distance(orderF.getTo());

    Assertions.assertEquals(expectedResultA, vrA.getScore(), 1e-10);
    Assertions.assertEquals(expectedResultB, vrB.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());

    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(3, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(5, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(2).getId());

    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(1).getRoute().get(1).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().get(2).getId());

    expectedResultA = homeA.distance(orderE.getTo()) + orderE.getTo().distance(orderB.getTo()) +
        orderB.getTo().distance(orderC.getTo());
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
    expectedResultB = homeB.distance(orderD.getTo()) + orderD.getTo().distance(orderA.getTo()) +
        orderA.getTo().distance(orderF.getTo());
    Assertions.assertEquals(expectedResultB, result.get().vehicles().get(1).getScore(), 1e-10);
  }

  @Test
  public void test2Vehicle6OrdersDifferentLengths() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 3});
    TSPMove move = new TSPSwapMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Point2D homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);

    VehicleRoute vrA =
        new VehicleRoute(1, homeA, List.of(orderA, orderB), false);
    VehicleRoute vrB =
        new VehicleRoute(2, homeB, List.of(orderC, orderD, orderE, orderF), false);

    re.scoreRouteFull(vrA);
    re.scoreRouteFull(vrB);

    double expectedResultA =
        homeA.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo());
    double expectedResultB =
        homeB.distance(orderC.getTo()) + orderC.getTo().distance(orderD.getTo()) +
            orderD.getTo().distance(orderE.getTo()) + orderE.getTo().distance(orderF.getTo());

    Assertions.assertEquals(expectedResultA, vrA.getScore(), 1e-10);
    Assertions.assertEquals(expectedResultB, vrB.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());

    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(1).getId());

    Assertions.assertEquals(3, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().get(1).getId());
    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(2).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(1).getRoute().get(3).getId());

    expectedResultA = homeA.distance(orderF.getTo()) + orderF.getTo().distance(orderB.getTo());
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
    expectedResultB = homeB.distance(orderC.getTo()) + orderC.getTo().distance(orderD.getTo()) +
        orderD.getTo().distance(orderE.getTo()) + orderE.getTo().distance(orderA.getTo());
    Assertions.assertEquals(expectedResultB, result.get().vehicles().get(1).getScore(), 1e-10);
  }

  @Test
  public void test1Vehicle6OrdersSameIndex() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0});
    TSPMove move = new TSPSwapMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA,
        List.of(orderA, orderB, orderC, orderD, orderE, orderF), false);

    re.scoreRouteFull(vrA);

    double expectedResultA =
        homeA.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo()) +
            orderB.getTo().distance(orderC.getTo()) + orderC.getTo().distance(orderD.getTo()) +
            orderD.getTo().distance(orderE.getTo()) + orderE.getTo().distance(orderF.getTo());

    Assertions.assertEquals(expectedResultA, vrA.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().size());

    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(3).getId());
    Assertions.assertEquals(5, result.get().vehicles().get(0).getRoute().get(4).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().get(5).getId());

    expectedResultA = homeA.distance(orderC.getTo()) +
        orderC.getTo().distance(orderB.getTo()) + orderB.getTo().distance(orderD.getTo()) +
        orderD.getTo().distance(orderE.getTo()) + orderE.getTo().distance(orderF.getTo());
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
  }

  @Test
  public void test1Vehicle6OrdersA() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {2, 4});
    TSPMove move = new TSPSwapMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA,
        List.of(orderA, orderB, orderC, orderD, orderE, orderF), false);

    re.scoreRouteFull(vrA);

    double expectedResultA =
        homeA.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo()) +
            orderB.getTo().distance(orderC.getTo()) + orderC.getTo().distance(orderD.getTo()) +
            orderD.getTo().distance(orderE.getTo()) + orderE.getTo().distance(orderF.getTo());

    Assertions.assertEquals(expectedResultA, vrA.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(3).getId());
    Assertions.assertEquals(5, result.get().vehicles().get(0).getRoute().get(4).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(5).getId());

    expectedResultA = homeA.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo()) +
        orderB.getTo().distance(orderF.getTo()) + orderF.getTo().distance(orderD.getTo()) +
        orderD.getTo().distance(orderE.getTo()) + orderE.getTo().distance(orderC.getTo());
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
  }

  @Test
  public void test1Vehicle6OrdersB() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {4, 2});
    TSPMove move = new TSPSwapMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA,
        List.of(orderA, orderB, orderC, orderD, orderE, orderF), false);

    re.scoreRouteFull(vrA);

    double expectedResultA =
        homeA.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo()) +
            orderB.getTo().distance(orderC.getTo()) + orderC.getTo().distance(orderD.getTo()) +
            orderD.getTo().distance(orderE.getTo()) + orderE.getTo().distance(orderF.getTo());

    Assertions.assertEquals(expectedResultA, vrA.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(5, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(3).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(4).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().get(5).getId());

    expectedResultA = homeA.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo()) +
        orderB.getTo().distance(orderE.getTo()) + orderE.getTo().distance(orderD.getTo()) +
        orderD.getTo().distance(orderC.getTo()) + orderC.getTo().distance(orderF.getTo());
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
  }
}