package de.westranger.optimization.common.algorithm.tools;

import com.google.gson.Gson;
import de.westranger.optimization.common.algorithm.tools.util.TSPCallable;
import de.westranger.optimization.common.algorithm.tsp.common.ProblemFormulation;
import de.westranger.optimization.common.algorithm.tsp.sa.SimulatedAnnealingTest;
import de.westranger.optimization.common.util.CombinationSearcher;
import de.westranger.optimization.common.util.ProgressTracker;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class OptimizeParameterV2 {

  public static void main(String[] args) {

    double bestScore = Double.POSITIVE_INFINITY;
    double minIter = Double.POSITIVE_INFINITY;

    final DecimalFormat df = new DecimalFormat("#.00");
    final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    final LinkedHashMap<String, List<Double>> input = new LinkedHashMap<>();

    final int numTries = 1;

    input.put("initialAcceptanceRatio", Arrays.asList(0.99, 0.95, 0.90, 0.8, 0.7, 0.6, 0.5, 0.4));
    input.put("gamma",
        Arrays.asList(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.95, 0.96, 0.97, 0.98, 0.999));
    input.put("beta",
        Arrays.asList(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.95, 0.96, 0.97, 0.98, 0.999));
    //Arrays.asList(0.99, 0.98, 0.97, 0.96, 0.95, 0.9, 0.8, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.1));
    input.put("tMin",
        Arrays.asList(1.0e-5, 1.0e-4, 1.0e-3, 1.0e-2, 1.0e-1, 1.0, 10.0, 100.0, 1000.0));
    input.put("omegaMax",
        Arrays.asList(100.0, 250.0, 500.0, 1000.0, 2500.0, 5000.0, 10000.0, 25000.0, 50000.0
            , 100000.0, 250000.0, 500000.0));
    input.put("maxImprovementPerTemperature",
        Arrays.asList(2.0, 5.0, 10.0, 25.0, 50.0, 100.0, 250.0, 500.0));

    final InputStreamReader reader = new InputStreamReader(

        SimulatedAnnealingTest.class.getResourceAsStream("/tsp/1_vehicle_194_orders.json"));
        //SimulatedAnnealingTest.class.getResourceAsStream("/vrp/50/vrp_problem_50_750____.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

    final double goalTolerance = 0.000001; // percentile
    final double goalScoreThreshold = problem.getExpectedScore() * (1.0 + goalTolerance);
    boolean thresholdPassed = false;

    final int threadPoolSize = 15;
    final int batchSize = 100;

    final Map<String, Integer> initIdx =
        Map.of("initialAcceptanceRatio", 0, "gamma", 0, "tMin", 0, "omegaMax", 0,
            "maxImprovementPerTemperature", 0, "beta", 0);
    final CombinationSearcher combinationSearcher =
        new CombinationSearcher(input, initIdx, Double.POSITIVE_INFINITY, Integer.MAX_VALUE,
            threadPoolSize);
    final ProgressTracker pt = new ProgressTracker(combinationSearcher.getNumCombinations());

    System.out.println(
        "starting parameter optimization, there are " + combinationSearcher.getNumCombinations() +
            " parameter combinations which will be evaluated");

    int activeTaskCounter = 0;

    final ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
    final CompletionService<Map<String, Double>> completionService =
        new ExecutorCompletionService<>(executorService);


    while (combinationSearcher.computeNextParameterList()) {

      int totalTasksSubmitted = 0;
      while (activeTaskCounter < threadPoolSize) {
        Map<String, Map<String, Double>> tasks = combinationSearcher.getCandidates(1);
        boolean added = false;
        for (Map.Entry<String, Map<String, Double>> entry : tasks.entrySet()) {
          final Map<String, Double> combination = entry.getValue();
          final Callable<Map<String, Double>> task =
              new TSPCallable(problem, combination, numTries, true, entry.getKey());
          completionService.submit(task);
          totalTasksSubmitted++;
          activeTaskCounter++;
          added = true;
        }

        if (!added) {
          break;
        }
      }
      //System.out.println("submitted " + totalTasksSubmitted);

      if (totalTasksSubmitted % batchSize == 0) {
        pt.nextRound(totalTasksSubmitted);
        final long current = System.currentTimeMillis();

        /*
        System.out.println(
            "[" + sdf.format(new Date(current)) + "] submitted next batch of " + batchSize +
                " jobs, total progress: " + df.format(pt.getProgressPercentage()) +
                " % estimated finish time " +
                sdf.format(new Date(pt.getEstimatedCompletionDate())) +
                " avg time for job " + df.format(pt.getAverageDelta()) + " ms");


         */
      }

      if (activeTaskCounter > 0)
      //for (int i = 0; i < totalTasksSubmitted; ++i) {
      {
        //System.out.println("collecting " + i);
        try {
          Future<Map<String, Double>> future = completionService.take();
          Map<String, Double> result = future.get();
          activeTaskCounter--;
          // TODO score berechnung umstellen das die SCore klasse verwendet wird

          for (String str : result.keySet()) {
            if (str.startsWith("param")) {
              String[] id = str.split("#");
              combinationSearcher.provideScore(id[1], result.get("score"),
                  result.get("iter").intValue());
            }
          }

          if ((bestScore - result.get("score") >= 1e-3 && !thresholdPassed) ||
              (bestScore - (result.get("score")) >= 1e-3 && thresholdPassed &&
                  minIter > result.get("iter"))) {

            if (!thresholdPassed) {
              bestScore = result.get("score");
            }

            if (result.get("score") < goalScoreThreshold) {
              thresholdPassed = true;
            }

            System.out.println(
                '\t' + result.toString() + " t:" + goalScoreThreshold + " " + thresholdPassed);

            minIter = result.get("iter");



          }
        } catch (InterruptedException | ExecutionException e) {
          throw new RuntimeException(e);
        }
      }
    }
    executorService.shutdown();
  }
}

