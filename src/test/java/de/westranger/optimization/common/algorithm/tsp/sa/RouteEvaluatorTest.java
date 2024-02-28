package de.westranger.optimization.common.algorithm.tsp.sa;

import static org.junit.jupiter.api.Assertions.*;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import java.util.List;
import org.junit.jupiter.api.Test;

class RouteEvaluatorTest {

  @Test
  void scoreRouteFullNoRoundtrip() {
    Point2D start = new Point2D(0.5, 0.5);
    Point2D ptA = new Point2D(1.0, 1.0);
    Point2D ptB = new Point2D(2.0, 2.0);
    Point2D ptC = new Point2D(3.0, 3.0);
    Point2D ptD = new Point2D(4.0, 4.0);
    Point2D ptE = new Point2D(5.0, 5.0);
    Point2D ptF = new Point2D(6.0, 6.0);

    final double score =
        start.distance(ptA) + ptA.distance(ptB) + ptB.distance(ptC) + ptC.distance(ptD) +
            ptD.distance(ptE) + ptE.distance(ptF);

    Order orderA = new Order(1, ptA, null);
    Order orderB = new Order(2, ptB, null);
    Order orderC = new Order(3, ptC, null);
    Order orderD = new Order(4, ptD, null);
    Order orderE = new Order(5, ptE, null);
    Order orderF = new Order(6, ptF, null);

    List<Order> order = List.of(orderA, orderB, orderC, orderD, orderE, orderF);
    VehicleRoute vr = new VehicleRoute(1, start, order, false);

    RouteEvaluator re = new RouteEvaluator();
    re.scoreRouteFull(vr);
    assertEquals(score, vr.getScore(), 1e-10);

    assertEquals(start.distance(orderA.getTo()), vr.getDistanceScoreAt(0), 1e-10);
    assertEquals(orderA.getTo().distance(orderB.getTo()), vr.getDistanceScoreAt(1), 1e-10);
    assertEquals(orderB.getTo().distance(orderC.getTo()), vr.getDistanceScoreAt(2), 1e-10);
    assertEquals(orderC.getTo().distance(orderD.getTo()), vr.getDistanceScoreAt(3), 1e-10);
    assertEquals(orderD.getTo().distance(orderE.getTo()), vr.getDistanceScoreAt(4), 1e-10);
    assertEquals(orderE.getTo().distance(orderF.getTo()), vr.getDistanceScoreAt(5), 1e-10);
  }

  @Test
  void scoreRouteFullRoundtrip() {
    Point2D start = new Point2D(0.5, 0.5);
    Point2D ptA = new Point2D(1.0, 1.0);
    Point2D ptB = new Point2D(2.0, 2.0);
    Point2D ptC = new Point2D(3.0, 3.0);
    Point2D ptD = new Point2D(4.0, 4.0);
    Point2D ptE = new Point2D(5.0, 5.0);
    Point2D ptF = new Point2D(6.0, 6.0);

    final double score =
        start.distance(ptA) + ptA.distance(ptB) + ptB.distance(ptC) + ptC.distance(ptD) +
            ptD.distance(ptE) + ptE.distance(ptF) + ptF.distance(start);

    Order orderA = new Order(1, ptA, null);
    Order orderB = new Order(2, ptB, null);
    Order orderC = new Order(3, ptC, null);
    Order orderD = new Order(4, ptD, null);
    Order orderE = new Order(5, ptE, null);
    Order orderF = new Order(6, ptF, null);

    List<Order> order = List.of(orderA, orderB, orderC, orderD, orderE, orderF);
    VehicleRoute vr = new VehicleRoute(1, start, order, true);

    RouteEvaluator re = new RouteEvaluator();
    re.scoreRouteFull(vr);
    assertEquals(score, vr.getScore(), 1e-10);

    assertEquals(start.distance(orderA.getTo()), vr.getDistanceScoreAt(0), 1e-10);
    assertEquals(orderA.getTo().distance(orderB.getTo()), vr.getDistanceScoreAt(1), 1e-10);
    assertEquals(orderB.getTo().distance(orderC.getTo()), vr.getDistanceScoreAt(2), 1e-10);
    assertEquals(orderC.getTo().distance(orderD.getTo()), vr.getDistanceScoreAt(3), 1e-10);
    assertEquals(orderD.getTo().distance(orderE.getTo()), vr.getDistanceScoreAt(4), 1e-10);
    assertEquals(orderE.getTo().distance(orderF.getTo()), vr.getDistanceScoreAt(5), 1e-10);
    assertEquals(orderF.getTo().distance(start), vr.getDistanceScoreAt(6), 1e-10);
  }

  @Test
  void scoreRoutePartialNoRoundtrip() {
    Point2D start = new Point2D(1.0, 1.0);
    Point2D ptA = new Point2D(1.0, 1.0);
    Point2D ptB = new Point2D(2.0, 2.0);
    Point2D ptC = new Point2D(3.0, 3.0);
    Point2D ptD = new Point2D(4.0, 4.0);
    Point2D ptE = new Point2D(5.0, 5.0);
    Point2D ptF = new Point2D(6.0, 6.0);

    final double score =
        start.distance(ptA) + ptA.distance(ptB) + ptB.distance(ptC) + ptC.distance(ptD) +
            ptD.distance(ptE) + ptE.distance(ptF);

    Order orderA = new Order(1, ptA, null);
    Order orderB = new Order(2, ptB, null);
    Order orderC = new Order(3, ptC, null);
    Order orderD = new Order(4, ptD, null);
    Order orderE = new Order(5, ptE, null);
    Order orderF = new Order(6, ptF, null);

    List<Order> order = List.of(orderA, orderB, orderC, orderD, orderE, orderF);
    VehicleRoute vr = new VehicleRoute(1, start, order, false);
    vr.setScore(0.0);

    RouteEvaluator re = new RouteEvaluator();
    re.scoreRoutePartial(vr, List.of(0, 1, 2, 3, 4, 5));
    assertEquals(score, vr.getScore(), 1e-10);

    assertEquals(start.distance(orderA.getTo()), vr.getDistanceScoreAt(0), 1e-10);
    assertEquals(orderA.getTo().distance(orderB.getTo()), vr.getDistanceScoreAt(1), 1e-10);
    assertEquals(orderB.getTo().distance(orderC.getTo()), vr.getDistanceScoreAt(2), 1e-10);
    assertEquals(orderC.getTo().distance(orderD.getTo()), vr.getDistanceScoreAt(3), 1e-10);
    assertEquals(orderD.getTo().distance(orderE.getTo()), vr.getDistanceScoreAt(4), 1e-10);
    assertEquals(orderE.getTo().distance(orderF.getTo()), vr.getDistanceScoreAt(5), 1e-10);
  }

  @Test
  void scoreRoutePartialRoundtrip() {
    Point2D start = new Point2D(1.0, 1.0);
    Point2D ptA = new Point2D(1.0, 1.0);
    Point2D ptB = new Point2D(2.0, 2.0);
    Point2D ptC = new Point2D(3.0, 3.0);
    Point2D ptD = new Point2D(4.0, 4.0);
    Point2D ptE = new Point2D(5.0, 5.0);
    Point2D ptF = new Point2D(6.0, 6.0);

    double score =
        start.distance(ptA) + ptA.distance(ptB) + ptB.distance(ptC) + ptC.distance(ptD) +
            ptD.distance(ptE) + ptE.distance(ptF) + ptF.distance(start);

    Order orderA = new Order(1, ptA, null);
    Order orderB = new Order(2, ptB, null);
    Order orderC = new Order(3, ptC, null);
    Order orderD = new Order(4, ptD, null);
    Order orderE = new Order(5, ptE, null);
    Order orderF = new Order(6, ptF, null);

    List<Order> order = List.of(orderA, orderB, orderC, orderD, orderE, orderF);
    VehicleRoute vr = new VehicleRoute(1, start, order, true);
    vr.setScore(0.0);

    RouteEvaluator re = new RouteEvaluator();
    re.scoreRoutePartial(vr, List.of(0, 1, 2, 3, 4, 5, 6));
    assertEquals(score, vr.getScore(), 1e-10);

    assertEquals(start.distance(orderA.getTo()), vr.getDistanceScoreAt(0), 1e-10);
    assertEquals(orderA.getTo().distance(orderB.getTo()), vr.getDistanceScoreAt(1), 1e-10);
    assertEquals(orderB.getTo().distance(orderC.getTo()), vr.getDistanceScoreAt(2), 1e-10);
    assertEquals(orderC.getTo().distance(orderD.getTo()), vr.getDistanceScoreAt(3), 1e-10);
    assertEquals(orderD.getTo().distance(orderE.getTo()), vr.getDistanceScoreAt(4), 1e-10);
    assertEquals(orderE.getTo().distance(orderF.getTo()), vr.getDistanceScoreAt(5), 1e-10);
    assertEquals(orderF.getTo().distance(start), vr.getDistanceScoreAt(6), 1e-10);
  }

  @Test
  void tooMuchArguments() {
    Point2D start = new Point2D(1.0, 1.0);
    Point2D ptA = new Point2D(1.0, 1.0);
    Point2D ptB = new Point2D(2.0, 2.0);
    Point2D ptC = new Point2D(3.0, 3.0);
    Point2D ptD = new Point2D(4.0, 4.0);
    Point2D ptE = new Point2D(5.0, 5.0);
    Point2D ptF = new Point2D(6.0, 6.0);

    Order orderA = new Order(1, ptA, null);
    Order orderB = new Order(2, ptB, null);
    Order orderC = new Order(3, ptC, null);
    Order orderD = new Order(4, ptD, null);
    Order orderE = new Order(5, ptE, null);
    Order orderF = new Order(6, ptF, null);

    List<Order> order = List.of(orderA, orderB, orderC, orderD, orderE, orderF);
    VehicleRoute vr = new VehicleRoute(1, start, order, true);
    vr.setScore(0.0);

    RouteEvaluator re = new RouteEvaluator();
    assertThrows(IllegalArgumentException.class,
        () -> re.scoreRoutePartial(vr, List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)));
  }

}