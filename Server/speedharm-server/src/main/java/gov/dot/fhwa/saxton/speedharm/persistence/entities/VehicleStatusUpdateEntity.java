package gov.dot.fhwa.saxton.speedharm.persistence.entities;

import gov.dot.fhwa.saxton.speedharm.api.objects.VehicleStatusUpdate;

import javax.persistence.*;

/**
 * JPA Database Persistence class for Vehicle Status Updates
 */

@Entity
@Table(name = "dbo.VehicleStatusUpdates")
public class VehicleStatusUpdateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "vsu_id", nullable = false)
    private Long id;

    private Double speed;
    private Double lat;
    private Double lon;
    private Double heading;

    @Column(name = "distance_2_nearest_radar_object", nullable = true)
    private Double distanceToNearestObject;

    @Column(name = "relative_speed_of_nearest_radar_target", nullable = true)
    private Double relativeSpeedOfNearestRadarTarget;
    private Double acceleration;

    @Column(name = "automated_control_status", nullable = false)
    private VehicleStatusUpdate.AutomatedControlStatus automatedControlEngaged;

    @Embedded
    private NetworkLatencyInformationEntity networkLatencyInformation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veh_sess_id")
    private VehicleSessionEntity vehicleSession;

    public VehicleStatusUpdateEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    public Double getDistanceToNearestObject() {
        return distanceToNearestObject;
    }

    public void setDistanceToNearestObject(Double distanceToNearestObject) {
        this.distanceToNearestObject = distanceToNearestObject;
    }

    public Double getRelativeSpeedOfNearestRadarTarget() {
        return relativeSpeedOfNearestRadarTarget;
    }

    public void setRelativeSpeedOfNearestRadarTarget(Double relativeSpeedOfNearestRadarTarget) {
        this.relativeSpeedOfNearestRadarTarget = relativeSpeedOfNearestRadarTarget;
    }

    public Double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Double acceleration) {
        this.acceleration = acceleration;
    }

    public VehicleStatusUpdate.AutomatedControlStatus getAutomatedControlEngaged() {
        return automatedControlEngaged;
    }

    public void setAutomatedControlEngaged(VehicleStatusUpdate.AutomatedControlStatus automatedControlEngaged) {
        this.automatedControlEngaged = automatedControlEngaged;
    }

    public VehicleSessionEntity getVehicleSession() {
        return vehicleSession;
    }

    public void setVehicleSession(VehicleSessionEntity vehicleSession) {
        this.vehicleSession = vehicleSession;
    }

    public NetworkLatencyInformationEntity getNetworkLatencyInformation() {
        return networkLatencyInformation;
    }

    public void setNetworkLatencyInformation(NetworkLatencyInformationEntity networkLatencyInformation) {
        this.networkLatencyInformation = networkLatencyInformation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleStatusUpdateEntity that = (VehicleStatusUpdateEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (speed != null ? !speed.equals(that.speed) : that.speed != null) return false;
        if (lat != null ? !lat.equals(that.lat) : that.lat != null) return false;
        if (lon != null ? !lon.equals(that.lon) : that.lon != null) return false;
        if (heading != null ? !heading.equals(that.heading) : that.heading != null) return false;
        if (distanceToNearestObject != null ? !distanceToNearestObject.equals(that.distanceToNearestObject) : that.distanceToNearestObject != null)
            return false;
        if (relativeSpeedOfNearestRadarTarget != null ? !relativeSpeedOfNearestRadarTarget.equals(that.relativeSpeedOfNearestRadarTarget) : that.relativeSpeedOfNearestRadarTarget != null)
            return false;
        if (acceleration != null ? !acceleration.equals(that.acceleration) : that.acceleration != null) return false;
        if (automatedControlEngaged != that.automatedControlEngaged) return false;
        if (networkLatencyInformation != null ? !networkLatencyInformation.equals(that.networkLatencyInformation) : that.networkLatencyInformation != null)
            return false;
        return !(vehicleSession != null ? !vehicleSession.equals(that.vehicleSession) : that.vehicleSession != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (speed != null ? speed.hashCode() : 0);
        result = 31 * result + (lat != null ? lat.hashCode() : 0);
        result = 31 * result + (lon != null ? lon.hashCode() : 0);
        result = 31 * result + (heading != null ? heading.hashCode() : 0);
        result = 31 * result + (distanceToNearestObject != null ? distanceToNearestObject.hashCode() : 0);
        result = 31 * result + (relativeSpeedOfNearestRadarTarget != null ? relativeSpeedOfNearestRadarTarget.hashCode() : 0);
        result = 31 * result + (acceleration != null ? acceleration.hashCode() : 0);
        result = 31 * result + (automatedControlEngaged != null ? automatedControlEngaged.hashCode() : 0);
        result = 31 * result + (networkLatencyInformation != null ? networkLatencyInformation.hashCode() : 0);
        result = 31 * result + (vehicleSession != null ? vehicleSession.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VehicleStatusUpdateEntity{" +
                "id=" + id +
                ", speed=" + speed +
                ", lat=" + lat +
                ", lon=" + lon +
                ", heading=" + heading +
                ", distanceToNearestObject=" + distanceToNearestObject +
                ", relativeSpeedOfNearestRadarTarget=" + relativeSpeedOfNearestRadarTarget +
                ", acceleration=" + acceleration +
                ", automatedControlEngaged=" + automatedControlEngaged +
                ", networkLatencyInformation=" + networkLatencyInformation +
                ", vehicleSession=" + vehicleSession +
                '}';
    }
}
