package gov.dot.fhwa.saxton.speedharm.datamgmt;

import gov.dot.fhwa.saxton.carmasecondary.logger.ILogger;
import gov.dot.fhwa.saxton.carmasecondary.logger.LoggerManager;
import gov.dot.fhwa.saxton.carmasecondary.utils.AppConfig;
import gov.dot.fhwa.saxton.carmasecondary.utils.IAppConfig;
import gov.dot.fhwa.saxton.speedharm.api.objects.NetworkLatencyInformation;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleSession;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleStatusUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages transmission of all data to the experiment server.
 *
 * Uses the following properties from the config file:
 *      server.rooturi
 *      toserver.period
 */
public class ServerOutgoingMgr implements Runnable {

    @Autowired
    private RestTemplate restTemplate; // = new RestTemplate();

    private static ILogger                  logger = LoggerManager.getLogger(ServerOutgoingMgr.class);
    private AtomicBoolean                   done_ = new AtomicBoolean(false);
    private AtomicBoolean                   newDataAvailable_ = new AtomicBoolean(false);
    private String                          rootUri_ = null;
    private int                             transmitPeriod_;
    private String                          ownName_ = null;
    private VehicleStatusUpdate             latestStatus_ = new VehicleStatusUpdate();
    private LocalDateTime                   prevSendTime_ = LocalDateTime.now();
    private long                            internalId_ = 0;    //server's ID for our vehicle
    private Duration                        measuredLatency_ = null;
    private Duration                        prevLatency_ = null;
    private boolean                         firstUpdateSent_ = false;

    public ServerOutgoingMgr() {
        IAppConfig config = AppConfig.getInstance();

        rootUri_ = config.getProperty("server.rooturi");
        transmitPeriod_ = config.getIntValue("toserver.period");
    }

    /**
     * Registers the own vehicle with the server and thereby obtains the server's internal ID for this vehicle,
     * which will be needed for all of our server comms.
     * @param ownName - the common name of our vehicle
     * @param tag - last 2 digits of the license plate number
     * @return the internal ID of this vehicle
     */
    public long initialize(String ownName, int tag) throws Exception {
        ownName_ = ownName; //guaranteed to be non-null

        //register our own vehicle with the server
        VehicleSession own = new VehicleSession();
        own.setUniqVehId(ownName);
        own.setDescription("CARMA Cadillac SRX, USDOT license plate ending in " + tag);
        logger.infof("DATSO", "Preparing to register vehicle %s", ownName);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity entity = new HttpEntity(own, headers);
            ResponseEntity<List<VehicleSession>> resp = restTemplate.exchange(rootUri_ + "/vehicles", HttpMethod.POST,
                    entity, new ParameterizedTypeReference<List<VehicleSession>>() {
                    });

            //find the server's internal ID for our vehicle
            HttpHeaders responseHeaders = resp.getHeaders();
            String rawHeader = responseHeaders.getFirst("Location");
            int beginId = rawHeader.lastIndexOf('/') + 1;
            String idString = rawHeader.substring(beginId);
            internalId_ = Long.parseLong(idString);
            logger.debugf("DATSO", "HTTP response string: %s --> internal ID = %d", rawHeader, internalId_);
        }catch (Exception e) {
            logger.warn("DATSO", "Error trapped while registering vehicle: " + e.getMessage());
        }

        //bail out if we don't have a valid ID
        if (internalId_ <= 0) {
            logger.error("DATSO", "Unable to properly register this vehicle with the server.");
            throw new Exception("Unable to register vehicle. No server internal ID returned.");
        }

        //set the vehicle ID in the first status message that will be built
        latestStatus_.setId(internalId_);

        //start the thread
        new Thread(this).start();

        logger.infof("DATSO", "Outgoing thread initialized. Vehicle internal ID = %d", internalId_);
        return internalId_;
    }

    public void run() {

        //loop until shutdown
        while (!done_.get()) {

            //if enough time has elapsed since our previous transmission then
            long timeSincePrev = Duration.between(prevSendTime_, LocalDateTime.now()).toMillis();
            if (timeSincePrev >= transmitPeriod_) {

                //if new data has arrived from the MAB then send it to the server
                if (newDataAvailable_.get()) {
                    LocalDateTime timeAtTop = LocalDateTime.now();

                    //add the current timestamp to the status message so we can determine network latency
                    NetworkLatencyInformation net = new NetworkLatencyInformation();
                    net.setVehicleTxTimestamp(timeAtTop);
                    net.setVehicleMeasuredNetworkLatency(measuredLatency_); //may be null
                    latestStatus_.setNetworkLatencyInformation(net);
                    VehicleStatusUpdate rtn;

                    try {
                        rtn = restTemplate.postForObject(rootUri_ + "/status/{id}",
                                                    latestStatus_, VehicleStatusUpdate.class, internalId_);
                        if (rtn == null) {
                            logger.warnf("DATSO", "Sending status update: vehicle ID %d not recognized by server.", internalId_);
                        }
                    }catch (RestClientException re) {
                        logger.warnf("DATSO", "Unable to post vehicle %d status to the server: %s", internalId_, re.toString());
                    }

                    //indicate that the most recent message has been forwarded
                    prevSendTime_ = LocalDateTime.now();
                    newDataAvailable_.set(false);

                    //Determine the latency and store it for next time (average one-way trip, assumes both directions are same)
                    prevLatency_ = measuredLatency_;
                    measuredLatency_ = Duration.between(timeAtTop, prevSendTime_).dividedBy(2L); //half of round trip
                    long prevMs = 0;
                    if (prevLatency_ != null) {
                        prevMs = prevLatency_.toMillis();
                    }
                    long curMs = measuredLatency_.toMillis();
                    double diff = 0.0;
                    if (prevMs > 0) {
                        diff = 100.0 * (curMs - prevMs) / prevMs;
                    }
                    if (Math.abs(diff) > 20.0  &&  !firstUpdateSent_) {
                        logger.warnf("DATSO", "LARGE one-way latency to server = %d ms (prev was %d ms); %.1f%% change", curMs, prevMs, diff);
                    }else {
                        logger.infof("DATSO", "One-way latency to server = %d ms (prev was %d ms); %.1f%% change", curMs, prevMs, diff);
                    }

                    //create a new empty status object for the next go-around
                    latestStatus_ = new VehicleStatusUpdate();
                    latestStatus_.setId(internalId_);
                    firstUpdateSent_ = true;

                    long elapsed = Duration.between(timeAtTop, prevSendTime_).toMillis();
                    logger.debugf("DATSO", "Message sent to server. Took %d ms", elapsed);
                }else {
                    //sleep just a bit to avoid a runaway loop, since we're already overdue for new data
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) { }
                }

            } else {
                //sleep until it's time to send another transmission
                try {
                    Thread.sleep(transmitPeriod_ - timeSincePrev);
                } catch (InterruptedException e) { }
            }
        }
    }

    public void terminate() {
        //delete my vehicle registration on the server
        if (internalId_ > 0) {
            restTemplate.delete(rootUri_ + "/vehicles/" + internalId_);
        }

        logger.info("DATSO", "Terminating the outgoing server thread.");
        done_.set(true);
    }

    /**
     * Parses a message from the MAB and stores the relevant elements.
     * @param msg - the raw status message as received from MAB
     */
    public void newMabMsg(byte[] msg) {

        //pull out the elements we need (everything but radar relative speed)
        // scaling and message structure is defined in the Speed Harm software design document
        VehicleStatusUpdate.AutomatedControlStatus controlState = VehicleStatusUpdate.AutomatedControlStatus.DISENGAGED;
        switch (msg[1]) {
            case 0:
                controlState = VehicleStatusUpdate.AutomatedControlStatus.DISENGAGED;
                break;
            case 1:
                controlState = VehicleStatusUpdate.AutomatedControlStatus.ENGAGED ;
                break;
            case 2:
                controlState = VehicleStatusUpdate.AutomatedControlStatus.ENGAGED_BUT_IGNORING;
                break;
            default:
                logger.warnf("DATSO", "Unrecognized automation state coming from MAB: %d", msg[1]);
        }
        latestStatus_.setAutomatedControlState(controlState);

        latestStatus_.setDistanceToNearestRadarObject(pull2Bytes(msg, 2, 0.1));

        latestStatus_.setSpeed(pull2Bytes(msg, 6, 0.01));

        latestStatus_.setAccel(pull2Bytes(msg, 8, 0.001));

        latestStatus_.setLat(pull4Bytes(msg, 10, 1.0e-7));

        latestStatus_.setLon(pull4Bytes(msg, 14, 1.0e-7));

        latestStatus_.setHeading(pull2Bytes(msg, 18, 0.1));

        //indicate that a new message is available
        newDataAvailable_.set(true);
    }

    //////////////////
    // private methods
    //////////////////

    private double pull2Bytes(byte[] msg, int offset, double scale) {
        long unscaledVal = (msg[offset] & 0x00ff) | ((msg[offset+1] << 8) & 0xff00);
        return scale * unscaledVal;
    }

    private double pull4Bytes(byte[] msg, int offset, double scale) {
        long unscaledVal =  (msg[offset]          & 0x000000ff) | ((msg[offset+1] <<  8) & 0x0000ff00)
                         | ((msg[offset+2] << 16) & 0x00ff0000) | ((msg[offset+3] << 24) & 0xff000000);
        return scale * unscaledVal;
    }

}

