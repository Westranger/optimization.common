package de.westranger.optimization.common.algorithm.tools;

import com.google.gson.Gson;
import de.westranger.geometry.common.simple.Point2D;
import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealing;
import de.westranger.optimization.common.algorithm.action.planning.solver.stochastic.SimulatedAnnealingParameter;
import de.westranger.optimization.common.algorithm.tsp.common.Order;
import de.westranger.optimization.common.algorithm.tsp.common.ProblemFormulation;
import de.westranger.optimization.common.algorithm.tsp.common.State;
import de.westranger.optimization.common.algorithm.tsp.common.TSPScore;
import de.westranger.optimization.common.algorithm.tsp.sa.SimulatedAnnealingTest;
import de.westranger.optimization.common.algorithm.tsp.sa.TSPNeighbourSelector;
import de.westranger.optimization.common.algorithm.tsp.sa.route.RouteEvaluator;
import de.westranger.optimization.common.algorithm.tsp.sa.route.VehicleRoute;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Assertions;

public class Benchmark {
  public static void main(String[] args) {
    final InputStreamReader reader = new InputStreamReader(
        //SimulatedAnnealingTest.class.getResourceAsStream("/tmp/vrp_problem_50_650_PDE.json"));
        SimulatedAnnealingTest.class.getResourceAsStream("/tmp/vrp_problem_50_650_PDE.json"));
    final Gson gson = new Gson();
    final ProblemFormulation problem = gson.fromJson(reader, ProblemFormulation.class);

    Random rng = new Random(47110815L);

    List<Order> orders = new LinkedList<>(problem.getOrders());
    Collections.shuffle(orders, rng);

    boolean isRoundtrip = problem.getVehicleStartPositions().size() == 1;
    List<VehicleRoute> emptyVehicle = new ArrayList<>();
    List<VehicleRoute> nonEmptyVehicle = new ArrayList<>();

    for (Map.Entry<Integer, Point2D> entry : problem.getVehicleStartPositions()
        .entrySet()) {

      if (entry.getKey() == 1) {
        final VehicleRoute vr =
            new VehicleRoute(entry.getKey(), entry.getValue(), orders, isRoundtrip);
        nonEmptyVehicle.add(vr);
      } else {
        final VehicleRoute vr =
            new VehicleRoute(entry.getKey(), entry.getValue(), new ArrayList<>(), isRoundtrip);
        emptyVehicle.add(vr);
      }
    }

    final RouteEvaluator re = new RouteEvaluator();
    State initialState =
        new State(new ArrayList<>(), emptyVehicle, nonEmptyVehicle, re);

    SimulatedAnnealingParameter sap =
        new SimulatedAnnealingParameter(0, 0.00001, 0.95, 25000, 500, 0.95);
    //{avg_score=92.87750632113925, gamma=0.96, initialAcceptanceRatio=0.95, iter=9453434.0, maxImprovementPerTemperature=2.0, omegaMax=25000.0, score=92.87750632113925, tMax=0.0, tMin=0.001}//
    TSPNeighbourSelector ns = new TSPNeighbourSelector(sap.tMax(), sap.tMin(), rng);

    SimulatedAnnealing sa = new SimulatedAnnealing(initialState, ns, rng, sap);

    State optimizedState = (State) sa.optimize(true);

    double sum = 0.0;
    for (VehicleRoute vr : optimizedState.getNonEmptyVehicles()) {
      System.out.print(vr.getScore() + " ");
      re.scoreRouteFull(vr);
      System.out.println(vr.getScore());
      sum += vr.getScore();
    }
    System.out.println(sum);


    try (BufferedWriter writer = new BufferedWriter(
        new FileWriter("/home/marius/Dokumente/omlox/opt_result.svg"))) {
      writer.write(optimizedState.toSVG());
    } catch (IOException e) {
      e.printStackTrace();
    }


    TSPScore expectedScore = new TSPScore(9353.678079851821);
    for (int i = 0; i < expectedScore.getDimensions(); i++) {
      Assertions.assertEquals(expectedScore.getValue(i), optimizedState.getScore().getValue(i),
          1e-10);
    }
    Assertions.assertEquals(5.2500528E7, sa.getTotalIterationCounter());
  }

}
