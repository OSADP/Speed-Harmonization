package gov.dot.fhwa.saxton.speedharm.infrastructure.mockrtms;

import gov.dot.fhwa.saxton.speedharm.api.objects.Infrastructure;
import gov.dot.fhwa.saxton.speedharm.api.objects.InfrastructureDataSource;
import gov.dot.fhwa.saxton.speedharm.api.objects.InfrastructureStatusUpdate;
import gov.dot.fhwa.saxton.speedharm.infrastructure.InfrastructureProvider;
import gov.dot.fhwa.saxton.speedharm.infrastructure.NewInfrastructureCallback;
import gov.dot.fhwa.saxton.speedharm.infrastructure.NewInfrastructureDataCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of InfrastructureProvider enabling the use of fake RTMS data.
 */
public class MockRtmsProvider implements InfrastructureProvider {

    private Logger log = LogManager.getLogger();
    private List<Infrastructure> infrastructures;
    private List<NewInfrastructureDataCallback> dataCallbacks = new ArrayList<>();
    private boolean initialized = false;
    private MockRtmsModel model;

    private AtomicBoolean running = new AtomicBoolean(false);

    @Value("${infrastructure.update_cycle_time}")
    private long infrastructureUpdateCycleTime;

    public MockRtmsProvider(List<Infrastructure> infrastructures, MockRtmsModel model) {
        this.infrastructures = infrastructures;
        this.model = model;
    }


    @PostConstruct
    public void startThreads() {
        log.info("Starting mock RTMS thread.");
        running.set(true);
        new Thread(() -> {
            while (running.get()) {
                log.info("Generating mock RTMS data.");

                Instant startTime = Instant.now();
                for (Infrastructure i : infrastructures) {
                    // Create a new status update
                    InfrastructureStatusUpdate isu = (initialized ? model.getNext(i) : model.getInitialState(i));
                    log.info("Reporting mock RTMS data " + isu);
                    fireNewDataCallbacks(isu);
                }

                initialized = true;

                // Sleep until our next timestep
                Instant endTime = Instant.now();
                Duration elapsedTime = Duration.between(startTime, endTime);
                if (elapsedTime.toMillis() < infrastructureUpdateCycleTime) {
                    try {
                        Thread.sleep(infrastructureUpdateCycleTime - elapsedTime.toMillis());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @PreDestroy
    public void stopThreads() {
        running.set(false);
    }

    @Override
    public InfrastructureDataSource getDataSource() {
        return InfrastructureDataSource.RTMS;
    }

    @Override
    public void registerNewDataCallback(NewInfrastructureDataCallback nidc) {
        dataCallbacks.add(nidc);
    }

    @Override
    public List<Infrastructure> getInfrastructures() {
        return infrastructures;
    }

    @Override
    public void registerNewInfrastructureCallback(NewInfrastructureCallback nic) {

    }

    private void fireNewDataCallbacks(InfrastructureStatusUpdate data) {
        List<InfrastructureStatusUpdate> out = new ArrayList<>();
        out.add(data);

        for (NewInfrastructureDataCallback callback : dataCallbacks) {
            callback.onNewData(out);
        }
    }
}
