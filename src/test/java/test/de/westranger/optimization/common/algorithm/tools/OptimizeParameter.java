package test.de.westranger.optimization.common.algorithm.tools;

import com.google.gson.Gson;
import de.westranger.optimization.common.util.PermutationIterator;
import java.io.InputStreamReader;
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
import test.de.westranger.optimization.common.algorithm.example.tsp.common.ProblemFormulation;
import test.de.westranger.optimization.common.algorithm.example.tsp.sa.SimulatedAnnealingTest;
import test.de.westranger.optimization.common.algorithm.tools.util.TSPCallable;

public class OptimizeParameter {

  public static void main(String[] args) {

    double bestScore = Double.POSITIVE_INFINITY;

    final LinkedHashMap<String, List<Double>> input = new LinkedHashMap<>();

    input.put("tMax", Arrays.asList(0.0));
    input.put("initialAcceptanceRatio", Arrays.asList(0.9, 0.8, 0.7));
    input.put("gamma", Arrays.asList(0.999, 0.99, 0.98, 0.97, 0.96, 0.95, 0.9, 0.8, 0.7));
    input.put("tMin", Arrays.asList(0.001, 0.01, 0.1, 1.0, 10.0, 100.0));
    input.put("omegaMax",
        Arrays.asList(100.0, 250.0, 500.0, 1000.0, 2500.0, 5000.0, 10000.0, 25000.0));
    input.put("maxImprovementPerTemperature",
        Arrays.asList(2.0, 5.0, 10.0, 25.0, 50.0, 100.0, 250.0, 500.0));

// {avg_score=1.1714766111E12, gamma=0.999, initialAcceptanceRatio=0.9, iter=1.511582724E8, maxImprovementPerTemperature=5.0, omegaMax=10000.0, score=7750.0, tMax=0.0, tMin=0.001}
// {avg_score=1.2468726692028261E11, gamma=0.999, initialAcceptanceRatio=0.9, iter=7014834.1, maxImprovementPerTemperature=2.0, omegaMax=500.0, score=17774.79911039986, tMax=0.0, tMin=0.001}
    final InputStreamReader reader = new InputStreamReader(
        SimulatedAnnealingTest.class.getResourceAsStream("/1_vehicle_980_orders.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);
    final int threadPoolSize = 15;
    final int batchSize = 100;
    final PermutationIterator permutationIterator = new PermutationIterator(input);

    while (permutationIterator.hasNext()) {
      final ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
      final CompletionService<Map<String, Double>> completionService =
          new ExecutorCompletionService<>(executorService);

      int totalTasksSubmitted = 0;
      for (int i = 0; i < batchSize && permutationIterator.hasNext(); i++) {
        final Map<String, Double> permutation = permutationIterator.next();
        final Callable<Map<String, Double>> task = new TSPCallable(problem, permutation);
        completionService.submit(task);
        totalTasksSubmitted++;
      }

      executorService.shutdown();

      // Process completed tasks from this batch
      for (int i = 0; i < totalTasksSubmitted; ++i) {
        try {
          Future<Map<String, Double>> future = completionService.take();
          ; // Blocks until at least one is complete
          Map<String, Double> result = future.get();
          // Verarbeiten Sie das Ergebnis wie gewünscht
          if (bestScore > result.get("score")) {
            System.out.println(result.toString());
            bestScore = result.get("score");
          }
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        } catch (ExecutionException e) {
          throw new RuntimeException(e);
        }
      }


    }

    // Schließen des ExecutorService
    // Warten Sie auf das vollständige Herunterfahren

  }
}
