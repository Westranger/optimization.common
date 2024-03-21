package de.westranger.optimization.common.algorithm.tsp.sa.move;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public class MoveTestBase {

  protected Random rng;
  protected RouteEvaluator re;
  protected Point2D homeA;
  protected Point2D homeB;

  private Map<Integer, Order> orderMap;

  @BeforeEach
  public void setup() {
    rng = new Random(47110815L);
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

  protected void evaluateMoveOneVehicle(TSPMove move, List<Integer> base, List<Integer> goal,
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

  protected void evaluateMoveTwoVehicles(TSPMove move, List<Integer> baseA, List<Integer> baseB,
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

  protected void checkVehicle(VehicleRoute vr, List<Integer> expectedRoute, Point2D home) {
    assertEquals(expectedRoute.size(), vr.getRoute().size());

    if (!expectedRoute.isEmpty()) {
      List<Order> orders = createList(expectedRoute);

      for (int i = 0; i < expectedRoute.size(); i++) {
        assertEquals(expectedRoute.get(i), vr.getRoute().get(i).getId());
      }

      double sum = scoreExpectedRoute(orders, home, vr.isRoundtrip());
      assertEquals(sum, vr.getScore(), 1e-10);
    }
  }

  protected List<Order> createList(List<Integer> expectedRoute) {
    List<Order> orders = new ArrayList<>(expectedRoute.size());
    for (int id : expectedRoute) {
      orders.add(orderMap.get(id));
    }
    return orders;
  }

  protected double scoreExpectedRoute(List<Order> orders, Point2D home, boolean isRoundTrip) {
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
