package gov.dot.fhwa.saxton.speedharm.infrastructure.mockrtms.models;

import gov.dot.fhwa.saxton.speedharm.api.objects.Infrastructure;
import gov.dot.fhwa.saxton.speedharm.api.objects.InfrastructureStatusUpdate;
import gov.dot.fhwa.saxton.speedharm.infrastructure.mockrtms.MockRtmsModel;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Model of RTMS data for generating randomly changing reports (within a threshhold)
 */
public class RandomRtmsModel implements MockRtmsModel {
    private static final double EPSILON = 5.0;

    private Map<Infrastructure, InfrastructureStatusUpdate> states = new HashMap<>();

    @Override
    public InfrastructureStatusUpdate getInitialState(Infrastructure i) {
        InfrastructureStatusUpdate isu = new InfrastructureStatusUpdate();

        isu.setSpeed(13.0);
        isu.setOccupancy(1000.0);
        isu.setTimestamp(LocalDateTime.now());
        isu.setVolume(1000);
        isu.setZone(0);
        isu.setInfrastructure(i);

        states.put(i, isu);

        return isu;
    }

    @Override
    public InfrastructureStatusUpdate getNext(Infrastructure i) {
        InfrastructureStatusUpdate prev = states.get(i);

        if (prev != null) {
            prev.setSpeed(getNewValue(prev.getSpeed(), EPSILON));
            prev.setOccupancy(getNewValue(prev.getOccupancy(), EPSILON));
            prev.setTimestamp(LocalDateTime.now());
            prev.setVolume(getNewValue(prev.getVolume(), EPSILON));
            prev.setZone(0);
            prev.setInfrastructure(i);

            return prev;
        } else {
            return null;
        }
    }

    private Double getNewValue(Double init, Double epsilon) {
        return init - epsilon + (Math.random() * epsilon * 2);
    }

    private Integer getNewValue(Integer init, Double epsilon) {
        Double value = getNewValue(init * 1.0, epsilon);
        return (int) Math.round(value.doubleValue());
    }
}
