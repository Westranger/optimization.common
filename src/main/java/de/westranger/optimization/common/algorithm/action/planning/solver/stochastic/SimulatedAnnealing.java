package de.westranger.optimization.common.algorithm.action.planning.solver.stochastic;

import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.Score;

import java.util.Random;

/**
 * @ see https://machinelearningmastery.com/simulated-annealing-from-scratch-in-python/
 */

// TODO Warnung einfügen, wenn Verhältnis E_max - E_min / T initial kleiner als 0,8 ist

public final class SimulatedAnnealing {

  private final SearchSpaceState initialSolution;
  private final NeighbourSelector ns;
  private final Random rng;
  private final SimulatedAnnealingParameter sap;

  private long totalIterationCounter;

  public SimulatedAnnealing(final SearchSpaceState initialSolution, final NeighbourSelector ns,
                            final Random rng, final SimulatedAnnealingParameter sap) {
    this.initialSolution = initialSolution;
    this.ns = ns;
    this.rng = rng;
    this.totalIterationCounter = 0L;
    this.sap = sap;
  }

  public SearchSpaceState optimize(final double solutionAccuracy) {

    SearchSpaceState currentBestSolution = this.initialSolution;
    double currentTemp = this.sap.getGamma() * this.sap.getTMax();

    int outerImproved = 0;
    while (currentTemp >= this.sap.getTMin() && outerImproved < 50) {
      Score currentBestScore = currentBestSolution.getScore();

      boolean improved = false;
      double attempted = 0;

      int iterSinceLastBestFound = 0;

      Score temperatureBestScore = currentBestSolution.getScore();
      SearchSpaceState temperatureBestSolution = currentBestSolution;
      while (iterSinceLastBestFound < sap.getOmegaMax()) {
        this.totalIterationCounter++;
        final SearchSpaceState solutionCandidate =
            this.ns.select(temperatureBestSolution, currentTemp);
        final Score candidateScore = solutionCandidate.getScore();

        attempted++;

        if (candidateScore.compareTo(currentBestScore) < 0) {
          currentBestScore = candidateScore;
          currentBestSolution = solutionCandidate;
          iterSinceLastBestFound = 0;
          improved = true;
        }

        if (candidateScore.compareTo(temperatureBestScore) < 0) {
          temperatureBestScore = candidateScore;
          temperatureBestSolution = solutionCandidate;

        } else if (candidateScore.compareTo(temperatureBestScore) > 0) {
          final double ex = -(Math.abs(solutionCandidate.getScore().getAbsoluteScore() -
              temperatureBestSolution.getScore().getAbsoluteScore()) / currentTemp);
          final double probability = Math.exp(ex);

          if (probability <= 1.0 && probability >
              this.rng.nextDouble()) {
            temperatureBestScore = candidateScore;
            temperatureBestSolution = solutionCandidate;
          }
        }

        if (temperatureBestScore.getAbsoluteScore() < solutionAccuracy) {
          return temperatureBestSolution;
        }
        iterSinceLastBestFound++;
      }

      if(!improved){
        outerImproved++;
      }else{
        outerImproved = 0;
      }

      //System.out.println(this.totalIterationCounter + ";" + currentTemp + ";" +
      //    currentBestScore.getAbsoluteScore() + ";" + temperatureBestScore.getAbsoluteScore() +
      //    ";" + attempted + ";" + outerImproved);

      if(attempted > sap.getOmegaMax()){
        currentTemp = 0.99 * currentTemp;
      }else{
        currentTemp = calculateTemp(currentTemp);
      }
      currentTemp = Math.max(this.sap.getTMin(),currentTemp);
    }

    return currentBestSolution;
  }

  private double calculateTemp(final double temperature) {
    return this.sap.getGamma() * temperature;
  }

  public long getTotalIterationCounter() {
    return totalIterationCounter;
  }
}
