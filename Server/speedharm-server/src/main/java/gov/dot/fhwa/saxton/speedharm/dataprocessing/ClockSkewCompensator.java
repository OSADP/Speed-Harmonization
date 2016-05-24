package gov.dot.fhwa.saxton.speedharm.dataprocessing;

import gov.dot.fhwa.saxton.speedharm.api.objects.NetworkLatencyInformation;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for managing timestamps in light of network latency and clock skew
 */
public class ClockSkewCompensator {

    private Map<Long, Duration> offsets = new HashMap<>();

    /**
     * Add a new report of network latency data to this TimingManager computations
     * @param vehId The vehicle id associated with this NetworkLatencyManager
     * @param nli The {@link NetworkLatencyInformation}
     */
    public void processNewLatencyInformation(Long vehId, NetworkLatencyInformation nli) {
        // Validate that all the necessary fields are present
        if (nli.getVehicleTxTimestamp() == null ||
            nli.getServerRxTimestamp() == null ||
            nli.getVehicleMeasuredNetworkLatency() == null) {
            return;
        }

        // Otherwise, compute our clock offset, taking into account the network latency
        Duration clockSkew = Duration.between(nli.getVehicleTxTimestamp(), nli.getServerRxTimestamp())
                                     .minus(nli.getVehicleMeasuredNetworkLatency());

        offsets.put(vehId, clockSkew); // TODO: Use a moving average?
    }

    /**
     * If we've got enough data to know the clock skew, offset the timestamp. Otherwise don't modify it at all.
     *
     * @param vehId The id of the vehicle that generated the timestamp
     * @param time The timestamp to adjust (measured by that vehicle)
     * @return The corresponding timestamp in server-time (if possible), otherwise an unmodified timestamp.
     */
    public LocalDateTime correctForClockSkew(Long vehId, LocalDateTime time) {
        Duration offset = Duration.ofMillis(0);

        if (offsets.containsKey(vehId)) {
            offset = offsets.get(vehId);
        }

        return time.plus(offset);
    }

}
