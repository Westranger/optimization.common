package test.de.westranger.optimization.common.algorithm.action.planning.solver.dfs.tsp;


import de.westranger.optimization.common.algorithm.action.planning.Action;

public final class TSPAction implements Action {
  private int vehicleID;
  private Order target;

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
