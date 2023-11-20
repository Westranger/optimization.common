package test.de.westranger.optimization.common.algorithm.tools.util;

import java.util.Random;

public class CustomIntegerRandom extends Random {

  private final int[] data;
  private int idx;

  public CustomIntegerRandom(final int[] data) {
    this.data = data;
    idx = 0;
  }

  @Override
  public int nextInt(final int bound) {
    return this.data[this.idx++];
  }

}
