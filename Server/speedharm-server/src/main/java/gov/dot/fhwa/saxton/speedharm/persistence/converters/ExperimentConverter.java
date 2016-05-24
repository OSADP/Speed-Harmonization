package gov.dot.fhwa.saxton.speedharm.persistence.converters;

import gov.dot.fhwa.saxton.speedharm.api.objects.Experiment;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.ExperimentEntity;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.VehicleSessionEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for converting Experiment <-> ExperimentEntity
 */
public class ExperimentConverter {

    public static ExperimentEntity webToDatabase(Experiment exp, List<VehicleSessionEntity> vehicleSessionEntities) {
        ExperimentEntity ent = new ExperimentEntity();
        ent.setDescription(exp.getDescription());
        ent.setLocation(exp.getLocation());
        ent.setStartTime(exp.getStartTime());
        ent.setEndTime(exp.getEndTime());
        ent.setId(exp.getId());
        ent.setVehicleSessions(vehicleSessionEntities);

        return ent;
    }

    public static Experiment databaseToWeb(ExperimentEntity ent) {
        Experiment exp = new Experiment();
        exp.setId(ent.getId());
        exp.setLocation(ent.getLocation());
        exp.setDescription(ent.getDescription());
        exp.setStartTime(ent.getStartTime());
        exp.setEndTime(ent.getEndTime());
        exp.setVehicleSessions(ent.getVehicleSessions().stream()
                .map(VehicleSessionEntity::getId)
                .collect(Collectors.toList()));

        return exp;
    }
}
