package gov.dot.fhwa.saxton.speedharm.algorithms;

import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleCommand;

/**
 * Functional interface for a callback to be fired when the algorithm has new output.
 */
public interface AlgorithmOutputCallback {
    void newAlgorithmOutput(VehicleCommand vc);
}
