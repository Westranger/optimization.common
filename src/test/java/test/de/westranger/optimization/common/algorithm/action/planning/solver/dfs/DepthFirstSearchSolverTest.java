package test.de.westranger.optimization.common.algorithm.action.planning.solver.dfs;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.ActionPlanningSolution;
import de.westranger.optimization.common.algorithm.action.planning.ActionPlanningSolver;
import de.westranger.optimization.common.algorithm.action.planning.solver.dfs.DepthFirstSearchSolver;
import org.junit.jupiter.api.Test;
import test.de.westranger.optimization.common.algorithm.action.planning.solver.dfs.tsp.Order;
import test.de.westranger.optimization.common.algorithm.action.planning.solver.dfs.tsp.State;
import test.de.westranger.optimization.common.algorithm.action.planning.solver.dfs.tsp.StateRepresentation;
import test.de.westranger.optimization.common.algorithm.action.planning.solver.dfs.tsp.TSPAction;

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
        ActionPlanningSolver<Long> aps = new DepthFirstSearchSolver<>(true);
        aps.setInitialState(initialState);

        Optional<List<ActionPlanningSolution<Long>>> solve = aps.solve();

        assertTrue(solve.isPresent());
        assertEquals(20, solve.get().size());

        for (ActionPlanningSolution<Long> solution : solve.get()) {
            assertEquals(6000L, solution.getScore());
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
}