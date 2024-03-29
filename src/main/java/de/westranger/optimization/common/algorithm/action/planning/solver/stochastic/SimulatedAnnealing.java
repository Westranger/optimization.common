package de.westranger.optimization.common.algorithm.action.planning.solver.stochastic;

import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.Score;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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
                            final long seed, final SimulatedAnnealingParameter sap,
                            final File telemetryFolder) {
    this.initialSolution = initialSolution;
    this.ns = ns;
    this.rng = new Random(seed);
    this.totalIterationCounter = 0L;
    this.sap = sap;
    this.telemetryFolder = telemetryFolder;
  }

  public SimulatedAnnealing(final SearchSpaceState<T> initialSolution, final NeighbourSelector ns,
                            final long seed, final SimulatedAnnealingParameter sap) {
    this(initialSolution, ns, seed, sap, null);
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

    final int numDeltaValues = 100;
    // estimate initial temperature
    double[] sums = new double[bestScore.getDimensions()];
    for (int i = 0; i < numDeltaValues; i++) {
      final SearchSpaceState<T> solution = this.ns.select(currentSolution, sap.tMax());
      final Score score = currentScore.difference(solution.getScore());

      for (int j = 0; j < sums.length; j++) {
        sums[j] += Math.abs(score.getValue(j));
      }

      //if (bestScore.compareTo(score) == -1) {
      //  bestSolution = solution;
      //  bestScore = solution.getScore();
      //}
    }

    for (int j = 0; j < sums.length; j++) {
      sums[j] /= numDeltaValues;
    }

    double[] currentTemps = new double[bestScore.getDimensions()];
    for (int j = 0; j < sums.length; j++) {
      currentTemps[j] = -sums[j] / Math.log(sap.initialAcceptanceRatio());
    }

    double initialTemperature = currentTemps[0];
    int mainloopIterationCounter = 1;
    // main loop
    while (currentTemps[0] >
        this.sap.tMin()) { // TODO das main loop wird über die temperatur der 1. dimension der score gestuert ... sollte man das ändern ?
      int improved = 0;
      int iterAtTemperature = 0;

      List<Double> costValues = new LinkedList<>();
      List<Double> probValues = new LinkedList<>();
      while (iterAtTemperature < sap.omegaMax()
          && improved <= sap.maxImprovementPerTemperature()) {
        this.totalIterationCounter++;
        final SearchSpaceState<T> solutionCandidate =
            this.ns.select(currentSolution,
                currentTemps[0]); // TODO hier wird immer eine nachbar selectirt anhand der temperatur der 1. dimension, ist das gut oder schlecht ?
        final T candidateScore = solutionCandidate.getScore();

        costValues.add(candidateScore.getValue(0));

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

          probValues.add(probability);

          if (Double.isNaN(ex)) {
            System.out.println("WTF SCORE im SA loop NaN");
          }

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

      // compute maximum heat
      double sum = 0.0;
      double count = 0.0;
      for (double value : costValues) {
        if (!Double.isNaN(value)) {
          sum += value;
          count++;
        }
      }
      double costMean = sum / count;
      double costVariance = 0.0;

      for (double value : costValues) {
        if (!Double.isNaN(value)) {
          costVariance += (value - costMean) * (value - costMean);
        }
      }

      costVariance /= count - 1;
      double maximumHeat = costVariance / currentTemps[0];

      double probMean = 0.0;
      for (double value : probValues) {
        if (!Double.isNaN(value)) {
          probMean += value;
        }
      }
      probMean /= probValues.size();

      double probVar = 0.0;
      for (double value : probValues) {
        probVar += (value - probMean) * (value - probMean);
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
        sb.append(';');
        sb.append(costVariance);
        sb.append(';');
        sb.append(maximumHeat);
        sb.append(';');
        sb.append(probMean);
        sb.append(';');
        sb.append(probVar);

        System.out.println(sb.toString());
      }

      for (int j = 0; j < currentTemps.length; j++) {
        double gamma = maximumHeat > currentTemps[j] ? this.sap.beta() : this.sap.gamma();
        currentTemps[j] = computeTemperature(mainloopIterationCounter, initialTemperature,
            TemperatureSchedule.geometric, gamma);
        currentTemps[j] = Math.max(this.sap.tMin(), currentTemps[j]);

        if (Double.isInfinite(currentTemps[j])) {
          throw new IllegalStateException("the temperature is infinity");
        }
      }


      if (bestScore.isInfinite()) {
        throw new IllegalStateException("the score is infinity");
      }
      mainloopIterationCounter++;
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

  private double computeTemperature(int currentIteration,
                                    double initialTemperature,
                                    TemperatureSchedule schedule, double gamma) {
    switch (schedule) {
      case linear -> {
        return initialTemperature - currentIteration * gamma;
      }
      case exponential -> {
        return initialTemperature * Math.exp(-gamma * currentIteration);
      }
      case logarithmic -> {
        return initialTemperature / (1.0 + gamma * Math.log(1.0 + currentIteration));
      }
    }
    // geometric is default
    return initialTemperature * Math.pow(gamma, currentIteration);
  }

  public enum TemperatureSchedule {
    linear, exponential, geometric, logarithmic
  }
}
