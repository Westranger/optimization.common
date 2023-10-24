package test.de.westranger.optimization.common.algorithm.tools;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.ActionPlanningSolution;
import de.westranger.optimization.common.algorithm.action.planning.ActionPlanningSolver;
import de.westranger.optimization.common.algorithm.action.planning.solver.dfs.DepthFirstSearchSolver;

import java.util.*;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.Order;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.State;

public class Benchmark {
  public static void main(String[] args) {
    final Map<Integer, List<Order>> orderMapping = new TreeMap<>();
    orderMapping.put(1, new ArrayList<>());

    final Map<Integer, Point2D> vehiclePositions = new TreeMap<>();
    vehiclePositions.put(1, new Point2D(26550.0000, 13850.0000));

    final List<Order> orderList = new LinkedList<>();
    orderList.add(new Order(1, new Point2D(20833.3333, 17100.0000)));
    orderList.add(new Order(2, new Point2D(20900.0000, 17066.6667)));
    orderList.add(new Order(3, new Point2D(21300.0000, 13016.6667)));
    orderList.add(new Order(4, new Point2D(21600.0000, 14150.0000)));
    orderList.add(new Order(5, new Point2D(21600.0000, 14966.6667)));
    orderList.add(new Order(6, new Point2D(21600.0000, 16500.0000)));
    orderList.add(new Order(7, new Point2D(22183.3333, 13133.3333)));
    orderList.add(new Order(8, new Point2D(22583.3333, 14300.0000)));
    orderList.add(new Order(9, new Point2D(22683.3333, 12716.6667)));
    orderList.add(new Order(10, new Point2D(23616.6667, 15866.6667)));
    orderList.add(new Order(11, new Point2D(23700.0000, 15933.3333)));
    orderList.add(new Order(12, new Point2D(23883.3333, 14533.3333)));
    orderList.add(new Order(13, new Point2D(24166.6667, 13250.0000)));
    orderList.add(new Order(14, new Point2D(25149.1667, 12365.8333)));
    orderList.add(new Order(15, new Point2D(26133.3333, 14500.0000)));
    orderList.add(new Order(16, new Point2D(26150.0000, 10550.0000)));
    orderList.add(new Order(17, new Point2D(26283.3333, 12766.6667)));
    orderList.add(new Order(18, new Point2D(26433.3333, 13433.3333)));
    orderList.add(new Order(19, new Point2D(26550.0000, 13850.0000)));
    orderList.add(new Order(20, new Point2D(26733.3333, 11683.3333)));
    orderList.add(new Order(21, new Point2D(27026.1111, 13051.9444)));
    orderList.add(new Order(22, new Point2D(27096.1111, 13415.8333)));
    orderList.add(new Order(23, new Point2D(27153.6111, 13203.3333)));
    orderList.add(new Order(24, new Point2D(27166.6667, 9833.3333)));
    orderList.add(new Order(25, new Point2D(27233.3333, 10450.0000)));
    orderList.add(new Order(26, new Point2D(27233.3333, 11783.3333)));
    orderList.add(new Order(27, new Point2D(27266.6667, 10383.3333)));
    orderList.add(new Order(28, new Point2D(27433.3333, 12400.0000)));
    orderList.add(new Order(29, new Point2D(27462.5000, 12992.2222)));


    Collections.shuffle(orderList, new Random(47110815L));

    System.out.print(
        "orders=" + orderList.size());

    ActionPlanningSolver aps = new DepthFirstSearchSolver(true);
    aps.setInitialState(new State(orderList, orderMapping, vehiclePositions));
    final long startSmart = System.currentTimeMillis();
    Optional<List<ActionPlanningSolution>> score = aps.solve();
    final long endSmart = System.currentTimeMillis();
    System.out.print(" smart=" + (endSmart - startSmart));
    System.out.println(" score=" + score.get().get(0).getScore());
  }

}
