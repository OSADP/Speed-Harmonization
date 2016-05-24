package gov.dot.fhwa.saxton.speedharm.api.controllers;

import gov.dot.fhwa.saxton.speedharm.api.models.VehicleManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

/**
 * Unit tests for AlgorithmController
 */
public class AlgorithmControllerTest {

    @InjectMocks
    private AlgorithmController algorithmController;

    @Mock
    private VehicleManager vehicleManagerMock;

    private MockMvc mockMvc;

    private Date now = new Date();

    private String algoUrl = "/rest/algorithms";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAlgorithms() throws Exception {

    }

    @Test
    public void testGetAlgorithms1() throws Exception {

    }

    @Test
    public void testGetCommandHistory() throws Exception {

    }

    @Test
    public void testGetSingleCommand() throws Exception {

    }

    @Test
    public void testCreateNewAlgorithmInstance() throws Exception {

    }

    @Test
    public void testAddVehicleToAlgorithm() throws Exception {

    }

    @Test
    public void testRemoveVehicleFromAlgorithm() throws Exception {

    }
}