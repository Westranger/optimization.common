package de.westranger.optimization.common.algorithm.tsp.sa;


import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import java.util.List;

public final class VehicleRoute {

  private final int id;
  private final Point2D homePosition;
  private List<Order> route;

  public VehicleRoute(final int id, final Point2D homePosition, final List<Order> route) {
    this.id = id;
    this.homePosition = homePosition;
    this.route = route;
  }

  public int getId() {
    return id;
  }

  public Point2D getHomePosition() {
    return homePosition;
  }

  public List<Order> getRoute() {
    return route;
  }

  public void setRoute(final List<Order> route) {
    this.route = route;
  }

  @Override
  public String toString() {
    String sb = "(id= " +
        this.id +
        ", home=" +
        this.homePosition.toString() +
        ", route=" +
        route.toString() +
        ')';
    return sb;
  }

}
