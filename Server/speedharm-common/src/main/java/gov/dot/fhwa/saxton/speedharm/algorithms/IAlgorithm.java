package gov.dot.fhwa.saxton.speedharm.algorithms;

import gov.dot.fhwa.saxton.speedharm.api.objects.*;

import java.util.List;

/**
 * Interface to be implemented by
 */
public interface IAlgorithm {
    /**
     * Add a session to this algorithm instance.
     * @param v The session to be added.
     */
    void initVehicle(VehicleSession v);

    /**
     * Remove a session from this algorithm instance.
     * @param v The session to be removed.
     */
    void terminateVehicle(VehicleSession v);

    /**
     * Report an update from a vehicle to this algorithm instance.
     * @param vsu The update from the vehicle.
     */
    void updateVehicleStatus(VehicleStatusUpdate vsu);

    /**
     * Report an update from the infrastructure to this algorithm instance.
     * @param isu The update from the infrastructure.
     */
    void updateInfrastructureStatus(InfrastructureStatusUpdate isu);

    /**
     * Get the string used to uniquely identify this algorithm in the API.
     * @return A unique string for this algorithm.
     */
    String getAlgorithmName();

    /**
     * Get the string used to identify this version of the algorithm.
     * @return A string representing the version of this algorithm.
     */
    String getAlgorithmVersion();

    /**
     * An algorithm may have a configurable number of vehicles registered with it. Once this
     * limit is reached, the algorithm manager will spawn a new instance. -1 indicates a single
     * instance has no limit
     *
     * @return The maximum number of vehicles that may be registered with one algorithm instance.
     */
    int getMaxNumVehiclesPerInstance();

    /**
     * Gets the current number of vehicles associated with this algorithm instance.
     * @return The current number of vehicles associated with this algorithm instance
     */
    int getNumCurrentVehicles();

    /**
     * Gets a list of InfrastructureDataSource values required by this algorithm instance.
     * Data from these sources will be passed into the algorithm at run time
     * @return A List of {@link InfrastructureDataSource} values.
     */
    List<InfrastructureDataSource> getRequiredInfrastructureDataSources();

    /**
     * Register a callback to recieve output from this algorithm instance.
	 * 
	 * The algorithm is responsible for invoking these callbacks when it has
	 * output to report. The Vehicle ID in the {@link VehicleCommand} object
	 * must match the vehicle ID the command is intended for.
     */
    void registerOutputCallback(AlgorithmOutputCallback aoc);

    /**
     * Begin execution of the algorithm.
	 * 
	 * The algorithm implementation should be responsible for managing it's own
	 * threading/execution.
     */
    void run();

    /**
     * End the execution of the algorithm.
     */
    void stop();
}
