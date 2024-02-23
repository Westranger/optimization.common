package de.westranger.optimization.common.spatial;

import de.westranger.geometry.common.simple.BoundingBox;
import de.westranger.geometry.common.simple.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class QuadTreeNode {

  private final Point2D center;
  private final double quadrantLength;

  private QuadTreeNode parent;
  private QuadTreeNode[] children;
  private List<Point2D> points;

  private final boolean isLeaf;

  public QuadTreeNode(final Point2D center, final double cellSideLength, final boolean isLeaf) {
    this(null, center, cellSideLength, isLeaf);
  }

  public QuadTreeNode(final QuadTreeNode parent, final Point2D center, final double cellSideLength,
                      final boolean isLeaf) {
    this.parent = parent;
    this.center = center;
    this.quadrantLength = cellSideLength * 0.5;
    this.children = null;
    this.points = null;
    this.isLeaf = isLeaf;
  }

  public void addPoint(final Point2D pt) {
    if (points == null) {
      this.points = new ArrayList<>(1);
    }
    this.points.add(pt);
  }

  public QuadTreeNode createChild(final QuadTreeChildPosition qtcp, final boolean isLeaf) {
    if (this.children == null) {
      this.children = new QuadTreeNode[4];
    }

    if (this.children[qtcp.getValue()] == null) {
      if (qtcp == QuadTreeChildPosition.CHILD_UPPER_RIGHT) {
        final double x = this.center.getX() + this.quadrantLength * 0.5;
        final double y = this.center.getY() + this.quadrantLength * 0.5;
        this.children[qtcp.getValue()] =
            new QuadTreeNode(this, new Point2D(x, y), this.quadrantLength, isLeaf);
      } else if (qtcp == QuadTreeChildPosition.CHILD_LOWER_RIGHT) {
        final double x = this.center.getX() + this.quadrantLength * 0.5;
        final double y = this.center.getY() - this.quadrantLength * 0.5;
        this.children[qtcp.getValue()] =
            new QuadTreeNode(this, new Point2D(x, y), this.quadrantLength, isLeaf);
      } else if (qtcp == QuadTreeChildPosition.CHILD_UPPER_LEFT) {
        final double x = this.center.getX() - this.quadrantLength * 0.5;
        final double y = this.center.getY() + this.quadrantLength * 0.5;
        this.children[qtcp.getValue()] =
            new QuadTreeNode(this, new Point2D(x, y), this.quadrantLength, isLeaf);
      } else if (qtcp == QuadTreeChildPosition.CHILD_LOWER_LEFT) {
        final double x = this.center.getX() - this.quadrantLength * 0.5;
        final double y = this.center.getY() - this.quadrantLength * 0.5;
        this.children[qtcp.getValue()] =
            new QuadTreeNode(this, new Point2D(x, y), this.quadrantLength, isLeaf);
      }
    }
    return this.children[qtcp.getValue()];
  }

  public boolean setChild(final QuadTreeNode qtn, final QuadTreeChildPosition qtcp) {
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

  public Optional<QuadTreeNode> getChild(final QuadTreeChildPosition qtcp) {
    if (this.children == null) {
      return Optional.empty();
    }
    if (this.children[qtcp.getValue()] != null) {
      return Optional.of(this.children[qtcp.getValue()]);
    }
    return Optional.empty();
  }

  public Optional<QuadTreeNode> getParent() {
    return Optional.of(this.parent);
  }

  public void setParent(final QuadTreeNode parent) {
    this.parent = parent;
  }

  public BoundingBox getBoundingBox() {
    final Point2D min = new Point2D(this.center.getY() - this.quadrantLength,
        this.center.getY() - this.quadrantLength);
    final Point2D max = new Point2D(this.center.getY() + this.quadrantLength,
        this.center.getY() + this.quadrantLength);
    return new BoundingBox(min, max);
  }

  public boolean isLeaf() {
    return isLeaf;
  }

  public Point2D getCenter() {
    return this.center;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("QTN(center=");
    sb.append(this.center);
    sb.append(" sl=");
    sb.append(this.quadrantLength * 2.0);
    sb.append(' ');
    if (this.children != null) {
      if (this.children[QuadTreeChildPosition.CHILD_UPPER_RIGHT.getValue()] != null) {
        sb.append("ur ");
      }
      if (this.children[QuadTreeChildPosition.CHILD_LOWER_RIGHT.getValue()] != null) {
        sb.append("lr ");
      }
      if (this.children[QuadTreeChildPosition.CHILD_UPPER_LEFT.getValue()] != null) {
        sb.append("ul ");
      }
      if (this.children[QuadTreeChildPosition.CHILD_LOWER_LEFT.getValue()] != null) {
        sb.append("ll ");
      }
    }
    sb.delete(sb.length() - 1, sb.length());
    sb.append(')');
    return sb.toString();
  }

  public Optional<List<Point2D>> getPoints() {
    if (points == null) {
      return Optional.empty();
    }
    return Optional.of(Collections.unmodifiableList(this.points));
  }

}
