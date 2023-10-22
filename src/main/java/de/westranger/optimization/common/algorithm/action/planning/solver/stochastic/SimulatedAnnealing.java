package de.westranger.optimization.common.algorithm.action.planning.solver.stochastic;

import de.westranger.optimization.common.algorithm.action.planning.SearchSpaceState;
import de.westranger.optimization.common.algorithm.util.Function;
import de.westranger.optimization.common.algorithm.util.Solution;

import java.util.Random;

/**
 * @ see https://machinelearningmastery.com/simulated-annealing-from-scratch-in-python/
 */

// TODO Warnung einfügen, wenn Verhältnis E_max - E_min / T initial kleiner als 0,8 ist

public final class SimulatedAnnealing<S extends Comparable<S>> {

    private final SearchSpaceState<S> initialSolution;
    private final NeighbourSelector ns;
    private final Function func;
    private final int maxTemperature;
    private final Random rng;

    public SimulatedAnnealing(final SearchSpaceState<S> initialSolution, final NeighbourSelector ns, final Function func, final int maxTemperature) {
        this.initialSolution = initialSolution;
        this.ns = ns;
        this.func = func;
        this.maxTemperature = maxTemperature;
        this.rng = new Random(0x05025b0e2a3df1cel);
    }

    public SearchSpaceState<S> optimize(final double solutionAccuracy) {

        SearchSpaceState<S> approxSolution = this.initialSolution;
        double resultApproxSolution = this.func.evaluate(approxSolution);

        for (int k = 0; k <= this.maxTemperature; k++) {
            final double T = (k + 1.0) / (double) this.maxTemperature;
            final Solution solution = this.ns.computeNeighbour(approxSolution, k, this.maxTemperature);
            final double resultSolution = this.func.evaluate(solution);
            System.out.println("result = " + resultSolution + " " + resultApproxSolution);
            if (resultSolution < resultApproxSolution) {
                approxSolution = solution;
                resultApproxSolution = resultSolution;
            } else if (Math.exp(-(resultSolution - resultApproxSolution) / T) > this.rng.nextDouble()) {
                approxSolution = solution;
                resultApproxSolution = resultSolution;
            }
            if (resultApproxSolution < solutionAccuracy) {
                break;
            }
        }

        return approxSolution;
    }


}
