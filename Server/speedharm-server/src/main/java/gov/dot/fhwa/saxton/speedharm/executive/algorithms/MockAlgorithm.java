package gov.dot.fhwa.saxton.speedharm.executive.algorithms;

import gov.dot.fhwa.saxton.speedharm.algorithms.AbstractAlgorithm;
import gov.dot.fhwa.saxton.speedharm.api.objects.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic implemenetation of algorithm interface to be used for testing.
 */
public class MockAlgorithm extends AbstractAlgorithm {

    private Logger log = LogManager.getLogger();
    private int messageCount = 0;
    private int count = 0;
    private boolean running = false;
    private boolean engaged = false;

    private static final double[] TRAJECTORY = { 12.0, 9.0, 6.0, 9.0 };

    @Override
    public String getAlgorithmName() {
        return "mock-algorithm";
    }

    @Override
    public String getAlgorithmVersion() {
        return "v1.0";
    }

    @Override
    public int getMaxNumVehiclesPerInstance() {
        return 1;
    }

    @Override
    public List<InfrastructureDataSource> getRequiredInfrastructureDataSources() {
        List<InfrastructureDataSource> sources = new ArrayList<>();
        sources.add(InfrastructureDataSource.RTMS);

        return sources;
    }

    @Override
    public void run() {
        running = true;
        new Thread(() -> {
            while (running) {
                for (VehicleStatusUpdate vsu : getPendingVehicleStatusUpdates()) {
                    // Check to see if a vehicle is automated yet.
                    if (vsu.getAutomatedControlState() == VehicleStatusUpdate.AutomatedControlStatus.ENGAGED) {
                        log.info("Engaged vehicle detected, starting trajectory.");
                        engaged = true;
                    }
                }

                for (InfrastructureStatusUpdate isu : getPendingInfrastructureStatusUpdates()) {
                    log.info("Received infrastructure update: " + isu);
                }

                for (VehicleSession vs : getVehicles()) {
                    VehicleCommand vc = new VehicleCommand();

                    if (engaged) {
                        double speed = TRAJECTORY[messageCount % TRAJECTORY.length];
                        log.info("Outputting Command: Speed: "  + speed + " Confidence: " + 99.99);
                        produceOutput(vs.getId(), speed, 99.99);
                        messageCount++;
                    } else {
                        log.info("Outputting Command: Speed: "  + 8.0 + " Confidence: " + 0.0);
                        produceOutput(vs.getId(), 8.0, 99.9);
                    }

                }

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public void stop() {
        running = false;
    }


}
