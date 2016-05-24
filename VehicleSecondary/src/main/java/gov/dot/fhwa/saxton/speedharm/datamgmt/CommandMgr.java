package gov.dot.fhwa.saxton.speedharm.datamgmt;


import gov.dot.fhwa.saxton.carmasecondary.logger.ILogger;
import gov.dot.fhwa.saxton.carmasecondary.logger.LoggerManager;

/**
 * Manages all driver commands coming from the HMI going to the MAB
 */

//This class is not used in this project, but is left here as a template for future projects
//that may use this code as a starting point.

public class CommandMgr {
    private static ILogger logger = LoggerManager.getLogger(CommandMgr.class);

    private MainDataMgr commandProcessor;

    ///// singleton management

    private CommandMgr() {
    }

    private static class CommandMgrHolder {
        private static final CommandMgr _instance = new CommandMgr();
    }

    public static CommandMgr getInstance()
    {
        return CommandMgrHolder._instance;
    }

    public void setCommandProcessor(MainDataMgr cmd)   {
        commandProcessor = cmd;
    }

    ///// workhorse methods

    //put methods here for each message that needs to be sent to the MAB.  Use the
    // MainDataMgr.writeCommandToMab() method to transmit a message.
    // Typical commands involve a stream of bytes where the first byte contains
    //      bit 7 on (indicates a user command)
    //      bits 0-6 indicate message type (application specific)
    // Second byte indicates the length of the message payload
    // Bytes 3-N is the payload: any number of bytes that are specific to the message type indicated.


}
