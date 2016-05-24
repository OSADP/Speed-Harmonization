package gov.dot.fhwa.saxton.speedharm.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.dot.fhwa.saxton.speedharm.api.models.ExperimentManager;
import gov.dot.fhwa.saxton.speedharm.api.models.VehicleManager;
import gov.dot.fhwa.saxton.speedharm.api.objects.Experiment;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test for experiment controller
 */
public class ExperimentControllerTest {
    @InjectMocks
    private ExperimentController experimentController;

    @Mock
    private VehicleManager vehicleManagerMock;

    @Mock
    private ExperimentManager experimentManagerMock;

    private MockMvc mockMvc;

    private LocalDateTime now = LocalDateTime.now();
    private long EXP_ID_1 = (long) 1;
    private long EXP_ID_2 = (long) 2;
    private long EXP_ID_3 = (long) 3;
    private long VEH_ID_1 = (long) 1;

    private Experiment exp1;
    private Experiment exp2;
    private Experiment exp3;

    private VehicleSession veh1;
    private List<Experiment> experiments;

    private String expUrl = "/rest/experiments";

    @Before
    public void setUp() throws Exception {
        // Begin the mock injection
        MockitoAnnotations.initMocks(this);

        // Setup dummy data for mocks to use
        exp1 = new Experiment();
        exp1.setId(EXP_ID_1);
        exp1.setDescription("Test experiment 1");
        exp1.setStartTime(now);
        exp1.setLocation("JUnit tests");
        exp1.setVehicleSessions(new ArrayList<>());

        exp2 = new Experiment();
        exp2.setId(EXP_ID_2);
        exp2.setDescription("Test experiment 2");
        exp2.setStartTime(now);
        exp2.setLocation("JUnit tests");
        exp2.setVehicleSessions(new ArrayList<>());


        exp3 = new Experiment();
        exp3.setId(EXP_ID_3);
        exp3.setDescription("Test experiment 3");
        exp3.setStartTime(now);
        exp3.setLocation("JUnit tests");
        exp3.setVehicleSessions(new ArrayList<>());

        experiments = new ArrayList<>();
        experiments.add(exp1);
        experiments.add(exp2);
        experiments.add(exp3);

        veh1 = new VehicleSession();
        veh1.setId(VEH_ID_1);
        veh1.setDescription("JUnit Test Vehicle");
        veh1.setRegisteredAt(now);
        veh1.setUniqVehId("JSDKFL:SDFJKL");

        // Mock the experiment manager to return good data
        when(experimentManagerMock.getExperiments()).thenReturn(experiments);
        when(experimentManagerMock.getExperimentById(EXP_ID_1)).thenReturn(exp1);
        when(experimentManagerMock.getExperimentById(EXP_ID_2)).thenReturn(exp2);
        when(experimentManagerMock.getExperimentById(EXP_ID_3)).thenReturn(exp3);

        // Also mock the vehicle manager
        when(vehicleManagerMock.getVehicleById(VEH_ID_1)).thenReturn(Optional.of(veh1));

        // Setup the standalone controller instance
        mockMvc = MockMvcBuilders.standaloneSetup(experimentController).build();
    }

    @Test
    public void testGetExperiments() throws Exception {
        mockMvc.perform(get(expUrl))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[2].id", is(3)));
    }

    @Test
    public void testGetExperiment() throws Exception {
        mockMvc.perform(get(expUrl + "/" + EXP_ID_1))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Test experiment 1")));
    }

    @Test
    public void testCreateExperiment() throws Exception {
        Experiment exp4 = new Experiment();
        exp4.setDescription("Test experiment 4");
        exp4.setLocation("Junit Tests");


        Experiment exp4Init = new Experiment();
        exp4Init.setDescription("Test experiment 4");
        exp4Init.setLocation("Junit Tests");
        exp4Init.setId((long) 4);
        exp4Init.setStartTime(now);
        exp4Init.setVehicleSessions(null);

        when(experimentManagerMock.initExperiment(exp4)).thenReturn(exp4Init);

        // Serialize object to JSON
        ObjectMapper om = new ObjectMapper();
        String expString = om.writeValueAsString(exp4);

        mockMvc.perform(post(expUrl).contentType(MediaType.APPLICATION_JSON).content(expString))
                .andExpect(status().is2xxSuccessful());

        verify(experimentManagerMock).addExperiment(exp4Init);
    }

    @Test
    public void testDeleteExperiment() throws Exception {
        mockMvc.perform(delete(expUrl + "/1"))
                .andExpect(status().is2xxSuccessful());

        verify(experimentManagerMock).removeExperiment((long) 1);
    }

    @Test
    public void testAddVehicleToExperiment() throws Exception {
        ObjectMapper om = new ObjectMapper();
        String content = om.writeValueAsString(veh1);

        mockMvc.perform(post(expUrl + "/1/vehicles").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().is2xxSuccessful());

        verify(experimentManagerMock).addVehicleToExperiment(veh1.getId(), exp1.getId());
    }

    @Test
    public void testRemoveVehicleFromExperiment() throws Exception {
        ObjectMapper om = new ObjectMapper();
        String content = om.writeValueAsString(veh1);

        mockMvc.perform(delete(expUrl + "/1/vehicles/1"))
                .andExpect(status().is2xxSuccessful());

        verify(experimentManagerMock).removeVehicleFromExperiment(veh1.getId(), exp1.getId());
    }
}