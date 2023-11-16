package test.de.westranger.optimization.common.algorithm.example.tsp.sa;


import de.westranger.geometry.common.simple.Point2D;
import java.util.List;
import test.de.westranger.optimization.common.algorithm.example.tsp.common.Order;

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
    final StringBuilder sb = new StringBuilder();
    sb.append("(id= ");
    sb.append(this.id);
    sb.append(", home=");
    sb.append(this.homePosition.toString());
    sb.append(", route=");
    sb.append(route.toString());
    sb.append(')');
    return sb.toString();
  }

}
