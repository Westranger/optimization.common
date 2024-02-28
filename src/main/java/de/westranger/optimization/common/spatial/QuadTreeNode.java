package de.westranger.optimization.common.spatial;

import de.westranger.geometry.common.simple.BoundingBox;
import de.westranger.geometry.common.simple.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class QuadTreeNode<T extends Point2D> {

  private final Point2D center;
  private final double quadrantLength;

  private QuadTreeNode<T>[] children;
  private List<T> points;

  private final boolean isLeaf;

  public QuadTreeNode(final Point2D center, final double cellSideLength,
                      final boolean isLeaf) {
    this.center = center;
    this.quadrantLength = cellSideLength * 0.5;
    this.children = null;
    this.points = null;
    this.isLeaf = isLeaf;
  }

  public void addPoint(final T pt) {
    if (points == null) {
      this.points = new ArrayList<>(1);
    }
    this.points.add(pt);
  }

  public QuadTreeNode<T> createChild(final QuadTreeChildPosition qtcp, final boolean isLeaf) {
    if (this.children == null) {
      this.children = new QuadTreeNode[4];
    }

    if (this.children[qtcp.getValue()] == null) {
      if (qtcp == QuadTreeChildPosition.CHILD_UPPER_RIGHT) {
        final double x = this.center.getX() + this.quadrantLength * 0.5;
        final double y = this.center.getY() + this.quadrantLength * 0.5;
        this.children[qtcp.getValue()] =
            new QuadTreeNode(new Point2D(x, y), this.quadrantLength, isLeaf);
      } else if (qtcp == QuadTreeChildPosition.CHILD_LOWER_RIGHT) {
        final double x = this.center.getX() + this.quadrantLength * 0.5;
        final double y = this.center.getY() - this.quadrantLength * 0.5;
        this.children[qtcp.getValue()] =
            new QuadTreeNode(new Point2D(x, y), this.quadrantLength, isLeaf);
      } else if (qtcp == QuadTreeChildPosition.CHILD_UPPER_LEFT) {
        final double x = this.center.getX() - this.quadrantLength * 0.5;
        final double y = this.center.getY() + this.quadrantLength * 0.5;
        this.children[qtcp.getValue()] =
            new QuadTreeNode(new Point2D(x, y), this.quadrantLength, isLeaf);
      } else if (qtcp == QuadTreeChildPosition.CHILD_LOWER_LEFT) {
        final double x = this.center.getX() - this.quadrantLength * 0.5;
        final double y = this.center.getY() - this.quadrantLength * 0.5;
        this.children[qtcp.getValue()] =
            new QuadTreeNode(new Point2D(x, y), this.quadrantLength, isLeaf);
      }
    }
    return this.children[qtcp.getValue()];
  }

  public boolean setChild(final QuadTreeNode<T> qtn, final QuadTreeChildPosition qtcp) {
    if (this.children == null) {
      this.children = new QuadTreeNode[4];
    }
    this.children[qtcp.getValue()] = qtn;
    return true;
  }

  public Optional<QuadTreeChildPosition> isInside(final Point2D pt) {
    if (pt.getX() < this.center.getX() && pt.getX() >= this.center.getX() - this.quadrantLength) {
      if (pt.getY() < this.center.getY() && pt.getY() >= this.center.getY() - this.quadrantLength) {
        return Optional.of(QuadTreeChildPosition.CHILD_LOWER_LEFT);
      } else if (pt.getY() >= this.center.getY() &&
          pt.getY() < this.center.getY() + this.quadrantLength) {
        return Optional.of(QuadTreeChildPosition.CHILD_UPPER_LEFT);
      }
    } else if (pt.getX() >= this.center.getX() &&
        pt.getX() < this.center.getX() + this.quadrantLength) {
      if (pt.getY() < this.center.getY() && pt.getY() >= this.center.getY() - this.quadrantLength) {
        return Optional.of(QuadTreeChildPosition.CHILD_LOWER_RIGHT);
      } else if (pt.getY() >= this.center.getY() &&
          pt.getY() < this.center.getY() + this.quadrantLength) {
        return Optional.of(QuadTreeChildPosition.CHILD_UPPER_RIGHT);
      }
    }
    return Optional.empty();
  }

  public Optional<QuadTreeNode<T>> getChild(final QuadTreeChildPosition qtcp) {
    if (this.children == null) {
      return Optional.empty();
    }
    if (this.children[qtcp.getValue()] != null) {
      return Optional.of(this.children[qtcp.getValue()]);
    }
    return Optional.empty();
  }

  public BoundingBox getBoundingBox() {
    final Point2D min = new Point2D(this.center.getX() - this.quadrantLength,
        this.center.getY() - this.quadrantLength);
    final Point2D max = new Point2D(this.center.getX() + this.quadrantLength,
        this.center.getY() + this.quadrantLength);
    return new BoundingBox(min, max);
  }

  public boolean isLeaf() {
    return isLeaf;
  }

  public Point2D getCenter() {
    return this.center;
  }

  public Optional<List<T>> getPoints() {
    if (points == null) {
      return Optional.empty();
    }
    return Optional.of(Collections.unmodifiableList(this.points));
  }

}
