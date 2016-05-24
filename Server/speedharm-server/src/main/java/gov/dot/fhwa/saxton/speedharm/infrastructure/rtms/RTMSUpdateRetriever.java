package gov.dot.fhwa.saxton.speedharm.infrastructure.rtms;

import gov.dot.fhwa.saxton.speedharm.api.objects.Infrastructure;
import gov.dot.fhwa.saxton.speedharm.api.objects.InfrastructureStatusUpdate;
import gov.dot.fhwa.saxton.speedharm.infrastructure.NewInfrastructureDataCallback;
import gov.dot.fhwa.saxton.speedharm.persistence.converters.InfrastructureStatusUpdateConverter;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.RTMSStatusUpdateEntity;
import gov.dot.fhwa.saxton.speedharm.persistence.repositories.RTMSStatusUpdateRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Class for retrieving RTMS status updates from the DataBase
 */
public class RTMSUpdateRetriever implements Runnable {

    private Logger log = LogManager.getLogger();

    private static final Duration RTMS_TIME_ADJUSTMENT_FACTOR = Duration.ofMinutes(10);

    private AtomicBoolean running = new AtomicBoolean(false);

    private LocalDateTime lastQueryAt = LocalDateTime.now();

    private RTMSStatusUpdateRepository repo;

    private Infrastructure rtms;

    private Long cycleTime = 0L;

    private NewInfrastructureDataCallback cb;

    public RTMSUpdateRetriever(Infrastructure rtms, Long cycleTime, RTMSStatusUpdateRepository repo, NewInfrastructureDataCallback cb) {
        this.rtms = rtms;
        this.cycleTime = cycleTime;
        this.repo = repo;
        this.cb = cb;
    }

    public void run() {
        running.getAndSet(true);
        lastQueryAt = LocalDateTime.now().minus(RTMS_TIME_ADJUSTMENT_FACTOR);
        while (running.get()) {
            Instant startTime = Instant.now();

            // Get all updates since last
            List<RTMSStatusUpdateEntity> updates = repo.findByRtmsNameAndTimestampAfter(rtms.getName(), lastQueryAt);
            log.info("Found " + updates.size() + " new RTMS updates for RTMS: " + rtms.getName() + ". Distributing to algorithms...");
            List<InfrastructureStatusUpdate> processed = updates.stream()
                    .map(isu -> {
                        InfrastructureStatusUpdate isu2 = InfrastructureStatusUpdateConverter.databaseToWeb(isu);
                        isu2.setInfrastructure(rtms);

                        // Find the newest timestamp among all incoming data.
                        if (isu.getTimestamp().isAfter(lastQueryAt)) {
                            lastQueryAt = isu.getTimestamp();
                        }

                        return isu2;
                    })
                    .collect(Collectors.toList());

            // Pass the data back up the pipeline
            cb.onNewData(processed);


            // Sleep until we've elapsed the rest of the period if necessary
            Instant endTime = Instant.now();
            Duration measured = Duration.between(startTime, endTime);
            Duration cycleTime = Duration.ofMillis(this.cycleTime);
            if (measured.compareTo(cycleTime) < 0) {
                try {
                    log.info("Finished RTMS update thread. Sleeping for " + cycleTime.minus(measured).toMillis() + "ms");
                    Thread.sleep(cycleTime.minus(measured).toMillis());
                } catch (InterruptedException e) {
                    log.warn("RTMS Update thread unable to sleep!!");
                }
            }
        }
    }

    public void stop() {
        running.getAndSet(false);
    }
}
