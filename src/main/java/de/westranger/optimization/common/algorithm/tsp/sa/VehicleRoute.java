package de.westranger.optimization.common.algorithm.tsp.sa;


import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import java.util.ArrayList;
import java.util.List;

/**
 * @param id
 * @param homePosition
 * @param route
 * @param distanceScore
 * @param score
 */
public record VehicleRoute(int id, Point2D homePosition, List<Order> route,
                           List<Double> distanceScore, double score, boolean isRoundtrip)
    implements Cloneable {

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

  @Override
  public VehicleRoute clone() {
    return new VehicleRoute(this.id, this.homePosition, new ArrayList<>(this.route),
        new ArrayList<>(this.distanceScore), this.score, this.isRoundtrip);
  }
}
