package de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tools.util.CustomRandom;
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

class TSPInsertionMoveTest {

  private Random rng;

  @BeforeEach
  public void setup() {
    rng = new Random(47110815L);
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
    TSPMove move = new TSPInsertionMove(rng, re);
    VehicleRoute
        vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>(), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test2VehiclesNoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertionMove(rng, re);
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
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D home = new Point2D(0.5, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);

    VehicleRoute vrA = new VehicleRoute(1, home, List.of(orderA, orderB), false);
    re.scoreRouteFull(vrA);

    double expectedResult = home.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo());
    Assertions.assertEquals(expectedResult, vrA.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().size());

    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(1).getId());

    expectedResult = home.distance(orderB.getTo()) + orderB.getTo().distance(orderA.getTo());
    Assertions.assertEquals(expectedResult, result.get().score(), 1e-10);
  }

  @Test
  public void test1Vehicle2OrdersARoundtrip() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D home = new Point2D(0.5, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);

    VehicleRoute vrA = new VehicleRoute(1, home, List.of(orderA, orderB), true);
    re.scoreRouteFull(vrA);

    double expectedResult =
        home.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo()) +
            orderB.getTo().distance(home);
    Assertions.assertEquals(expectedResult, vrA.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().size());

    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(1).getId());

    expectedResult = home.distance(orderB.getTo()) + orderB.getTo().distance(orderA.getTo()) +
        orderA.getTo().distance(home);
    Assertions.assertEquals(expectedResult, result.get().score(), 1e-10);
  }

  @Test
  public void test1Vehicle4Orders() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 1});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D home = new Point2D(0.5, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);

    VehicleRoute vrA = new VehicleRoute(1, home, List.of(orderA, orderB, orderC, orderD), false);
    re.scoreRouteFull(vrA);

    double expectedResult =
        home.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo()) +
            orderB.getTo().distance(orderC.getTo()) + orderC.getTo().distance(orderD.getTo());
    Assertions.assertEquals(expectedResult, vrA.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().size());

    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().get(3).getId());

    expectedResult = home.distance(orderB.getTo()) + orderB.getTo().distance(orderC.getTo()) +
        orderC.getTo().distance(orderA.getTo()) + orderA.getTo().distance(orderD.getTo());
    Assertions.assertEquals(expectedResult, result.get().score(), 1e-10);
  }

  @Test
  public void test1Vehicle2OrdersB() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D home = new Point2D(0.5, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);

    VehicleRoute vrA = new VehicleRoute(1, home, List.of(orderA, orderB), false);
    re.scoreRouteFull(vrA);

    double expectedResult = home.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo());
    Assertions.assertEquals(expectedResult, vrA.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().size());

    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(1).getId());

    expectedResult = home.distance(orderB.getTo()) + orderB.getTo().distance(orderA.getTo());
    Assertions.assertEquals(expectedResult, result.get().score(), 1e-10);
  }

  @Test
  public void test1Vehicle3OrdersA() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0}, new boolean[] {true});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D home = new Point2D(0.5, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);

    VehicleRoute vrA = new VehicleRoute(1, home, List.of(orderA, orderB, orderC), false);
    re.scoreRouteFull(vrA);

    double expectedResult =
        home.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo()) +
            orderB.getTo().distance(orderC.getTo());
    Assertions.assertEquals(expectedResult, vrA.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().size());

    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(2).getId());

    expectedResult = home.distance(orderB.getTo()) + orderB.getTo().distance(orderA.getTo()) +
        orderA.getTo().distance(orderC.getTo());
    Assertions.assertEquals(expectedResult, result.get().score(), 1e-10);
  }

  @Test
  public void test1Vehicle3OrdersB() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D home = new Point2D(0.5, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);

    VehicleRoute vrA = new VehicleRoute(1, home, List.of(orderA, orderB, orderC), false);
    re.scoreRouteFull(vrA);

    double expectedResult =
        home.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo()) +
            orderB.getTo().distance(orderC.getTo());
    Assertions.assertEquals(expectedResult, vrA.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(1, result.get().vehicles().size());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(2).getId());

    expectedResult = home.distance(orderA.getTo()) + orderA.getTo().distance(orderC.getTo()) +
        orderC.getTo().distance(orderB.getTo());
    Assertions.assertEquals(expectedResult, result.get().score(), 1e-10);
  }

  @Test
  public void test2VehicleANoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Point2D homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA, new LinkedList<>(), false);
    VehicleRoute vrB = new VehicleRoute(2, homeB, List.of(orderA, orderB, orderC), false);

    re.scoreRouteFull(vrA);
    re.scoreRouteFull(vrB);

    double expectedResultA = Double.NaN;
    double expectedResultB =
        homeB.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo()) +
            orderB.getTo().distance(orderC.getTo());

    Assertions.assertEquals(expectedResultA, vrA.getScore(), 1e-10);
    Assertions.assertEquals(expectedResultB, vrB.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(1).getRoute().get(1).getId());

    expectedResultA = homeA.distance(orderB.getTo());
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
    expectedResultB = homeB.distance(orderA.getTo()) + orderA.getTo().distance(orderC.getTo());
    Assertions.assertEquals(expectedResultB, result.get().vehicles().get(1).getScore(), 1e-10);
  }

  @Test
  public void test2VehicleBNoOrdersRemoveLastFromA() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Point2D homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA, List.of(orderA), false);
    VehicleRoute vrB = new VehicleRoute(2, homeB, new LinkedList<>(), false);

    re.scoreRouteFull(vrA);
    re.scoreRouteFull(vrB);

    double expectedResultA = homeA.distance(orderA.getTo());
    double expectedResultB = Double.NaN;

    Assertions.assertEquals(expectedResultA, vrA.getScore(), 1e-10);
    Assertions.assertEquals(expectedResultB, vrB.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(0, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(1, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(1).getRoute().get(0).getId());

    expectedResultA = Double.NaN;
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
    expectedResultB = homeB.distance(orderA.getTo());
    Assertions.assertEquals(expectedResultB, result.get().vehicles().get(1).getScore(), 1e-10);
  }

  @Test
  public void test2VehicleBNoOrdersRemoveLastFromARoundtrip() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Point2D homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA, List.of(orderA), true);
    VehicleRoute vrB = new VehicleRoute(2, homeB, new LinkedList<>(), true);

    re.scoreRouteFull(vrA);
    re.scoreRouteFull(vrB);

    double expectedResultA = homeA.distance(orderA.getTo()) + orderA.getTo().distance(homeA);
    double expectedResultB = Double.NaN;

    Assertions.assertEquals(expectedResultA, vrA.getScore(), 1e-10);
    Assertions.assertEquals(expectedResultB, vrB.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(0, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(1, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(1).getRoute().get(0).getId());

    expectedResultA = Double.NaN;
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
    expectedResultB = homeB.distance(orderA.getTo()) + orderA.getTo().distance(homeB);
    Assertions.assertEquals(expectedResultB, result.get().vehicles().get(1).getScore(), 1e-10);
  }

  @Test
  public void test2VehicleANoOrdersRemoveLastFromB() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Point2D homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA, new LinkedList<>(), false);
    VehicleRoute vrB = new VehicleRoute(2, homeB, List.of(orderA), false);

    re.scoreRouteFull(vrA);
    re.scoreRouteFull(vrB);

    double expectedResultA = Double.NaN;
    double expectedResultB = homeB.distance(orderA.getTo());

    Assertions.assertEquals(expectedResultA, vrA.getScore(), 1e-10);
    Assertions.assertEquals(expectedResultB, vrB.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(0, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());

    expectedResultA = homeA.distance(orderA.getTo());
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
    expectedResultB = Double.NaN;
    Assertions.assertEquals(expectedResultB, result.get().vehicles().get(1).getScore(), 1e-10);
  }

  @Test
  public void test2VehicleBNoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Point2D homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA, List.of(orderA, orderB, orderC), false);
    VehicleRoute vrB = new VehicleRoute(2, homeB, new LinkedList<>(), false);

    re.scoreRouteFull(vrA);
    re.scoreRouteFull(vrB);

    double expectedResultA =
        homeA.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo()) +
            orderB.getTo().distance(orderC.getTo());
    double expectedResultB = Double.NaN;

    Assertions.assertEquals(expectedResultA, vrA.getScore(), 1e-10);
    Assertions.assertEquals(expectedResultB, vrB.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(1, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().get(0).getId());

    expectedResultA = homeA.distance(orderA.getTo()) + orderA.getTo().distance(orderC.getTo());
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
    expectedResultB = homeB.distance(orderB.getTo());
    Assertions.assertEquals(expectedResultB, result.get().vehicles().get(1).getScore(), 1e-10);
  }

  @Test
  public void test2Vehicle6OrdersRemoveAMiddleInsertBEnd() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 3}, new boolean[] {true});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Point2D homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA, List.of(orderA, orderB, orderC), false);
    VehicleRoute vrB = new VehicleRoute(2, homeB, List.of(orderD, orderE, orderF), false);

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

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(1).getId());

    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(1).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().get(2).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().get(3).getId());

    expectedResultA = homeA.distance(orderA.getTo()) + orderA.getTo().distance(orderC.getTo());
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
    expectedResultB = homeB.distance(orderD.getTo()) + orderD.getTo().distance(orderE.getTo()) +
        orderE.getTo().distance(orderF.getTo()) + orderF.getTo().distance(orderB.getTo());
    Assertions.assertEquals(expectedResultB, result.get().vehicles().get(1).getScore(), 1e-10);
  }

  @Test
  public void test2Vehicle6OrdersRemoveAMiddleInsertBEndRoundtrip() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 3}, new boolean[] {true});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Point2D homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA, List.of(orderA, orderB, orderC), true);
    VehicleRoute vrB = new VehicleRoute(2, homeB, List.of(orderD, orderE, orderF), true);

    re.scoreRouteFull(vrA);
    re.scoreRouteFull(vrB);

    double expectedResultA =
        homeA.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo()) +
            orderB.getTo().distance(orderC.getTo()) + orderC.getTo().distance(homeA);
    double expectedResultB =
        homeB.distance(orderD.getTo()) + orderD.getTo().distance(orderE.getTo()) +
            orderE.getTo().distance(orderF.getTo()) + orderF.getTo().distance(homeB);

    Assertions.assertEquals(expectedResultA, vrA.getScore(), 1e-10);
    Assertions.assertEquals(expectedResultB, vrB.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(1).getId());

    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(1).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().get(2).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().get(3).getId());

    expectedResultA = homeA.distance(orderA.getTo()) + orderA.getTo().distance(orderC.getTo()) +
        orderC.getTo().distance(homeA);
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
    expectedResultB = homeB.distance(orderD.getTo()) + orderD.getTo().distance(orderE.getTo()) +
        orderE.getTo().distance(orderF.getTo()) + orderF.getTo().distance(orderB.getTo()) +
        orderB.getTo().distance(homeB);
    Assertions.assertEquals(expectedResultB, result.get().vehicles().get(1).getScore(), 1e-10);
  }


  @Test
  public void test2Vehicle6OrdersRemoveAMiddleInsertBBegin() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0}, new boolean[] {true});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Point2D homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA, List.of(orderA, orderB, orderC), false);
    VehicleRoute vrB = new VehicleRoute(2, homeB, List.of(orderD, orderE, orderF), false);

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

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(1).getId());

    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().get(1).getId());
    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(2).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().get(3).getId());

    expectedResultA = homeA.distance(orderA.getTo()) + orderA.getTo().distance(orderC.getTo());
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
    expectedResultB = homeB.distance(orderB.getTo()) + orderB.getTo().distance(orderD.getTo()) +
        orderD.getTo().distance(orderE.getTo()) + orderE.getTo().distance(orderF.getTo());
    Assertions.assertEquals(expectedResultB, result.get().vehicles().get(1).getScore(), 1e-10);
  }

  @Test
  public void test2Vehicle6OrdersRemoveAEndInsertBBegin() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {2, 0}, new boolean[] {true});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Point2D homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA, List.of(orderA, orderB, orderC), false);
    VehicleRoute vrB = new VehicleRoute(2, homeB, List.of(orderD, orderE, orderF), false);

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

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(1).getId());

    Assertions.assertEquals(3, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().get(1).getId());
    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(2).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().get(3).getId());

    expectedResultA = homeA.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo());
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
    expectedResultB = homeB.distance(orderC.getTo()) + orderC.getTo().distance(orderD.getTo()) +
        orderD.getTo().distance(orderE.getTo()) + orderE.getTo().distance(orderF.getTo());
    Assertions.assertEquals(expectedResultB, result.get().vehicles().get(1).getScore(), 1e-10);
  }

  @Test
  public void test2Vehicle6OrdersRemoveBMiddleInsertAEnd() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 3}, new boolean[] {false});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Point2D homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA, List.of(orderA, orderB, orderC), false);
    VehicleRoute vrB = new VehicleRoute(2, homeB, List.of(orderD, orderE, orderF), false);

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

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(5, result.get().vehicles().get(0).getRoute().get(3).getId());

    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().get(1).getId());

    expectedResultA = homeA.distance(orderA.getTo()) + orderA.getTo().distance(orderB.getTo()) +
        orderB.getTo().distance(orderC.getTo()) + orderC.getTo().distance(orderE.getTo());
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
    expectedResultB = homeB.distance(orderD.getTo()) + orderD.getTo().distance(orderF.getTo());
    Assertions.assertEquals(expectedResultB, result.get().vehicles().get(1).getScore(), 1e-10);
  }

  @Test
  public void test2Vehicle6OrdersRemoveBMiddleInsertABegin() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {1, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Point2D homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA, List.of(orderA, orderB, orderC), false);
    VehicleRoute vrB = new VehicleRoute(2, homeB, List.of(orderD, orderE, orderF), false);

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

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(5, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(3).getId());

    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(6, result.get().vehicles().get(1).getRoute().get(1).getId());

    expectedResultA = homeA.distance(orderE.getTo()) + orderE.getTo().distance(orderA.getTo()) +
        orderA.getTo().distance(orderB.getTo()) + orderB.getTo().distance(orderC.getTo());
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
    expectedResultB = homeB.distance(orderD.getTo()) + orderD.getTo().distance(orderF.getTo());
    Assertions.assertEquals(expectedResultB, result.get().vehicles().get(1).getScore(), 1e-10);
  }

  @Test
  public void test2Vehicle6OrdersRemoveBEndInsertABegin() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {2, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertionMove(rng, re);

    Point2D homeA = new Point2D(0.5, 0.5);
    Point2D homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);

    VehicleRoute vrA = new VehicleRoute(1, homeA, List.of(orderA, orderB, orderC), false);
    VehicleRoute vrB = new VehicleRoute(2, homeB, List.of(orderD, orderE, orderF), false);

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

    Assertions.assertEquals(2, result.get().vehicles().size());
    Assertions.assertEquals(4, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(2, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(6, result.get().vehicles().get(0).getRoute().get(0).getId());
    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().get(1).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(2).getId());
    Assertions.assertEquals(3, result.get().vehicles().get(0).getRoute().get(3).getId());

    Assertions.assertEquals(4, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(5, result.get().vehicles().get(1).getRoute().get(1).getId());

    expectedResultA = homeA.distance(orderF.getTo()) + orderF.getTo().distance(orderA.getTo()) +
        orderA.getTo().distance(orderB.getTo()) + orderB.getTo().distance(orderC.getTo());
    Assertions.assertEquals(expectedResultA, result.get().vehicles().get(0).getScore(), 1e-10);
    expectedResultB = homeB.distance(orderD.getTo()) + orderD.getTo().distance(orderE.getTo());
    Assertions.assertEquals(expectedResultB, result.get().vehicles().get(1).getScore(), 1e-10);
  }
}