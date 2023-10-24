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
    System.out.println("start optimization with score " + currentBestSolution.getScore().getAbsoluteScore());
    while (currentTemp > this.sap.getTMin()) {
      Score currentBestScore = currentBestSolution.getScore();
      //System.out.println(this.totalIterationCounter + ";" +currentTemp + ";"+ currentBestScore.getAbsoluteScore() );
      int notImproved = 0;
      while (notImproved < this.sap.getOmegaMax()) {
        this.totalIterationCounter++;
        final SearchSpaceState solutionCandidate =
            this.ns.select(currentBestSolution, currentTemp);
        final Score candidateScore = solutionCandidate.getScore();

        if (candidateScore.compareTo(currentBestScore) < 0) {
          currentBestScore = candidateScore;
          currentBestSolution = solutionCandidate;
          notImproved = 0;
          System.out.println("BEST " + this.totalIterationCounter + ";" +currentTemp + ";"+ currentBestScore.getAbsoluteScore());
        } else if (candidateScore.compareTo(currentBestScore) > 0) {
          final double ex = -((solutionCandidate.getScore().getAbsoluteScore() -
              currentBestSolution.getScore().getAbsoluteScore()) / currentTemp);
          final double probability = Math.exp(ex);

          System.out.println("prob " + probability);

          if (probability <= 1.0 && probability >
              this.rng.nextDouble()) {
            //System.out.println("WORSE " + this.totalIterationCounter + ";" +currentTemp + ";"+ currentBestScore.getAbsoluteScore() + " " + ex);
            currentBestScore = candidateScore;
            currentBestSolution = solutionCandidate;
          }else {
            notImproved++;
          }
        } else {
          notImproved++;
        }

        if (currentBestScore.getAbsoluteScore() < solutionAccuracy) {
          return currentBestSolution;
        }
        System.out.println("not improved " + notImproved);
      }

      currentTemp = calculateTemp(currentTemp);
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
