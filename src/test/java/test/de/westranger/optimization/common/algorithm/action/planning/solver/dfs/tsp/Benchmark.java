package test.de.westranger.optimization.common.algorithm.action.planning.solver.dfs.tsp;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.ActionPlanningSolver;
import de.westranger.optimization.common.algorithm.action.planning.solver.dfs.DepthFirstSearchSolver;

import java.util.*;

public class Benchmark {
    public static void main(String[] args){
        for (int i = 1; i < 20; i++) {
            final Map<Integer, List<Order>> orderMapping = new TreeMap<>();
            orderMapping.put(1, new ArrayList<>());
            orderMapping.put(2, new ArrayList<>());
            orderMapping.put(3, new ArrayList<>());

            final Map<Integer, Point2D> vehiclePositions = new TreeMap<>();
            vehiclePositions.put(1, new Point2D(1.0, 1.0));
            vehiclePositions.put(2, new Point2D(3.0, 1.0));
            vehiclePositions.put(3, new Point2D(5.0, 1.0));

            final List<Order> orderList = new LinkedList<>();
            int oderCounter = 1;
            for (int a = 2; a < i + 2; a++) {
                orderList.add(new Order(oderCounter++, new Point2D(3.0, a + 1)));
                orderList.add(new Order(oderCounter++, new Point2D(5.0, a + 1)));
            }

            Collections.shuffle(orderList, new Random(47110815L));

            System.out.print(
                    "orders=" + orderList.size());

            ActionPlanningSolver<StateRepresentation, Long> aps = new DepthFirstSearchSolver<>(true);
            aps.setInitialState(new State(orderList, orderMapping, vehiclePositions));
            final long startSmart = System.currentTimeMillis();
            aps.solve();
            final long endSmart = System.currentTimeMillis();
            System.out.println(" smart=" + (endSmart - startSmart));
        }
    }

}
