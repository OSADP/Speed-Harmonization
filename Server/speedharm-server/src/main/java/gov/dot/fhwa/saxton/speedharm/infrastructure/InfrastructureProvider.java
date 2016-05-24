package gov.dot.fhwa.saxton.speedharm.infrastructure;

import gov.dot.fhwa.saxton.speedharm.api.objects.Infrastructure;
import gov.dot.fhwa.saxton.speedharm.api.objects.InfrastructureDataSource;

import java.util.List;

/**
 * Base of inheritance for a generic infrastructure data source.
 */
public interface InfrastructureProvider {

    InfrastructureDataSource getDataSource();
    void registerNewDataCallback(NewInfrastructureDataCallback nidc);

    List<Infrastructure> getInfrastructures();
    void registerNewInfrastructureCallback(NewInfrastructureCallback nic);
}
