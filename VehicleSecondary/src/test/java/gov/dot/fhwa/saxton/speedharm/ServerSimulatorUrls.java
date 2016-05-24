package gov.dot.fhwa.saxton.speedharm;

public class ServerSimulatorUrls {

    private static final String BASE_URL = "http://localhost:8081/rest";

    public static final String VEHICLES = BASE_URL + "/vehicles";
    public static final String VEHICLE = BASE_URL + "/vehicles/{internalId}";
    public static final String COMMAND = BASE_URL + "/commands/{internalId}";
    public static final String STATUS = BASE_URL + "/status/{internalId}";
}
