package de.westranger.optimization.common.algorithm.tsp.sa.route;


import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class VehicleRoute implements Cloneable {

  private final int id;
  private final Point2D homePosition;
  private final List<Order> route;
  private final List<Double> distanceScore;
  private double score;
  private final boolean isRoundtrip;

  public VehicleRoute(final int id, final Point2D homePosition, final List<Order> route,
                      final boolean isRoundtrip) {
    if (route == null) {
      throw new IllegalArgumentException("route and distanceScore lists must not be null");
    }

    this.id = id;
    this.homePosition = homePosition;
    this.route = route;
    if (isRoundtrip) {
      this.distanceScore = new ArrayList<>(Collections.nCopies(this.route.size() + 1, 0.0));
    } else {
      this.distanceScore = new ArrayList<>(Collections.nCopies(this.route.size(), 0.0));
    }
    this.score = Double.NaN;
    this.isRoundtrip = isRoundtrip;
  }


  public VehicleRoute(final int id, final Point2D homePosition, final List<Order> route,
                      final List<Double> distanceScore, final double score,
                      final boolean isRoundtrip) {

    if (distanceScore == null || route == null) {
      throw new IllegalArgumentException("route and distanceScore lists must not be null");
    }
    if ((isRoundtrip && route.size() + 1 != distanceScore.size())
        || (!isRoundtrip && route.size() != distanceScore.size())) {
      throw new IllegalArgumentException("route and distanceScore lengths do not match");
    }

    this.id = id;
    this.homePosition = homePosition;
    this.route = route;
    this.distanceScore = distanceScore;
    this.score = score;
    this.isRoundtrip = isRoundtrip;
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

  @Override
  public VehicleRoute clone() {
    return new VehicleRoute(this.id, this.homePosition, new ArrayList<>(this.route),
        new ArrayList<>(this.distanceScore), this.score, this.isRoundtrip);
  }

  public int getId() {
    return id;
  }

  public Point2D getHomePosition() {
    return homePosition;
  }

  public List<Order> getRoute() {
    return Collections.unmodifiableList(route);
  }

  public List<Double> getDistanceScore() {
    return Collections.unmodifiableList(distanceScore);
  }

  public double updateDistanceScoreAt(final int idx, double value) {
    return this.distanceScore.set(idx, value);
  }

  public double getDistanceScoreAt(final int idx) {
    return this.distanceScore.get(idx);
  }

  public Order getLocationAt(final int idx) {
    return this.route.get(idx);
  }

  public int getTripLocationCount() {
    return this.route.size();
  }

  public double getScore() {
    return score;
  }

  public void setScore(final double score) {
    this.score = score;
  }

  public boolean isRoundtrip() {
    return isRoundtrip;
  }

}
