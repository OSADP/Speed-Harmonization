package gov.dot.fhwa.saxton.speedharm.api.models;

import gov.dot.fhwa.saxton.speedharm.algorithms.AbstractAlgorithm;
import gov.dot.fhwa.saxton.speedharm.algorithms.IAlgorithm;
import gov.dot.fhwa.saxton.speedharm.api.objects.AlgorithmInformation;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleCommand;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleSession;
import gov.dot.fhwa.saxton.speedharm.executive.algorithms.AlgorithmExecutor;
import gov.dot.fhwa.saxton.speedharm.persistence.converters.AlgorithmConverter;
import gov.dot.fhwa.saxton.speedharm.persistence.converters.VehicleCommandConverter;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.AlgorithmEntity;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.VehicleCommandEntity;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.VehicleSessionEntity;
import gov.dot.fhwa.saxton.speedharm.persistence.repositories.AlgorithmRepository;
import gov.dot.fhwa.saxton.speedharm.persistence.repositories.VehicleCommandRepository;
import gov.dot.fhwa.saxton.speedharm.persistence.repositories.VehicleSessionRepository;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class responsible for managing algorithms and information about algorithms and algorithm instances.
 */
public class AlgorithmManager {

    private static final String ROOT_PACKAGE = "gov.dot.fhwa.saxton";
    private Map<Long, IAlgorithm> algorithmInstances = new HashMap<>();
    private Map<Long, IAlgorithm> instanceVehicleAssignments = new HashMap<>();
    private Map<Long, ArrayList<VehicleCommand>> commandHistory = new HashMap<>();
    private Map<Long, DeferredResult<VehicleCommand>> pendingCommandRequests = new HashMap<>();

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private VehicleCommandRepository vehicleCommandRepository;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private AlgorithmRepository algorithmRepository;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private VehicleSessionRepository vehicleSessionRepository;

    @Autowired
    private InfrastructureManager infrastructureManager;

    @Autowired
    private AlgorithmExecutor executor;

    @Autowired
    private VehicleManager vehicleManager;

    @Autowired
    private VehicleStatusManager vehicleStatusManager;

    /**
     * Search the classpath starting at ROOT_PACKAGE to find any instances of GenericAlgorithm.
     *
     * @return A list of class objects guaranteed to be a subclass of GenericAlgorithm.
     */
    public List<Class> getAvailableAlgorithms() {
        Reflections reflections = new Reflections(ROOT_PACKAGE);
        Set<Class<? extends IAlgorithm>> algoTypes = reflections.getSubTypesOf(IAlgorithm.class);

        algoTypes.remove(AbstractAlgorithm.class);

        return new ArrayList<>(algoTypes);
    }

    /**
     * Assign a vehicle to an algorithm for status updates and commands.
     *
     * @param vehId The ID of the vehicle to be assigned
     * @param algoId The ID of the algorithm to assign it to
     * @return True if the assignment was successful (if both the vehicle and the algo IDs are valid)
     */
    public boolean assignVehicleToAlgorithm(Long vehId, Long algoId) {
        Optional<VehicleSession> veh = vehicleManager.getVehicleById(vehId);
        Optional<IAlgorithm> algo = getAlgorithm(algoId);

        if (veh.isPresent() && algo.isPresent()) {
            algo.get().initVehicle(veh.get());
            instanceVehicleAssignments.put(vehId, algo.get());

            // Save algorithm assignment foreign key in vehicle entity
            VehicleSessionEntity vse = vehicleSessionRepository.findOne(vehId);
            AlgorithmEntity ae = algorithmRepository.findOne(algoId);
            vse.setAlgorithm(ae);

            vehicleSessionRepository.save(vse);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove a vehicle from association with an algorithm instance.
     *
     * @param vehId The ID of the vehilce to unassign
     * @param algoId The ID of the algorithm to assign it from.
     * @return True if the removal was successful, false o.w.
     */
    public boolean removeVehicleFromAlgorithm(Long vehId, Long algoId) {
        Optional<VehicleSession> veh = vehicleManager.getVehicleById(vehId);
        Optional<IAlgorithm> algo = getAlgorithm(algoId);

        if (veh.isPresent() && algo.isPresent()) {
            algo.get().terminateVehicle(veh.get());
            instanceVehicleAssignments.remove(veh.get().getId());

            // Close out the algorithm instance if there are no more vehicles assigned to it.
            if (algo.get().getNumCurrentVehicles() == 0) {
                // Stop the algorithm
                algo.get().stop();

                // Record the stop time for this instance
                AlgorithmEntity ent = algorithmRepository.findOne(algoId);
                ent.setEndTime(LocalDateTime.now());
                algorithmRepository.save(ent);
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the algorithm instance to which a particular vehicle is assigned.
     *
     * @param vehicleId The vehicle ID of the vehicle in question
     * @return An Optional containing the algorithm instance the vehicle is assigned to, if it exists, or empty if it
     * does not.
     */
    public Optional<IAlgorithm> getAlgorithmForVehicle(Long vehicleId) {
        return Optional.ofNullable(instanceVehicleAssignments.get(vehicleId));
    }

    /**
     * Returns the algorithm object for the specified instance ID
     * @param id The Long valued instance ID of the desired algorithm
     * @return An Optional containing the Generic Algorithm object if it exists.
     */
    public Optional<IAlgorithm> getAlgorithm(Long id) {
        return Optional.ofNullable(algorithmInstances.get(id));
    }

    /**
     * Create a new instance of the Algorithm with the specified class name, then spawn an executor thread and let it
     * run.
     *
     * @param algo1 An AlgorithmInformation instance containing the data needed to instantiate a new instance
     * @return A Long ID associated with the new algorithm instance
     * @throws ClassNotFoundException If the string does not uniquely identify a class that implements GenericAlgorithm
     * @throws InstantiationException If there's an error in instantiating the desired algorithm
     */
    @Transactional(rollbackFor = InstantiationException.class)
    public Long spawnAlgorithmInstance(AlgorithmInformation algo1) throws ClassNotFoundException, InstantiationException {
        // Check if the requested name matches an algorithm known to the system
        List<Class> algorithms = getAvailableAlgorithms();

        String name = algo1.getClassName();

        Class selected = null;
        for (Class algo : algorithms) {
           if (algo.getName().equals(name)) {
               selected = algo;
           }
        }

        if (selected == null) {
            throw new ClassNotFoundException(name + " is not an algorithm in the current namespace!");
        }

        // Pass off new instance to algorithm executor
        long newId = -1;
        try {
            Constructor<?> ctor = selected.getConstructor();
            IAlgorithm ga = (IAlgorithm) ctor.newInstance();

            ga.registerOutputCallback(this::handleAlgorithmOutput);


            // Initialize the algo in the database and use the new ID number
            AlgorithmInformation ai = initAlgorithm(algo1, ga);
            newId = ai.getId();

            algorithmInstances.put(newId, ga);

            // Hook the algorithm up to the infrastructure data
            infrastructureManager.registerAlgorithmForUpdates(ga);

            // Put new instance in Executor
            executor.addInstance(newId, ga);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new InstantiationException("Unable to instantiate class: " + name + ".");
        }

        return newId;
    }

    /**
     * Get a list of previously issued commands for a vehicle.
     * @param vehId The ID of the vehicle in question
     * @return A list of {@link VehicleCommand} objects for that vehicle
     */
    public List<VehicleCommand> getCommandHistory(Long vehId) {
        return commandHistory.get(vehId);
    }

    /**
     * Get a list of the last n previously issued commands for a vehicle.
     * @param vehId The ID of the vehicle in question
     * @param numEntries The number of most recent entries to return
     * @return A list of the numEntries most recent {@link VehicleCommand} objects for that vehicle
     */
    public List<VehicleCommand> getCommandHistory(Long vehId, int numEntries) {
        List<VehicleCommand> commands = commandHistory.get(vehId);

        if (commands.size() > numEntries) {
            // Consider optimizing if insertion at front of list is inefficient
            return commands.subList(0, numEntries);
        } else {
            return commands;
        }
    }

    /**
     * Callback for recording algorithm output prior to ensuring the correct vehicle receives it.
     * @param vsc
     */
    private void handleAlgorithmOutput(VehicleCommand vsc) {
        vsc = initVehicleCommand(vsc);

        // Check to see if we have an outstanding long-poll request and
        // satisfy it we do.
        if (pendingCommandRequests.containsKey(vsc.getVehId())) {
            pendingCommandRequests.get(vsc.getVehId()).setResult(vsc);
        }

        // Then add the command to the general command history
        if (commandHistory.containsKey(vsc.getVehId())) {
            commandHistory.get(vsc.getVehId()).add(0, vsc);
        } else {
            ArrayList<VehicleCommand> commands = new ArrayList<>();
            commands.add(vsc);
            commandHistory.put(vsc.getVehId(), commands);
        }
    }

    private VehicleCommand initVehicleCommand(VehicleCommand vsc) {
        // Allow the ID field to be auto-generated, 0 or null triggers the database auto-increment
        vsc.setId(null);

        // Find the VehicleSession that corresponds to this VehicleCommand, we'll need it to establish the foreign key relationship
        VehicleSessionEntity vse = vehicleSessionRepository.findOne(vsc.getVehId());

        // Convert the web object to a database entity object
        VehicleCommandEntity vce = VehicleCommandConverter.webToDatabase(vsc, vse);

        // Save the database entity in the databse and get the updated copy of it (with auto-incremented ID)
        vce = vehicleCommandRepository.save(vce);

        // Convert the updated database entry back to web object, then return it.
        return VehicleCommandConverter.databaseToWeb(vce);
    }

    /**
     * Get all currently running instances of IAlgorithm managed by this manager.
     * @return A List containing {@link AlgorithmInformation} instances describing the active algorithms
     */
    public List<AlgorithmInformation> getActiveAlgorithmInstances() {
        return algorithmInstances.entrySet().stream()
                .map(AlgorithmConverter::databaseToWeb)
                .collect(Collectors.toList());
    }

    /**
     * Initialize a partial {@link AlgorithmInformation} object received from a client
     * @param partial A partially (or fully) instantiated AlgorithmInformation object
     * @return An intialized AlgorithmInformation instance ready to be used by other modules.
     */
    public AlgorithmInformation initAlgorithm(AlgorithmInformation partial, IAlgorithm algo) {
        // Init the fields
        partial.setId(null);
        partial.setStartTime(LocalDateTime.now());
        partial.setEndTime(null);
        partial.setVersionString(algo.getAlgorithmVersion());

        // Save to database
        AlgorithmEntity ae = AlgorithmConverter.webToDatabase(partial); // Convert to database entity
        ae = algorithmRepository.save(ae); // Persist to database and get updated data

        // Convert back to web object and return
        return AlgorithmConverter.databaseToWeb(ae);
    }

    /**
     * Request that this AlgorithmManager track a running long-poll request for data
     * from a client.
     *
     * @param result The DeferredResult instance to put data in when it becomes available
     * @param vehicleId The ID of the vehicle requesting data.
     */
    public void addPendingCommandRequest(DeferredResult<VehicleCommand> result, Long vehicleId) {
        pendingCommandRequests.put(vehicleId, result);

        result.onCompletion(() -> pendingCommandRequests.remove(vehicleId));
    }
}
