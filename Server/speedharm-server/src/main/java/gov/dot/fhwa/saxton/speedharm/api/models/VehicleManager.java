package gov.dot.fhwa.saxton.speedharm.api.models;

import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleSession;
import gov.dot.fhwa.saxton.speedharm.persistence.converters.VehicleSessionConverter;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.VehicleSessionEntity;
import gov.dot.fhwa.saxton.speedharm.persistence.repositories.VehicleSessionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Keeps track of and manages creation of vehicle sessions.
 */
public class VehicleManager {
    private Logger log = LogManager.getLogger();
    private List<VehicleSession> activeVehicles = new ArrayList<>();

    @SuppressWarnings("SpringJavaAutowiringInspection") // IntelliJ doesn't understand Spring Boot...
    @Autowired
    private VehicleSessionRepository vehicleSessionRepository;

    @Autowired
    private ExperimentManager experimentManager;

    @Autowired
    private AlgorithmManager algorithmManager;

    /**
     * Initialize a {@link VehicleSession} recieved from a client for use by the rest of the system.
     * Sanitizes fields the client should be unable to control (id, registration time, experiment)
     * and initializes id to the proper value.
     *
     * @param partial The VehicleSession object to be initialized.
     * @return A reference to the original object with the necessary initialization performed.
     */
    public VehicleSession initVehicle(VehicleSession partial) {

        // Initialize the VehicleSession object
        partial.setId(null);
        partial.setExpId(null);
        partial.setUnregisteredAt(null);
        partial.setRegisteredAt(LocalDateTime.now());

        // Convert to a managed entity and save it
        VehicleSessionEntity vse = VehicleSessionConverter.webToDatabase(partial); // Convert to database persistence class
        vse = vehicleSessionRepository.save(vse); // Save to database and get updated data, including auto-generated ID

        // Convert back to web class and return the entity as it's saved in the database
        return VehicleSessionConverter.databaseToWeb(vse);
    }

    /**
     * Delete all currently registered and active vehicles.
     *
     * Primarily exposed for the purposes of unit testing.
     */
    public void deleteAllActiveVehicles() {
        activeVehicles = new ArrayList<>();
    }

    /**
     * @return a clone of the current list of active vehicles in the environment.
     */
    public List<VehicleSession> getActiveVehicles() {
        return new ArrayList<>(activeVehicles);
    }

    /**
     * Add a vehicle to the list of active vehicles in the environment. If a vehicle is already active, update the data
     * associated with the vehicle.
     * @param v The vehicle API object to activate
     */
    public void activateVehicle(VehicleSession v) {
        Optional<VehicleSession> veh = getVehicleById(v.getId());

        if (veh.isPresent()) {
            activeVehicles.remove(veh.get());
        }

        activeVehicles.add(v);
    }

    /**
     * Remove a vehicle from the environment.
     * @param vehId The ID of the vehicle to be deactivated.
     * @return True if the vehicle was sucessfully deactivated. False o.w.
     */
    public boolean deactivateVehicle(Long vehId) {
        Optional<VehicleSession> veh = getVehicleById(vehId);
        if (veh.isPresent()) {
            // Remove it from any experiments or algorithms it may be participating in
            experimentManager.removeVehicleFromExperiment(veh.get().getId(), veh.get().getExpId());
            algorithmManager.removeVehicleFromAlgorithm(veh.get().getId(), veh.get().getExpId());

            activeVehicles.remove(veh.get());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Find a vehicle in the environment based on it's ID number.
     * @param vehId The ID to query for
     * @return An optional containing either the vehicle if it exists or empty if it does not.
     */
    public Optional<VehicleSession> getVehicleById(Long vehId) {
        List<VehicleSession> results = activeVehicles.stream()
                .filter((VehicleSession v) -> v != null && v.getId().equals(vehId))
                .collect(Collectors.toList());

        if (results.size() >= 1) {
            return Optional.of(results.get(0));
        } else {
            return Optional.empty();
        }
    }
}
