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

public final class SimulatedAnnealing<T extends Score> {

  private final SearchSpaceState<T> initialSolution;
  private final NeighbourSelector ns;
  private final Random rng;
  private final SimulatedAnnealingParameter sap;
  private final File telemetryFolder;

  private long totalIterationCounter;

  public SimulatedAnnealing(final SearchSpaceState<T> initialSolution, final NeighbourSelector ns,
                            final Random rng, final SimulatedAnnealingParameter sap,
                            final File telemetryFolder) {
    this.initialSolution = initialSolution;
    this.ns = ns;
    this.rng = rng;
    this.totalIterationCounter = 0L;
    this.sap = sap;
    this.telemetryFolder = telemetryFolder;
  }

  public SimulatedAnnealing(final SearchSpaceState<T> initialSolution, final NeighbourSelector ns,
                            final Random rng, final SimulatedAnnealingParameter sap) {
    this(initialSolution, ns, rng, sap, null);
  }

  public SearchSpaceState optimize(final boolean loggingEnabled) {
    SearchSpaceState<T> bestSolution = this.initialSolution;
    T bestScore = bestSolution.getScore();

    SearchSpaceState<T> currentSolution = this.initialSolution;
    T currentScore = bestSolution.getScore();

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
    // estimate initial temperature
    double[] sums = new double[bestScore.getDimensions()];
    for (int i = 0; i < numDeltaValues; i++) {
      final SearchSpaceState<T> solution = this.ns.select(currentSolution, sap.tMax());
      final Score score = currentScore.difference(solution.getScore());

      for (int j = 0; j < sums.length; j++) {
        sums[j] += Math.abs(currentScore.getValue(j) - score.getValue(j));
      }

      if (bestScore.compareTo(score) == -1) {
        bestSolution = solution;
        bestScore = solution.getScore();
      }
    }

    for (int j = 0; j < sums.length; j++) {
      sums[j] /= numDeltaValues;
    }

    double[] currentTemps = new double[bestScore.getDimensions()];
    for (int j = 0; j < sums.length; j++) {
      currentTemps[j] = -sums[j] / Math.log(sap.initialAcceptanceRatio());
    }

    // main loop
    while (currentTemps[0] >
        this.sap.tMin()) { // TODO das main loop wird über die temperatur der 1. dimension der score gestuert ... sollte man das ändern ?
      int improved = 0;
      int iterAtTemperature = 0;

      while (iterAtTemperature < sap.omegaMax()
          && improved <= sap.maxImprovementPerTemperature()) {
        this.totalIterationCounter++;
        final SearchSpaceState<T> solutionCandidate =
            this.ns.select(currentSolution,
                currentTemps[0]); // TODO hier wird immer eine nachbar selectirt anhand der temperatur der 1. dimension, ist das gut oder schlecht ?
        final T candidateScore = solutionCandidate.getScore();

        if (candidateScore.compareTo(bestScore) == 1) {
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

        if (candidateScore.compareTo(currentScore) > 0) {
          currentScore = candidateScore;
          currentSolution = solutionCandidate;
        } else if (candidateScore.compareTo(currentScore) < 0) {
          final int dim = this.rng.nextInt(
              currentScore.getDimensions()); // select a radom dimension and computes on this if the bad solution will be accepted

          final double ex = -(Math.abs(solutionCandidate.getScore().getValue(dim)
              - currentScore.getValue(dim)) / currentTemps[dim]);
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
          for (int j = 0; j < currentTemps.length; j++) {
            sb.append(currentTemps[j]);
            sb.append(';');
          }
          sb.append(candidateScore);
          sb.append(';');
          sb.append(bestScore);
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
        final StringBuilder sb = new StringBuilder();
        sb.append(this.totalIterationCounter);
        sb.append(';');
        for (int j = 0; j < currentTemps.length; j++) {
          sb.append(currentTemps[j]);
          sb.append(';');
        }
        sb.append(bestScore);
        sb.append(';');
        sb.append(currentScore);
        sb.append(';');
        sb.append(sap.gamma());
        sb.append(';');
        sb.append(improved);
        System.out.println(sb.toString());
      }

      for (int j = 0; j < currentTemps.length; j++) {
        currentTemps[j] = computeTemperature(currentTemps[j]);
        currentTemps[j] = Math.max(this.sap.tMin(), currentTemps[j]);

        if (Double.isInfinite(currentTemps[j])) {
          throw new IllegalStateException("the temperature is infinity");
        }
      }

      if (bestScore.isInfinite()) {
        throw new IllegalStateException("the score is infinity");
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
