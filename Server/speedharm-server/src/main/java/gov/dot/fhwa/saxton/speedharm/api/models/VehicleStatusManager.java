package gov.dot.fhwa.saxton.speedharm.api.models;

import gov.dot.fhwa.saxton.speedharm.algorithms.IAlgorithm;
import gov.dot.fhwa.saxton.speedharm.api.objects.NetworkLatencyInformation;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleStatusUpdate;
import gov.dot.fhwa.saxton.speedharm.dataprocessing.ClockSkewCompensator;
import gov.dot.fhwa.saxton.speedharm.persistence.converters.VehicleStatusUpdateConverter;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.VehicleSessionEntity;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.VehicleStatusUpdateEntity;
import gov.dot.fhwa.saxton.speedharm.persistence.repositories.VehicleSessionRepository;
import gov.dot.fhwa.saxton.speedharm.persistence.repositories.VehicleStatusUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class responsible for aggregating vehicle statuses and managing them
 */
public class VehicleStatusManager {

    @Autowired
    private AlgorithmManager algorithmManager;

    private HashMap<Long, List<VehicleStatusUpdate>> statuses = new HashMap<>();

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private VehicleStatusUpdateRepository vehicleStatusUpdateRepository;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private VehicleSessionRepository vehicleSessionRepository;

    @Autowired
    private ClockSkewCompensator clockSkewCompensator;

    /**
     * Begin tracking a new status update in the system.
     * @param vsu The status update to add.
     */
    public void processNewStatusUpdate(VehicleStatusUpdate vsu) {
        if (statuses.containsKey(vsu.getVehId())) {
            statuses.get(vsu.getVehId()).add(vsu);
        } else {
            ArrayList<VehicleStatusUpdate> tmp = new ArrayList<>();
            tmp.add(0, vsu);
            statuses.put(vsu.getVehId(), tmp);
        }


        // Check to see if this vehicle is registered with an algorithm
        Optional<IAlgorithm> algo = algorithmManager.getAlgorithmForVehicle(vsu.getVehId());
        if (algo.isPresent()) {
            algo.get().updateVehicleStatus(vsu);
        }
    }

    /**
     * Get some of the recent status updates for the desired vehicle.
     * @param vehId The entity ID of the desired vehicle
     * @param numStatuses The maximum number of statuses to retrieve
     * @return A list of status updates no longer than numStatuses. If more than
     *         numStatuses updates exist, only return the most recent numStatuses
     *         of them. If fewer exist, return all.
     */
    public List<VehicleStatusUpdate> getVehicleStatuses(Long vehId, int numStatuses) {
        List<VehicleStatusUpdate> tmp = statuses.get(vehId);

        if (tmp != null) {
            return tmp.stream().limit(numStatuses).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public VehicleStatusUpdate initVehicleStatusUpdate(VehicleStatusUpdate vsu, Long vehId) {
        vsu.setId(null); // Set the primary-key id to null to allow for auto-generation in the database
        vsu.setVehId(vehId); // The URL the update was posted to overrides the vehID it contains

        // Apply the server's timestamp to the received message.
        // Update the vehicles clock skew data
        NetworkLatencyInformation nli = vsu.getNetworkLatencyInformation();
        if (nli != null) {
            nli.setServerRxTimestamp(LocalDateTime.now());
            clockSkewCompensator.processNewLatencyInformation(vsu.getVehId(), nli);

            // Store the corrected timestamp in the VSU
            nli.setCorrectedTxTimestamp(clockSkewCompensator.correctForClockSkew(vsu.getVehId(),
                    nli.getVehicleTxTimestamp()));
        }

        VehicleSessionEntity veh = vehicleSessionRepository.findOne(vehId); // Get the associated vehicle session from DB
        VehicleStatusUpdateEntity ent = VehicleStatusUpdateConverter.webToDatabase(vsu, veh); // Convert the update to a databse object

        ent = vehicleStatusUpdateRepository.save(ent); // Save it in the database and get the auto-generated data

        // Convert it back to a web object and return it
        return VehicleStatusUpdateConverter.databaseToWeb(ent);
    }
}
