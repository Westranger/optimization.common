package de.westranger.optimization.common.spatial;

public enum QuadTreeChildPosition {
  CHILD_UPPER_RIGHT(0),
  CHILD_LOWER_RIGHT(1),
  CHILD_LOWER_LEFT(2),
  CHILD_UPPER_LEFT(3);

  private final int value;

  private QuadTreeChildPosition(final int value) {
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

}
