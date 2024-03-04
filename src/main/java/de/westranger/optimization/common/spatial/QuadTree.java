package de.westranger.optimization.common.spatial;

import de.westranger.geometry.common.simple.BoundingBox;
import de.westranger.geometry.common.simple.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/*
 * TODO methode zum entfernen ... wie wäre es wenn man der get points in are noch ein lambda (oder
 * predicate?) geben kann und alle punkte die übereinstimmen werden zurückgeben und entfernt, dann
 * weiss mann das diese schon benutzt wurden, allerdings muss dann auch der baum mit aufgeräumt
 * werden.
 */


public final class QuadTree<T extends Point2D> {

  private QuadTreeNode<T> root;
  private final double smallestCellSideLength;

  public QuadTree(final double smallestCellSideLength, final T firstPt) {
    this.smallestCellSideLength = smallestCellSideLength;

    this.root =
        new QuadTreeNode(firstPt, smallestCellSideLength, true);
    this.root.addPoint(firstPt);
  }

  public void add(final T pt) {
    if (this.root.isInside(pt).isEmpty()) {
      // we need to calculate the extend and in which direction we need to extend the tree
      extendTreeUpwards(pt);
    }
    // lets add and also create new children if no children are present

    QuadTreeNode<T> current = this.root;
    BoundingBox bb = this.root.getBoundingBox();
    double sideLength = bb.getMax().getX() - bb.getMin().getX();

    while (!current.isLeaf()) {
      final Optional<QuadTreeChildPosition> inside = current.isInside(pt);
      final QuadTreeChildPosition qtcp = inside.get();
      final Optional<QuadTreeNode<T>> child = current.getChild(qtcp);

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
        QuadTreeNode<T> newRoot = new QuadTreeNode(newCenter, initialSideLength * 2.0, false);
        newRoot.setChild(this.root, qtcp);
        this.root = newRoot;
      } else if (qtcp == QuadTreeChildPosition.CHILD_LOWER_RIGHT) {
        final double newX = this.root.getCenter().getX() - initialSideLength * 0.5;
        final double newY = this.root.getCenter().getY() + initialSideLength * 0.5;
        final Point2D newCenter = new Point2D(newX, newY);
        QuadTreeNode<T> newRoot = new QuadTreeNode(newCenter, initialSideLength * 2.0, false);
        newRoot.setChild(this.root, qtcp);
        this.root = newRoot;
      } else if (qtcp == QuadTreeChildPosition.CHILD_UPPER_LEFT) {
        final double newX = this.root.getCenter().getX() + initialSideLength * 0.5;
        final double newY = this.root.getCenter().getY() - initialSideLength * 0.5;
        final Point2D newCenter = new Point2D(newX, newY);
        QuadTreeNode<T> newRoot = new QuadTreeNode(newCenter, initialSideLength * 2.0, false);
        newRoot.setChild(this.root, qtcp);
        this.root = newRoot;
      } else {
        final double newX = this.root.getCenter().getX() + initialSideLength * 0.5;
        final double newY = this.root.getCenter().getY() + initialSideLength * 0.5;
        final Point2D newCenter = new Point2D(newX, newY);
        QuadTreeNode<T> newRoot = new QuadTreeNode(newCenter, initialSideLength * 2.0, false);
        newRoot.setChild(this.root, qtcp);
        this.root = newRoot;
      }
      initialSideLength *= 2.0;
    }
  }

  public QuadTreeNode<T> getRoot() {
    return this.root;
  }

  public Optional<List<T>> getPointsInArea(final BoundingBox area) {

    if (!intersectsOrInsideOrTouches(root.getBoundingBox(), area)) {
      return Optional.empty();
    }

    final Optional<List<T>> points = findPoints(this.root, area);
    if (points.isPresent()) {
      final Iterator<T> listIterator = points.get().iterator();

      while (listIterator.hasNext()) {
        final Point2D pt = listIterator.next();

        if (pt.getX() < area.getMin().getX() || pt.getX() > area.getMax().getX()
            || pt.getY() < area.getMin().getY() || pt.getY() > area.getMax().getY()) {
          listIterator.remove();
        }
      }

      if (points.get().isEmpty()) {
        return Optional.empty();
      }

      return Optional.of(Collections.unmodifiableList(points.get()));
    }
    return Optional.empty();
  }

  private Optional<List<T>> findPoints(QuadTreeNode<T> node, BoundingBox bb) {
    if (intersectsOrInsideOrTouches(node.getBoundingBox(), bb)) {
      if (node.isLeaf()) {
        return node.getPoints();
      } else {
        final List<T> result = new ArrayList<>();
        collectPoints(node, bb, result, QuadTreeChildPosition.CHILD_UPPER_RIGHT);
        collectPoints(node, bb, result, QuadTreeChildPosition.CHILD_LOWER_RIGHT);
        collectPoints(node, bb, result, QuadTreeChildPosition.CHILD_UPPER_LEFT);
        collectPoints(node, bb, result, QuadTreeChildPosition.CHILD_LOWER_LEFT);

        if (result.isEmpty()) {
          return Optional.empty();
        }
        return Optional.of(Collections.unmodifiableList(result));
      }
    }
    return Optional.empty();
  }

  private void collectPoints(final QuadTreeNode<T> node, final BoundingBox bb, List<T> result,
                             QuadTreeChildPosition qtcp) {
    final Optional<QuadTreeNode<T>> qtn = node.getChild(qtcp);
    if (qtn.isPresent()) {
      final Optional<List<T>> rqtn = findPoints(qtn.get(), bb);
      rqtn.ifPresent(result::addAll);
    }
  }

  private boolean intersectsOrInsideOrTouches(final BoundingBox outer, final BoundingBox inner) {
    return !(inner.getMax().getX() < outer.getMin().getX()
        || inner.getMax().getY() < outer.getMin().getY()
        || inner.getMin().getX() > outer.getMax().getX()
        || inner.getMin().getY() > outer.getMax().getY());
  }
}
