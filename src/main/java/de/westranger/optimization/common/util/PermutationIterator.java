package de.westranger.optimization.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

public final class PermutationIterator<T> implements Iterator<List<T>> {
  private final List<T> elements;
  private final List<T> nextPermutation;
  private boolean canGenerateNextPermutation;

  private final int[] indices;

  public PermutationIterator(final List<T> elements, final int permutationLength) {
    if (permutationLength < 1 || permutationLength > elements.size()) {
      throw new IllegalArgumentException(
          "Permutation length must be between 1 and the size of elements list");
    }
    this.elements = new ArrayList<>(elements);
    this.nextPermutation = new ArrayList<>(this.elements.subList(0, permutationLength));
    this.canGenerateNextPermutation = true;

    this.indices = new int[permutationLength];
    for (int i = 0; i < permutationLength; i++) {
      this.indices[i] = i;
    }
  }

  @Override
  public boolean hasNext() {
    return canGenerateNextPermutation;
  }

  @Override
  public List<T> next() {
    if (!hasNext()) {
      throw new NoSuchElementException("No more permutations are available");
    }

    List<T> currentPermutation = new ArrayList<>(nextPermutation);
    generateNextPermutation();
    return currentPermutation;
  }

  private void generateNextPermutation() {
    this.canGenerateNextPermutation = false;

    for (int idx = this.indices.length - 1; idx >= 0; idx--) {
      if (!incrementIfPossible(this.indices, this.elements.size(), idx)) {
        continue;
      }

      final TreeSet<Integer> tmp = new TreeSet<>();
      for (int j = 0; j < this.elements.size(); j++) {
        tmp.add(j);
      }

      for (int j = 0; j <= idx; j++) {
        tmp.remove(this.indices[j]);
      }

      for (int j = idx + 1; j < this.indices.length; j++) {
        this.indices[j] = tmp.pollFirst();
      }

      this.canGenerateNextPermutation = true;
      break;
    }

    for (int i = 0; i < this.indices.length; i++) {
      this.nextPermutation.set(i, this.elements.get(this.indices[i]));
    }
  }

  private boolean findFirst(final int[] values, final int endIdx,
                            final int valueToFind) {
    for (int i = 0; i <= endIdx; i++) {
      if (values[i] == valueToFind) {
        return true;
      }
    }
    return false;
  }

  boolean incrementIfPossible(int[] indices, final int numElements, final int idx) {
    for (int i = indices[idx] + 1; i < numElements; i++) {
      if (!findFirst(indices, idx - 1, i)) {
        indices[idx] = i;
        return true;
      }
    }
    return false;
  }
}
