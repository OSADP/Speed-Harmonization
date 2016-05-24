package gov.dot.fhwa.saxton.speedharm.api.controllers;

import gov.dot.fhwa.saxton.speedharm.api.models.ExperimentManager;
import gov.dot.fhwa.saxton.speedharm.api.models.VehicleManager;
import gov.dot.fhwa.saxton.speedharm.api.objects.Experiment;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * REST endpoint controller responsible for handling requests related to Experiments.
 *
 * Allows for the registration/creation of experiments, querying of experiment status, adding vehicles to an extant
 * experiment, and deleting experiments.
 */

@RestController
public class ExperimentController {

    private Logger log = LogManager.getLogger();

    @Autowired
    private ExperimentManager experimentManager;

    @Autowired
    private VehicleManager vehicleManager;

    @RequestMapping(value="/rest/experiments", method=RequestMethod.GET)
    public List<Experiment> getExperiments() {
        return experimentManager.getExperiments();
    }

    @RequestMapping(value="/rest/experiments/{expId}", method=RequestMethod.GET)
    public Experiment getExperiment(@PathVariable String expId, HttpServletResponse response) {
        log.info("Processing GET request for experiment " + expId + "...");
        Experiment exp = experimentManager.getExperimentById(new Long(expId));

        if (exp == null) {
            log.error("Requested experiment " + expId + " not found. Returning 404...");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }

        return exp;
    }

    @RequestMapping(value="/rest/experiments", method=RequestMethod.POST)
    public List<Experiment> createExperiment(@RequestBody Experiment exp, HttpServletResponse response,
                                             UriComponentsBuilder uriB) {
        log.info("Processing new experiment creation request...");
        if (exp.getDescription() == null || exp.getDescription().equals("") || exp.getLocation() == null || exp.getLocation().equals("")) {
            log.error("Invalid JSON structure detected. Returning HTTP 400. No experiment will be created.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            exp = experimentManager.initExperiment(exp);
            log.info("Creating experiment with ID " + exp.getId() + "...");
            experimentManager.addExperiment(exp);

            // Set the Location header in the HTTP response so our client knows where to find the new experiment
            UriComponents uriComponents = uriB.path("/rest/experiments/{id}").buildAndExpand(exp.getId());
            response.setHeader("Location", uriComponents.toUriString());
        }


        return experimentManager.getExperiments();
    }

    @RequestMapping(value="/rest/experiments/{expId}", method=RequestMethod.DELETE)
    public List<Experiment> deleteExperiment(@PathVariable String expId, HttpServletResponse response) {
        log.info("Processing request to delete experiment with ID " + expId + "...");
        experimentManager.removeExperiment(new Long(expId));
        return experimentManager.getExperiments();
    }

    @RequestMapping(value="/rest/experiments/{expId}/vehicles", method=RequestMethod.POST)
    public Experiment addVehicleToExperiment(@PathVariable Long expId, @RequestBody VehicleSession veh, HttpServletResponse response) {
        log.info("Processing request to add vehicle" + veh.getId() + " to experiment" + expId + "...");
        Optional<VehicleSession> vehSess = vehicleManager.getVehicleById(veh.getId());
        Experiment exp = experimentManager.getExperimentById(expId);

        if (!vehSess.isPresent()) {
            log.error("Request vehicle does not exist. Returning 400...");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            if (exp != null) {
                experimentManager.addVehicleToExperiment(vehSess.get().getId(), expId);
            } else {
                log.error("Requested experiment does not exist. Returning 400...");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }

        return exp;
    }

    @RequestMapping(value="/rest/experiments/{expId}/vehicles/{vehId}", method=RequestMethod.DELETE)
    public Experiment removeVehicleFromExperiment(@PathVariable Long expId, @PathVariable Long vehId, HttpServletResponse response) {
        log.info("Processing request to remove vehicle " + vehId + " from experiment " + expId);

        Experiment exp = experimentManager.getExperimentById(expId);

        // Check to see if we need to return a 404
        if (exp == null) {
            log.error("Requested experiment " + expId + " not found. Returning 404...");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if (!vehicleManager.getVehicleById(vehId).isPresent()) {
            log.error("Requested vehicle " + vehId + " not found. Returning 404...");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if (!exp.getVehicleSessions().contains(vehId)) {
            log.error("Requested vehicle " + vehId + " is not a part of experiment " + expId + " . Returning 404...");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        // Lets update it and return the new object
        experimentManager.removeVehicleFromExperiment(expId, vehId);
        return experimentManager.getExperimentById(expId);
    }
}
