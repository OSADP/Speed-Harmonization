package gov.dot.fhwa.saxton.speedharm.api.objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;


/**
 * Represents a command returned to an experimental vehicle.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleCommand {
    private Long id;
    private Double speedCommand;
    private Long vehId;
    private Double commandConfidence;
    private LocalDateTime timestamp;

    /**
     * Get the unique entity ID for this VehicleCommand
     * @return A Long-valued entity ID unique amongst VehicleCommand instances.
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get the recommended speedCommand value for this VehicleCommand.
     * @return The speedCommand recommended by the algorithm, in m/s
     */
    public Double getSpeed() {
        return speedCommand;
    }

    public void setSpeed(Double speedCommand) {
        this.speedCommand = speedCommand;
    }

    /**
     * Get the entity ID of the {@link VehicleSession} object associated with this VehicleCommand.
     * @return The Long-valued entity id of the associated VehicleSesion
     */
    public Long getVehId() {
        return vehId;
    }

    public void setVehId(Long vehId) {
        this.vehId = vehId;
    }

    /**
     * Get the timestamp of creation for this VehicleCommand.
     * @return The millisecond resolution timestamp of creation for this VehicleCommand.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get the confidence level for this VehicleCommand.
     * @return A percentage value in the range [0, 100] representing the confidence of the
     *         algorithm in the accuracy of this VehicleCommand.
     */
    public Double getCommandConfidence() {
        return commandConfidence;
    }

    public void setCommandConfidence(Double commandConfidence) {
        this.commandConfidence = commandConfidence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleCommand that = (VehicleCommand) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (speedCommand != null ? !speedCommand.equals(that.speedCommand) : that.speedCommand != null) return false;
        if (vehId != null ? !vehId.equals(that.vehId) : that.vehId != null) return false;
        if (commandConfidence != null ? !commandConfidence.equals(that.commandConfidence) : that.commandConfidence != null)
            return false;
        return !(timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (speedCommand != null ? speedCommand.hashCode() : 0);
        result = 31 * result + (vehId != null ? vehId.hashCode() : 0);
        result = 31 * result + (commandConfidence != null ? commandConfidence.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VehicleCommand{" +
                "id=" + id +
                ", speedCommand=" + speedCommand +
                ", vehId=" + vehId +
                ", commandConfidence=" + commandConfidence +
                ", timestamp=" + timestamp +
                '}';
    }
}
