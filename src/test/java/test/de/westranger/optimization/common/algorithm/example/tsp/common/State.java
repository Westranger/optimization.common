package test.de.westranger.optimization.common.algorithm.example.tsp.common;

import de.westranger.geometry.common.simple.BoundingBox;
import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.Action;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.Score;

import java.util.*;

public class State extends SearchSpaceState {

    private final List<Order> orderList;
    private final Map<Integer, List<Order>> orderMapping;
    private final Map<Integer, Point2D> vehiclePositions;

    private TSPScore score;

    private Optional<Action> lastPerformedAction;

    public State(final List<Order> orderList, final Map<Integer, List<Order>> orderMapping,
                 final Map<Integer, Point2D> vehiclePositions) {
        this.orderList = new LinkedList<>(orderList);
        this.orderMapping = new TreeMap<>(orderMapping);
        this.vehiclePositions = new TreeMap<>(vehiclePositions);
        this.lastPerformedAction = Optional.empty();

        //System.out.println("start score ");
        double score = 0.0;
        for (Map.Entry<Integer, Point2D> entry : vehiclePositions.entrySet()) {
            Point2D prevPt = entry.getValue();

            List<Order> om = orderMapping.get(entry.getKey());
            if (om != null) {
                for (Order order : om) {
                    score += prevPt.distance(order.getTo());
                    prevPt = order.getTo();
                }
                //System.out.println("score " + score);
            }
        }
        this.score = new TSPScore(score);
    }

    private State(final List<Order> orderList, final Map<Integer, List<Order>> orderMapping,
                  final Map<Integer, Point2D> vehiclePositions, final TSPScore score) {
        this(orderList, orderMapping, vehiclePositions);
        this.score = score;
    }

    @Override
    public List<Action> getPossibleActions() {
        final List<Action> result = new LinkedList<>();
        for (int i : this.orderMapping.keySet()) {
            for (Order order : this.orderList) {
                result.add(new TSPAction(i, order));
            }
        }
        return result;
    }

    @Override
    public boolean perform(Action action) {
        if (!(action instanceof TSPAction act)) {
            throw new IllegalArgumentException("illegal action type passed");
        }

        final boolean result = this.orderList.remove(act.getOrder());
        if (result) {
            List<Order> list = this.orderMapping.get(act.getVehicleID());
            final double dist;
            if (list.isEmpty()) {
                dist = this.vehiclePositions.get(act.getVehicleID()).distance(act.getOrder().getTo());
            } else {
                dist = list.get(list.size() - 1).getTo().distance(act.getOrder().getTo());
            }
            score = new TSPScore(this.score.getAbsoluteScore() + dist);
            list.add(act.getOrder());
        }

        this.lastPerformedAction = Optional.of(act);

        return result;
    }

    @Override
    public Score getScore() {
        return this.score;
    }

    @Override
    public int compareTo(SearchSpaceState o) {
        return this.score.compareTo(o.getScore());
    }

    @Override
    public State clone() {
        final List<Order> orderList = new ArrayList<>(this.orderList.size());
        orderList.addAll(this.orderList);

        final Map<Integer, List<Order>> orderMapping = new TreeMap<>();
        for (Map.Entry<Integer, List<Order>> entry : this.orderMapping.entrySet()) {
            List<Order> tmp = new ArrayList<>(entry.getValue().size());
            tmp.addAll(entry.getValue());
            orderMapping.put(entry.getKey(), tmp);
        }

        final Map<Integer, Point2D> vehiclePositions = new TreeMap<>(this.vehiclePositions);

        return new State(orderList, orderMapping, vehiclePositions, score);
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

    public Map<Integer, List<Order>> getOrderMapping() {
        return orderMapping;
    }

    public Map<Integer, Point2D> getVehiclePositions() {
        return vehiclePositions;
    }

    private double computeScore(final Point2D vehicleStart, final List<Order> orders) {
        double sum = 0.0;
        Point2D prev = vehicleStart;
        for (int i = 0; i < orders.size(); i++) {
            final Point2D current = orders.get(i).getTo();
            sum += prev.distance(current);
            prev = current;
        }

        return sum;
    }

    private double computeFullScore() {
        double score = 0.0;
        for (Map.Entry<Integer, Point2D> entry : vehiclePositions.entrySet()) {
            score += computeScore(entry.getValue(), orderMapping.get(entry.getKey()));
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
            sb.append(this.score.getAbsoluteScore());
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
        svgBuilder.append(String.format(Locale.ENGLISH, "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%f\" height=\"%f\" viewBox=\"%f %f %f %f\">", maxY - minY, maxX - minX, minY, minX, maxY - minY, maxX - minX));
        svgBuilder.append('\n');

        for (Order order : this.orderList) {
            svgBuilder.append('\t');
            svgBuilder.append(String.format(Locale.ENGLISH, "<circle cx=\"%f\" cy=\"%f\" r=\"5\" fill=\"black\"/>", order.getTo().getY(), order.getTo().getX()));
            svgBuilder.append('\n');
        }

        Point2D previousPoint = null;
        for (Map.Entry<Integer, List<Order>> entry : this.orderMapping.entrySet()) {
            String vehicleColor = colors.get(entry.getKey());
            for (Order order : entry.getValue()) {
                if (previousPoint != null) {
                    svgBuilder.append('\t');
                    svgBuilder.append(String.format(Locale.ENGLISH, "<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" stroke=\"%s\"/>", previousPoint.getY(), previousPoint.getX(), order.getTo().getY(), order.getTo().getX(), vehicleColor));
                    svgBuilder.append('\n');
                }
                svgBuilder.append('\t');
                svgBuilder.append(String.format(Locale.ENGLISH, "<circle cx=\"%f\" cy=\"%f\" r=\"5\" fill=\"black\"/>", order.getTo().getY(), order.getTo().getX()));
                svgBuilder.append('\n');

                previousPoint = order.getTo();
            }
            previousPoint = null;
        }

        for (Map.Entry<Integer, Point2D> entry : this.vehiclePositions.entrySet()) {
            String vehicleColor = colors.get(entry.getKey());
            svgBuilder.append('\t');
            svgBuilder.append(String.format(Locale.ENGLISH, "<rect x=\"%f\" y=\"%f\" width=\"25\" height=\"25\" fill=\"%s\"/>", entry.getValue().getY(), entry.getValue().getX(), vehicleColor));
            svgBuilder.append('\n');

            List<Order> orders = this.orderMapping.get(entry.getKey());
            if(orders != null && !orders.isEmpty()){
                final Point2D start = orders.get(0).getTo();
                svgBuilder.append('\t');
                svgBuilder.append(String.format(Locale.ENGLISH, "<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" stroke=\"%s\"/>", entry.getValue().getY(), entry.getValue().getX(), start.getY(), start.getX(), vehicleColor));
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

        for (Map.Entry<Integer, Point2D> entry : this.vehiclePositions.entrySet()) {
            minX = Math.min(minX, entry.getValue().getX());
            maxX = Math.max(maxX, entry.getValue().getX());
            minY = Math.min(minY, entry.getValue().getY());
            maxY = Math.max(maxY, entry.getValue().getY());
        }

        for (Order order : this.orderList) {
            minX = Math.min(minX, order.getTo().getX());
            maxX = Math.max(maxX, order.getTo().getX());
            minY = Math.min(minY, order.getTo().getY());
            maxY = Math.max(maxY, order.getTo().getY());
        }

        for (Map.Entry<Integer, List<Order>> entry : this.orderMapping.entrySet()) {
            for (Order order : entry.getValue()) {
                minX = Math.min(minX, order.getTo().getX());
                maxX = Math.max(maxX, order.getTo().getX());
                minY = Math.min(minY, order.getTo().getY());
                maxY = Math.max(maxY, order.getTo().getY());
            }
        }
        return new BoundingBox(new Point2D(minX * .95, minY * 0.95), new Point2D(maxX * 1.05, maxY * 1.05));
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
