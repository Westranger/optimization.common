package de.westranger.optimization.common.algorithm.tsp.common;

import de.westranger.geometry.common.simple.Point2D;

import java.util.List;
import java.util.Map;

public final class ProblemFormulation {
  private List<Order> orders;
  private Map<Integer, Point2D> vehicleStartPositions;

  private Map<Integer, List<Order>> finalOrderMapping;
  private double expectedScore;

  public ProblemFormulation(final List<Order> orders,
                            final Map<Integer, Point2D> vehicleStartPositions,
                            final Map<Integer, List<Order>> finalOrderMapping,
                            final double expectedScore) {
    this.orders = orders;
    this.vehicleStartPositions = vehicleStartPositions;
    this.finalOrderMapping = finalOrderMapping;
    this.expectedScore = expectedScore;
  }

  public List<Order> getOrders() {
    return orders;
  }

  public void setOrders(List<Order> orders) {
    this.orders = orders;
  }

  public Map<Integer, Point2D> getVehicleStartPositions() {
    return vehicleStartPositions;
  }

  public void setVehicleStartPositions(Map<Integer, Point2D> vehicleStartPositions) {
    this.vehicleStartPositions = vehicleStartPositions;
  }

  public Map<Integer, List<Order>> getFinalOrderMapping() {
    return finalOrderMapping;
  }

  public void setFinalOrderMapping(Map<Integer, List<Order>> finalOrderMapping) {
    this.finalOrderMapping = finalOrderMapping;
  }

  public double getExpectedScore() {
    return expectedScore;
  }

  public void setExpectedScore(double expectedScore) {
    this.expectedScore = expectedScore;
  }
}
