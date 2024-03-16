package de.westranger.optimization.common.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class CombinationSearcher {

  private final Map<String, List<Double>> inputMap;

  private Map<String, Map<String, Double>> openParamCandidates;
  private SortedMap<String, Map<String, Integer>> openIdxCandidates;
  private Map<String, Map<String, Integer>> visited;
  private SortedMap<Double, String> bestNextCandidate;
  private long numPermutations;

  public CombinationSearcher(Map<String, List<Double>> inputMap, Map<String, Integer> startIdx,
                             double score) {
    this.inputMap = inputMap;

    openParamCandidates = new TreeMap<>();
    openIdxCandidates = new TreeMap<>();
    bestNextCandidate = new TreeMap<>();
    visited = new TreeMap<>();

    String id = generateID(startIdx);
    bestNextCandidate.put(score, id);
    visited.put(id, startIdx);

    numPermutations = 1;
    for (Map.Entry<String, List<Double>> entry : inputMap.entrySet()) {
      numPermutations *= entry.getValue().size();
    }
  }

  public boolean computeNextParameterList() {
    if (!openParamCandidates.isEmpty()) {
      return true;
    }


    int numCombinations = (int) Math.pow(2.0, this.inputMap.size());
    int numBits = this.inputMap.size();

    Map<String, Integer> currentIdx;
    if (!bestNextCandidate.isEmpty()) {
      String id = bestNextCandidate.remove(this.bestNextCandidate.firstKey());
      currentIdx = visited.get(id);
    } else {
      return false;
    }

    for (int i = 1; i < numCombinations; i++) {
      //System.out.print(i + " ");

      Map<String, Double> newParamMap = new LinkedHashMap<>();
      Map<String, Integer> newIdxMap = new LinkedHashMap<>();
      Iterator<Map.Entry<String, Integer>> iterIdx = currentIdx.entrySet().iterator();
      for (int b = 0; b < numBits; b++) {
        iterIdx.hasNext();
        Map.Entry<String, Integer> next = iterIdx.next();

        int mask = (0x1 << b);
        int inc = i & mask;
        if (inc == mask) {
          int newIdx = next.getValue() + 1;
          if (newIdx < this.inputMap.get(next.getKey()).size()) {
            newIdxMap.put(next.getKey(), newIdx);
            String tmpK = next.getKey();
            List<Double> lst = this.inputMap.get(tmpK);
            double tmp = lst.get(newIdx);
            newParamMap.put(next.getKey(), tmp);
          } else {
            newIdxMap.put(next.getKey(), next.getValue());
            newParamMap.put(next.getKey(), this.inputMap.get(next.getKey()).get(next.getValue()));
          }
          //System.out.print("1 ");
        } else {
          newIdxMap.put(next.getKey(), next.getValue());
          newParamMap.put(next.getKey(), this.inputMap.get(next.getKey()).get(next.getValue()));

          //System.out.print("0 ");
        }
      }
      //System.out.println();

      final String id = generateID(newIdxMap);
      if (!visited.containsKey(id) && !openIdxCandidates.containsKey(id)) {
        openParamCandidates.put(id, newParamMap);
        openIdxCandidates.put(id, newIdxMap);
      }
    }

    System.out.println("generated new  set of params " + openIdxCandidates.size());

    return true;
  }

  public void provideScore(String id, double score) {
    if (!this.openIdxCandidates.containsKey(id)) {
      //System.out.println("we discarded " + id + " " + score);
      return;
      //throw new IllegalArgumentException("the id is not in the open list");
    }

    //System.out.println("we added " + id + " " + score);
    visited.put(id, this.openIdxCandidates.remove(id));
    this.bestNextCandidate.put(score, id);
    this.openParamCandidates.remove(id);
  }

  public Map<String, Map<String, Double>> getCandidates() {
    return new TreeMap(openParamCandidates);
  }

  private String generateID(Map<String, Integer> map) {
    StringBuilder sb = new StringBuilder();

    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      sb.append(entry.getValue());
      sb.append('_');
    }
    sb.delete(sb.length() - 1, sb.length());
    return sb.toString();
  }

  public long getNumCombinations() {
    return numPermutations;
  }

}
