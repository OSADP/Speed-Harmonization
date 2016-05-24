package gov.dot.fhwa.saxton.speedharm.api.controllers;

import gov.dot.fhwa.saxton.speedharm.algorithms.IAlgorithm;
import gov.dot.fhwa.saxton.speedharm.api.models.AlgorithmManager;
import gov.dot.fhwa.saxton.speedharm.api.models.VehicleManager;
import gov.dot.fhwa.saxton.speedharm.api.models.VehicleStatusManager;
import gov.dot.fhwa.saxton.speedharm.api.objects.AlgorithmInformation;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleCommand;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleSession;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleStatusUpdate;
import gov.dot.fhwa.saxton.speedharm.persistence.converters.AlgorithmConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller responsible for handling requests associated with Algorithm objects.
 *
 * Supports querying available algorithm types, currently running algorithm instances, adding a vehicle to an algorithm,
 * removing a vehicle from an algorithm, and creating a new algorithm instance.
 */

@RestController
@PropertySource("classpath:/algorithms.properties")
public class AlgorithmController {
    private Logger log = LogManager.getLogger();

    @Value("${algorithms.polling_timeout}")
    private long LONG_POLLING_TIMEOUT;

   @Autowired
    private AlgorithmManager algorithmManager;

    @Autowired
    private VehicleStatusManager vehicleStatusManager;

    @Autowired
    private VehicleManager vehicleManager;

    @RequestMapping(value="/rest/algorithms", method=RequestMethod.GET)
    public List<AlgorithmInformation> getAlgorithms() {
        return algorithmManager.getActiveAlgorithmInstances();
    }

    @RequestMapping(value = "/rest/commands/{vehId}", method = RequestMethod.GET, params = {"num"})
    public List<VehicleCommand> getCommandHistory(@PathVariable Long vehId, @RequestParam("num") int numCommands,
                                                  HttpServletResponse response) {
        log.info("Getting command history for vehicle " + vehId);

        if (vehicleManager.getVehicleById(vehId).isPresent()) {
            log.info("Successfully retrieved command history for vehicle " + vehId);
            return algorithmManager.getCommandHistory(vehId, numCommands);
        } else {
            log.info("Vehicle " + vehId + " not found. Sending 404.");
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * A long polling method to get the next vehicle command from the server. A client should
     * create a poll request and then wait until timeout (in which case it should request again)
     * or until the poll request returns with data. In the event of a timeout this method will
     * return a null entry.
     *
     * @param vehId The vehicle to get data for
     * @return A {@link DeferredResult} instance waiting to recieve data.
     */
    @RequestMapping(value = "/rest/commands/{vehId}", method = RequestMethod.GET)
    public DeferredResult<VehicleCommand> getSingleCommand(@PathVariable Long vehId, HttpServletResponse response) {
        log.info("Processing long-poll request for vehicle " + vehId);

        if (vehicleManager.getVehicleById(vehId).isPresent()) {
            DeferredResult<VehicleCommand> result = new DeferredResult<>(LONG_POLLING_TIMEOUT, null);
            result.onCompletion(() -> log.info("Responding to long poll request with " + (VehicleCommand) result.getResult()));
            result.onTimeout(() -> log.warn("Long-poll request timeout, no command generated for " + vehId));
            algorithmManager.addPendingCommandRequest(result, vehId);

            return result;
        } else {
            log.info("Vehicle " + vehId + " not found. Sending 404.");
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @RequestMapping(value = "/rest/algorithms", method = RequestMethod.POST)
    public List<AlgorithmInformation> createNewAlgorithmInstance(@RequestBody AlgorithmInformation algo,
                                                                 HttpServletResponse response,
                                                                 UriComponentsBuilder uriB) {
        log.info("Creating new algorithm instance based on request " + algo);

        try {
            Long id = algorithmManager.spawnAlgorithmInstance(algo);
            log.info("Successfully spawned new algorithm " + algo);

            // Set the Location header in the HTTP response so our client knows where to find the new algorithm
            UriComponents uriComponents = uriB.path("/rest/algorithms/{id}").buildAndExpand(id);
            response.setHeader("Location", uriComponents.toUriString());
        } catch (ClassNotFoundException | InstantiationException e) {
            log.error("Error instantiating algorithm " + algo, e);
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } catch (IOException e1) {
                e.printStackTrace();
            }
        }



        return algorithmManager.getActiveAlgorithmInstances();
    }

    @RequestMapping(value = "/rest/algorithms/{algoId}/vehicles", method = RequestMethod.POST)
    public AlgorithmInformation addVehicleToAlgorithm(@PathVariable Long algoId, @RequestBody VehicleSession veh,
                                                      HttpServletResponse response) {
        log.info("Processing request to add vehicle " + veh + " to algorithm " + algoId);
        Long vehId = veh.getId(); // Pull the ID out of the VehicleSession object, that's all we care about.

        // Add vehicle to algorithm
        if (algorithmManager.assignVehicleToAlgorithm(vehId, algoId)) {
            // Assignment successfull, return the new object.
            log.info("Successfully added vehicle" + veh + " to algorithm " + algoId);

            // Get the new algorithm data, convert it, and return it as our response.
            Optional<IAlgorithm> changed = algorithmManager.getAlgorithm(algoId);
            return AlgorithmConverter.databaseToWeb(changed.get()); // Convert from IAlgorithm to AlgorithmInformation
        } else {
            // Either the algorithm or the vehicle is missing, return a 404 and call it a day.
            log.warn("Requested algorithm " + algoId + "and/or vehicle " + vehId + " not found.");

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @RequestMapping(value = "/rest/algorithms/{algoId}/vehicles/{vehId}", method = RequestMethod.DELETE)
    public AlgorithmInformation removeVehicleFromAlgorithm(@PathVariable Long algoId, @PathVariable Long vehId,
                                                           HttpServletResponse response) {
        log.info("Processing request to remove vehicle " + vehId + " from algorithm " + algoId);
        if (algorithmManager.removeVehicleFromAlgorithm(vehId, algoId)) {
            // Deletion successful, return the new object
            log.info("Successfully removed vehicle " + vehId + " from algorithm " + algoId);

            Optional<IAlgorithm> changed = algorithmManager.getAlgorithm(algoId);
            return AlgorithmConverter.databaseToWeb(changed.get());
        } else {
            // Either the algorithm or the vehicle is missing, return a 404 and call it a day.
            log.warn("Requested algorithm " + algoId + " and/or vehicle " + vehId +" not found.");

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @RequestMapping(value = "/rest/status/{vehId}", method = RequestMethod.POST)
    public VehicleStatusUpdate updateVehicleStatus(@RequestBody VehicleStatusUpdate vsu, @PathVariable Long vehId,
                                                   HttpServletResponse hsr) {
        log.info("Processing vehicle status update request for " + vsu);

        // Validate the existence of the vehicle
        Optional<VehicleSession> vehicleSession = vehicleManager.getVehicleById(vehId);
        if (vehicleSession.isPresent()) {
            // It's good, we'll accept it and record it.
            VehicleStatusUpdate initialized = vehicleStatusManager.initVehicleStatusUpdate(vsu, vehId);
            log.info("Recording vehicle status update " + initialized);
            vehicleStatusManager.processNewStatusUpdate(initialized);
            return initialized;
        } else {
            // No such vehicle, reject it and send 404
            log.warn("Vehicle " + vehId + " not found");
            try {
                hsr.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    @RequestMapping(value = "/rest/status/{vehId}", method = RequestMethod.GET)
    public VehicleStatusUpdate getVehicleStatus(@PathVariable Long vehId, HttpServletResponse hsr) {
        log.info("Processing vehicle status request for " + vehId);
        if (vehicleManager.getVehicleById(vehId).isPresent()) {
            return vehicleStatusManager.getVehicleStatuses(vehId, 1).get(0);
        } else {
            log.info("Vehicle " + vehId + " not found. Sending 404.");
            try {
                hsr.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @RequestMapping(value = "/rest/status/{vehId}", method = RequestMethod.GET, params = {"num"})
    public List<VehicleStatusUpdate> getVehicleStatuses(@PathVariable Long vehId, @RequestParam int numStatuses,
                                                        HttpServletResponse hsr) {
        log.info("Processing vehicle status request for " + vehId);
        if (vehicleManager.getVehicleById(vehId).isPresent()) {
            return vehicleStatusManager.getVehicleStatuses(vehId, numStatuses);
        } else {
            log.info("Vehicle " + vehId + " not found. Sending 404.");
            try {
                hsr.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

