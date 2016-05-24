package gov.dot.fhwa.saxton.speedharm;

import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleCommand;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleSession;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleStatusUpdate;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static gov.dot.fhwa.saxton.speedharm.ServerSimulatorUrls.*;

@Controller
@SpringBootApplication
public class ServerSimulator {

    private List<VehicleSession> registrations;
    private List<VehicleStatusUpdate> statuses;
    private Random prng = new Random();


    //Register a vehicle
    @RequestMapping(value= VEHICLES, method=RequestMethod.POST)
    public @ResponseBody List<VehicleSession> registerVehicle(@RequestBody VehicleSession ses) {

        System.out.println("* registerVehicle received " + ses.toString());
        registrations.add(ses);
        System.out.println("  returning list of " + registrations.size() + " registrations.");
        return registrations;
    }


    //Close registration of a vehicle
    @RequestMapping(value= VEHICLE, method=RequestMethod.DELETE)
    public @ResponseBody List<VehicleSession> unregister(@PathVariable("internalId") long internalId) {
        System.out.println("* unregister received ID = " + internalId);
        for (VehicleSession s : registrations) {
            if (s.getId() == internalId) {
                registrations.remove(s);
            }
        }
        System.out.println("  returning list of " + registrations.size() + " registrations.");
        return registrations;
    }


    //Provide the list of all vehicles in the experiment
    @RequestMapping(value= VEHICLES, method=RequestMethod.GET)
    public @ResponseBody List<VehicleSession> getVehicleList() {
        System.out.println("* getVehicleList has been invoked.");
        System.out.println("  returning list of " + registrations.size() + " registrations.");
        return registrations;
    }


    //Provide the speed command to a vehicle
    @RequestMapping(value=COMMAND, method=RequestMethod.GET)
    public @ResponseBody DeferredResult<VehicleCommand> getSpeedCommand(@PathVariable("internalId") long internalId) {
        System.out.println("* getSpeedCommand received ID = " + internalId);

        VehicleCommand cmd = new VehicleCommand();
        cmd.setId(prng.nextLong());
        cmd.setVehId(internalId); //this is our command
        //cmd.setAccel(0.8); //m/s2
        double speed = 27.0*prng.nextFloat();
        cmd.setSpeed(speed); // m/s
        double conf = (float)100.0*prng.nextFloat();
        if (conf < 40.0) conf *= 2.0;
        cmd.setCommandConfidence(conf); //this is a percentage
        cmd.setTimestamp(LocalDateTime.now());

        DeferredResult<VehicleCommand> result = new DeferredResult<>(1000L, null);
        result.setResult(cmd);

        return result;
    }


    //Get a status update from a vehicle to the server
    @RequestMapping(value=STATUS, method=RequestMethod.POST)
    public @ResponseBody VehicleStatusUpdate sendStatusUpdate(@RequestBody VehicleStatusUpdate stat,
                                                              @PathVariable Long id) {
        System.out.println("* sendStatusUpdate received " + stat.toString());

        //add it to the database
        statuses.add(stat);

        return stat;
    }


    //Provide the info of a specified vehicle
    @RequestMapping(value=VEHICLE, method=RequestMethod.GET)
    public @ResponseBody VehicleSession getVehicle(@PathVariable("internalId") long internalId) {
        System.out.println("* getVehicleStatus received ID = " + internalId);

        //find the last item in the list that matches this ID (assuming the list is chronologically ordered)
        VehicleSession ses = null;

        for (VehicleSession s : registrations) {
            if (s.getId() == internalId) {
                ses = s;
            }
        }
        System.out.println("  returning " + ses.toString());

        return ses;
    }

    // no getVehicleStatus() to retrieve status of a single vehicle for now


    ////////////////////////////////////////

    public static void main(String[] args) {
        ServerSimulator sim = new ServerSimulator();
        sim.setup();
        sim.keepAlive();
    }

    private void setup() {
        //set up a pre-existing vehicle in the list so that registration will return a multi-vehicle list
        long dummyId = 77L;
        VehicleSession dummy = new VehicleSession();
        dummy.setDescription("Dummy vehicle already registered.");
        dummy.setUniqVehId("Purple");
        dummy.setId(dummyId);
        dummy.setRegisteredAt(LocalDateTime.now());
        registrations = new ArrayList<VehicleSession>();
        registrations.add(dummy);

        //create a history for the dummy vehicle also to provide status reports for it
        VehicleStatusUpdate stat = new VehicleStatusUpdate();
        stat.setId(303882L);
        stat.setVehId(dummyId);
        stat.setSpeed(16.383);
        stat.setAccel(0.084);
        stat.setAutomatedControlState(VehicleStatusUpdate.AutomatedControlStatus.ENGAGED);
        stat.setHeading(38.58);
        stat.setLat(36.3636);
        stat.setLon(-77.202484);
        stat.setDistanceToNearestRadarObject(14.45);
        statuses = new ArrayList<VehicleStatusUpdate>();
        statuses.add(stat);

    }

    private void keepAlive() {
        int iter = 0;
        int variant = 0;
        while (registrations.size() > 0) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {}

            if (++iter > 100) {
                if (++variant > 5) {
                    System.out.println("Still here!");
                    variant = 0;
                }else {
                    System.out.println("Server simulator still alive...");
                }
                iter = 0;
            }
        }
    }
}
