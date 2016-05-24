package gov.dot.fhwa.saxton.speedharm.persistence.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Database persistence class for VehicleSpeedCommands
 */

@Entity
@Table(name = "dbo.VehicleSpeedCommands")
public class VehicleCommandEntity {
    @Id
    @Column(name = "vsc_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double speed;

    @Column(name = "command_confidence")
    private Double commandConfidence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veh_sess_id")
    private VehicleSessionEntity vehicleSessionEntity;

    private LocalDateTime timestamp;

    public VehicleCommandEntity() {
    }

    public VehicleCommandEntity(Double speed, VehicleSessionEntity vehicleSessionEntity, Double commandConfidence, LocalDateTime timestamp) {
        this.speed = speed;
        this.vehicleSessionEntity = vehicleSessionEntity;
        this.timestamp = timestamp;
        this.commandConfidence = commandConfidence;
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

    public VehicleSessionEntity getVehicleSession() {
        return vehicleSessionEntity;
    }

    public void setVehicleSession(VehicleSessionEntity vehicleSessionEntity) {
        this.vehicleSessionEntity = vehicleSessionEntity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

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

        VehicleCommandEntity that = (VehicleCommandEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (speed != null ? !speed.equals(that.speed) : that.speed != null) return false;
        if (commandConfidence != null ? !commandConfidence.equals(that.commandConfidence) : that.commandConfidence != null)
            return false;
        return !(timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (speed != null ? speed.hashCode() : 0);
        result = 31 * result + (commandConfidence != null ? commandConfidence.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VehicleCommandEntity{" +
                "id=" + id +
                ", speed=" + speed +
                ", commandConfidence=" + commandConfidence +
                ", vehicleSessionEntity=" + vehicleSessionEntity.getId() +
                ", timestamp=" + timestamp +
                '}';
    }
}
