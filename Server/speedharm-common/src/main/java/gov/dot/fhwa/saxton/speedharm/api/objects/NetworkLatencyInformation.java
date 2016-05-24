package gov.dot.fhwa.saxton.speedharm.api.objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Add on message (or really could be standalone) used for storing computed values related to network latency and clock
 * skew between a client vehicle and the infrastructure server.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NetworkLatencyInformation {

    // Timing information
    private LocalDateTime vehicleTxTimestamp;
    private LocalDateTime serverRxTimestamp;
    private Duration vehicleMeasuredNetworkLatency;

    private LocalDateTime correctedTxTimestamp;

    public LocalDateTime getVehicleTxTimestamp() {
        return vehicleTxTimestamp;
    }

    /**
     * Set the timestamp at which the vehicle transmitted this message.
     * @param vehicleTxTimestamp
     */
    public void setVehicleTxTimestamp(LocalDateTime vehicleTxTimestamp) {
        this.vehicleTxTimestamp = vehicleTxTimestamp;
    }

    public LocalDateTime getServerRxTimestamp() {
        return serverRxTimestamp;
    }

    /**
     * Set the timestamp at which the server received this message.
     * @param serverRxTimestamp
     */
    public void setServerRxTimestamp(LocalDateTime serverRxTimestamp) {
        this.serverRxTimestamp = serverRxTimestamp;
    }

    public Duration getVehicleMeasuredNetworkLatency() {
        return vehicleMeasuredNetworkLatency;
    }

    /**
     * Report the measured and computed network latency between the vehicle and the server.
     * @param vehicleMeasuredNetworkLatency
     */
    public void setVehicleMeasuredNetworkLatency(Duration vehicleMeasuredNetworkLatency) {
        this.vehicleMeasuredNetworkLatency = vehicleMeasuredNetworkLatency;
    }

    public LocalDateTime getCorrectedTxTimestamp() {
        return correctedTxTimestamp;
    }

    /**
     * Set the clock-skew corrected timestamp of transmission (in server time).
     * @param correctedTxTimestamp
     */
    public void setCorrectedTxTimestamp(LocalDateTime correctedTxTimestamp) {
        this.correctedTxTimestamp = correctedTxTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkLatencyInformation that = (NetworkLatencyInformation) o;

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

    @Override
    public String toString() {
        return "NetworkLatencyInformation{" +
                "vehicleTxTimestamp=" + vehicleTxTimestamp +
                ", serverRxTimestamp=" + serverRxTimestamp +
                ", vehicleMeasuredNetworkLatency=" + vehicleMeasuredNetworkLatency +
                ", correctedTxTimestamp=" + correctedTxTimestamp +
                '}';
    }
}
