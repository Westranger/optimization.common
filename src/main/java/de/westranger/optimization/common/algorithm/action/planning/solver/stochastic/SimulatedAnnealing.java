package de.westranger.optimization.common.algorithm.action.planning.solver.stochastic;

import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.Score;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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


  public SearchSpaceState optimize(final double solutionAccuracy) {
    SearchSpaceState currentBestSolution = this.initialSolution;
    double currentTemp = this.sap.getGamma() * this.sap.getTMax();
    double gamma = this.sap.getGamma();
    int outerImproved = 0;

    BufferedWriter bwData = null;
    if (telemetryFolder != null) {
      try {
        bwData =
            new BufferedWriter(new FileWriter(telemetryFolder.getAbsolutePath() + "/data.csv"));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    while (currentTemp >= this.sap.getTMin() && outerImproved < 50) {
      Score currentBestScore = currentBestSolution.getScore();

      int improved = 0;
      double attempted = 0;
      int iterSinceLastBestFound = 0;

      Score temperatureBestScore = currentBestSolution.getScore();
      SearchSpaceState temperatureBestSolution = currentBestSolution;
      double bestScoreValue = currentBestScore.getAbsoluteScore();

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
          improved++;

          if (this.telemetryFolder != null) {
            try {
              BufferedWriter bwImage = new BufferedWriter(new FileWriter(
                  this.telemetryFolder.getAbsolutePath() + "/img_" + this.totalIterationCounter +
                      ".svg"));
              bwImage.write(currentBestSolution.toSVG());
              bwImage.close();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
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

        if (telemetryFolder != null) {
          final StringBuilder sb = new StringBuilder();
          sb.append(this.totalIterationCounter);
          sb.append(';');
          sb.append(currentTemp);
          sb.append(';');
          sb.append(candidateScore.getAbsoluteScore());
          sb.append(';');
          sb.append(currentBestScore.getAbsoluteScore());
          sb.append('\n');
          try {
            bwData.write(sb.toString());
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      }

      if (improved > 0) {
        outerImproved = 0;
      } else {
        outerImproved++;
      }

      System.out.println(this.totalIterationCounter + ";" + currentTemp + ";" +
          currentBestScore.getAbsoluteScore() + ";" + temperatureBestScore.getAbsoluteScore() +
          ";" + attempted + ";" + outerImproved + ";" + gamma + ";" + improved);

      if ((bestScoreValue - currentBestScore.getAbsoluteScore()) / bestScoreValue > 0.005) {
        gamma = 0.999;
      } else {
        gamma = this.sap.getGamma();
      }

      currentTemp = gamma * currentTemp;
      currentTemp = Math.max(this.sap.getTMin(), currentTemp);
    }

    if (this.telemetryFolder != null) {
      try {
        bwData.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return currentBestSolution;
  }

  public long getTotalIterationCounter() {
    return totalIterationCounter;
  }
}
