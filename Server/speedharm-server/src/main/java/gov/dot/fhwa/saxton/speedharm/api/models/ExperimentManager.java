package gov.dot.fhwa.saxton.speedharm.api.models;

import gov.dot.fhwa.saxton.speedharm.api.objects.Experiment;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleSession;
import gov.dot.fhwa.saxton.speedharm.persistence.converters.ExperimentConverter;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.ExperimentEntity;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.VehicleSessionEntity;
import gov.dot.fhwa.saxton.speedharm.persistence.repositories.ExperimentRepository;
import gov.dot.fhwa.saxton.speedharm.persistence.repositories.VehicleSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Class resposible for managing Experiment instances both in memory and on
 * disk.
 */
public class ExperimentManager {

    private List<Experiment> experiments = new ArrayList<>();
    private Map<VehicleSession, Experiment> vehExpMap = new HashMap<>();

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private ExperimentRepository experimentRepository;

    @Autowired
    private VehicleManager vehicleManager;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private VehicleSessionRepository vehicleSessionRepository;


    public Experiment initExperiment(Experiment partial) {
        // Initialize the fields
        partial.setId(null);
        partial.setStartTime(LocalDateTime.now());
        partial.setEndTime(null);
        partial.setVehicleSessions(null);

        // Convert it and save it in the database
        ExperimentEntity ent = ExperimentConverter.webToDatabase(partial, new ArrayList<>()); // Convert it to a persistence entity
        ent = experimentRepository.save(ent); // Save to database and get new data, including new ID

        // Convert the result back to a web class and return it
        return ExperimentConverter.databaseToWeb(ent);
    }

    public void addExperiment(Experiment exp) {
        experiments.add(exp);
    }

    public Experiment getExperimentById(Long id) {
        for (Experiment exp : experiments) {
            if (exp.getId().equals(id)) {
                return exp;
            }
        }

        return null;
    }

    public List<Experiment> getExperiments() {
        return experiments;
    }

    public void removeExperiment(Long id) {
        Experiment exp = getExperimentById(id);

        if (exp != null) {
            // Remove it from our in-memory list
            experiments.remove(exp);

            // Set it's end time and save it to the DB
            ExperimentEntity ent = experimentRepository.findOne(id);
            ent.setEndTime(LocalDateTime.now());
            experimentRepository.save(ent);
        }
    }

    public void addVehicleToExperiment(Long vehId, Long expId) {
        Optional<VehicleSession> veh = vehicleManager.getVehicleById(vehId);
        Experiment exp = getExperimentById(expId);

        if (veh.isPresent() && exp != null) {
            // Both exist, lets update them
            veh.get().setExpId(exp.getId());
            exp.getVehicleSessions().add(veh.get().getId());

            vehExpMap.put(veh.get(), exp);

            // Persist it to the DB
            ExperimentEntity expEnt = experimentRepository.findOne(expId);
            VehicleSessionEntity vehEnt = vehicleSessionRepository.findOne(vehId);
            vehEnt.setExperiment(expEnt);
            vehicleSessionRepository.save(vehEnt);
        }
    }

    public void removeVehicleFromExperiment(Long vehId, Long expId) {
        Optional<VehicleSession> veh = vehicleManager.getVehicleById(vehId);
        Experiment exp = getExperimentById(expId);

        if (veh.isPresent() && exp != null) {
            // Both exist, lets update them
            veh.get().setExpId(null);
            exp.getVehicleSessions().remove(veh.get().getId());

            vehExpMap.remove(veh.get());

            // If this was the last vehicle for that experiment, close it out.
            if (exp.getVehicleSessions().isEmpty()) {
                removeExperiment(exp.getId());
            }

            // Persist it to the DB
            VehicleSessionEntity vehEnt = vehicleSessionRepository.findOne(vehId);
            vehEnt.setExperiment(null);
            vehicleSessionRepository.save(vehEnt);
        }
    }
}
