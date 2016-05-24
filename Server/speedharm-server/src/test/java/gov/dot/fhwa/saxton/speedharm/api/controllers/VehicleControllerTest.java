package gov.dot.fhwa.saxton.speedharm.api.controllers;

import gov.dot.fhwa.saxton.speedharm.api.models.VehicleManager;
import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleSession;
import gov.dot.fhwa.saxton.speedharm.executive.main.StolInfrastructureServer;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests functionality of VehicleController
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StolInfrastructureServer.class)
@WebAppConfiguration
public class VehicleControllerTest extends TestCase {

    private MockMvc mockMvc;

    private LocalDateTime now = LocalDateTime.now();

    private long VEH_ID_1 = (long) 1;
    private long VEH_ID_2 = (long) 2;

    @InjectMocks
    private VehicleController vehicleController;

    @Mock
    VehicleManager vehicleManagerMock;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();

        vehicleManagerMock.deleteAllActiveVehicles();
        VehicleSession vhs = new VehicleSession();
        vhs.setId(VEH_ID_1);
        vhs.setExpId(null);
        vhs.setDescription("Unit Test Vehicle 1");
        vhs.setRegisteredAt(now);
        vhs.setUniqVehId("3BT0324");

        ArrayList<VehicleSession> sessions = new ArrayList<>();
        sessions.add(vhs);

        when(vehicleManagerMock.getActiveVehicles()).thenReturn(sessions);
        when(vehicleManagerMock.getVehicleById(VEH_ID_1)).thenReturn(Optional.of(vhs));

    }

    @Test
    public void testGetVehicles() throws Exception {
        mockMvc.perform(get("/rest/vehicles"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    public void testGetSingleVehicle() throws Exception {
        mockMvc.perform(get("/rest/vehicles/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Unit Test Vehicle 1")));
    }

    @Test
    public void testAddVehicle() throws Exception {
        VehicleSession vhs = new VehicleSession();
        vhs.setDescription("Unit Test Vehicle 2");
        vhs.setUniqVehId("4BT0325");
        vhs.setId(VEH_ID_2);
        vhs.setRegisteredAt(now);

        when(vehicleManagerMock.getVehicleById(VEH_ID_2)).thenReturn(Optional.of(vhs));

        mockMvc.perform(get("/rest/vehicles/2"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.description", is("Unit Test Vehicle 2")))
                .andExpect(jsonPath("$.uniqVehId", is("4BT0325")));
    }
}
