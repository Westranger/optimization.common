package test.de.westranger.optimization.common.util;

import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import java.util.List;

public class SvgPlotter {

  private static final int HEIGHT = 220; // Feste Höhe eines einzelnen Plots
  private static final int MARGIN = 60; // Randabstand
  private static final int TEXT_SIZE = 8; // Schriftgröße

  private static double minX, maxX, minY, maxY;
  private static int plotWidth; // Dynamische Breite eines einzelnen Plots

  public static String plotProblemFinderScores(PermutationIteratorTest.ProblemFinderScore[] scores,
                                               Point2D start, Point2D end) {
    calculateBounds(scores, start, end);
    calculatePlotWidth();

    StringBuilder svgContent = new StringBuilder();
    svgContent.append("<svg width=\"" + (4 * plotWidth) + "\" height=\"" + HEIGHT +
        "\" xmlns=\"http://www.w3.org/2000/svg\">");

    for (int i = 0; i < scores.length; i++) {
      svgContent.append(plotScore(scores[i], i * plotWidth, start, end));
    }

    svgContent.append("</svg>");
    return svgContent.toString();
  }

  private static void calculatePlotWidth() {
    double xSpan = maxX - minX;
    double ySpan = maxY - minY;
    double aspectRatio = xSpan / ySpan;
    plotWidth = (int) Math.round(HEIGHT * aspectRatio) + 2 * MARGIN;
  }

  // Weitere Methoden folgen im nächsten Teil ...
  private static String drawGrid(int xOffset) {
    StringBuilder grid = new StringBuilder();
    for (int x = 0; x <= plotWidth - MARGIN; x += 10) {
      for (int y = 0; y <= HEIGHT - MARGIN; y += 10) {
        // Stellen Sie sicher, dass der Punkt innerhalb des Plots liegt
        if (x >= MARGIN && y >= MARGIN) {
          grid.append(
              "<circle cx=\"" + (x + xOffset) + "\" cy=\"" + y + "\" r=\"1\" fill=\"gray\" />");
        }
      }
    }
    return grid.toString();
  }

  // Fortsetzung der Klasse SvgPlotter ...

  private static void calculateBounds(PermutationIteratorTest.ProblemFinderScore[] scores,
                                      Point2D start, Point2D end) {
    minX = Double.MAX_VALUE;
    minY = Double.MAX_VALUE;
    maxX = Double.MIN_VALUE;
    maxY = Double.MIN_VALUE;

    for (PermutationIteratorTest.ProblemFinderScore score : scores) {
      for (Order order : score.getOrders()) {
        Point2D point = order.getTo();
        updateBounds(point);
      }
    }
    updateBounds(start);
    updateBounds(end);
  }

  private static void updateBounds(Point2D point) {
    minX = Math.min(minX, point.getX());
    minY = Math.min(minY, point.getY());
    maxX = Math.max(maxX, point.getX());
    maxY = Math.max(maxY, point.getY());
  }

  private static String plotScore(PermutationIteratorTest.ProblemFinderScore score, int xOffset,
                                  Point2D start, Point2D end) {
    StringBuilder plot = new StringBuilder();
    List<Order> orders = score.getOrders();

    // Zeichnen des Gitters
    plot.append(drawGrid(xOffset));

    // Zeichnen des Startpunkts und Linie zum ersten Punkt der Liste
    Point2D startPoint = transform(start);
    plot.append(drawPoint(startPoint, xOffset, "blue")); // Startpunkt in Blau

    Point2D endPoint = transform(end);
    plot.append(drawPoint(endPoint, xOffset, "blue")); // Startpunkt in Blau

    if (!orders.isEmpty()) {
      Point2D firstPoint = transform(orders.get(0).getTo());
      plot.append(drawLine(startPoint, firstPoint, xOffset));

      Point2D lastPoint = transform(orders.get(orders.size() - 1).getTo());
      plot.append(drawLine(endPoint, lastPoint, xOffset));
    }

    // Zeichnen der Linien, Punkte und Beschriftungen
    for (int i = 0; i < orders.size(); i++) {
      Order order = orders.get(i);
      Point2D currentPoint = transform(order.getTo());
      plot.append(drawLine(i > 0 ? transform(orders.get(i - 1).getTo()) : startPoint, currentPoint,
          xOffset));
      plot.append(drawPoint(currentPoint, xOffset, "red"));

      // Beschriftung mit Priorität und dueTime
      plot.append(
          drawText(currentPoint, "Prio: " + order.getPriority() + ", Time: " + order.getDueTime(),
              xOffset));
    }

    // Füge Score-Informationen hinzu
    plot.append(drawText(new Point2D(xOffset + MARGIN, HEIGHT - 5), formatScore(score), 0));

    return plot.toString();
  }

  // Weitere Zeichenmethoden folgen im nächsten Teil ...
// Fortsetzung der Klasse SvgPlotter ...
  // Fortsetzung der Klasse SvgPlotter ...

  private static String drawLine(Point2D from, Point2D to, int xOffset) {
    return "<line x1=\"" + (from.getX() + xOffset) + "\" y1=\"" + from.getY() + "\" x2=\"" +
        (to.getX() + xOffset) + "\" y2=\"" + to.getY() +
        "\" style=\"stroke:rgb(0,0,0);stroke-width:2\" />";
  }

  private static String drawPoint(Point2D point, int xOffset, String color) {
    return "<circle cx=\"" + (point.getX() + xOffset) + "\" cy=\"" + point.getY() +
        "\" r=\"3\" fill=\"" + color + "\" />";
  }

  private static String drawText(Point2D point, String text, int xOffset) {
    return "<text x=\"" + (point.getX() + xOffset) + "\" y=\"" + point.getY() +
        "\" fill=\"black\" font-size=\"" + TEXT_SIZE + "\">" + text + "</text>";
  }

  private static Point2D transform(Point2D point) {
    double x = (point.getX() - minX) / (maxX - minX) * (plotWidth - 2 * MARGIN) + MARGIN;
    double y = (point.getY() - minY) / (maxY - minY) * (HEIGHT - 2 * MARGIN) + MARGIN;
    return new Point2D(x, y);
  }


  private static String formatScore(PermutationIteratorTest.ProblemFinderScore score) {
    return String.format("Path: %.2f, Priority: %.2f, DueDate: %.2f", score.getPathScore(),
        score.getPriorityScore(), score.getDueDateScore());
  }
}
