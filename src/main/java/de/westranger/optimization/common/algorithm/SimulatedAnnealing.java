package de.westranger.optimization.common.algorithm;

import java.util.Random;

/**
 * @ see https://machinelearningmastery.com/simulated-annealing-from-scratch-in-python/
 */

public final class SimulatedAnnealing {

    private final Solution initialSolution;
    private final NeighbourSelector ns;
    private final Function func;
    private final int maxTemperature;
    private final Random rng;

    public SimulatedAnnealing(final Solution initialSolution, final NeighbourSelector ns, final Function func, final int maxTemperature) {
        this.initialSolution = initialSolution;
        this.ns = ns;
        this.func = func;
        this.maxTemperature = maxTemperature;
        this.rng = new Random(0x05025b0e2a3df1cel);
    }

    public Solution optimize(final double solutionAccuracy) {

        Solution approxSolution = this.initialSolution;
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
