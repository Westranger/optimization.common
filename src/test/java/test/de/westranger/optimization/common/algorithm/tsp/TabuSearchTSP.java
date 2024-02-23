package test.de.westranger.optimization.common.algorithm.tsp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabuSearchTSP {
  private static final int TABU_TENURE = 20; // Länge des Tabu-Gedächtnisses
  private List<Point> cities; // Liste von Städten/Knoten
  private List<List<Integer>> tabuList; // Tabu-Liste
  private List<Point> bestSolution; // Beste gefundene Lösung
  private double bestCost; // Kosten der besten Lösung

  public TabuSearchTSP(List<Point> cities) {
    this.cities = new ArrayList<>(cities);
    this.tabuList = new ArrayList<>();
    for (int i = 0; i < cities.size(); i++) {
      List<Integer> row = new ArrayList<>(Collections.nCopies(cities.size(), 0));
      tabuList.add(row);
    }
    this.bestSolution = new ArrayList<>(cities);
    this.bestCost = calculateTotalDistance(this.bestSolution);
  }

  public void search(int iterations) {
    for (int iter = 0; iter < iterations; iter++) {
      List<Point> currentSolution = new ArrayList<>(bestSolution);
      double currentBestCost = bestCost;
      int bestI = -1, bestJ = -1;
      boolean isSwap = true;

      // Durchführen von Swap und Remove-Insert Operationen
      for (int i = 0; i < currentSolution.size(); i++) {
        for (int j = i + 1; j < currentSolution.size(); j++) {
          // Swap
          Collections.swap(currentSolution, i, j);
          double newCost = calculateTotalDistance(currentSolution);
          if ((newCost < currentBestCost) && (tabuList.get(i).get(j) == 0)) {
            currentBestCost = newCost;
            bestI = i;
            bestJ = j;
            isSwap = true;
          }
          Collections.swap(currentSolution, i, j); // Swap zurücksetzen

          // Remove-Insert
          Point removed = currentSolution.remove(i);
          currentSolution.add(j, removed);
          newCost = calculateTotalDistance(currentSolution);
          if ((newCost < currentBestCost) && (tabuList.get(i).get(j) == 0)) {
            currentBestCost = newCost;
            bestI = i;
            bestJ = j;
            isSwap = false;
          }
          // Zustand zurücksetzen
          currentSolution.remove(j);
          currentSolution.add(i, removed);
        }
      }

      if (bestI != -1 && bestJ != -1) {
        if (isSwap) {
          Collections.swap(bestSolution, bestI, bestJ);
        } else {
          Point removed = bestSolution.remove(bestI);
          bestSolution.add(bestJ, removed);
        }
        decrementTabu();
        tabuList.get(bestI).set(bestJ, TABU_TENURE);
        if (currentBestCost < bestCost) {
          bestCost = currentBestCost;
          System.out.println(iter + " --> " + bestCost);
        }
      }
    }
  }

  private void decrementTabu() {
    for (List<Integer> row : tabuList) {
      for (int i = 0; i < row.size(); i++) {
        if (row.get(i) > 0) {
          row.set(i, row.get(i) - 1);
        }
      }
    }
  }

  private double calculateTotalDistance(List<Point> solution) {
    double totalDistance = 0;
    for (int i = 0; i < solution.size(); i++) {
      Point start = solution.get(i);
      Point end = solution.get((i + 1) % solution.size());
      totalDistance += start.distanceTo(end);
    }
    return totalDistance;
  }

  public List<Point> getBestSolution() {
    return bestSolution;
  }

  public double getBestCost() {
    return bestCost;
  }

  public static class Point {
    double x, y;

    public Point(double x, double y) {
      this.x = x;
      this.y = y;
    }

    public double distanceTo(Point other) {
      return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
  }

  public static void main(String[] args) {
    // Beispiel: Initialisierung und Start der Suche
    List<Point> cities = new ArrayList<>();
    cities.add(new Point(20833.3333,17100.0000));
    cities.add(new Point(20900.0000,17066.6667));
    cities.add(new Point(21300.0000,13016.6667));
    cities.add(new Point(21600.0000,14150.0000));
    cities.add(new Point(21600.0000,14966.6667));
    cities.add(new Point(21600.0000,16500.0000));
    cities.add(new Point(22183.3333,13133.3333));
    cities.add(new Point(22583.3333,14300.0000));
    cities.add(new Point(22683.3333,12716.6667));
    cities.add(new Point(23616.6667,15866.6667));
    cities.add(new Point(23700.0000,15933.3333));
    cities.add(new Point(23883.3333,14533.3333));
    cities.add(new Point(24166.6667,13250.0000));
    cities.add(new Point(25149.1667,12365.8333));
    cities.add(new Point(26133.3333,14500.0000));
    cities.add(new Point(26150.0000,10550.0000));
    cities.add(new Point(26283.3333,12766.6667));
    cities.add(new Point(26433.3333,13433.3333));
    cities.add(new Point(26550.0000,13850.0000));
    cities.add(new Point(26733.3333,11683.3333));
    cities.add(new Point(27026.1111,13051.9444));
    cities.add(new Point(27096.1111,13415.8333));
    cities.add(new Point(27153.6111,13203.3333));
    cities.add(new Point(27166.6667,9833.3333));
    cities.add(new Point(27233.3333,10450.0000));
    cities.add(new Point(27233.3333,11783.3333));
    cities.add(new Point(27266.6667,10383.3333));
    cities.add(new Point(27433.3333,12400.0000));
    cities.add(new Point(27462.5000,12992.2222));

    TabuSearchTSP tsp = new TabuSearchTSP(cities);
    tsp.search(10000); // Anzahl der Iterationen
    System.out.println("Beste Lösung: " + tsp.getBestSolution());
    System.out.println("Kosten der besten Lösung: " + tsp.getBestCost());
  }
}
