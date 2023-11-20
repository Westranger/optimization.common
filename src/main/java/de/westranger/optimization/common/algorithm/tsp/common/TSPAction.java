package de.westranger.optimization.common.algorithm.tsp.common;


import de.westranger.optimization.common.algorithm.action.planning.Action;

public final class TSPAction implements Action {
  private final int vehicleID;
  private final Order target;

  public TSPAction(final int vehicleID, final Order target) {
    this.vehicleID = vehicleID;
    this.target = target;
  }

  public int getVehicleID() {
    return vehicleID;
  }

  public Order getOrder() {
    return target;
  }
}
