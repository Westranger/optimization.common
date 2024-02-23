package de.westranger.optimization.common.algorithm.tools.util;

import java.util.Random;

public final class CustomRandom extends Random {

  private final int[] iData;
  private final boolean[] bData;
  private int iIdx;
  private int bIdx;

  public CustomRandom(final int[] data) {
    this(data, null);
  }

  public CustomRandom(final int[] iData, final boolean[] bData) {
    this.iData = iData;
    this.bData = bData;
    this.iIdx = 0;
    this.bIdx = 0;
  }

  @Override
  public int nextInt(final int bound) {
    if (this.iData[this.iIdx] > bound) {
      throw new IllegalStateException(
          "the value which has inserted into the testcase does exceed the provided bound");
    }
    return this.iData[this.iIdx++];
  }

  @Override
  public boolean nextBoolean() {
    return this.bData[this.bIdx++];
  }

}
