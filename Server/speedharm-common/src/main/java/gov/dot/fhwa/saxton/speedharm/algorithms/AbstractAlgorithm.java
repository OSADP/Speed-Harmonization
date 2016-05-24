package gov.dot.fhwa.saxton.speedharm.algorithms;

import gov.dot.fhwa.saxton.speedharm.api.objects.InfrastructureStatusUpdate;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleCommand;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleSession;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleStatusUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Partially implemented framework for algorithms to use
 */
public abstract class AbstractAlgorithm implements IAlgorithm {
    private List<VehicleSession> vehicles = new ArrayList<>();
    private Queue<VehicleStatusUpdate> vsuQueue = new LinkedBlockingQueue<>();
    private Queue<InfrastructureStatusUpdate> isuQueue = new LinkedBlockingQueue<>();
    private List<AlgorithmOutputCallback> outputCallbacks = new ArrayList<>();

    @Override
    public void initVehicle(VehicleSession v) {
        if (!vehicles.contains(v)) {
            vehicles.add(v);
        }
    }

    @Override
    public void terminateVehicle(VehicleSession v) {
        vehicles.remove(v);
    }

    @Override
    public synchronized void updateVehicleStatus(VehicleStatusUpdate vsu) {
        vsuQueue.add(vsu);
    }

    @Override
    public synchronized void updateInfrastructureStatus(InfrastructureStatusUpdate isu) {
        isuQueue.add(isu);
    }

    @Override
    public void registerOutputCallback(AlgorithmOutputCallback aoc) {
        if (!outputCallbacks.contains(aoc)) {
            outputCallbacks.add(aoc);
        }
    }

    public List<VehicleSession> getVehicles() {
        return new ArrayList<>(vehicles);
    }

    /**
     * Get the data off the vehicle update queue and return it, then empty the queue.
     * @return A list containing all data that was on the queue.
     */
    protected synchronized List<VehicleStatusUpdate> getPendingVehicleStatusUpdates() {
        List<VehicleStatusUpdate> out = new ArrayList<>(vsuQueue);
        vsuQueue = new LinkedBlockingQueue<>();
        return out;
    }

    /**
     * Get the data off the infrastructure update queue and return it, then empty the queue.
     * @return A list containing all data that was on the queue.
     */
    protected synchronized List<InfrastructureStatusUpdate> getPendingInfrastructureStatusUpdates() {
        List<InfrastructureStatusUpdate> out = new ArrayList<>(isuQueue);
        isuQueue = new LinkedBlockingQueue<>();
        return out;
    }

    /**
     * Notify all registered callbacks of new output data.
     * @param vsc The new output value to be passed to the callbacks.
     */
    protected void fireOutputCallbacks(VehicleCommand vsc) {
        for (AlgorithmOutputCallback aoc : outputCallbacks) {
            aoc.newAlgorithmOutput(vsc);
        }
    }

    /**
     * Create a vehicle command with the parameter's data, then fire off all output callbacks.
     *
     * @param vehId The id of the vehicle to command
     * @param speed The speed recommended for the vehicle
     * @param commandConfidence The confidence in the accuracy of the command
     */
    protected void produceOutput(Long vehId, Double speed, Double commandConfidence) {
        VehicleCommand vc = new VehicleCommand();
        vc.setCommandConfidence(commandConfidence);
        vc.setSpeed(speed);
        vc.setTimestamp(LocalDateTime.now());
        vc.setVehId(vehId);

        fireOutputCallbacks(vc);
    }

    @Override
    public int getNumCurrentVehicles() {
        return vehicles.size();
    }

}
