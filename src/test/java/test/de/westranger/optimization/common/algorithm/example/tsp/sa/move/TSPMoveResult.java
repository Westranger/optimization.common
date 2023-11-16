package test.de.westranger.optimization.common.algorithm.example.tsp.sa.move;

import java.util.List;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.VehicleRoute;

public record TSPMoveResult(double score, List<VehicleRoute> vehicles) {
}
