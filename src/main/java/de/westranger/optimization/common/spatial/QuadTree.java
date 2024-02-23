package de.westranger.optimization.common.spatial;

import de.westranger.geometry.common.math.Vector2D;
import de.westranger.geometry.common.simple.BoundingBox;
import de.westranger.geometry.common.simple.Circle;
import de.westranger.geometry.common.simple.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class QuadTree {

  private QuadTreeNode root;
  private final double smallestCellSideLength;

  public QuadTree(final double smallestCellSideLength, final Point2D firstPt) {
    this.smallestCellSideLength = smallestCellSideLength;

    this.root =
        new QuadTreeNode(firstPt, smallestCellSideLength, true);
    this.root.addPoint(firstPt);
  }

  public void add(final Point2D pt) {
    if (this.root.isInside(pt).isEmpty()) {
      // we need to calculate the extend and in which direction we need to extend the tree
      extendTreeUpwards(pt);
    }
    // lets add and also create new children if no children are present

    QuadTreeNode current = this.root;
    BoundingBox bb = this.root.getBoundingBox();
    double sideLength = bb.getMax().getX() - bb.getMin().getX();

    while (!current.isLeaf()) {
      final Optional<QuadTreeChildPosition> inside = current.isInside(pt);
      if (inside.isEmpty()) {
        throw new IllegalStateException("something went wrong");
      }

      final QuadTreeChildPosition qtcp = inside.get();
      final Optional<QuadTreeNode> child = current.getChild(qtcp);

      if (child.isPresent()) {
        current = child.get();
      } else {
        final boolean isLeaf = Math.abs((sideLength * 0.5) - smallestCellSideLength) <= 1e-10;
        current = current.createChild(qtcp, isLeaf);
      }
      sideLength *= 0.5;
    }

    current.addPoint(pt);
  }

  private void extendTreeUpwards(final Point2D pt) {
    final double extendX = pt.getX() - this.root.getCenter().getX();
    final double extendY = pt.getY() - this.root.getCenter().getY();

    final BoundingBox bb = this.root.getBoundingBox();
    double initialSideLength = bb.getMax().getX() - bb.getMin().getX();
    int lvlUp = 0;
    double finalSideLength = initialSideLength;
    while (finalSideLength <= Math.abs(extendX) || finalSideLength <= Math.abs(extendY)) {
      finalSideLength *= 2;
      lvlUp++;
    }

    // lets find the child position
    QuadTreeChildPosition qtcp = QuadTreeChildPosition.CHILD_LOWER_RIGHT;
    if (extendY < 0.0) {
      if (extendX >= 0.0) {
        qtcp = QuadTreeChildPosition.CHILD_UPPER_LEFT;
      } else {
        qtcp = QuadTreeChildPosition.CHILD_UPPER_RIGHT;
      }
    } else {
      if (extendX >= 0.0) {
        qtcp = QuadTreeChildPosition.CHILD_LOWER_LEFT;
      }
    }

    // lets go up
    for (int i = 0; i < lvlUp; i++) {
      if (qtcp == QuadTreeChildPosition.CHILD_UPPER_RIGHT) {
        final double newX = this.root.getCenter().getX() - initialSideLength * 0.5;
        final double newY = this.root.getCenter().getY() - initialSideLength * 0.5;
        final Point2D newCenter = new Point2D(newX, newY);
        QuadTreeNode newRoot = new QuadTreeNode(newCenter, initialSideLength * 2.0, false);
        this.root.setParent(newRoot);
        newRoot.setChild(this.root, qtcp);
        this.root = newRoot;
      } else if (qtcp == QuadTreeChildPosition.CHILD_LOWER_RIGHT) {
        final double newX = this.root.getCenter().getX() - initialSideLength * 0.5;
        final double newY = this.root.getCenter().getY() + initialSideLength * 0.5;
        final Point2D newCenter = new Point2D(newX, newY);
        QuadTreeNode newRoot = new QuadTreeNode(newCenter, initialSideLength * 2.0, false);
        this.root.setParent(newRoot);
        newRoot.setChild(this.root, qtcp);
        this.root = newRoot;
      } else if (qtcp == QuadTreeChildPosition.CHILD_UPPER_LEFT) {
        final double newX = this.root.getCenter().getX() + initialSideLength * 0.5;
        final double newY = this.root.getCenter().getY() - initialSideLength * 0.5;
        final Point2D newCenter = new Point2D(newX, newY);
        QuadTreeNode newRoot = new QuadTreeNode(newCenter, initialSideLength * 2.0, false);
        this.root.setParent(newRoot);
        newRoot.setChild(this.root, qtcp);
        this.root = newRoot;
      } else if (qtcp == QuadTreeChildPosition.CHILD_LOWER_LEFT) {
        final double newX = this.root.getCenter().getX() + initialSideLength * 0.5;
        final double newY = this.root.getCenter().getY() + initialSideLength * 0.5;
        final Point2D newCenter = new Point2D(newX, newY);
        QuadTreeNode newRoot = new QuadTreeNode(newCenter, initialSideLength * 2.0, false);
        this.root.setParent(newRoot);
        newRoot.setChild(this.root, qtcp);
        this.root = newRoot;
      }
      initialSideLength *= 2.0;
    }
  }

  public QuadTreeNode getRoot() {
    return this.root;
  }

  public Optional<List<Point2D>> getPointsInArea(final Point2D referencePoint,
                                                 final double distance) {
    final Circle circ = new Circle(referencePoint, distance);
    final BoundingBox bb = circ.getBoundingBox();

    if (!intersectsOrInsideOrTouches(root.getBoundingBox(), bb)) {
      return Optional.empty();
    }

    final Optional<List<Point2D>> points = findPoints(this.root, bb);
    if (points.isPresent()) {
      List<Point2D> result = points.get();
      List<Point2D> tmp = new ArrayList<>();

      for (Point2D pt : result) {
        final Vector2D diffMax = pt.diff(bb.getMax());
        final Vector2D diffMin = bb.getMin().diff(pt);

        if (diffMax.getX() >= 0 && diffMax.getY() >= 0 && diffMin.getX() >= 0 &&
            diffMin.getY() >= 0) {
          tmp.add(pt);
        }
      }

      result = tmp;
      tmp = new ArrayList<>();
      for (Point2D pt : result) {
        double dist = referencePoint.distance(pt);
        if (dist <= distance) {
          tmp.add(pt);
        }
      }
      return Optional.of(Collections.unmodifiableList(tmp));
    }
    return Optional.empty();
  }

  private Optional<List<Point2D>> findPoints(QuadTreeNode node, BoundingBox bb) {
    if (intersectsOrInsideOrTouches(node.getBoundingBox(), bb)) {
      if (node.isLeaf()) {
        return node.getPoints();
      } else {
        final List<Point2D> result = new ArrayList<>();
        collectPoints(node, bb, result, QuadTreeChildPosition.CHILD_UPPER_RIGHT);
        collectPoints(node, bb, result, QuadTreeChildPosition.CHILD_LOWER_RIGHT);
        collectPoints(node, bb, result, QuadTreeChildPosition.CHILD_UPPER_LEFT);
        collectPoints(node, bb, result, QuadTreeChildPosition.CHILD_LOWER_LEFT);
        return Optional.of(Collections.unmodifiableList(result));
      }
    }
    return Optional.empty();
  }

  private void collectPoints(final QuadTreeNode node, final BoundingBox bb, List<Point2D> result,
                             QuadTreeChildPosition qtcp) {
    final Optional<QuadTreeNode> qtn = node.getChild(qtcp);
    if (qtn.isPresent()) {
      final Optional<List<Point2D>> rqtn = findPoints(qtn.get(), bb);
      if (rqtn.isPresent()) {
        result.addAll(rqtn.get());
      }
    }
  }

  private boolean intersectsOrInsideOrTouches(BoundingBox outer, BoundingBox inner) {
    final Vector2D diffMin = outer.getMax().diff(inner.getMin());
    final Vector2D diffMax = inner.getMax().diff(outer.getMin());

    if ((diffMin.getX() > 0.0 || diffMin.getY() > 0.0) || (diffMax.getX() > 0.0 ||
        diffMax.getY() > 0.0)) {
      return false;
    }
    return true;
  }

}
