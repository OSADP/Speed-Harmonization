package gov.dot.fhwa.saxton.speedharm.api.objects;

//import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;


/*
 * Domain/API object representing an experimental vehicle.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleSession {
    private Long id;
    private String uniqVehId;
    private String description;
    private Long expId;
    private Long algoId;
    private LocalDateTime registeredAt;
    private LocalDateTime unregisteredAt;

    /**
     * Get the unique entity ID for this VehicleSession object.
     * @return A Long-valued entity ID unique among all VehicleSession instances.
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get a descriptive string for this VehicleSession. This string can describe
     * the physical characteristics of the vehicle or of its role in the
     * experiment.
     *
     * @return A String describing the vehicle.
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the entity ID of the experiment this vehicle participates in.
     * @return The Long-valued ID of the associated experiment, if it exists.
     *         Null, o.w.
     */
    public Long getExpId() {
        return expId;
    }

    public void setExpId(Long expId) {
        this.expId = expId;
    }

    /**
     * Get the timestamp of registration for this VehicleSession.
     * @return The millisecond-resolution timestamp for this vehicle session.
     */
    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    /**
     * Get the timestamp of unregistration for this VehicleSession.
     * @return The millisecond-resolution of unregistration for this vehicle session if it exists,
     *         Null, o.w.
     */
    public LocalDateTime getUnregisteredAt() {
        return unregisteredAt;
    }

    public void setUnregisteredAt(LocalDateTime unregisteredAt) {
        this.unregisteredAt = unregisteredAt;
    }

    /**
     * Get a unique ID for the physical vehicle associated with this VehicleSession.
     * @return A String that should only be used in associated with this physical vehicle.
     */
    public String getUniqVehId() {
        return uniqVehId;
    }

    public void setUniqVehId(String uniqVehId) {
        this.uniqVehId = uniqVehId;
    }

    public Long getAlgoId() {
        return algoId;
    }

    public void setAlgoId(Long algoId) {
        this.algoId = algoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleSession that = (VehicleSession) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (uniqVehId != null ? !uniqVehId.equals(that.uniqVehId) : that.uniqVehId != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (expId != null ? !expId.equals(that.expId) : that.expId != null) return false;
        if (algoId != null ? !algoId.equals(that.algoId) : that.algoId != null) return false;
        if (registeredAt != null ? !registeredAt.equals(that.registeredAt) : that.registeredAt != null) return false;
        return !(unregisteredAt != null ? !unregisteredAt.equals(that.unregisteredAt) : that.unregisteredAt != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (uniqVehId != null ? uniqVehId.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (expId != null ? expId.hashCode() : 0);
        result = 31 * result + (algoId != null ? algoId.hashCode() : 0);
        result = 31 * result + (registeredAt != null ? registeredAt.hashCode() : 0);
        result = 31 * result + (unregisteredAt != null ? unregisteredAt.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VehicleSession{" +
                "id=" + id +
                ", uniqVehId='" + uniqVehId + '\'' +
                ", description='" + description + '\'' +
                ", expId=" + expId +
                ", algoId=" + algoId +
                ", registeredAt=" + registeredAt +
                ", unregisteredAt=" + unregisteredAt +
                '}';
    }
}
