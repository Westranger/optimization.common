package test.de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.sa.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.VehicleRoute;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPMove;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPMoveResult;
import de.westranger.optimization.common.algorithm.tsp.sa.move.TSPSwapMove;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.de.westranger.optimization.common.algorithm.tools.util.CustomRandom;

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

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>());
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), new LinkedList<>());
    VehicleRoute vrC = new VehicleRoute(3, new Point2D(3.0, 3.0), new LinkedList<>());

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> move.performMove(List.of(vrA, vrB, vrC)));
  }

  @Test
  public void test1VehicleNoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re);
    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>());

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test2VehiclesNoOrders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re);
    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>());
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), new LinkedList<>());

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test2VehicleOneOrderA() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), List.of(orderA));
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), new LinkedList<>());

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test2VehicleOneOrderB() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re);

    Order orderB = new Order(2, new Point2D(2.0, 2.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), new LinkedList<>());
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), List.of(orderB));

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isEmpty());
  }

  @Test
  public void test2Vehicle2Orders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));
    Order orderB = new Order(2, new Point2D(2.0, 2.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), List.of(orderA));
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), List.of(orderB));

    Optional<TSPMoveResult> result = move.performMove(List.of(vrA, vrB));
    Assertions.assertTrue(result.isPresent());

    Assertions.assertEquals(2, result.get().vehicles().size());

    Assertions.assertEquals(1, result.get().vehicles().get(0).getRoute().size());
    Assertions.assertEquals(1, result.get().vehicles().get(1).getRoute().size());

    Assertions.assertEquals(1, result.get().vehicles().get(1).getRoute().get(0).getId());
    Assertions.assertEquals(2, result.get().vehicles().get(0).getRoute().get(0).getId());
  }

  @Test
  public void test2Vehicle6Orders() {
    RouteEvaluator re = new RouteEvaluator();
    TSPMove move = new TSPSwapMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));
    Order orderB = new Order(2, new Point2D(2.0, 2.0));
    Order orderC = new Order(3, new Point2D(3.0, 3.0));
    Order orderD = new Order(4, new Point2D(4.0, 4.0));
    Order orderE = new Order(5, new Point2D(5.0, 5.0));
    Order orderF = new Order(6, new Point2D(6.0, 6.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0), List.of(orderA, orderB, orderC));
    VehicleRoute vrB = new VehicleRoute(2, new Point2D(2.0, 2.0), List.of(orderD, orderE, orderF));

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
  }

  @Test
  public void test1Vehicle6OrdersSameIndex() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {0, 0});
    TSPMove move = new TSPSwapMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));
    Order orderB = new Order(2, new Point2D(2.0, 2.0));
    Order orderC = new Order(3, new Point2D(3.0, 3.0));
    Order orderD = new Order(4, new Point2D(4.0, 4.0));
    Order orderE = new Order(5, new Point2D(5.0, 5.0));
    Order orderF = new Order(6, new Point2D(6.0, 6.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD, orderE, orderF));


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
  }

  @Test
  public void test1Vehicle6OrdersA() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {2, 4});
    TSPMove move = new TSPSwapMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));
    Order orderB = new Order(2, new Point2D(2.0, 2.0));
    Order orderC = new Order(3, new Point2D(3.0, 3.0));
    Order orderD = new Order(4, new Point2D(4.0, 4.0));
    Order orderE = new Order(5, new Point2D(5.0, 5.0));
    Order orderF = new Order(6, new Point2D(6.0, 6.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD, orderE, orderF));

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
  }

  @Test
  public void test1Vehicle6OrdersB() {
    RouteEvaluator re = new RouteEvaluator();
    Random rng = new CustomRandom(new int[] {4, 2});
    TSPMove move = new TSPSwapMove(rng, re);

    Order orderA = new Order(1, new Point2D(1.0, 1.0));
    Order orderB = new Order(2, new Point2D(2.0, 2.0));
    Order orderC = new Order(3, new Point2D(3.0, 3.0));
    Order orderD = new Order(4, new Point2D(4.0, 4.0));
    Order orderE = new Order(5, new Point2D(5.0, 5.0));
    Order orderF = new Order(6, new Point2D(6.0, 6.0));

    VehicleRoute vrA = new VehicleRoute(1, new Point2D(1.0, 1.0),
        List.of(orderA, orderB, orderC, orderD, orderE, orderF));


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
  }


}