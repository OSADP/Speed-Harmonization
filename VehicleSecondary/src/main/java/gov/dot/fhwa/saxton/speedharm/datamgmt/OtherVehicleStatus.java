package gov.dot.fhwa.saxton.speedharm.datamgmt;

/**
 * Represents the state of a given vehicle other than "own vehicle"
 */
public class OtherVehicleStatus {

    private String  name_ = null;
    private int     automation_ = 0;
    private long    serverId_ = 0; //this is the server's internal ID of the other vehicle, not our own vehicle

    public OtherVehicleStatus(long id, String name) {
        serverId_ = id;
        name_ = name;
    }

    public String getName() { return name_; }

    public void setName(String name) { name_ = name; }

    public void setAutomation(int auto) { automation_ = auto; }

    public int getAutomation() { return automation_; }

    public long getId() { return serverId_; }
}
