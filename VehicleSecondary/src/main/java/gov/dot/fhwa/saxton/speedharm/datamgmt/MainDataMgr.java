package gov.dot.fhwa.saxton.speedharm.datamgmt;

import gov.dot.fhwa.saxton.carmasecondary.logger.ILogger;
import gov.dot.fhwa.saxton.carmasecondary.logger.LoggerManager;
import gov.dot.fhwa.saxton.carmasecondary.utils.AppConfig;
import gov.dot.fhwa.saxton.carmasecondary.utils.IAppConfig;
import gov.dot.fhwa.saxton.speedharm.ProjectVersion;
import gov.dot.fhwa.saxton.speedharm.config.ProjectApplicationContext;
import gov.dot.fhwa.saxton.speedharm.services.ProjectService;
import gov.dot.fhwa.saxton.speedharm.ui.UiMessage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.time.*;


/**
 * MainDataMgr
 *
 * Listens for incoming status messages from the MAB on mab.datamgmt.port and distributes them
 * to the UI and the server. Sends messages to the MAB at mab.host, mab.datamgmt.port.
 *
 * Uses the following properties from the config file:
 *      asd.maxpacketsize
 *      confidence.threshold
 *      cycle.period
 *      gps.enable
 *      mab.command.port
 *      mab.host
 *      mab.timeout
 *      server.lostconnection.time
 *      toserver.period
 *      udp.timeout
 *      ui.duration
 *      vehicle1.tag
 *      vehicle1.name
 *      ...
 *      vehicle5.tag
 *      vehicle5.name
 */
public class MainDataMgr implements Runnable {

    private static ILogger logger = LoggerManager.getLogger(MainDataMgr.class);

    private AtomicBoolean       bShutdown = new AtomicBoolean(false);
    private int                 maxPacketSize;
    private int                 udpTimeout;

    // MAB configuration for Commands
    private DatagramSocket      mabSocket;
    private String              mabIp;
    private int                 mabUdpPort;
    private InetSocketAddress   mabAddress;
    private boolean             gpsEnabled_;        //has the user enabled the GPS collector (in CarmaSecondary library)?
    private int                 mabTimeout_;        //duration main loop will wait for a message from the MAB, ms
    private int                 cyclePeriod_;       //duration of each loop, ms
    private int                 toServerPeriod_;    //duration between data sends to the server, ms
    private int                 minUiDuration_;     //duration between data sends to the UI, ms
    private LocalDateTime       prevSentToUi_;      //time that the previous message was sent to the UI
    private LocalDateTime       prevLoopTime_;      //time that the previous main loop completed
    private boolean             continueOperation_ = true; //should the MAB continue controlling the vehicle?
    private AtomicBoolean       newCommand_ = new AtomicBoolean(false);//has new speed command data arrived from the server?
    private double              serverCommand_;     //latest speed command from the server, m/s
    private double              serverConfidence_;  //latest command confidence from the server [0..100]
    private LocalDateTime       prevCommandTime_;   //timestamp of most recent command receipt from the server
    private double              confidenceThreshold_;//min acceptable command confidence [0..100]
    private int                 serverDisconnectTime_;//max time, ms, allowed before server is considered disconnected
    private String              ownVehicleName_ = null;       //display name of own vehicle

    @Autowired
    private ProjectService      service;
    @Autowired
    private ServerIncomingMgr   serverInput_; // = new ServerIncomingMgr(this);
    @Autowired
    private ServerOutgoingMgr   serverOutput_; // = new ServerOutgoingMgr();

    private Object              UiMessageLock_ = new Object(); //lock for synchronizing thread access to the genUiMessage
    private Object              CommandLock_ = new Object();   //lock for synchronizing thread access to the vehicle command info
    private Map<Integer, String> vehicleIdMap_ = new HashMap<Integer, String>();
    private UiMessage           genUiMessage_ = new UiMessage(); //collects data from various places throughout the loop

    private final double        MPH_PER_METER_PER_SEC = 2.2369;
    private double              confidenceDecay_; //decay rate per second

    public MainDataMgr(){
        IAppConfig config = AppConfig.getInstance();

        this.maxPacketSize = config.getIntValue("asd.maxpacketsize");
        this.udpTimeout = config.getIntValue("udp.timeout");
        this.prevSentToUi_ = LocalDateTime.now();

        this.mabUdpPort = config.getIntValue("mab.command.port");
        this.mabIp = config.getProperty("mab.host");
        this.mabAddress = new InetSocketAddress(mabIp, mabUdpPort);

        mabTimeout_ = config.getIntValue("mab.timeout");

        //get other config properties
        gpsEnabled_ = config.getIntValue("gps.enable") == 1;
        cyclePeriod_ = config.getIntValue("cycle.period");
        toServerPeriod_ = config.getIntValue("toserver.period");
        minUiDuration_ = config.getIntValue("ui.duration");
        confidenceThreshold_ = config.getDoubleValue("confidence.threshold");
        serverDisconnectTime_ = config.getIntValue("server.lostconnection.time");
        confidenceDecay_ = config.getDoubleValue("confidence.decay");
        logger.infof("DATM", "Created with cyclePeriod = %d, toServerPeriod = %d, minUiDuration = %d, serverDisconnectTime = %d",
                        cyclePeriod_, toServerPeriod_, minUiDuration_, serverDisconnectTime_);
        logger.infof("DATM", "             confidenceDecay = %.3f, confidenceThreshold = %.3f", confidenceDecay_, confidenceThreshold_);

        //load the mapping of vehicle tag # to common names
        int id = config.getIntValue("vehicle1.tag");
        String name = config.getProperty("vehicle1.name");
        vehicleIdMap_.put(id, name);
        id = config.getIntValue("vehicle2.tag");
        name = config.getProperty("vehicle2.name");
        vehicleIdMap_.put(id, name);
        id = config.getIntValue("vehicle3.tag");
        name = config.getProperty("vehicle3.name");
        vehicleIdMap_.put(id, name);
        id = config.getIntValue("vehicle4.tag");
        name = config.getProperty("vehicle4.name");
        vehicleIdMap_.put(id, name);
        id = config.getIntValue("vehicle5.tag");
        name = config.getProperty("vehicle5.name");
        vehicleIdMap_.put(id, name);
    }

    /**
     * initialize - opens UDP socket on the MAB if possible.  Starts a new thread to listen to this socket.
     */
    public void initialize() throws Exception {
        logger.info("DATM", "Initializing MAB Commmand/Status Interface on port: " + mabUdpPort);

        //establish the UDP socket to the MAB
        try   {
            mabSocket = new DatagramSocket(mabUdpPort);
            mabSocket.setSoTimeout(mabTimeout_);
        }
        catch(Exception e)   {
            logger.error("DATM", "Exception initializing mabSocket: " + e.getMessage());
            throw e;
        }

        //initialize the timestamp of the previous command to something long ago, which ensures the command confidence
        // will be low
        prevCommandTime_ = LocalDateTime.now().minusSeconds(10);

        //set up the communication service object
        service = ProjectApplicationContext.getInstance().getService();
        if (service != null) {

            logger.info("DATM", "Start the UI...");
            try {
                Thread.sleep(5000);
            }catch (InterruptedException ie) { }

            //send our version ID string to the UI
            UiMessage versionMsg = new UiMessage(ProjectVersion.getInstance().toString());
            service.sendUiMessage(versionMsg);

            //initialize the time of the previous loop
            prevLoopTime_ = LocalDateTime.now();

            //start this thread
            new Thread(this).start();
            logger.info("DATM", "Main data manager thread initialized.");
        }else {
            logger.error("DATM", "FATAL:  Unable to access the ProjectService object. MainDataMgr not started.");
            throw new Exception("ProjectService object not created.");
        }

        //we need to wait until we receive a message from the MAB before we can initialize the incoming & outgoing
        // servers, since that initialization depends on the vehicle ID info we get from the MAB.
    }


    /**
     * run - watches for new incoming data from the MAB and passes the data elements on to their destinations,
     * and passes speed commands to the MAB
     */
    @Override
    public void run() {
        LocalDateTime prevTimeStep = LocalDateTime.now();

        while (!bShutdown.get())  {
            byte[] buf = new byte[maxPacketSize];
            DatagramPacket curPacket = null;
            DatagramPacket prevPacket = null;

            int numMsgs = -1;
            int prevPacketLength = 0;
            int bytesRead = 0;
            byte[] mabMessageIn = null;

            //read all messages currently on the MAB port and keep the most recent one
            LocalDateTime beforeMabRead = LocalDateTime.now();
            do {
                ++numMsgs;
                prevPacket = curPacket;
                prevPacketLength = bytesRead;
                bytesRead = 0;
                try {
                    curPacket = new DatagramPacket(buf, maxPacketSize);
                    mabSocket.receive(curPacket);
                    bytesRead = curPacket.getLength();
                } catch (SocketTimeoutException ste) {
                    // expected
                } catch (SocketException e1) {
                    logger.info("DATM", "read: MAB socket exception: " + e1.toString());
                } catch (IOException e2) {
                    logger.info("DATM", "read: MAB IO exception: " + e2.toString());
                } catch (Exception ex) {
                    logger.warn("DATM", "read: exception trapped: " + ex.toString());
                }
                //logger.debugf("DATM", "   bytesRead = %d, prevPacketLength = %d", bytesRead, prevPacketLength);
            }while (bytesRead > 0);
            //logger.debugf("DATM", "Pulled %d messages off MAB port. Last one contains %d bytes.", numMsgs, prevPacketLength);

            //if the message is the correct size (per the Speed Harm software design spec) then
            LocalDateTime beforeSendingtoServer = LocalDateTime.now();
            LocalDateTime afterSendingToServer = beforeSendingtoServer;
            LocalDateTime afterSendingToUi = beforeSendingtoServer;
            if (prevPacketLength == 20) {
                mabMessageIn = Arrays.copyOf(prevPacket.getData(), prevPacketLength);
                logger.debugf("DATM", "Raw message from MAB after %d ms: %s",
                                Duration.between(beforeMabRead, beforeSendingtoServer).toMillis(),
                                javax.xml.bind.DatatypeConverter.printHexBinary(mabMessageIn));

                //translate vehicle ID to vehicle name (assume it won't change during the experiment)
                if (ownVehicleName_ == null) {
                    int tag = (int) mabMessageIn[0];
                    ownVehicleName_ = vehicleIdMap_.get(tag);
                    if (ownVehicleName_ == null) {
                        String msg = "Unable to translate tag " + tag + " from MAB into a vehicle name.";
                        logger.error("DATM", msg);
                        terminate();
                        break;
                    }else {

                        //with this info we can initialize the server managers
                        long vid = 0;
                        try {
                            vid = serverOutput_.initialize(ownVehicleName_, tag);
                            serverInput_.initialize(vid);
                        } catch (Exception e) {
                            logger.error("DATM", "Error initializing server managers - shutting down threads.");
                            terminate();
                            break;
                        }
                    }
                }

                //send elements to server
                sendToServer(mabMessageIn);
                afterSendingToServer = LocalDateTime.now();

                //send elements of the new MAB incoming message to the UI
                //Note: this seems less important than getting the updated command to the MAB, but that has to be done
                //      below this if block. If testing shows that this transmission takes significant time, it may
                //      need to be redesigned.
                sendToUi(mabMessageIn);
                afterSendingToUi = LocalDateTime.now();

            }else if (prevPacketLength > 0){
                logger.warnf("DATM", "Unexpected number of status bytes received from MAB: %d. Ignoring this message.", prevPacketLength);
            }

            //send a speed command to the MAB (whether we've received a new one or not)
            sendSpeedCmdToMab();
            LocalDateTime afterSendingToMab = LocalDateTime.now();

            //if we're testing with the GPS consumer turned off, then flush logs to disk (GPS consumer does this, and
            // we don't want to overdo it because it may not be thread-safe)
            if (!gpsEnabled_) {
                try {
                    LoggerManager.writeToDisk();
                } catch (IOException e) {
                    System.err.println("Could not write log to disk: " + e.getMessage());
                }
            }
            LocalDateTime afterWritingLogs = LocalDateTime.now();

            logger.debugf("DATM", "      Main loop timing: MAB read    %4d ms", Duration.between(beforeMabRead, beforeSendingtoServer).toMillis());
            logger.debugf("DATM", "                        Server xmit %4d ms", Duration.between(beforeSendingtoServer, afterSendingToServer).toMillis());
            logger.debugf("DATM", "                        UI xmit     %4d ms", Duration.between(afterSendingToServer, afterSendingToUi).toMillis());
            logger.debugf("DATM", "                        Mab xmit    %4d ms", Duration.between(afterSendingToUi, afterSendingToMab).toMillis());
            logger.debugf("DATM", "                        Log write   %4d ms", Duration.between(afterSendingToMab, afterWritingLogs).toMillis());
            logger.infof( "DATM", "      Total loop processing time =  %4d ms", Duration.between(beforeMabRead, afterWritingLogs).toMillis());

            //sleep until the time step duration is complete
            LocalDateTime curTime = LocalDateTime.now();
            long elapsed = Duration.between(prevTimeStep, curTime).toMillis();
            if (elapsed < cyclePeriod_) {
                long remaining =  cyclePeriod_ - elapsed;
                logger.infof("DATM", "----- Bottom of main loop - going to sleep for %d ms", remaining);
                try {
                    Thread.sleep(remaining);
                } catch (InterruptedException e) { }
            }else {
                logger.infof("DATM", "----- Bottom of main loop - no time to sleep!  Elapsed time was %d ms", elapsed);
            }
            prevTimeStep = LocalDateTime.now();

        } //end while

        closeConnection();
    }


    /**
     * Event handler that accepts new command info from the server and makes it available for distribution.
     * @param command is the new speed command, m/s
     * @param confidence is the new confidence level in the command, range [0..100]
     */
    public void updateCommand(double command, double confidence) {
        //This method is accessed by the ServerIncomingMgr thread and updates state variables that will be
        // consumed by the sendSpeedCmdToMab method below, which is on the MainDataMgr thread, so they are
        // both synchronized to avoid contention.

        LocalDateTime readyTime = LocalDateTime.now();

        synchronized(CommandLock_) {
            LocalDateTime insideLockTime = LocalDateTime.now();
            long wait = Duration.between(readyTime, insideLockTime).toMillis();
            if (wait > 0) {
                logger.debugf("DATM", "Entry to updateCommand: waited %d ms to acquire CommandLock", wait);
            }

            //set flag to authorize continue operation because a fresh command is available
            continueOperation_ = true;

            //store data for future consumption
            serverCommand_ = command;
            serverConfidence_ = confidence;

            //indicate a new command is in place and record time of message receipt
            newCommand_.getAndSet(true);
            prevCommandTime_ = LocalDateTime.now();
        }
        logger.debugf("DATSI", "updateCommand completed.  serverCommand = %.2f", serverCommand_);
    }


    /**
     * Event handler that accepts new status info on the other vehicles in the experiment and makes it available to the UI
     * Assumes that the incoming array has no empty slots in it (i.e. all valid other vehicles are in indexes 0..N).
     * @param others is a list of all other vehicles currently in the experiment
     */
    public void updateOtherVehicleData(List<OtherVehicleStatus> others) {
        //This method updates the genUiMessage variable that is used by the sendToUi method below,
        // on different threads, so needs to be synchronized.
        logger.debugf("DATSI", "Entering updateOtherVehicleData; %d other vehicles.", others.size());

        synchronized(UiMessageLock_) {
            //add the status info to the general UI message currently under construction
            if (others.size() >= 1) {
                genUiMessage_.setV1Name(others.get(0).getName());
                genUiMessage_.setV1Auto(others.get(0).getAutomation());
            }
            if (others.size() >= 2) {
                genUiMessage_.setV2Name(others.get(1).getName());
                genUiMessage_.setV2Auto(others.get(1).getAutomation());
            }
            if (others.size() >= 3) {
                genUiMessage_.setV3Name(others.get(2).getName());
                genUiMessage_.setV3Auto(others.get(2).getAutomation());
            }
            if (others.size() >= 4) {
                genUiMessage_.setV4Name(others.get(3).getName());
                genUiMessage_.setV4Auto(others.get(3).getAutomation());
            }
        }
    }


    /**
     * terminate
     * Indicate that we are shutting down threads
     */
    public void terminate() {

        if (!bShutdown.get()) {
            bShutdown.getAndSet(true);
            //shut down the child threads as well
            if (serverInput_ != null) serverInput_.terminate();
            if (serverOutput_ != null) serverOutput_.terminate();

            //inform the parent we are shutting down
            service.destroy();
        }
    }

    ///////////////////
    // internal methods
    ///////////////////

    /**
     * Parses the incoming MAB message for data elements needed and sends all available data to the UI on the appropriate schedule
     */
    private void sendToUi(byte[] msg) {

        //if it has been long enough since previous UI message then
        long timeSincePrev = Duration.between(prevSentToUi_, LocalDateTime.now()).toMillis();
        if (timeSincePrev >= minUiDuration_) {

            //pull bytes from the message to construct the MAB UI message
            //scale & convert speed to mph
            int speedRawMetric = ((int) msg[6] & 0x000000ff) | (((int) msg[7] << 8) & 0x0000ff00);
            int speedMph = (int) ((double) speedRawMetric * 0.01 * MPH_PER_METER_PER_SEC + 0.5);

            //pull out the own vehicle automation state
            int ownAutomation = (int) msg[1];

            synchronized (UiMessageLock_) {
                //add the new MAB info to the current UI message
                genUiMessage_.setOwnName(ownVehicleName_);
                genUiMessage_.setOwnSpeed(speedMph);
                genUiMessage_.setOwnAuto(ownAutomation);

                //send the message - it may contain other values deposited from server messages as well
                service.sendUiMessage(genUiMessage_);
                prevSentToUi_ = LocalDateTime.now();

                //create a new empty message for the next update
                genUiMessage_ = new UiMessage();
            }
        }
    }


    /**
     * Ensures speed command/confidence data is realistic and assembles the message for distribution
     * to both the MAB and the UI
     */
    private void sendSpeedCmdToMab() {
        logger.debugf("DATM", "Ready to send to MAB. newCommand = %b, serverCommand = %.2f", newCommand_.get(), serverCommand_);
        LocalDateTime readyTime = LocalDateTime.now();

        synchronized (CommandLock_) {

            LocalDateTime insideLockTime = LocalDateTime.now();
            long wait = Duration.between(readyTime, insideLockTime).toMillis();
            if (wait > 0) {
                logger.debugf("DATM", "Waited %d ms to acquire CommandLock", wait);
            }

            double conf = serverConfidence_;
            boolean connected = true;

            //if no new command data from the server then
            if (!newCommand_.get()) {
                //compute degraded command confidence
                conf = degradeConfidence();

                //if confidence is below threshold, then signal MAB to abort
                if (conf < confidenceThreshold_) {
                    continueOperation_ = false;
                }

                //if previous server message is older than threshold turn off its connection flag
                long elapsed = Duration.between(prevCommandTime_, LocalDateTime.now()).toMillis();
                if (elapsed > serverDisconnectTime_) {
                    connected = false;
                }
            }
            //send the speed command and run authorization to the MAB
            sendToMab(serverCommand_, continueOperation_);

            //set the speed command, confidence level and connection status in the UI message
            // (UI will get the actual run status back from the MAB after the MAB acknowledges this command)
            synchronized (UiMessageLock_) {
                int cmd = (int) (serverCommand_ * MPH_PER_METER_PER_SEC + 0.5);
                genUiMessage_.setSpdCmd(cmd);
                genUiMessage_.setConf((int) (conf + 0.5));
                genUiMessage_.setOwnConnection(connected);
            }

            //indicate that the new command has now been consumed
            newCommand_.getAndSet(false);
        }
    }


    /**
     * Computes the degradation of the command confidence due to elapsed time since the latest server message
     * @return new confidence value [0..100]
     */
    private double degradeConfidence() {

        long elapsedTime = Duration.between(prevCommandTime_, LocalDateTime.now()).toMillis();
        double exponent = Math.min((double)elapsedTime / (double)cyclePeriod_, 100.0); //limit in case of a really long time w/o new command
        double newConf = serverConfidence_ * Math.pow((1.0 - confidenceDecay_), exponent);
        logger.debugf("DATM", "degradeConfidence: elapsedtime = %d ms, exponent = %f, newConf = %f",
                        elapsedTime, exponent, newConf);

        return newConf;
    }


    /**
     * Parses the message for data elements needed and sends to the server on the appropriate schedule
     * @param msg
     */
    private void sendToServer(byte[] msg) {
        serverOutput_.newMabMsg(msg);
    }


    /**
     * Builds a MAB command message and sends it to the MAB
     * @param speedCmd is the speed command, m/s
     * @param runAuth is the flag that authorizes continued operation
     */
    private void sendToMab(double speedCmd, boolean runAuth) {
        byte[] msg = new byte[5];

        //build the message
        msg[0] = (byte)0x83; //message type
        msg[1] = 3; //payload length
        int scaledCmd = (int)(100.0*speedCmd + 0.5);
        msg[2] = (byte)(scaledCmd & 0x000000ff);
        msg[3] = (byte)((scaledCmd >> 8) & 0x000000ff);
        msg[4] = (byte)(runAuth ? 1 : 0);

        //send it
        writeCommandToMab(msg);
    }


    /**
     * writeCommandToMab - forwards the specified message to the MAB
     * @param command formatted per MAB-Secondary ICD
     */
    public void writeCommandToMab(byte[] command)   {

        try   {
            // Send command to MAB
            DatagramPacket sendPacket = new DatagramPacket(command, command.length, mabAddress);

            mabSocket.send(sendPacket);

            logger.debug("DATM", "Sent " + command.length + " bytes to MAB Command: " + mabIp + ":" + mabUdpPort
                                                + "  Content=" + javax.xml.bind.DatatypeConverter.printHexBinary(command));
        } catch (SocketException e) {
            logger.warn("DATM", "read: socket exception ");
        } catch (IOException e) {
            logger.warn("DATM", "read: IO exception ");
        }
    }


    /**
     * closeConnection
     */
    private void closeConnection(){
        logger.info("DATM", "Stopping MAB Command thread for port: " + mabUdpPort);
        try {
            if (mabSocket != null) {
                mabSocket.close();
            }
        } catch (Exception e) {
            logger.error("DATM", "Error closing MAB Command for ports: " + mabUdpPort + ", " + e.getMessage());
        }
    }
}

