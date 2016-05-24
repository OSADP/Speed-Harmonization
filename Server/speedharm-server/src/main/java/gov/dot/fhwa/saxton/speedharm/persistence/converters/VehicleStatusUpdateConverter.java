package gov.dot.fhwa.saxton.speedharm.persistence.converters;

import gov.dot.fhwa.saxton.speedharm.api.objects.NetworkLatencyInformation;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleStatusUpdate;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.NetworkLatencyInformationEntity;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.VehicleSessionEntity;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.VehicleStatusUpdateEntity;

/**
 * Class converting between VehicleStatusUpdate <-> VehicleStatusUpdateEntity
 */
public class VehicleStatusUpdateConverter {
    public static VehicleStatusUpdate databaseToWeb(VehicleStatusUpdateEntity ent) {
        VehicleStatusUpdate vsu = new VehicleStatusUpdate();
        vsu.setAccel(ent.getAcceleration());
        vsu.setAutomatedControlState(ent.getAutomatedControlEngaged());
        vsu.setDistanceToNearestRadarObject(ent.getDistanceToNearestObject());
        vsu.setHeading(ent.getHeading());
        vsu.setId(ent.getId());
        vsu.setLat(ent.getLat());
        vsu.setLon(ent.getLon());
        vsu.setSpeed(ent.getSpeed());
        vsu.setVehId(ent.getVehicleSession() != null ? ent.getVehicleSession().getId() : null);

        NetworkLatencyInformation nli = NetworkLatencyInformationConverter.databaseToWeb(ent.getNetworkLatencyInformation());
        vsu.setNetworkLatencyInformation(nli);
        return vsu;
    }

    public static VehicleStatusUpdateEntity webToDatabase(VehicleStatusUpdate vsu, VehicleSessionEntity vse) {
        VehicleStatusUpdateEntity ent = new VehicleStatusUpdateEntity();
        ent.setId(vsu.getId());
        ent.setLat(vsu.getLat());
        ent.setLon(vsu.getLon());
        ent.setSpeed(vsu.getSpeed());
        ent.setAcceleration(vsu.getAccel());
        ent.setAutomatedControlEngaged(vsu.getAutomatedControlState());
        ent.setDistanceToNearestObject(vsu.getDistanceToNearestRadarObject());
        ent.setHeading(vsu.getHeading());
        ent.setVehicleSession(vse);

        NetworkLatencyInformationEntity nli = NetworkLatencyInformationConverter.webToDatabase(vsu.getNetworkLatencyInformation());
        ent.setNetworkLatencyInformation(nli);
        ent.setRelativeSpeedOfNearestRadarTarget(null);

        return ent;
    }
}
