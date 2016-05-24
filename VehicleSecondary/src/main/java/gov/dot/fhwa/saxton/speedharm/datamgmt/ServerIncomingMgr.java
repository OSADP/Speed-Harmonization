package gov.dot.fhwa.saxton.speedharm.datamgmt;

import gov.dot.fhwa.saxton.carmasecondary.logger.ILogger;
import gov.dot.fhwa.saxton.carmasecondary.logger.LoggerManager;
import gov.dot.fhwa.saxton.carmasecondary.utils.AppConfig;
import gov.dot.fhwa.saxton.carmasecondary.utils.IAppConfig;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleCommand;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleSession;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleStatusUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Receives data from the experiment server and forwards it to the MainDataMgr for distribution
 * Uses the following properties from the config file:
 *      othervehicle.status.period
 *      server.rooturi
 */
public class ServerIncomingMgr implements Runnable {

    @Autowired
    private RestTemplate restTemplate;

    private static ILogger  logger = LoggerManager.getLogger(ServerIncomingMgr.class);
    @Autowired
    private MainDataMgr     mainMgr_;
    private AtomicBoolean   done_ = new AtomicBoolean(false);
    private String          rootUri_;
    private int             otherVehicleUpdatePeriod_;
    private LocalDateTime   prevOtherUpdateTime_ = LocalDateTime.now();
    private long            internalId_ = 0;

    public ServerIncomingMgr() {
        IAppConfig config = AppConfig.getInstance();

        rootUri_ = config.getProperty("server.rooturi");
        otherVehicleUpdatePeriod_ = config.getIntValue("othervehicle.status.period");
    }

    /**
     * Sets up the management thread
     */
    public void initialize(long id) {
        internalId_ = id;

        new Thread(this).start();
        logger.info("DATSI", "Incoming thread initialized.");
    }

    /**
     * Polls the server for new command data as frequently as possible.  Occasionally it will also ask the server for
     * an update on the other vehicles participating in the experiment.  It then passes these data on to the main
     * data manager to distribute as necessary.
     */
    public void run() {

        while (!done_.get()) {

            try {

                //create a request for the next command for this vehicle and send it to the server (long polling)
                LocalDateTime begin = LocalDateTime.now();

                VehicleCommand cmd = restTemplate.getForObject(rootUri_ + "/commands/{id}", VehicleCommand.class, internalId_);

                long elapsed = Duration.between(begin, LocalDateTime.now()).toMillis();
                if (cmd != null) {
                    logger.infof("DATSI", "Received command from server after %d ms: %s", elapsed, cmd.toString());

                    //distribute the command data to the main data manager
                    Double cmdSpeedIn = cmd.getSpeed();
                    double cmdSpeed = 0.0;
                    if (cmdSpeedIn != null) {
                        cmdSpeed = cmdSpeedIn;
                    }
                    Double cmdConfIn = cmd.getCommandConfidence();
                    double cmdConf = 0.0;
                    if (cmdConfIn != null) {
                        cmdConf = cmdConfIn;
                    }
                    mainMgr_.updateCommand(cmdSpeed, cmdConf);

                    //if enough time has elapsed since previous other-vehicle request then
                    long timeSincePrev = Duration.between(prevOtherUpdateTime_, LocalDateTime.now()).toMillis();
                    if (timeSincePrev > otherVehicleUpdatePeriod_) {

                        //make new request for other vehicle data and send it to the main data manager
                        requestOtherVehicleData();
                        prevOtherUpdateTime_ = LocalDateTime.now();

                    }
                }else {
                    logger.warnf("DATSI", "Null command object received from server after %d ms", elapsed);
                }
            }catch (Exception e) {
                logger.warn("DATSI", "Exception trapped in incoming command loop: " + e.getMessage());
            }
        }
    }


    public void terminate() {
        done_.getAndSet(true);
    }


    /**
     * Makes an HTTP request to the server to get pertinent status data from each of the partner vehicles in the
     * experiment, repackages that data and forwards it to the main data manager.
     */
    private void requestOtherVehicleData() {
        List<OtherVehicleStatus> others = new ArrayList<OtherVehicleStatus>();
        LocalDateTime begin = LocalDateTime.now();

        //ask the server what vehicles are currently part of the experiment
        List<VehicleSession> vehicles = null;
        try {
            ResponseEntity<List<VehicleSession>> resp = restTemplate.exchange(rootUri_ + "/vehicles", HttpMethod.GET,
                    null, new ParameterizedTypeReference<List<VehicleSession>>() {
                    });
            vehicles = resp.getBody();
        }catch (Exception e) {
            logger.warn("DATSI", "Exception trying to get list of vehicles from server: " + e.getMessage());
        }

        //loop through all vehicles in the experiment
        for (VehicleSession v : vehicles) {

            if (v.getId() != internalId_) {
                //if it's not ours add it to the list of others
                long otherId = v.getId();
                OtherVehicleStatus stat = new OtherVehicleStatus(otherId, v.getUniqVehId());

                //request the latest status of this vehicle from the server
                VehicleStatusUpdate serverStatus = restTemplate.getForObject(rootUri_ + "/status/{id}",
                                                    VehicleStatusUpdate.class, otherId);

                if (serverStatus != null) {
                    int automationState = 0;
                    switch (serverStatus.getAutomatedControlState()) {
                        case DISENGAGED:
                            automationState = 0;
                            break;

                        case ENGAGED:
                            automationState = 1;
                            break;

                        case ENGAGED_BUT_IGNORING:
                            automationState = 2;
                            break;

                        default:
                            logger.warnf("DATSI", "Received unknown automation status of %s for vehicle ID %d",
                                    serverStatus.getAutomatedControlState().toString(), otherId);
                    }

                    stat.setAutomation(automationState);

                    others.add(stat);
                }
            }
        }
        long elapsed = Duration.between(begin, LocalDateTime.now()).toMillis();
        logger.debugf("DATSI", "requestOtherVehicleData completed in %d ms for %d vehicles.", elapsed, vehicles.size() - 1);

        //send the list to the data manager
        mainMgr_.updateOtherVehicleData(others);
    }
}
