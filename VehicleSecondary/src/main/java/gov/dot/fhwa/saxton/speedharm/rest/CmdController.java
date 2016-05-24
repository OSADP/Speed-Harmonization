package gov.dot.fhwa.saxton.speedharm.rest;

import gov.dot.fhwa.saxton.carmasecondary.logger.ILogger;
import gov.dot.fhwa.saxton.carmasecondary.logger.LoggerManager;
import gov.dot.fhwa.saxton.carmasecondary.utils.AppConfig;
import gov.dot.fhwa.saxton.carmasecondary.utils.IAppConfig;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * CmdController class is the controller that handles user commands, retrieving them from the web UI and acting on them.
 *
 * Note: in the speed harm project there are no user commands (UI is read-only).  So this class has been stripped down
 * to a skeleton. For examples of how to use it refer to the lane merge project.
 */

@RestController
public class CmdController {

    IAppConfig appConfig = AppConfig.getInstance();

    private static ILogger logger = LoggerManager.getLogger(CmdController.class);

    /**
     * setParameters - allows the Javascript to pass datamgmt values into the Java server.
     * @return - response message that can be used by the Javascript for status feedback.
     */
    @RequestMapping("/setParameters")
    public AjaxResponse setParameters(@RequestParam(value="arg1", required=true) int arg1)   {
        logger.warn("REST", "Java setParameters called, but method contains no code.");
        return new AjaxResponse(true, "setParameters took no action!");
    }

    /**
     * logUiEvent - allows the Javascript UI code to add a message to the Java server log file.
     * @param eventDescrip - description of the event
     * @return - response message that can be used by the Javascript for status feedback.
     */
    @RequestMapping("/logUiEvent")
    public AjaxResponse logUiEvent(@RequestParam(value="eventDescrip", required=true) String eventDescrip) {
        String statusMessage = "Logging UI event: " + eventDescrip;

        logger.info("REST", " ");
        logger.info("REST", "########## UI Event: " + eventDescrip);
        logger.info("REST", " ");

        return new AjaxResponse(true, statusMessage);
    }

}
