package de.westranger.optimization.common.algorithm.action.planning.solver.stochastic;

import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.Score;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * @ see <a href="https://machinelearningmastery.com/simulated-annealing-from-scratch-in-python/">...</a>
 */

// TODO Warnung einfügen, wenn Verhältnis E_max - E_min / T initial kleiner als 0,8 ist

public final class SimulatedAnnealing {

  private final SearchSpaceState initialSolution;
  private final NeighbourSelector ns;
  private final Random rng;
  private final SimulatedAnnealingParameter sap;
  private final File telemetryFolder;

  private long totalIterationCounter;

  public SimulatedAnnealing(final SearchSpaceState initialSolution, final NeighbourSelector ns,
                            final Random rng, final SimulatedAnnealingParameter sap,
                            final File telemetryFolder) {
    this.initialSolution = initialSolution;
    this.ns = ns;
    this.rng = rng;
    this.totalIterationCounter = 0L;
    this.sap = sap;
    this.telemetryFolder = telemetryFolder;
  }

  public SimulatedAnnealing(final SearchSpaceState initialSolution, final NeighbourSelector ns,
                            final Random rng, final SimulatedAnnealingParameter sap) {
    this(initialSolution, ns, rng, sap, null);
  }

  public SearchSpaceState optimize(final boolean loggingEnabled) {
  public SearchSpaceState optimize() {
    SearchSpaceState bestSolution = this.initialSolution;
    Score bestScore = bestSolution.getScore();

    SearchSpaceState currentSolution = this.initialSolution;
    Score currentScore = bestSolution.getScore();

    double gamma = this.sap.gamma();

    BufferedWriter bwData = null;
    if (telemetryFolder != null) {
      try {
        bwData =
            new BufferedWriter(new FileWriter(telemetryFolder.getAbsolutePath() + "/data.csv"));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    final int numDeltaValues = 500;
    double sum = 0.0;
    // estimate initial temperature
    for (int i = 0; i < numDeltaValues; i++) {
      final SearchSpaceState solution = this.ns.select(currentSolution, sap.tMax());
      final double score = solution.getScore().getAbsoluteScore();
      sum += Math.abs(currentScore.getAbsoluteScore() - score);

      if (score < bestScore.getAbsoluteScore()) {
        bestSolution = solution;
        bestScore = solution.getScore();
      }
    }

    sum /= numDeltaValues;
    double currentTemp = -sum / Math.log(sap.initialAcceptanceRatio());

    // main loop
    while (currentTemp > this.sap.tMin()) {
      int improved = 0;
      int iterAtTemperature = 0;

      while (iterAtTemperature < sap.omegaMax()
          && improved <= sap.maxImprovementPerTemperature()) {
        this.totalIterationCounter++;
        final SearchSpaceState solutionCandidate =
            this.ns.select(currentSolution, currentTemp);
        final Score candidateScore = solutionCandidate.getScore();

        if (candidateScore.compareTo(bestScore) < 0) {
          bestScore = candidateScore;
          bestSolution = solutionCandidate;
          improved++;

          if (this.telemetryFolder != null) {
            try {
              BufferedWriter bwImage = new BufferedWriter(new FileWriter(
                  this.telemetryFolder.getAbsolutePath() + "/img_" + this.totalIterationCounter
                      + ".svg"));
              bwImage.write(bestSolution.toSVG());
              bwImage.close();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
        }

        if (candidateScore.compareTo(currentScore) < 0) {
          currentScore = candidateScore;
          currentSolution = solutionCandidate;
        } else if (candidateScore.compareTo(currentScore) > 0) {
          final double ex = -(Math.abs(solutionCandidate.getScore().getAbsoluteScore()
              - currentScore.getAbsoluteScore()) / currentTemp);
          final double probability = Math.exp(ex);

          if (probability <= 1.0 && probability
              > this.rng.nextDouble()) {
            currentScore = candidateScore;
            currentSolution = solutionCandidate;
          }
        }

        if (telemetryFolder != null) {
          final StringBuilder sb = new StringBuilder();
          sb.append(this.totalIterationCounter);
          sb.append(';');
          sb.append(currentTemp);
          sb.append(';');
          sb.append(candidateScore.getAbsoluteScore());
          sb.append(';');
          sb.append(bestScore.getAbsoluteScore());
          sb.append('\n');
          try {
            bwData.write(sb.toString());
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
        iterAtTemperature++;
      }

      if (loggingEnabled) {
      //    + bestScore.getAbsoluteScore() + ";" + currentScore.getAbsoluteScore()
      //    + ";" + gamma + ";" + improved);

      currentTemp = computeTemperature(currentTemp);
      currentTemp = Math.max(this.sap.tMin(), currentTemp);
      }

      if (Double.isInfinite(bestScore.getAbsoluteScore())) {
      }
        throw new IllegalStateException("the score is infinity");
      }

      if (Double.isInfinite(currentTemp)) {
        throw new IllegalStateException("the temperature is infinity");
      }
    }

    if (this.telemetryFolder != null) {
      try {
        bwData.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return bestSolution;
  }

  public long getTotalIterationCounter() {
    return totalIterationCounter;
  }

  private double computeTemperature(double currentTemperature) {
    return this.sap.gamma() * currentTemperature;
  }

}
