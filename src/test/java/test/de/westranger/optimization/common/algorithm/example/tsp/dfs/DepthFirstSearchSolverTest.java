package test.de.westranger.optimization.common.algorithm.example.tsp.dfs;

import com.google.gson.Gson;
import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.ActionPlanningSolution;
import de.westranger.optimization.common.algorithm.action.planning.ActionPlanningSolver;
import de.westranger.optimization.common.algorithm.action.planning.solver.dfs.DepthFirstSearchSolver;
import org.junit.jupiter.api.Test;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.Order;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.ProblemFormulation;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.State;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.TSPAction;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DepthFirstSearchSolverTest {

  @Test
  void solveTSP6Locations() {
    final List<Order> orderList = new LinkedList<>();
    orderList.add(new Order(1, new Point2D(3.0, 2.0)));
    orderList.add(new Order(2, new Point2D(3.0, 3.0)));
    orderList.add(new Order(3, new Point2D(3.0, 4.0)));

    orderList.add(new Order(4, new Point2D(1.0, 2.0)));
    orderList.add(new Order(5, new Point2D(1.0, 3.0)));
    orderList.add(new Order(6, new Point2D(1.0, 4.0)));

    final Map<Integer, List<Order>> orderMapping = new TreeMap<>();
    orderMapping.put(1, new ArrayList<>());
    orderMapping.put(2, new ArrayList<>());
    orderMapping.put(3, new ArrayList<>());

    final Map<Integer, Point2D> vehiclePositions = new TreeMap<>();
    vehiclePositions.put(1, new Point2D(1.0, 1.0));
    vehiclePositions.put(2, new Point2D(3.0, 1.0));
    vehiclePositions.put(3, new Point2D(5.0, 1.0));

    State initialState = new State(orderList, orderMapping, vehiclePositions);
    ActionPlanningSolver aps = new DepthFirstSearchSolver(true);
    aps.setInitialState(initialState);

    Optional<List<ActionPlanningSolution>> solve = aps.solve();

    assertTrue(solve.isPresent());
    assertEquals(1, solve.get().size());

    for (ActionPlanningSolution solution : solve.get()) {
      assertEquals(6.0, solution.getScore().getAbsoluteScore(), 1e-6);
      assertEquals(6, solution.getActions().size());

      Map<Integer, Integer> map = new TreeMap<>();
      for (Action a : solution.getActions()) {
        assertTrue(a instanceof TSPAction);
        TSPAction act = (TSPAction) a;

        if (map.containsKey(act.getVehicleID())) {
          map.put(act.getVehicleID(), map.get(act.getVehicleID()) + 1);
        } else {
          map.put(act.getVehicleID(), +1);
        }
      }

      assertEquals(3, map.get(1));
      assertEquals(3, map.get(2));
      assertNull(map.get(3));
    }
  }

  @Test
  public void testGSON() {
    Gson gson = new Gson();

    final List<Order> orderList = new LinkedList<>();
    Order o1 = new Order(1, new Point2D(3.0, 2.0));
    Order o2 = new Order(2, new Point2D(3.0, 3.0));
    Order o3 = new Order(3, new Point2D(3.0, 4.0));
    Order o4 = new Order(4, new Point2D(1.0, 2.0));
    Order o5 = new Order(5, new Point2D(1.0, 3.0));
    Order o6 = new Order(6, new Point2D(1.0, 4.0));

    orderList.add(o1);
    orderList.add(o2);
    orderList.add(o3);
    orderList.add(o4);
    orderList.add(o5);
    orderList.add(o6);

    final Map<Integer, Point2D> vehiclePositions = new TreeMap<>();
    vehiclePositions.put(1, new Point2D(1.0, 1.0));
    vehiclePositions.put(2, new Point2D(3.0, 1.0));
    vehiclePositions.put(3, new Point2D(5.0, 1.0));

    final Map<Integer, List<Order>> orderMapping = new TreeMap<>();
    orderMapping.put(1, List.of(o1, o2, o3));
    orderMapping.put(2, new ArrayList<>());
    orderMapping.put(3, List.of(o4, o5, o6));


    // Konvertieren Sie die Person-Instanz in JSON
    String json =
        gson.toJson(new ProblemFormulation(orderList, vehiclePositions, orderMapping, 6000));
    System.out.println(json);
  }
}