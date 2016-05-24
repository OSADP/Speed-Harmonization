package gov.dot.fhwa.saxton.speedharm.infrastructure;

import gov.dot.fhwa.saxton.speedharm.api.objects.InfrastructureStatusUpdate;

import java.util.List;

/**
 * Functional Interface for a callback invoked upon receiving new infrastructure data.
 */
public interface NewInfrastructureDataCallback {
    /**
     * Callback invoked when the associated infrastructure provider has new data to report.
     * @param isus A list of new infrastructure status updates.
     */
    void onNewData(List<InfrastructureStatusUpdate> isus);
}
