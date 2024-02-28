package de.westranger.optimization.common.algorithm;

import de.westranger.optimization.common.util.PermutationIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class Kopt<T> implements Iterator<List<List<T>>> {

  private final int kValue;
  private final PermutationIterator<List<T>> permutationIterator;
  private int subCounter;
  private final int subCounterMax;
  private List<List<T>> nextPermutation;

  public Kopt(final List<List<T>> elements) {
    if (elements.size() > 31) {
      throw new IllegalArgumentException(
          "k is larger than 32 (was " + elements.size()
              + ") which is not supported by this implementation");
    } else if (elements.isEmpty()) {
      throw new IllegalArgumentException(
          "k must be larger than 1");
    }
    this.kValue = elements.size();
    this.permutationIterator = new PermutationIterator<>(elements, kValue);
    this.subCounter = 0;
    this.subCounterMax = (int) Math.pow(2.0, kValue);
  }

  @Override
  public boolean hasNext() {
    if (this.permutationIterator.hasNext()) {
      return true;
    }
    return this.subCounter != 0;
  }

  @Override
  public List<List<T>> next() {
    if (subCounter == 0) {
      nextPermutation = this.permutationIterator.next();
    }
    final List<List<T>> result = new ArrayList<>();
    for (int i = 0; i < this.kValue; i++) {
      int mask = 1 << i;
      if ((mask & this.subCounter) == mask) {
        result.add(reverse(nextPermutation.get(i)));
      } else {
        result.add(nextPermutation.get(i));
      }
    }
    this.subCounter = (this.subCounter + 1) % this.subCounterMax;
    return result;
  }

  private List<T> reverse(List<T> original) {
    final List<T> copy = new ArrayList<>(original);
    Collections.reverse(copy);
    return copy;
  }
}