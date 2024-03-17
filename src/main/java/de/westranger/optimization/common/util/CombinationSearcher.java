package de.westranger.optimization.common.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class CombinationSearcher {

  private class Candidate implements Comparable<Candidate> {
    private final double score;
    private final int iter;
    private final String id;

    public Candidate(double score, int iter, String id) {
      this.score = score;
      this.iter = iter;
      this.id = id;
    }

    public double getScore() {
      return score;
    }

    public int getIter() {
      return iter;
    }

    public String getId() {
      return id;
    }

    @Override
    public int compareTo(Candidate o) {
      final double diffScore = this.score - o.getScore();
      if (diffScore > 1e-6) {
        return 1;
      } else if (Math.abs(diffScore) <= 1e-6) {
        final int diffIter = this.iter - o.getIter();
        if (diffIter > 0) {
          return 1;
        } else if (diffIter == 0) {
          return 0;
        }
      }
      return -1;
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("(score=");
      sb.append(this.score);
      sb.append(" iter=");
      sb.append(this.iter);
      sb.append(" id=");
      sb.append(this.id);
      sb.append(")");
      return sb.toString();
    }
  }

  private final Map<String, List<Double>> inputMap;
  private final int numTasksToMaintain;

  private Map<String, Map<String, Double>> openParamCandidates;
  private SortedMap<String, Map<String, Integer>> openIdxCandidates;
  private Map<String, Map<String, Integer>> visited;
  private LinkedList<Candidate> bestNextCandidate;
  private long numPermutations;

  public CombinationSearcher(Map<String, List<Double>> inputMap, Map<String, Integer> startIdx,
                             double score, int iter, int numTasksToMaintain) {
    this.inputMap = inputMap;
    this.numTasksToMaintain = numTasksToMaintain;

    openParamCandidates = new TreeMap<>();
    openIdxCandidates = new TreeMap<>();
    bestNextCandidate = new LinkedList<>();
    visited = new TreeMap<>();

    String id = generateID(startIdx);
    bestNextCandidate.offer(new Candidate(score, iter, id));
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

    Collections.sort(this.bestNextCandidate);

    int numCombinations = (int) Math.pow(2.0, this.inputMap.size());
    int numBits = this.inputMap.size();

    boolean created = false;

    while (!bestNextCandidate.isEmpty() && openParamCandidates.size() < this.numTasksToMaintain) {
      Map<String, Integer> currentIdx;
      Candidate can = this.bestNextCandidate.poll();
      String id = can.getId();
      currentIdx = visited.get(id);

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

        id = generateID(newIdxMap);
        if (!visited.containsKey(id) && !openIdxCandidates.containsKey(id)) {
          openParamCandidates.put(id, newParamMap);
          openIdxCandidates.put(id, newIdxMap);
          created = true;
        }
      }
    }

    System.out.println("generated new  set of params " + openIdxCandidates.size());

    return created;
  }

  public void provideScore(String id, double score, int iter) {
    if (!visited.containsKey(id)) {
      visited.put(id, this.openIdxCandidates.remove(id));
      this.bestNextCandidate.offer(new Candidate(score, iter, id));
    }
  }

  public Map<String, Map<String, Double>> getCandidates(int numTasks) {
    final Map<String, Map<String, Double>> result = new TreeMap<>();
    int cnt = 0;

    Iterator<Map.Entry<String, Map<String, Double>>> iterator =
        openParamCandidates.entrySet().iterator();

    while (iterator.hasNext() && cnt < numTasks) {
      Map.Entry<String, Map<String, Double>> next = iterator.next();
      iterator.remove();
      result.put(next.getKey(), next.getValue());
      //this.visited.put(next.getKey(), this.openIdxCandidates.remove(next.getKey()));
      cnt++;
    }

    return result;
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
