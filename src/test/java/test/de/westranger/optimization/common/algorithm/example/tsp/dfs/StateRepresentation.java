package test.de.westranger.optimization.common.algorithm.example.tsp.dfs;

import de.westranger.geometry.common.simple.Point2D;

import java.util.List;
import java.util.Map;

public final class StateRepresentation implements Comparable<StateRepresentation> {

    public byte[] data;

    public StateRepresentation(int numOrders, Map<Integer, List<Point2D>> orderMapping) {
        this.data = new byte[numOrders * orderMapping.size()];
        // TODO must be filled
    }

    @Override
    public int compareTo(StateRepresentation o) {
        return 0;
    }
}
