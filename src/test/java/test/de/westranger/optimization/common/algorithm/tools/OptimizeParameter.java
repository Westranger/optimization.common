package test.de.westranger.optimization.common.algorithm.tools;

import com.google.gson.Gson;
import de.westranger.optimization.common.algorithm.tsp.common.ProblemFormulation;
import de.westranger.optimization.common.util.CombinationIterator;
import de.westranger.optimization.common.util.ProgressTracker;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
import test.de.westranger.optimization.common.algorithm.tools.util.TSPCallable;
import test.de.westranger.optimization.common.algorithm.tsp.sa.SimulatedAnnealingTest;

public final class OptimizeParameter {

  public static void main(String[] args) {

    double bestScore = Double.POSITIVE_INFINITY;
    double minIter = Double.POSITIVE_INFINITY;

    final DecimalFormat df = new DecimalFormat("#.00");
    final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    final LinkedHashMap<String, List<Double>> input = new LinkedHashMap<>();

    final int numTries = 1;

    input.put("tMax", List.of(0.0));
    input.put("initialAcceptanceRatio", Arrays.asList(0.9, 0.8, 0.7));
    input.put("gamma",
        Arrays.asList(0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.95, 0.96, 0.97 , 0.98, 0.99, 0.999));
    input.put("tMin", Arrays.asList(0.001, 0.01, 0.1, 1.0, 10.0, 100.0, 1000.0));
    input.put("omegaMax",
        Arrays.asList(100.0, 250.0, 500.0, 1000.0, 2500.0, 5000.0, 10000.0, 25000.0, 50000.0,
            100000.0, 250000.0));
    input.put("maxImprovementPerTemperature",
        Arrays.asList(2.0, 5.0, 10.0, 25.0, 50.0, 100.0, 250.0, 500.0));

    final InputStreamReader reader = new InputStreamReader(
        SimulatedAnnealingTest.class.getResourceAsStream("/tsp/1_vehicle_29_orders.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);
    final int threadPoolSize = 12;
    final int batchSize = 100;
    final CombinationIterator combinationIterator = new CombinationIterator(input);
    final ProgressTracker pt = new ProgressTracker(combinationIterator.getNumCombinations());

    System.out.println(
        "starting parameter optimization, there are " + combinationIterator.getNumCombinations() +
            " parameter combinations which will be evaluated");

    int activeTaskCounter = 0;

    final ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
    final CompletionService<Map<String, Double>> completionService =
        new ExecutorCompletionService<>(executorService);

    int totalTasksSubmitted = 0;
    while (combinationIterator.hasNext()) {

      while (activeTaskCounter < threadPoolSize && combinationIterator.hasNext()) {
//      for (int i = 0; i < batchSize && combinationIterator.hasNext(); i++) {
        final Map<String, Double> combination = combinationIterator.next();
        final Callable<Map<String, Double>> task = new TSPCallable(problem, combination, numTries);
        completionService.submit(task);
        totalTasksSubmitted++;
        activeTaskCounter++;
      }

      if (totalTasksSubmitted % batchSize == 0) {
        pt.nextRound(combinationIterator.getCombinationCounter());
        final long current = System.currentTimeMillis();

        System.out.println(
            "[" + sdf.format(new Date(current)) + "] submitted next batch of " + batchSize +
                " jobs, total progress: " + df.format(pt.getProgressPercentage()) +
                " % estimated finish time " +
                sdf.format(new Date(pt.getEstimatedCompletionDate())) +
                " avg time for job " + df.format(pt.getAverageDelta()) + " ms");
      }

      if (activeTaskCounter > 0)
      //for (int i = 0; i < totalTasksSubmitted; ++i) {
      {
        try {
          Future<Map<String, Double>> future = completionService.take();
          Map<String, Double> result = future.get();
          activeTaskCounter--;
          if (bestScore > result.get("score") ||
              (Math.abs(bestScore - result.get("score")) < 1e-3 && minIter > result.get("iter"))) {
            System.out.println('\t' + result.toString());
            bestScore = result.get("score");
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

