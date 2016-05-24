package gov.dot.fhwa.saxton.speedharm.api.objects;

import java.time.LocalDateTime;

/**
 * Class to contain data about an information update from an {@link Infrastructure}
 * device.
 */
public class InfrastructureStatusUpdate {
    private Integer zone; // Zone ID
    private Double speed; // Speed in m/s
    private Integer volume; // Volume in TODO: FIGURE OUT VOLUME UNITS
    private Double occupancy; // Occupancy in TODO: FIGURE OUT OCCUPANCY UNITS
    private Infrastructure infrastructure; // Associated infrastructure object
    private LocalDateTime timestamp; // Timestamp corresponding to this object, ms resolution.

    public Integer getZone() {
        return zone;
    }

    public void setZone(Integer zone) {
        this.zone = zone;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Double getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(Double occupancy) {
        this.occupancy = occupancy;
    }

    public Infrastructure getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(Infrastructure infrastructure) {
        this.infrastructure = infrastructure;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InfrastructureStatusUpdate that = (InfrastructureStatusUpdate) o;

        if (zone != null ? !zone.equals(that.zone) : that.zone != null) return false;
        if (speed != null ? !speed.equals(that.speed) : that.speed != null) return false;
        if (volume != null ? !volume.equals(that.volume) : that.volume != null) return false;
        if (occupancy != null ? !occupancy.equals(that.occupancy) : that.occupancy != null) return false;
        if (infrastructure != null ? !infrastructure.equals(that.infrastructure) : that.infrastructure != null)
            return false;
        return !(timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null);

    }

    @Override
    public int hashCode() {
        int result = zone != null ? zone.hashCode() : 0;
        result = 31 * result + (speed != null ? speed.hashCode() : 0);
        result = 31 * result + (volume != null ? volume.hashCode() : 0);
        result = 31 * result + (occupancy != null ? occupancy.hashCode() : 0);
        result = 31 * result + (infrastructure != null ? infrastructure.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InfrastructureStatusUpdate{" +
                "zone=" + zone +
                ", speed=" + speed +
                ", volume=" + volume +
                ", occupancy=" + occupancy +
                ", infrastructure=" + infrastructure +
                ", timestamp=" + timestamp +
                '}';
    }
}
