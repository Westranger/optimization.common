package de.westranger.optimization.common.util;

public final class Triple<A, B, C> extends Tuple<A, B> {

  private C thrid;

  public Triple(final A first, final B second, final C third) {
    super(first, second);
    this.thrid = third;
  }

  public C getThird() {
    return thrid;
  }

  public void setThird(final C third) {
    this.thrid = third;
  }

}

