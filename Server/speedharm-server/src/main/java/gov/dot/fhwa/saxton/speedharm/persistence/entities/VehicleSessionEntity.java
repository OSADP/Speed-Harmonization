package gov.dot.fhwa.saxton.speedharm.persistence.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DatabaseEntity class for Vehicle Session objects.
 */

@Entity
@Table(name = "dbo.VehicleSessions")
public class VehicleSessionEntity {
    @Id
    @Column(name="veh_sess_id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(name = "uniq_veh_id")
    private String uniqVehId;

    private String description;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "unregistered_at")
    private LocalDateTime unregisteredAt;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="exp_id")
    private ExperimentEntity experiment;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="algo_id")
    private AlgorithmEntity algorithm;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vehicleSession")
    private List<VehicleStatusUpdateEntity> statusUpdates;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "vehicleSessionEntity")
    private List<VehicleCommandEntity> speedCommands;

    public VehicleSessionEntity() {
    }

    public VehicleSessionEntity(String uniqVehId, String description, LocalDateTime registeredAt, LocalDateTime unregisteredAt, ExperimentEntity experiment, AlgorithmEntity algorithm, List<VehicleStatusUpdateEntity> statusUpdates, List<VehicleCommandEntity> speedCommands) {
        this.uniqVehId = uniqVehId;
        this.description = description;
        this.registeredAt = registeredAt;
        this.unregisteredAt = unregisteredAt;
        this.experiment = experiment;
        this.algorithm = algorithm;
        this.statusUpdates = statusUpdates;
        this.speedCommands = speedCommands;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqVehId() {
        return uniqVehId;
    }

    public void setUniqVehId(String uniqVehId) {
        this.uniqVehId = uniqVehId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public LocalDateTime getUnregisteredAt() {
        return unregisteredAt;
    }

    public void setUnregisteredAt(LocalDateTime unregisteredAt) {
        this.unregisteredAt = unregisteredAt;
    }

    public ExperimentEntity getExperiment() {
        return experiment;
    }

    public void setExperiment(ExperimentEntity experiment) {
        this.experiment = experiment;
    }

    public AlgorithmEntity getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(AlgorithmEntity algorithm) {
        this.algorithm = algorithm;
    }

    public List<VehicleStatusUpdateEntity> getStatusUpdates() {
        return statusUpdates;
    }

    public void setStatusUpdates(List<VehicleStatusUpdateEntity> statusUpdates) {
        this.statusUpdates = statusUpdates;
    }

    public List<VehicleCommandEntity> getSpeedCommands() {
        return speedCommands;
    }

    public void setSpeedCommands(List<VehicleCommandEntity> speedCommands) {
        this.speedCommands = speedCommands;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleSessionEntity that = (VehicleSessionEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (uniqVehId != null ? !uniqVehId.equals(that.uniqVehId) : that.uniqVehId != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (registeredAt != null ? !registeredAt.equals(that.registeredAt) : that.registeredAt != null) return false;
        if (unregisteredAt != null ? !unregisteredAt.equals(that.unregisteredAt) : that.unregisteredAt != null)
            return false;
        if (experiment != null ? !experiment.equals(that.experiment) : that.experiment != null) return false;
        if (algorithm != null ? !algorithm.equals(that.algorithm) : that.algorithm != null) return false;
        if (statusUpdates != null ? !statusUpdates.equals(that.statusUpdates) : that.statusUpdates != null)
            return false;
        return !(speedCommands != null ? !speedCommands.equals(that.speedCommands) : that.speedCommands != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (uniqVehId != null ? uniqVehId.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (registeredAt != null ? registeredAt.hashCode() : 0);
        result = 31 * result + (unregisteredAt != null ? unregisteredAt.hashCode() : 0);
        result = 31 * result + (experiment != null ? experiment.hashCode() : 0);
        result = 31 * result + (algorithm != null ? algorithm.hashCode() : 0);
        result = 31 * result + (statusUpdates != null ? statusUpdates.hashCode() : 0);
        result = 31 * result + (speedCommands != null ? speedCommands.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VehicleSessionEntity{" +
                "id=" + id +
                ", uniqVehId='" + uniqVehId + '\'' +
                ", description='" + description + '\'' +
                ", registeredAt=" + registeredAt +
                ", unregisteredAt=" + unregisteredAt +
                ", experiment=" + (experiment != null ? experiment.getId() : null) +
                ", algorithm=" + (algorithm != null ? algorithm.getId() : null) +
                '}';
    }
}

