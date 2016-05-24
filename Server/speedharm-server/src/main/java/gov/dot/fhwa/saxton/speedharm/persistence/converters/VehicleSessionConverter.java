package gov.dot.fhwa.saxton.speedharm.persistence.converters;

import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleSession;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.VehicleSessionEntity;

/**
 * Class for converting between VehicleSession <-> VehicleSessionEntity
 */
public class VehicleSessionConverter {

    public static VehicleSessionEntity webToDatabase(VehicleSession veh) {
        VehicleSessionEntity vse = new VehicleSessionEntity();
        vse.setId(veh.getId());
        vse.setDescription(veh.getDescription());
        vse.setRegisteredAt(veh.getRegisteredAt());
        vse.setUnregisteredAt(veh.getUnregisteredAt());
        vse.setUniqVehId(veh.getUniqVehId());

        return vse;
    }

    public static VehicleSession databaseToWeb(VehicleSessionEntity vse) {
        VehicleSession veh = new VehicleSession();

        veh.setId(vse.getId());
        veh.setDescription(vse.getDescription());
        veh.setRegisteredAt(vse.getRegisteredAt());
        veh.setUnregisteredAt(vse.getUnregisteredAt());
        veh.setUniqVehId(vse.getUniqVehId());
        veh.setExpId((vse.getExperiment() != null ? vse.getExperiment().getId() : null));

        return veh;
    }
}
