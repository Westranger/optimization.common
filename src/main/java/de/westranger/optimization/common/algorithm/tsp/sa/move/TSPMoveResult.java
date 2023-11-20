package de.westranger.optimization.common.algorithm.tsp.sa.move;

import de.westranger.optimization.common.algorithm.tsp.sa.VehicleRoute;
import java.util.List;

public record TSPMoveResult(double score, List<VehicleRoute> vehicles) {
}
