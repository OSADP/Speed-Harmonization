package gov.dot.fhwa.saxton.speedharm.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

/**
 * Database persistence class for network latency information.
 * Implemented as a JPA embeddable so that it can be used in multiple other entities.
 */

@Embeddable
public class NetworkLatencyInformationEntity {
    @Column(name = "vehicle_tx_timestamp")
    private LocalDateTime vehicleTxTimestamp;

    @Column(name = "server_rx_timestamp")
    private LocalDateTime serverRxTimestamp;

    @Column(name = "measured_latency")
    private Long vehicleMeasuredNetworkLatency;

    @Column(name = "corrected_tx_timestamp")
    private LocalDateTime correctedTxTimestamp;

    @Override
    public String toString() {
        return "NetworkLatencyInformationEntity{" +
                "vehicleTxTimestamp=" + vehicleTxTimestamp +
                ", serverRxTimestamp=" + serverRxTimestamp +
                ", vehicleMeasuredNetworkLatency=" + vehicleMeasuredNetworkLatency +
                ", correctedTxTimestamp=" + correctedTxTimestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkLatencyInformationEntity that = (NetworkLatencyInformationEntity) o;

        if (vehicleTxTimestamp != null ? !vehicleTxTimestamp.equals(that.vehicleTxTimestamp) : that.vehicleTxTimestamp != null)
            return false;
        if (serverRxTimestamp != null ? !serverRxTimestamp.equals(that.serverRxTimestamp) : that.serverRxTimestamp != null)
            return false;
        if (vehicleMeasuredNetworkLatency != null ? !vehicleMeasuredNetworkLatency.equals(that.vehicleMeasuredNetworkLatency) : that.vehicleMeasuredNetworkLatency != null)
            return false;
        return !(correctedTxTimestamp != null ? !correctedTxTimestamp.equals(that.correctedTxTimestamp) : that.correctedTxTimestamp != null);

    }

    @Override
    public int hashCode() {
        int result = vehicleTxTimestamp != null ? vehicleTxTimestamp.hashCode() : 0;
        result = 31 * result + (serverRxTimestamp != null ? serverRxTimestamp.hashCode() : 0);
        result = 31 * result + (vehicleMeasuredNetworkLatency != null ? vehicleMeasuredNetworkLatency.hashCode() : 0);
        result = 31 * result + (correctedTxTimestamp != null ? correctedTxTimestamp.hashCode() : 0);
        return result;
    }

    public LocalDateTime getCorrectedTxTimestamp() {
        return correctedTxTimestamp;
    }

    public void setCorrectedTxTimestamp(LocalDateTime correctedTxTimestamp) {
        this.correctedTxTimestamp = correctedTxTimestamp;
    }

    public LocalDateTime getVehicleTxTimestamp() {
        return vehicleTxTimestamp;
    }

    public void setVehicleTxTimestamp(LocalDateTime vehicleTxTimestamp) {
        this.vehicleTxTimestamp = vehicleTxTimestamp;
    }

    public LocalDateTime getServerRxTimestamp() {
        return serverRxTimestamp;
    }

    public void setServerRxTimestamp(LocalDateTime serverRxTimestamp) {
        this.serverRxTimestamp = serverRxTimestamp;
    }

    public Long getVehicleMeasuredNetworkLatency() {
        return vehicleMeasuredNetworkLatency;
    }

    public void setVehicleMeasuredNetworkLatency(Long vehicleMeasuredNetworkLatency) {
        this.vehicleMeasuredNetworkLatency = vehicleMeasuredNetworkLatency;
    }

}
