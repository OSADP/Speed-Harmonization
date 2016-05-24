package gov.dot.fhwa.saxton.speedharm.api.objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Class representing a group of vehicles participating in an experiment together.
 * Not all vehicles are necessarily using the same algorithm. This class primarily
 * exists to unify data for post-processing.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Experiment {
    private Long id;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Long> vehicleSessions;

    /**
     * Get the unique entity ID associated with this Experiment instance.
     * @return A Long-valued entity ID unique among all Experiment instances.
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get a descriptive String associated with this experiment instance. This
     * value is purely for human readability.
     * @return A String describing the experiment.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get a string describing the location this experiment takes place in. This
     * value is purely for human readability.
     * @return A String describing the location of the experiment.
     */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Get the timestamp of creation for this experiment.
     * @return The millisecond-resolution timestamp of creation for this experiment.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Get the timestamp of completion for this experiment instance. This value
     * is automatically generated at end of experiment, when all vehicles have
     * disconnected or the experiment has been manually terminated.
     * @return The millsecond-resolution timestamp of completion, if it exists.
     *         Null, o.w.
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Get the entity IDs of the {@link VehicleSession}s associted with this experiment
     * instance.
     * @return A List containing the Long-valued entity ID's of the associated vehicles.
     */
    public List<Long> getVehicleSessions() {
        return vehicleSessions;
    }

    public void setVehicleSessions(List<Long> vehicleSessions) {
        this.vehicleSessions = vehicleSessions;
    }

    /**
     * Add vehicle with id vehId to this experiment object. If the vehicle is already part of this experiment,
     * do nothing.
     * @param vehId The id of the vehicle to add
     */
    public void addVehicleToExperiment(Long vehId) {
        if (!vehicleSessions.contains(vehId)) {
            vehicleSessions.add(vehId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Experiment that = (Experiment) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
        return !(vehicleSessions != null ? !vehicleSessions.equals(that.vehicleSessions) : that.vehicleSessions != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (vehicleSessions != null ? vehicleSessions.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Experiment{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", vehicleSessions=" + vehicleSessions +
                '}';
    }
}
