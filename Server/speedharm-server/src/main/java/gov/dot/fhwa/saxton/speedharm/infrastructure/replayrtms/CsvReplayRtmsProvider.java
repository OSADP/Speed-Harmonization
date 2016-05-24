package gov.dot.fhwa.saxton.speedharm.infrastructure.replayrtms;

import gov.dot.fhwa.saxton.speedharm.api.objects.Infrastructure;
import gov.dot.fhwa.saxton.speedharm.api.objects.InfrastructureDataSource;
import gov.dot.fhwa.saxton.speedharm.api.objects.InfrastructureStatusUpdate;
import gov.dot.fhwa.saxton.speedharm.infrastructure.InfrastructureProvider;
import gov.dot.fhwa.saxton.speedharm.infrastructure.NewInfrastructureCallback;
import gov.dot.fhwa.saxton.speedharm.infrastructure.NewInfrastructureDataCallback;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Infrastructure provider for RTMS which replays data from a CSV file.
 */
public class CsvReplayRtmsProvider implements InfrastructureProvider {

    private Logger log = LogManager.getLogger();
    private List<NewInfrastructureDataCallback> callbacks = new ArrayList<>();
    private List<Infrastructure> rtmsUnits = new ArrayList<>();
    private RtmsReplayThread thread;
    
    // Column constants
    private static final int NAME_COLUMN = 2;
    private static final int SPEED_COLUMN = 4;
    private static final int OCCUPANCY_COLUMN = 10;
    private static final int VOLUME_COLUMN = 6;
    private static final int TIMESTAMP_COLUMN = 0;
    private static final int ZONE_COLUMN = 3;

    private static final String FILE_NAME = "rtms.csv";

    public CsvReplayRtmsProvider(List<Infrastructure> rtmsUnits) {
        this.rtmsUnits = rtmsUnits;
    }

    private class RtmsReplayThread implements Runnable {
        private List<InfrastructureStatusUpdate> updates;
        private AtomicBoolean running = new AtomicBoolean(false);
        private static final long ITERATION_DELAY = 15000;

        public RtmsReplayThread(List<InfrastructureStatusUpdate> updates) {
            this.updates = updates;
        }

        @Override
        public void run() {
            // Sanity checking on data, we'll iterate too tightly if its one or zero entries
            if (updates.size() >= 2) {
                log.info("Beginning replay of " + updates.size() + " RTMS datapoints.");

                running.set(true);
                while (running.get()) {
                    for (int i = 0; i < updates.size() - 1; i++) {
                        InfrastructureStatusUpdate isu = updates.get(i);
                        Duration replayDelay = Duration.between(isu.getTimestamp(), updates.get(i + 1).getTimestamp());
                        isu.setTimestamp(LocalDateTime.now());
                        fireDataCallbacks(isu);

                        // Sleep until the time for the next ISU to be published
                        try {
                            if (replayDelay.toMillis() > 0) {
                                Thread.sleep(replayDelay.toMillis());
                            }
                        } catch (InterruptedException e) {
                            log.warn("RTMS replay thread woken up too early!");
                        }
                    }

                    fireDataCallbacks(updates.get(updates.size() - 1)); // Send last message before we loop over data

                    // Sleep the standard iteration delay between repetitions of the messages.
                    try {
                        Thread.sleep(ITERATION_DELAY);
                    } catch (InterruptedException e) {
                        log.warn("RTMS replay thread woken up too early!");
                    }
                }
            }
        }
    }

    @PostConstruct
    public void start() {
        log.info("Starting RTMS replay thread...");
        thread = new RtmsReplayThread(loadUpdates(new File(FILE_NAME)));
        new Thread(thread).start();
    }
    
    public List<InfrastructureStatusUpdate> loadUpdates(File f) {
        List<InfrastructureStatusUpdate> updates = new ArrayList<>();

        Reader in = null;
        try {
            in = new FileReader(f);

            for (CSVRecord record : CSVFormat.DEFAULT.parse(in)) {

                InfrastructureStatusUpdate isu = convert(record);

                if (isu != null) {
                    updates.add(isu);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("Replay RTMS loaded " + updates.size() + " datapoints for replay!");
        return updates;
    }

    private void fireDataCallbacks(InfrastructureStatusUpdate isu) {
        log.info("Replay RTMS data outputting " + isu);
        List<InfrastructureStatusUpdate> out = new ArrayList<>();
        out.add(isu);
        for (NewInfrastructureDataCallback nidc : callbacks) {
            nidc.onNewData(out);
        }
    }
    
    private Optional<Infrastructure> getInfrastructureByName(String name) {
        return rtmsUnits.stream().filter((Infrastructure i) -> i.getName().equals(name)).findFirst();
    }

    private InfrastructureStatusUpdate convert(CSVRecord record) {
        InfrastructureStatusUpdate isu = null;
        Optional<Infrastructure> i = getInfrastructureByName(record.get(NAME_COLUMN));
        if (!i.isPresent()) {
            return null; // No matching RTMS unit defined, signal invalid
        } else {
            try {
                isu = new InfrastructureStatusUpdate();
                isu.setInfrastructure(i.get());
                isu.setSpeed(Double.valueOf(record.get(SPEED_COLUMN)));
                isu.setOccupancy(Double.valueOf(record.get(OCCUPANCY_COLUMN)));
                isu.setVolume(Integer.valueOf(record.get(VOLUME_COLUMN)));
                isu.setZone(Integer.valueOf(record.get(ZONE_COLUMN)));

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d/y  h:m:sa");
                isu.setTimestamp(LocalDateTime.parse(record.get(TIMESTAMP_COLUMN), dtf));

                return isu;
            } catch (Exception e) {
                log.error("Unable to parse csv row " + record, e);
                return null;
            }
        }
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

    @Override
    public void registerNewInfrastructureCallback(NewInfrastructureCallback nic) {

    }
}
