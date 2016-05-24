package gov.dot.fhwa.saxton.speedharm.api.controllers;

import gov.dot.fhwa.saxton.speedharm.api.models.VehicleManager;
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
 * REST endpoint responsible for handling requests pertaining to vehicles and vehicle sessions.
 *
 * Suppports registration of vehicle sessions, unregistration of vehicle sessions, and querying of vehicle session
 * states.
 */

@RestController
public class VehicleController {

    private Logger log = LogManager.getLogger();

    @Autowired
    private VehicleManager vehicleManager;

    public VehicleController() {
        log.info("VehicleController instance started.");
    }

    @RequestMapping(value="/rest/vehicles", method= RequestMethod.POST)
    public List<VehicleSession> registerVehicle(@RequestBody VehicleSession veh, HttpServletResponse response,
                                                UriComponentsBuilder uriB) {
        log.info("Received vehicle registration request for vehicle " + veh);
        veh = vehicleManager.initVehicle(veh);
        vehicleManager.activateVehicle(veh);

        // Set the Location header in the HTTP response so our client knows where to find the new experiment
        UriComponents uriComponents = uriB.path("/rest/vehicles/{id}").buildAndExpand(veh.getId());
        response.setHeader("Location", uriComponents.toUriString());

        return vehicleManager.getActiveVehicles();
    }

    @RequestMapping(value="/rest/vehicles/{vehicleID}", method=RequestMethod.DELETE)
    public List<VehicleSession> unregisterVehicle(@PathVariable String vehicleID, HttpServletResponse response) {
        log.info("Attempting of unregister vehicle with ID " + vehicleID + ".");
        boolean status = vehicleManager.deactivateVehicle(Long.parseLong(vehicleID));

        if (!status) {
            log.warn("Vehicle deactivation of vehicle " + vehicleID + " unsuccessful...");
            int scConflict = HttpServletResponse.SC_CONFLICT;
            response.setStatus(scConflict);
        } else {
            log.info("Vehicle deactivation " + vehicleID + "  successful...");
        }

        return vehicleManager.getActiveVehicles();
    }

    @RequestMapping(value="/rest/vehicles/{vehicleID}", method=RequestMethod.GET)
    public VehicleSession getVehicle(@PathVariable String vehicleID, HttpServletResponse hsr) {
        Optional<VehicleSession> veh = vehicleManager.getVehicleById(Long.parseLong(vehicleID));
        if (veh.isPresent()) {
            return veh.get();
        } else {
            hsr.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    @RequestMapping(value="/rest/vehicles", method=RequestMethod.GET)
    public List<VehicleSession> getVehicles() {
        return vehicleManager.getActiveVehicles();
    }

}
