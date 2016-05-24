package gov.dot.fhwa.saxton.speedharm.persistence.converters;

import gov.dot.fhwa.saxton.speedharm.api.objects.InfrastructureStatusUpdate;
import gov.dot.fhwa.saxton.speedharm.misc.Constants;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.RTMSStatusUpdateEntity;

/**
 * Converts the various infrastructure entity classes to the universal API object
 */
public class InfrastructureStatusUpdateConverter {

    public static InfrastructureStatusUpdate databaseToWeb(RTMSStatusUpdateEntity ent) {
        InfrastructureStatusUpdate isu = new InfrastructureStatusUpdate();
        isu.setOccupancy(ent.getOccupancy());
        isu.setSpeed(ent.getSpeed() * Constants.MILES_PER_HOUR_TO_METERS_PER_SECOND);
        isu.setTimestamp(ent.getTimestamp());
        isu.setVolume(ent.getVolume());
        isu.setZone(ent.getZone());

        isu.setInfrastructure(null);

        return isu;
    }
}
