package gov.dot.fhwa.saxton.speedharm.infrastructure.mockrtms;

import gov.dot.fhwa.saxton.speedharm.api.objects.Infrastructure;
import gov.dot.fhwa.saxton.speedharm.api.objects.InfrastructureStatusUpdate;

/**
 * An interface describing the way an RTMS units data changes over time. Used by the MockRtmsProvider to simulate
 * data.
 */
public interface MockRtmsModel {
    InfrastructureStatusUpdate getInitialState(Infrastructure i);
    InfrastructureStatusUpdate getNext(Infrastructure i);
}
