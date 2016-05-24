package gov.dot.fhwa.saxton.speedharm.api.models;

import gov.dot.fhwa.saxton.speedharm.algorithms.IAlgorithm;
import gov.dot.fhwa.saxton.speedharm.api.objects.InfrastructureDataSource;
import gov.dot.fhwa.saxton.speedharm.api.objects.InfrastructureStatusUpdate;
import gov.dot.fhwa.saxton.speedharm.infrastructure.InfrastructureProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager class for Infrastructure objects and InfrastructureStatusUpdates
 */
public class InfrastructureManager {

    private Logger log = LogManager.getLogger();

    private Map<IAlgorithm, List<InfrastructureDataSource>> algorithmDataTypesMapping = new HashMap<>();

    private List<InfrastructureProvider> providers;

    public InfrastructureManager(List<InfrastructureProvider> providers) {
        this.providers = providers;
        for (InfrastructureProvider provider : providers) {
            provider.registerNewDataCallback(this::reportNewInfrastructureStatusUpdates);
        }
    }

    private void reportNewInfrastructureStatusUpdates(List<InfrastructureStatusUpdate> updates) {
        /* Potentially quadratic algorithm, keep an eye on this as a potential source of performance issues.
         * Realistically, shouldn't be a problem, since the nubmer of algorithms is likely to be small (and largely constant)
         * compared to the number of updates so it's effectively O(N) rather than O(M*N).
         * - Kyle */
        for (Map.Entry<IAlgorithm, List<InfrastructureDataSource>> entry : algorithmDataTypesMapping.entrySet()) {
            updates.stream()
                    .filter(isu -> entry.getValue().contains(isu.getInfrastructure().getDataSource()))
                    .forEach(isu -> entry.getKey().updateInfrastructureStatus(isu));
        }
    }

    /**
     * Register an algorithm to recieve updates from its list of required data sources
     * @param algo The algorithm to recieve updates
     */
    public void registerAlgorithmForUpdates(IAlgorithm algo) {
        algorithmDataTypesMapping.put(algo, algo.getRequiredInfrastructureDataSources());
    }
}
