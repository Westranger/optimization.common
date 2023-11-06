package de.westranger.optimization.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;

public final class PermutationIterator {
  private final LinkedHashMap<String, List<Double>> inputMap;
  private final LinkedHashMap<String, Integer> currentIndices;
  private boolean done = false;

  public PermutationIterator(LinkedHashMap<String, List<Double>> inputMap) {
    this.inputMap = inputMap;
    this.currentIndices = new LinkedHashMap<>();
    for (String key : inputMap.keySet()) {
      currentIndices.put(key, 0);
    }
  }

  public boolean hasNext() {
    return !done;
  }

  public Map<String, Double> next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }

    // Erzeuge die aktuelle Permutation
    Map<String, Double> currentPermutation = new LinkedHashMap<>();
    for (Map.Entry<String, List<Double>> entry : inputMap.entrySet()) {
      String key = entry.getKey();
      List<Double> list = entry.getValue();
      int index = currentIndices.get(key);
      currentPermutation.put(key, list.get(index));
    }

    // Finde das erste Element von hinten, das erhöht werden kann
    ListIterator<String> listIterator =
        new ArrayList<>(currentIndices.keySet()).listIterator(currentIndices.size());
    boolean incremented = false;
    while (listIterator.hasPrevious()) {
      String key = listIterator.previous();
      int index = currentIndices.get(key);
      if (index + 1 < inputMap.get(key).size()) {
        currentIndices.put(key, index + 1);
        incremented = true;
        break;
      } else {
        currentIndices.put(key, 0);
      }
    }

    // Wenn nichts erhöht werden konnte, sind wir fertig
    if (!incremented) {
      done = true;
    }

    return Collections.unmodifiableMap(currentPermutation);
  }

}
