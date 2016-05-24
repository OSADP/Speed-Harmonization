package gov.dot.fhwa.saxton.speedharm.persistence.converters;

import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleCommand;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.VehicleCommandEntity;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.VehicleSessionEntity;

/**
 * Converter between VehicleCommand <-> VehicleCommandEntity classes
 */
public class VehicleCommandConverter {

    public static VehicleCommandEntity webToDatabase(VehicleCommand vc, VehicleSessionEntity vse) {
        VehicleCommandEntity vce = new VehicleCommandEntity();
        vce.setId(vc.getId());
        vce.setSpeed(vc.getSpeed());
        vce.setTimestamp(vc.getTimestamp());
        vce.setVehicleSession(vse);
        vce.setCommandConfidence(vc.getCommandConfidence());

        return vce;
    }

    public static VehicleCommand databaseToWeb(VehicleCommandEntity vce) {
        VehicleCommand vc = new VehicleCommand();
        vc.setId(vce.getId());
        vc.setSpeed(vce.getSpeed());
        vc.setTimestamp(vce.getTimestamp());
        vc.setVehId(vce.getVehicleSession() != null ? vce.getVehicleSession().getId() : null);

        vc.setCommandConfidence(vce.getCommandConfidence());

        return vc;
    }
}
