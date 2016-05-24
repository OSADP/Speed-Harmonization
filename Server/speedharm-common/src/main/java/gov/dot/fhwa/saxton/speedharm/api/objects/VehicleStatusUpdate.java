package gov.dot.fhwa.saxton.speedharm.api.objects;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents a status update from an experimental vehicle.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleStatusUpdate {

    public enum AutomatedControlStatus {
        DISENGAGED,
        ENGAGED,
        ENGAGED_BUT_IGNORING
    }

    private Long id;
    private Long vehId;
    private Double speed;
    private Double lat;
    private Double lon;
    private Double heading;
    private Double nearestRadarDist;
    private Double relativeSpeedOfNearestRadarObject;
    private Double accel;
    private AutomatedControlStatus automatedControlState;
    private NetworkLatencyInformation networkLatencyInformation;

    /**
     * Get the unique entity ID associated with this VehicleStatusUpdate.
     * @return A Long-valued ID unique to this VehicleStatusUpdate.
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the entity ID of the VehicleStatusUpdate. Can be left null by the
     * client as the server will initialize this value from its database,
     * even if the client does include a value.
     * @param id The desired ID for the VehicleStatusUpdate to have.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the speed value recorded in this VehicleStatusUpdate.
     * @return The speed recorded in this VehicleStatusUpdate in m/s.
     */
    public Double getSpeed() {
        return speed;
    }

    /**
     * Set the speed value recorded in this VehicleStatusUpdate.
     * @param speed The current speed of the vehicle in m/s.
     */
    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    /**
     * Get the latitude recorded in this VehicleStatusUpdate.
     * @return The latitude recorded in this VehicleStatusUpdate in degrees
     *         range [-90, 90].
     */
    public Double getLat() {
        return lat;
    }

    /**
     * Set the latitude recorded in this VehicleStatusUpdate.
     * @param lat The measured latitude in degrees range [-90, 90]
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * Get the longitude recorded in this VehicleStatusUpdate.
     * @return The longitude recorded in this VehicleStatusUpdate in degrees
     *         range [-180, 180].
     */
    public Double getLon() {
        return lon;
    }

    /**
     * Set the longitude recorded in this VehicleStatusUpdate.
     * @param lon The measured longitude in degrees range [-180, 180].
     */
    public void setLon(Double lon) {
        this.lon = lon;
    }

    /**
     * Get the heading recorded in this VehicleStatusUpdate.
     * @return The heading recorded in this VehicleStatusUpdate in degrees range
     *         [-180, 180]. Both -180 and 180 represent a heading of due south.
     */
    public Double getHeading() {
        return heading;
    }

    /**
     * Set the heading recorded in this VehicleStatusUpdate.
     * @param heading The measured heading in degrees range [-180, 180]. Both
     *                -180 and 180 are accepted as due south.
     */
    public void setHeading(Double heading) {
        this.heading = heading;
    }

    /**
     * Get the distance to the nearest object as detected by radar and reported
     * in this VehicleStatusUpdate.
     *
     * @return The measured distance to the nearest radar object in meters.
     */
    public Double getDistanceToNearestRadarObject() {
        return nearestRadarDist;
    }

    /**
     * Set the measured distance to the nearest object (as detected by radar) reported
     * in this VehicleStatusUpdate.
     * @param nearestRadarDist The measured distance to the nearest radar object in meters.
     */
    public void setDistanceToNearestRadarObject(Double nearestRadarDist) {
        this.nearestRadarDist = nearestRadarDist;
    }

    public Double getRelativeSpeedOfNearestRadarObject() {
        return relativeSpeedOfNearestRadarObject;
    }

    public void setRelativeSpeedOfNearestRadarObject(Double relativeSpeedOfNearestRadarObject) {
        this.relativeSpeedOfNearestRadarObject = relativeSpeedOfNearestRadarObject;
    }

    /**
     * Get the current acceleration of the vehicle as reported in this VehicleStatusUpdate.
     * @return The acceleration of the vehicle in m/s^2.
     */
    public Double getAccel() {
        return accel;
    }

    /**
     * Set the acceleration recorded in this VehicleStatusUpdate.
     * @param accel The measured acceleration in m/s^2.
     */
    public void setAccel(Double accel) {
        this.accel = accel;
    }

    /**
     * Get the state of the vehicle's automated control system represented by a
     * {@link gov.dot.fhwa.saxton.speedharm.api.objects.VehicleStatusUpdate.AutomatedControlStatus} value.
     * 0 - Disengaged/Manual
     * 1 - Automated control engaged and operational
     * 3 - Vehicle is under autonomous control but is ignoring server commands
     * @return An AutomatedControlStatus value that describes the state of the vehicle's control system
     */
    public AutomatedControlStatus getAutomatedControlState() {
        return automatedControlState;
    }

    /**
     * Set the state of the vehicle's automated control system represented by a
     * {@link gov.dot.fhwa.saxton.speedharm.api.objects.VehicleStatusUpdate.AutomatedControlStatus} value.
     * 0 - Disengaged/Manual
     * 1 - Automated control engaged and operational
     * 3 - Vehicle is under autonomous control but is ignoring server commands
     * @param automatedControlState AutomatedControlStatus value that describes the state of the vehicle's control system
     */
    public void setAutomatedControlState(AutomatedControlStatus automatedControlState) {
        this.automatedControlState = automatedControlState;
    }

    /**
     * Get the entity ID of the {@link VehicleSession} object this VehicleStatusUpdate
     * is associated with.
     * @return The Long-valued entity ID of the VehicleSession that generated this VehicleStatusUpdate.
     */
    public Long getVehId() {
        return vehId;
    }

    /**
     * Set the entity ID of the {@link VehicleSession} object this VehicleStatusUpdate
     * is associated with. The client does not need to set this, as the server will
     * re-initialize this value even if there is already data present.
     * @param vehId
     */
    public void setVehId(Long vehId) {
        this.vehId = vehId;
    }

    public NetworkLatencyInformation getNetworkLatencyInformation() {
        return networkLatencyInformation;
    }

    public void setNetworkLatencyInformation(NetworkLatencyInformation networkLatencyInformation) {
        this.networkLatencyInformation = networkLatencyInformation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleStatusUpdate that = (VehicleStatusUpdate) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (vehId != null ? !vehId.equals(that.vehId) : that.vehId != null) return false;
        if (speed != null ? !speed.equals(that.speed) : that.speed != null) return false;
        if (lat != null ? !lat.equals(that.lat) : that.lat != null) return false;
        if (lon != null ? !lon.equals(that.lon) : that.lon != null) return false;
        if (heading != null ? !heading.equals(that.heading) : that.heading != null) return false;
        if (nearestRadarDist != null ? !nearestRadarDist.equals(that.nearestRadarDist) : that.nearestRadarDist != null)
            return false;
        if (relativeSpeedOfNearestRadarObject != null ? !relativeSpeedOfNearestRadarObject.equals(that.relativeSpeedOfNearestRadarObject) : that.relativeSpeedOfNearestRadarObject != null)
            return false;
        if (accel != null ? !accel.equals(that.accel) : that.accel != null) return false;
        if (automatedControlState != that.automatedControlState) return false;
        return !(networkLatencyInformation != null ? !networkLatencyInformation.equals(that.networkLatencyInformation) : that.networkLatencyInformation != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (vehId != null ? vehId.hashCode() : 0);
        result = 31 * result + (speed != null ? speed.hashCode() : 0);
        result = 31 * result + (lat != null ? lat.hashCode() : 0);
        result = 31 * result + (lon != null ? lon.hashCode() : 0);
        result = 31 * result + (heading != null ? heading.hashCode() : 0);
        result = 31 * result + (nearestRadarDist != null ? nearestRadarDist.hashCode() : 0);
        result = 31 * result + (relativeSpeedOfNearestRadarObject != null ? relativeSpeedOfNearestRadarObject.hashCode() : 0);
        result = 31 * result + (accel != null ? accel.hashCode() : 0);
        result = 31 * result + (automatedControlState != null ? automatedControlState.hashCode() : 0);
        result = 31 * result + (networkLatencyInformation != null ? networkLatencyInformation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VehicleStatusUpdate{" +
                "id=" + id +
                ", vehId=" + vehId +
                ", speed=" + speed +
                ", lat=" + lat +
                ", lon=" + lon +
                ", heading=" + heading +
                ", nearestRadarDist=" + nearestRadarDist +
                ", relativeSpeedOfNearestRadarObject=" + relativeSpeedOfNearestRadarObject +
                ", accel=" + accel +
                ", automatedControlState=" + automatedControlState +
                ", networkLatencyInformation=" + networkLatencyInformation +
                '}';
    }
}
