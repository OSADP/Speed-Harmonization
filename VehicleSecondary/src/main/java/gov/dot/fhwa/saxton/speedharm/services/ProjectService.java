package gov.dot.fhwa.saxton.speedharm.services;

import gov.dot.fhwa.saxton.carmasecondary.IConsumerInitializer;
import gov.dot.fhwa.saxton.carmasecondary.consumers.dsrc.MabDsrcProcessor;
import gov.dot.fhwa.saxton.carmasecondary.consumers.dsrc.ObuDsrcProcessor;
import gov.dot.fhwa.saxton.carmasecondary.consumers.gps.GpsScheduledMessageConsumer;
import gov.dot.fhwa.saxton.carmasecondary.logger.ILogger;
import gov.dot.fhwa.saxton.carmasecondary.logger.LogEntry;
import gov.dot.fhwa.saxton.carmasecondary.logger.LoggerManager;
import gov.dot.fhwa.saxton.carmasecondary.speedfile.SpeedCmdProcessor;
import gov.dot.fhwa.saxton.carmasecondary.utils.AppConfig;
import gov.dot.fhwa.saxton.carmasecondary.utils.IAppConfig;
import gov.dot.fhwa.saxton.speedharm.datamgmt.MainDataMgr;
import gov.dot.fhwa.saxton.speedharm.ui.UiMessage;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

@Service("ProjectService")
public class ProjectService implements DisposableBean {
    private static ILogger logger = LoggerManager.getLogger(ProjectService.class);

    private IAppConfig appConfig = AppConfig.getInstance();

    @Autowired
    private SimpMessagingTemplate template; // = new SimpMessagingTemplate(new ExecutorSubscribableChannel());

    SpeedCmdProcessor speedCmdProcessor;
    ObuDsrcProcessor obuDsrcProcessor;
    MabDsrcProcessor mabDsrcProcessor;

    GpsScheduledMessageConsumer gpsConsumer;

    @Autowired
    MainDataMgr dataMgr;

    public void start() throws Exception {
        setLogLevel();
        LoggerManager.setRecordData(true);

        // initialize the speed profile capability (must be done before initializing gps consumer)
        initSpeedCmdProcessor();

        // allow running of components via configuration
        if ( appConfig.getIntValue("dsrc.enable") == 1 )   {
            initDsrcProcessor();
        }

        if ( appConfig.getIntValue("gps.enable") == 1 )   {
            initGpsConsumer();
        }

        if ( appConfig.getIntValue("command.enable") == 1 )   {
            initDataMgr();
        }
    }

    protected void initGpsConsumer()   {
        gpsConsumer = new GpsScheduledMessageConsumer();
        gpsConsumer.initialize(speedCmdProcessor);
        IConsumerInitializer gpsInitializer = gpsConsumer.getInitializer();

        Boolean bInit;
        try   {
            bInit = gpsInitializer.call();
            logger.debug(ILogger.TAG_GPS, "--- initGpsConsumer: ready to schedule messages.");
            if (bInit)   {
                gpsConsumer.sendScheduleMessages();
            }
            else   {
                // a consumer failed to initialize, stop the app
                logger.error("GPS", "GPS failed to initialize, please review the log");
            }
        }
        catch(Exception e)   {
            logger.error("GPS", "Error initializing GPS handshaking: " + e.getMessage());
            try { LoggerManager.writeToDisk(); } catch(Exception e2)   { logger.info(ILogger.TAG_GPS, "Error writing to log file:" + e2.getMessage()); }
            System.exit(1);
        }
    }

    protected void initDsrcProcessor() {

        int udpTimeout = appConfig.getIntValue("udp.timeout");
        int mabDsrcInport = appConfig.getIntValue("mab.dsrc.inport");
        int mabDsrcOutport = appConfig.getIntValue("mab.dsrc.outport");

        //due to port budget limitations on the MAB, we may need to consolidate the in & out ports there;
        // no such problem talking to the OBU.
        try {
            DatagramSocket mabInSocket = new DatagramSocket(mabDsrcInport);  //will be closed by MabDsrcProcessor or ObuDsrcProcessor
            mabInSocket.setSoTimeout(udpTimeout);
            DatagramSocket mabOutSocket = null;
            if (mabDsrcInport == mabDsrcOutport) {
                mabOutSocket = mabInSocket;
            }else {
                mabOutSocket = new DatagramSocket(mabDsrcOutport);  //will be closed by MabDsrcProcessor or ObuDsrcProcessor
                mabOutSocket.setSoTimeout(udpTimeout);
            }

            mabDsrcProcessor = new MabDsrcProcessor();
            mabDsrcProcessor.initialize(mabInSocket);

            obuDsrcProcessor = new ObuDsrcProcessor();
            obuDsrcProcessor.initialize(mabOutSocket);
            logger.infof(ILogger.TAG_EXECUTOR, "Established MAB sockets on ports %d (in), %d (out)", mabDsrcInport, mabDsrcOutport);

        } catch (SocketException e) {
            //stop the app if catching an exception when initialing the MABDsrcProcessor and obuDsrcProcessor
            logger.error(ILogger.TAG_EXECUTOR, "Unexpected System Exit due to unable to open MAB socket(s) for DSRC: "
                    + e.toString());
            System.exit(1);
        }
    }

    protected void initDataMgr()   {
        //dataMgr = new MainDataMgr();
        try {
            dataMgr.initialize();
        } catch (Exception e) {
            logger.error("DATM", "Error initializing main data manager: " + e.toString());
            System.exit(1);
        }
    }

    /**
     * Initializes the speed profile capability
     */
    protected void initSpeedCmdProcessor(){
        try{
            speedCmdProcessor = new SpeedCmdProcessor(appConfig);
            speedCmdProcessor.initialize();

        } catch (Exception e) {
            logger.error("SPD-FL", "Error initializing the speed profile: " + e.toString());
            try {
                LoggerManager.writeToDisk();
            } catch (Exception e1){
                System.out.println("Error writing to log file: " + e1.getMessage());
            }
            //stop the app if catching an exception when initialing the speedFileManager
            System.exit(1);
        }
    }

    /**
     * sends a message to the Javascript UI code
     * @param uiMessage
     */
    public synchronized void sendUiMessage(UiMessage uiMessage) {
        logger.info(ILogger.TAG_DVI,  "Send to client: " + uiMessage.toString());
        try {
            template.convertAndSend("/topic/dvitopic", uiMessage);
        }catch (Exception e) {
            logger.warnf(ILogger.TAG_DVI, "Failed to send message to HMI client. Message=%s, Exception=%s", uiMessage.toString(), e.getMessage());
        }

        try   {
            LoggerManager.writeToDisk();
        }
        catch(IOException ioe)   {
            System.err.println("Error writing log to disk: " + ioe.getMessage());
        }
    }


    @Override
    public void destroy()   {

        if (obuDsrcProcessor != null)  obuDsrcProcessor.terminate();
        if (mabDsrcProcessor != null)  mabDsrcProcessor.terminate();
        if (gpsConsumer != null)  gpsConsumer.terminate();
        if (dataMgr != null) dataMgr.terminate();

        try   {
            Thread.sleep(100);
        }
        catch(Exception e) {};
        logger.info(ILogger.TAG_EXECUTOR,  "Destroying bean ProjectService via lifecycle destroy().");
    }

    /**
     * Set min log level
     *
     * If not configured or configured incorrectly, uses DEBUG
     */
    public void setLogLevel()   {
        String logLevel = appConfig.getProperty("log.level");

        LogEntry.Level enumLevel = null;

        try   {
            enumLevel = LogEntry.Level.valueOf(logLevel.toUpperCase());
        }
        catch(Exception e)   {
            logger.warn("EXEC", "log.level value improperly configured: " + logLevel);
            enumLevel = LogEntry.Level.DEBUG;
        }

        LoggerManager.setMinOutputToWrite(enumLevel);
    }
}
