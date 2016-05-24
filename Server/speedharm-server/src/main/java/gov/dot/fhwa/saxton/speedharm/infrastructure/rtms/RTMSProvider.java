package gov.dot.fhwa.saxton.speedharm.infrastructure.rtms;

import gov.dot.fhwa.saxton.speedharm.api.objects.Infrastructure;
import gov.dot.fhwa.saxton.speedharm.api.objects.InfrastructureDataSource;
import gov.dot.fhwa.saxton.speedharm.api.objects.InfrastructureStatusUpdate;
import gov.dot.fhwa.saxton.speedharm.infrastructure.InfrastructureProvider;
import gov.dot.fhwa.saxton.speedharm.infrastructure.NewInfrastructureCallback;
import gov.dot.fhwa.saxton.speedharm.infrastructure.NewInfrastructureDataCallback;
import gov.dot.fhwa.saxton.speedharm.persistence.repositories.RTMSStatusUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Provider for RTMS infrastructure data
 */
public class RTMSProvider implements InfrastructureProvider {

    private List<NewInfrastructureDataCallback> callbacks = new ArrayList<>();

    private List<RTMSUpdateRetriever> collectorThreads = new ArrayList<>();

    private List<Infrastructure> rtmsUnits;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private RTMSStatusUpdateRepository rtmsStatusUpdateRepository;

    public RTMSProvider(List<Infrastructure> rtmsUnits) {
        this.rtmsUnits = rtmsUnits;
    }

    @Override
    public InfrastructureDataSource getDataSource() {
        return InfrastructureDataSource.RTMS;
    }

    @Override
    public void registerNewDataCallback(NewInfrastructureDataCallback nidc) {
        callbacks.add(nidc);
    }

    @Override
    public List<Infrastructure> getInfrastructures() {
        return rtmsUnits;
    }

    /**
     * We don't ever expect to see a new RTMS appear at run time, all must be configured before at compilation.
     * @param nic
     */
    @Override
    public void registerNewInfrastructureCallback(NewInfrastructureCallback nic) {

    }

    /**
     * Start the data source threads after this object has been fully constructed.
     */
    @PostConstruct
    public void startCollectorThreads() {
        for (Infrastructure rtmsUnit : rtmsUnits) {
            createUpdateRetriever(rtmsUnit);
        }

        for (RTMSUpdateRetriever rur : collectorThreads) {
            new Thread(rur).start();
        }
    }

    @PreDestroy
    public void stopCollectorThreads() {
        for (RTMSUpdateRetriever rur : collectorThreads) {
            rur.stop();
        }

        collectorThreads = new ArrayList<>();
    }

    /**
     * Pass the new data to all the callbacks
     * @param updates The new data to distribute to the callback holders
     */
    protected void fireCallbacks(List<InfrastructureStatusUpdate> updates) {
        for (NewInfrastructureDataCallback cb : callbacks) {
            cb.onNewData(updates);
        }
    }

    private void createUpdateRetriever(Infrastructure rtms) {
        RTMSUpdateRetriever rtmsUpdateRetriever = new RTMSUpdateRetriever(
                rtms,
                infrastructureUpdateCycleTime,
                rtmsStatusUpdateRepository,
                this::fireCallbacks);
        this.collectorThreads.add(rtmsUpdateRetriever);
    }

    @Value("${infrastructure.update_cycle_time}")
    private long infrastructureUpdateCycleTime;

}
