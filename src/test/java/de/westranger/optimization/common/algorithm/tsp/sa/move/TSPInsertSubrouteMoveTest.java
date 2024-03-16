package de.westranger.optimization.common.algorithm.tsp.sa.move;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tools.util.CustomRandom;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TSPInsertSubrouteMoveTest {
  private Random rng;

  private RouteEvaluator re;
  private Point2D homeA;
  private Point2D homeB;


  private Map<Integer, Order> orderMap;

  @BeforeEach
  public void setup() {
    rng = new Random(47110815);
    this.re = new RouteEvaluator();
    this.homeA = new Point2D(0.5, 0.5);
    this.homeB = new Point2D(2.0, 0.5);
    Order orderA = new Order(1, new Point2D(1.0, 1.0), null);
    Order orderB = new Order(2, new Point2D(2.0, 2.0), null);
    Order orderC = new Order(3, new Point2D(3.0, 3.0), null);
    Order orderD = new Order(4, new Point2D(4.0, 4.0), null);
    Order orderE = new Order(5, new Point2D(5.0, 5.0), null);
    Order orderF = new Order(6, new Point2D(6.0, 6.0), null);
    Order orderG = new Order(7, new Point2D(7.0, 7.0), null);
    Order orderH = new Order(8, new Point2D(8.0, 8.0), null);

    this.orderMap = new TreeMap<>();
    orderMap.put(orderA.getId(), orderA);
    orderMap.put(orderB.getId(), orderB);
    orderMap.put(orderC.getId(), orderC);
    orderMap.put(orderD.getId(), orderD);
    orderMap.put(orderE.getId(), orderE);
    orderMap.put(orderF.getId(), orderF);
    orderMap.put(orderG.getId(), orderG);
    orderMap.put(orderH.getId(), orderH);
  }

  @Test
  public void testNoVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> move.performMove(new LinkedList<>()));
  }

  @Test
  public void testThreeVehicle() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

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
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);
    VehicleRoute
        vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>(), false);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test2VehiclesNoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);
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
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

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
    Random rng = new CustomRandom(new int[] {1, 0, 0});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4), List.of(2, 3, 1, 4), false);
  }

  @Test
  public void test1Vehicle4OrdersRemoveMiddleInsertEnd() {
    Random rng = new CustomRandom(new int[] {1, 0, 2});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4), List.of(1, 4, 2, 3), false);
  }

  @Test
  public void test1Vehicle4OrdersRemoveMiddleInsertMiddle() {
    Random rng = new CustomRandom(new int[] {0, 0, 1});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4), List.of(3, 1, 2, 4), false);
  }

  @Test
  public void test1Vehicle4OrdersRemoveMiddleInsertBeginReverse() {
    Random rng = new CustomRandom(new int[] {2, 0, 0});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, true , false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4), List.of(4, 3, 1, 2), false);
  }

  @Test
  public void test1Vehicle4OrdersRemoveMiddleInsertEndReverse() {
    Random rng = new CustomRandom(new int[] {2, 0, 2});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, true , false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4), List.of(1, 2, 4, 3), false);
  }

  @Test
  public void test1Vehicle4OrdersRemoveMiddleInsertAlmostEndRoundtrip() {
    Random rng = new CustomRandom(new int[] {1, 1, 2});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4, 5, 6), List.of(1, 5, 2, 3, 4, 6), true);
  }


  @Test
  public void test1Vehicle4OrdersRemoveMiddleInsertMiddleReverse() {
    Random rng = new CustomRandom(new int[] {1, 0, 1});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, true , false);

    evaluateMoveOneVehicle(move, List.of(1, 2, 3, 4), List.of(1, 3, 2, 4), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveMiddleInsertVehicleABegin() {
    Random rng = new CustomRandom(new int[] {1, 0, 0}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 4), List.of(2, 3, 5, 6, 7, 8), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveMiddleInsertVehicleAEnd() {
    Random rng = new CustomRandom(new int[] {1, 0, 4}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 4), List.of(5, 6, 7, 8, 2, 3), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveMiddleInsertVehicleAMiddle() {
    Random rng = new CustomRandom(new int[] {1, 0, 2}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 4), List.of(5, 6, 2, 3, 7, 8), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveMiddleInsertVehicleBBegin() {
    Random rng = new CustomRandom(new int[] {1, 0, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(6, 7, 1, 2, 3, 4), List.of(5, 8), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveMiddleInsertVehicleBEnd() {
    Random rng = new CustomRandom(new int[] {1, 0, 4}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 2, 3, 4, 6, 7), List.of(5, 8), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveMiddleInsertVehicleBMiddle() {
    Random rng = new CustomRandom(new int[] {1, 0, 2}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 2, 6, 7, 3, 4), List.of(5, 8), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveEndInsertVehicleBMiddle() {
    Random rng = new CustomRandom(new int[] {2, 0, 2}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 2, 7, 8, 3, 4), List.of(5, 6), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveEndInsertVehicleBMiddleReverse() {
    Random rng = new CustomRandom(new int[] {2, 0, 2}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, true, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 2, 8, 7, 3, 4), List.of(5, 6), false);
  }

  @Test
  public void test2Vehicle8OrdersRemoveBeginInsertVehicleBMiddleReverse() {
    Random rng = new CustomRandom(new int[] {0, 0, 2}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, true, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6, 7, 8),
        List.of(1, 2, 6, 5, 3, 4), List.of(7, 8), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveMiddleInsertVehicleABegin() {
    Random rng = new CustomRandom(new int[] {1, 0, 0}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6),
        List.of(1, 4), List.of(2, 3, 5, 6), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveMiddleInsertVehicleAEnd() {
    Random rng = new CustomRandom(new int[] {1, 0, 2}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6),
        List.of(1, 4), List.of(5, 6, 2, 3), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveMiddleInsertVehicleAMiddle() {
    Random rng = new CustomRandom(new int[] {1, 0, 1}, new boolean[] {true});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4), List.of(5, 6),
        List.of(1, 4), List.of(5, 2, 3, 6), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveMiddleInsertVehicleBBegin() {
    Random rng = new CustomRandom(new int[] {1, 0, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2), List.of(5, 6, 7, 8),
        List.of(6, 7, 1, 2), List.of(5, 8), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveMiddleInsertVehicleBEnd() {
    Random rng = new CustomRandom(new int[] {1, 0, 2}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2), List.of(5, 6, 7, 8),
        List.of(1, 2, 6, 7), List.of(5, 8), false);
  }

  @Test
  public void test2Vehicle6OrdersRemoveMiddleInsertVehicleBMiddle() {
    Random rng = new CustomRandom(new int[] {1, 0, 1}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2), List.of(5, 6, 7, 8),
        List.of(1, 6, 7, 2), List.of(5, 8), false);
  }


  @Test
  public void test2Vehicle6OrdersRemoveMiddleInsertVehicleBEmpty() {
    Random rng = new CustomRandom(new int[] {2, 0, 0}, new boolean[] {false});
    TSPMove move = new TSPInsertSubrouteMove(rng, re, false, false);

    evaluateMoveTwoVehicles(move, List.of(1, 2, 3, 4, 5), List.of(),
        List.of(1, 2, 5), List.of(3, 4), false);
  }


  private void evaluateMoveOneVehicle(TSPMove move, List<Integer> base, List<Integer> goal,
                                      boolean isRoundtrip) {

    VehicleRoute vrA = new VehicleRoute(1, homeA, createList(base), isRoundtrip);

    re.scoreRouteFull(vrA);

    double expectedResultA = scoreExpectedRoute(createList(base), homeA, isRoundtrip);

    assertEquals(expectedResultA, vrA.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isPresent());

    assertEquals(1, result.get().vehicles().size());
    checkVehicle(result.get().vehicles().get(0), goal, homeA);
  }

  private void evaluateMoveTwoVehicles(TSPMove move, List<Integer> baseA, List<Integer> baseB,
                                       List<Integer> goalA, List<Integer> goalB,
                                       boolean isRoundtrip) {

    VehicleRoute vrA = new VehicleRoute(1, homeA, createList(baseA), isRoundtrip);
    VehicleRoute vrB = new VehicleRoute(2, homeB, createList(baseB), isRoundtrip);

    re.scoreRouteFull(vrA);
    re.scoreRouteFull(vrB);

    double expectedResultA = scoreExpectedRoute(createList(baseA), homeA, isRoundtrip);
    double expectedResultB = scoreExpectedRoute(createList(baseB), homeB, isRoundtrip);

    assertEquals(expectedResultA, vrA.getScore(), 1e-10);
    assertEquals(expectedResultB, vrB.getScore(), 1e-10);

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    assertEquals(2, result.get().vehicles().size());
    checkVehicle(result.get().vehicles().get(0), goalA, homeA);
    checkVehicle(result.get().vehicles().get(1), goalB, homeB);
  }

  private void checkVehicle(VehicleRoute vr, List<Integer> expectedRoute, Point2D home) {
    assertEquals(expectedRoute.size(), vr.getRoute().size());

    if (!expectedRoute.isEmpty()) {
      List<Order> orders = createList(expectedRoute);

      for (int i = 0; i < expectedRoute.size(); i++) {
        assertEquals(expectedRoute.get(i), vr.getRoute().get(i).getId());
      }

      for (int i = 1; i < expectedRoute.size(); i++) {
        double dist = vr.getLocationAt(i).getTo().distance(vr.getLocationAt(i - 1).getTo());
        assertEquals(dist, vr.getDistanceScoreAt(i), 1e-6);
      }

      double sum = scoreExpectedRoute(orders, home, vr.isRoundtrip());
      assertEquals(sum, vr.getScore(), 1e-10);
    }
  }

  private List<Order> createList(List<Integer> expectedRoute) {
    List<Order> orders = new ArrayList<>(expectedRoute.size());
    for (int id : expectedRoute) {
      orders.add(orderMap.get(id));
    }
    return orders;
  }

  private double scoreExpectedRoute(List<Order> orders, Point2D home, boolean isRoundTrip) {
    if (orders.isEmpty()) {
      return Double.NaN;
    }
    double sum = home.distance(orders.get(0).getTo());
    for (int i = 1; i < orders.size(); i++) {
      sum += orders.get(i - 1).getTo().distance(orders.get(i).getTo());
    }
    if (isRoundTrip) {
      sum += orders.get(orders.size() - 1).getTo().distance(home);
    }
    return sum;
  }

}
