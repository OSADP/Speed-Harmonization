package gov.dot.fhwa.saxton.speedharm.persistence.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Database object for Experiments
 */

@Entity
@Table(name = "dbo.Experiments")
public class ExperimentEntity {
    @Id
    @Column(name = "exp_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;
    private String location;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = true)
    private LocalDateTime endTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "experiment")
    private List<VehicleSessionEntity> vehicleSessions;

    public ExperimentEntity() {
    }

    public ExperimentEntity(String description, String location, LocalDateTime startTime, LocalDateTime endTime, List<VehicleSessionEntity> vehicleSessions) {
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.vehicleSessions = vehicleSessions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<VehicleSessionEntity> getVehicleSessions() {
        return vehicleSessions;
    }

    public void setVehicleSessions(List<VehicleSessionEntity> vehicleSessions) {
        this.vehicleSessions = vehicleSessions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExperimentEntity that = (ExperimentEntity) o;

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
        return "ExperimentEntity{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", vehicleSessions=" + vehicleSessions +
                '}';
    }
}
