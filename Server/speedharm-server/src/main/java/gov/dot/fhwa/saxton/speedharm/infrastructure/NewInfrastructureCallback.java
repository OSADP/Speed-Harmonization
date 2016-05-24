package gov.dot.fhwa.saxton.speedharm.infrastructure;

import gov.dot.fhwa.saxton.speedharm.api.objects.Infrastructure;

/**
 * Callback to report the discovery of a new Infrastructure object
 */
public interface NewInfrastructureCallback {
    /**
     * Callback invoked when an infrastrcuture provider discovers a new {@link Infrastructure} instance at run time.
     * @param i The new infrastructure instance
     */
    void onNewInfrastructure(Infrastructure i);
}
