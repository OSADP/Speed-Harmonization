package gov.dot.fhwa.saxton.speedharm.executive.algorithms;

import gov.dot.fhwa.saxton.speedharm.algorithms.IAlgorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for managing the execution of Algorithm instances.
 */
public class AlgorithmExecutor {
    private Map<Long, IAlgorithm> algorithms = new HashMap<>();

    /**
     * Begin tracking instance in Executor and run the algorithm.
     * @param id
     * @param algorithm
     */
    public void addInstance(Long id, IAlgorithm algorithm) {
        algorithms.put(id, algorithm);
        algorithm.run();
    }
}
