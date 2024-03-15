package de.westranger.optimization.common.algorithm.tsp.common;

import de.westranger.geometry.common.simple.BoundingBox;
import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.Score;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class State extends SearchSpaceState<TSPScore> {

  private final List<Order> orderList;

  private final List<VehicleRoute> emptyVehicles;
  private final List<VehicleRoute> nonEmptyVehicles;

  private final RouteEvaluator routeEval;

  private TSPScore score;

  private Optional<Action> lastPerformedAction;

  public State(final List<Order> orderList, final List<VehicleRoute> emptyVehicles,
               final List<VehicleRoute> nonEmptyVehicles, final RouteEvaluator routeEval) {
    this.orderList = new LinkedList<>(orderList);
    this.routeEval = routeEval;
    this.lastPerformedAction = Optional.empty();

    this.emptyVehicles = emptyVehicles;
    this.nonEmptyVehicles = nonEmptyVehicles;

    for (VehicleRoute vr : emptyVehicles) {
      if (!vr.getRoute().isEmpty()) {
        throw new IllegalArgumentException(
            "the route of the provided vehicle " + vr.getId() + " is not empty");
      }
    }

    for (VehicleRoute vr : nonEmptyVehicles) {
      if (vr.getRoute().isEmpty()) {
        throw new IllegalArgumentException(
            "the route of the provided vehicle " + vr.getId() + " is empty");
      }
    }

    double score = 0.0;
    for (VehicleRoute vr : nonEmptyVehicles) {
      routeEval.scoreRouteFull(vr);
      double tmp = vr.getScore();
      score += !Double.isNaN(tmp) ? tmp : 0.0;
    }

    this.score = new TSPScore(score);
  }

  public State(final List<Order> orderList, final List<VehicleRoute> emptyVehicles,
               final List<VehicleRoute> nonEmptyVehicles, final RouteEvaluator routeEval,
               final double score) {
    this.orderList = new LinkedList<>(orderList);
    this.emptyVehicles = emptyVehicles;
    this.nonEmptyVehicles = nonEmptyVehicles;
    this.routeEval = routeEval;
    this.lastPerformedAction = Optional.empty();
    this.score = new TSPScore(score);
  }

  private State(final List<Order> orderList, final List<VehicleRoute> emptyVehicles,
                final List<VehicleRoute> nonEmptyVehicles, final RouteEvaluator routeEval,
                final TSPScore score) {
    this(orderList, emptyVehicles, nonEmptyVehicles, routeEval);
    this.score = score;
  }

  @Override
  public List<Action> getPossibleActions() {
    final List<Action> result = new LinkedList<>();
    for (VehicleRoute vr : this.emptyVehicles) {
      for (Order order : this.orderList) {
        result.add(new TSPAction(vr.getId(), order));
      }
    }
    return result;
  }

  @Override
  public boolean perform(Action action) {
    return true;
  }

  @Override
  public TSPScore getScore() {
    return this.score;
  }

  public RouteEvaluator getRouteEval() {
    return routeEval;
  }

  @Override
  public int compareTo(SearchSpaceState o) {
    return this.score.compareTo(o.getScore());
  }

  @Override
  public State clone() {
    final List<Order> orderList = new ArrayList<>(this.orderList);
    final List<VehicleRoute> emptyVehicles = new ArrayList<>(this.emptyVehicles.size());
    final List<VehicleRoute> nonEmptyVehicles = new ArrayList<>(this.nonEmptyVehicles.size());

    for (VehicleRoute vr : this.emptyVehicles) {
      emptyVehicles.add(vr.clone());
    }

    for (VehicleRoute vr : this.nonEmptyVehicles) {
      nonEmptyVehicles.add(vr.clone());
    }

    return new State(orderList, emptyVehicles, nonEmptyVehicles, this.routeEval, this.score);
  }

  @Override
  public Optional<Action> getLastPerformedAction() {
    return this.lastPerformedAction;
  }

  @Override
  public boolean isGoalState() {
    return this.orderList.isEmpty();
  }

  public List<Order> getOrderList() {
    return orderList;
  }

  public List<VehicleRoute> getEmptyVehicles() {
    return Collections.unmodifiableList(emptyVehicles);
  }

  public List<VehicleRoute> getNonEmptyVehicles() {
    return Collections.unmodifiableList(nonEmptyVehicles);
  }

  private double computeFullScore() {
    double score = 0.0;
    for (VehicleRoute vr : this.getNonEmptyVehicles()) {
      this.routeEval.scoreRouteFull(vr);
      if (!Double.isNaN(vr.getScore())) {
        score += vr.getScore();
      }
    }
    return score;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("(state score=");
    sb.append(computeFullScore());
    sb.append(' ');
    if (this.score != null) {
      sb.append(this.score);
    } else {
      sb.append('x');
    }
    sb.append(')');

    return sb.toString();
  }

  @Override
  public String toSVG() {
    final BoundingBox bb = this.getBoundingBox();
    final double minX = bb.getMin().getX();
    final double minY = bb.getMin().getY();
    final double maxX = bb.getMax().getX();
    final double maxY = bb.getMax().getY();

    List<String> colors = getDistinctColors();

    // SVG-String erstellen
    StringBuilder svgBuilder = new StringBuilder();
    svgBuilder.append(String.format(Locale.ENGLISH,
        "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%f\" height=\"%f\" viewBox=\"%f %f %f %f\">",
        maxY - minY, maxX - minX, minY, minX, maxY - minY, maxX - minX));
    svgBuilder.append('\n');

    for (Order order : this.orderList) {
      svgBuilder.append('\t');
      svgBuilder.append(
          String.format(Locale.ENGLISH, "<circle cx=\"%f\" cy=\"%f\" r=\"0.01\" fill=\"black\"/>",
              order.getTo().getY(), order.getTo().getX()));
      svgBuilder.append('\n');
    }

    Point2D previousPoint = null;
    for (VehicleRoute vr : this.nonEmptyVehicles) {
      String vehicleColor = colors.get(vr.getId());
      for (Order order : vr.getRoute()) {
        if (previousPoint != null) {
          svgBuilder.append('\t');
          svgBuilder.append(String.format(Locale.ENGLISH,
              "<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" stroke-width=\"0.1\" stroke=\"%s\"/>", previousPoint.getY(),
              previousPoint.getX(), order.getTo().getY(), order.getTo().getX(), vehicleColor));
          svgBuilder.append('\n');
        }
        svgBuilder.append('\t');
        svgBuilder.append(
            String.format(Locale.ENGLISH, "<circle cx=\"%f\" cy=\"%f\" r=\"0.01\" fill=\"black\"/>",
                order.getTo().getY(), order.getTo().getX()));
        svgBuilder.append('\n');

        previousPoint = order.getTo();
      }
      previousPoint = null;
    }

    for (VehicleRoute vr : this.nonEmptyVehicles) {
      String vehicleColor = colors.get(vr.getId());
      svgBuilder.append('\t');
      svgBuilder.append(String.format(Locale.ENGLISH,
          "<rect x=\"%f\" y=\"%f\" width=\"0.025\" height=\"0.025\" fill=\"%s\"/>",
          vr.getHomePosition().getY(), vr.getHomePosition().getX(), vehicleColor));
      svgBuilder.append('\n');

      List<Order> orders = vr.getRoute();
      if (orders != null && !orders.isEmpty()) {
        final Point2D start = orders.get(0).getTo();
        svgBuilder.append('\t');
        svgBuilder.append(String.format(Locale.ENGLISH,
            "<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" stroke-width=\"0.1\" stroke=\"%s\"/>",
            vr.getHomePosition().getY(), vr.getHomePosition().getX(), start.getY(), start.getX(),
            vehicleColor));
        svgBuilder.append('\n');
      }
    }

    svgBuilder.append("</svg>");
    return svgBuilder.toString();
  }

  private BoundingBox getBoundingBox() {
    double minX = Double.POSITIVE_INFINITY;
    double maxX = Double.NEGATIVE_INFINITY;
    double minY = Double.POSITIVE_INFINITY;
    double maxY = Double.NEGATIVE_INFINITY;

    for (Order order : this.orderList) {
      minX = Math.min(minX, order.getTo().getX());
      maxX = Math.max(maxX, order.getTo().getX());
      minY = Math.min(minY, order.getTo().getY());
      maxY = Math.max(maxY, order.getTo().getY());
    }

    for (VehicleRoute vr : this.nonEmptyVehicles) {
      final Point2D src = vr.getHomePosition();

      minX = Math.min(minX, src.getX());
      maxX = Math.max(maxX, src.getX());
      minY = Math.min(minY, src.getY());
      maxY = Math.max(maxY, src.getY());

      for (Order order : vr.getRoute()) {
        minX = Math.min(minX, order.getTo().getX());
        maxX = Math.max(maxX, order.getTo().getX());
        minY = Math.min(minY, order.getTo().getY());
        maxY = Math.max(maxY, order.getTo().getY());
      }
    }
    return new BoundingBox(new Point2D(minX * .95, minY * 0.95),
        new Point2D(maxX * 1.05, maxY * 1.05));
  }

  public static List<String> getDistinctColors() {
    return List.of(
        "#e6194b", "#3cb44b", "#ffe119", "#0082c8", "#f58231",
        "#911eb4", "#46f0f0", "#f032e6", "#d2f53c", "#fabebe",
        "#008080", "#e6beff", "#aa6e28", "#fffac8", "#800000",
        "#aaffc3", "#808000", "#ffd8b1", "#000080", "#808080",
        "#000000", "#0a74da", "#f095e1", "#6c8e7e", "#9c7043",
        "#4287f5", "#f24236", "#f4e542", "#82f542", "#4211a5"
    );
  }

}
