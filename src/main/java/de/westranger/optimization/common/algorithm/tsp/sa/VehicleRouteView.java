package de.westranger.optimization.common.algorithm.tsp.sa;

import java.util.Map;
import java.util.TreeMap;

public final class VehicleRouteView {

  private final VehicleRoute vehicleRoute;

  private final Map<Integer, Integer> swappedEdges;

  public VehicleRouteView(final VehicleRoute vehicleRoute) {
    this.vehicleRoute = vehicleRoute;
    this.swappedEdges = new TreeMap<>();
  }

  public VehicleRoute getVehicleRoute() {
    return vehicleRoute;
  }

  public Map<Integer, Integer> getSwappedEdges() {
    return swappedEdges;
  }

  public void swap(final int edgeA, final int edgeB) {
    this.swappedEdges.put(edgeA, edgeB);
    this.swappedEdges.put(edgeB, edgeA);
  }

}
