package de.westranger.optimization.common.spatial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.westranger.geometry.common.math.Vector2D;
import de.westranger.geometry.common.simple.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class QuadTreeTest {


  @Test
  void testAddPointsA() {
    final double baseLength = 0.5;
    final Point2D upperRight = new Point2D(7 * 0.5, 7 * 0.5);
    final Point2D lowerRight = new Point2D(7 * 0.5, 0.5);
    final Point2D upperLeft = new Point2D(0.5, 7 * 0.5);
    final Point2D lowerLeft = new Point2D(0.5, 0.5);

    List<Point2D> pts = new ArrayList<>();
    List<List<QuadTreeChildPosition>> solutionPosition = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        pts.add(new Point2D(baseLength + i * baseLength * 2, baseLength + j * baseLength * 2));
        List<QuadTreeChildPosition> pos = new ArrayList<>();
        if (i > 1 && j > 1) {
          pos.add(QuadTreeChildPosition.CHILD_UPPER_RIGHT);
        } else if (i > 1 && j < 2) {
          pos.add(QuadTreeChildPosition.CHILD_LOWER_RIGHT);
        } else if (i < 2 && j > 1) {
          pos.add(QuadTreeChildPosition.CHILD_UPPER_LEFT);
        } else if (i < 2 && j < 2) {
          pos.add(QuadTreeChildPosition.CHILD_LOWER_LEFT);
        }

        if (i % 2 == 1 && j % 2 == 1) {
          pos.add(QuadTreeChildPosition.CHILD_UPPER_RIGHT);
        } else if (i % 2 == 1 && j % 2 == 0) {
          pos.add(QuadTreeChildPosition.CHILD_LOWER_RIGHT);
        } else if (i % 2 == 0 && j % 2 == 1) {
          pos.add(QuadTreeChildPosition.CHILD_UPPER_LEFT);
        } else if (i % 2 == 0 && j % 2 == 0) {
          pos.add(QuadTreeChildPosition.CHILD_LOWER_LEFT);
        }

        solutionPosition.add(pos);
      }
    }

    for (int i = 0; i < pts.size(); i++) {
      for (int j = 0; j < pts.size(); j++) {
        final Point2D ptA = pts.get(i);
        final Point2D ptB = pts.get(j);

        Vector2D diff = ptA.diff(ptB);
        if (Math.abs(diff.getX()) < 2.0 || Math.abs(diff.getY()) < 2.0) {
          continue;
        }

        QuadTree qt = new QuadTree(1.0, ptA);
        qt.add(ptB);

        QuadTreeNode root = qt.getRoot();
        if (root.getCenter().distance(new Point2D(2.0, 2.0)) > 0.0) {
          continue;
        }

        System.out.println("testing ptA=" + ptA + " ptB=" + ptB);

        assertEquals(0.0, root.getCenter().distance(new Point2D(2.0, 2.0)), 1e-10);
        assertFalse(root.isLeaf());
        assertTrue(root.getPoints().isEmpty());

        for (QuadTreeChildPosition value : QuadTreeChildPosition.values()) {
          if (value == solutionPosition.get(i).get(0) || value == solutionPosition.get(j).get(0)) {
            continue;
          }
          assertTrue(root.getChild(value).isEmpty());
        }

        Optional<QuadTreeNode> childA = root.getChild(solutionPosition.get(i).get(0));
        assertTrue(childA.isPresent());
        assertFalse(childA.get().isLeaf());
        assertTrue(childA.get().getPoints().isEmpty());

        Optional<QuadTreeNode> childB = root.getChild(solutionPosition.get(j).get(0));
        assertTrue(childB.isPresent());
        assertFalse(childB.get().isLeaf());
        assertTrue(childB.get().getPoints().isEmpty());

        root = childA.get();
        for (QuadTreeChildPosition value : QuadTreeChildPosition.values()) {
          if (value == solutionPosition.get(i).get(1)) {
            continue;
          }
          assertTrue(root.getChild(value).isEmpty());
        }
        childA = root.getChild(solutionPosition.get(i).get(1));
        assertTrue(childA.isPresent());
        assertTrue(childA.get().isLeaf());
        assertTrue(childA.get().getPoints().isPresent());
        assertEquals(1, childA.get().getPoints().get().size());

        root = childB.get();
        for (QuadTreeChildPosition value : QuadTreeChildPosition.values()) {
          if (value == solutionPosition.get(j).get(1)) {
            continue;
          }
          assertTrue(root.getChild(value).isEmpty());
        }
        childB = root.getChild(solutionPosition.get(j).get(1));
        assertTrue(childB.isPresent());
        assertTrue(childB.get().isLeaf());
        assertTrue(childB.get().getPoints().isPresent());
        assertEquals(1, childB.get().getPoints().get().size());
      }
    }
  }

  @Test
  void testAddPointsB() {
    final double baseLength = 0.5;
    final Point2D upperRight = new Point2D(7 * 0.5, 7 * 0.5);
    final Point2D lowerLeft = new Point2D(0.5, 0.5);

    List<Point2D> pts = new ArrayList<>();
    List<List<QuadTreeChildPosition>> solutionPosition = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        pts.add(new Point2D(baseLength + i * baseLength * 2, baseLength + j * baseLength * 2));
        List<QuadTreeChildPosition> pos = new ArrayList<>();
        if (i > 1 && j > 1) {
          pos.add(QuadTreeChildPosition.CHILD_UPPER_RIGHT);
        } else if (i > 1 && j < 2) {
          pos.add(QuadTreeChildPosition.CHILD_LOWER_RIGHT);
        } else if (i < 2 && j > 1) {
          pos.add(QuadTreeChildPosition.CHILD_UPPER_LEFT);
        } else if (i < 2 && j < 2) {
          pos.add(QuadTreeChildPosition.CHILD_LOWER_LEFT);
        }

        if (i % 2 == 1 && j % 2 == 1) {
          pos.add(QuadTreeChildPosition.CHILD_UPPER_RIGHT);
        } else if (i % 2 == 1 && j % 2 == 0) {
          pos.add(QuadTreeChildPosition.CHILD_LOWER_RIGHT);
        } else if (i % 2 == 0 && j % 2 == 1) {
          pos.add(QuadTreeChildPosition.CHILD_UPPER_LEFT);
        } else if (i % 2 == 0 && j % 2 == 0) {
          pos.add(QuadTreeChildPosition.CHILD_LOWER_LEFT);
        }

        solutionPosition.add(pos);
      }
    }

    QuadTree qt = new QuadTree(1.0, upperRight);
    qt.add(lowerLeft);

    for (int i = 1; i < pts.size() - 1; i++) {
      qt.add(pts.get(i));
    }

    for (int i = 0; i < pts.size(); i++) {
      QuadTreeNode root = qt.getRoot();
      List<QuadTreeChildPosition> list = solutionPosition.get(i);

      Optional<QuadTreeNode> child = root.getChild(list.get(0));
      assertTrue(child.isPresent());
      child = child.get().getChild(list.get(1));
      assertTrue(child.isPresent());

      assertEquals(1, child.get().getPoints().get().size());
      assertEquals(0.0, child.get().getPoints().get().get(0).distance(pts.get(i)), 1e-10);
    }
  }

  @Test
  void testFindPoints() {
    final double baseLength = 0.5;
    final Point2D upperRight = new Point2D(7 * 0.5, 7 * 0.5);
    final Point2D lowerRight = new Point2D(7 * 0.5, 0.5);
    final Point2D upperLeft = new Point2D(0.5, 7 * 0.5);
    final Point2D lowerLeft = new Point2D(0.5, 0.5);


    QuadTree qt = new QuadTree(1.0, upperRight);
    qt.add(lowerLeft);
    qt.add(lowerRight);
    qt.add(upperLeft);

    final Point2D a1 = new Point2D(3.1, 1.1);
    final Point2D a2 = a1.pointAt(Math.PI * 0.5, 0.3);
    final Point2D a3 = a1.pointAt(Math.PI * 1.0, 0.3);
    final Point2D a4 = a1.pointAt(Math.PI * 1.5, 0.3);
    final Point2D a5 = a1.pointAt(Math.PI * 2.0, 0.3);
    final Point2D a6 = a1.pointAt(Math.PI * 0.5, 0.7);
    final Point2D a7 = a1.pointAt(Math.PI * 1.0, 0.7);
    final Point2D a8 = a1.pointAt(Math.PI * 1.5, 0.7);
    final Point2D a9 = a1.pointAt(Math.PI * 2.0, 0.7);

    qt.add(a2);
    qt.add(a3);
    qt.add(a4);
    qt.add(a5);
    qt.add(a6);
    qt.add(a7);
    qt.add(a8);
    qt.add(a9);

    Optional<List<Point2D>> pointsInArea = qt.getPointsInArea(a1, 0.4);
    assertTrue(pointsInArea.isPresent());
    assertEquals(4, pointsInArea.get().size());

  }


}