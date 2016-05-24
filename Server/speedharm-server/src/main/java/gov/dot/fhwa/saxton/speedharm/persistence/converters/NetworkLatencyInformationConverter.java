package gov.dot.fhwa.saxton.speedharm.persistence.converters;

import gov.dot.fhwa.saxton.speedharm.api.objects.NetworkLatencyInformation;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.NetworkLatencyInformationEntity;

import java.time.Duration;

/**
 * Class for converting between NetworkLatencyInformation <-> NetworkLatencyInformationEntity.
 */
public class NetworkLatencyInformationConverter {

    public static NetworkLatencyInformation databaseToWeb(NetworkLatencyInformationEntity ent) {
        NetworkLatencyInformation nli = new NetworkLatencyInformation();
        nli.setCorrectedTxTimestamp(ent.getCorrectedTxTimestamp());
        nli.setVehicleTxTimestamp(ent.getVehicleTxTimestamp());
        nli.setServerRxTimestamp(ent.getServerRxTimestamp());
        nli.setVehicleMeasuredNetworkLatency((ent.getVehicleMeasuredNetworkLatency() != null ? Duration.ofMillis(ent.getVehicleMeasuredNetworkLatency()) : null));

        return nli;
    }

    public static NetworkLatencyInformationEntity webToDatabase(NetworkLatencyInformation nli) {
        NetworkLatencyInformationEntity ent = new NetworkLatencyInformationEntity();
        ent.setCorrectedTxTimestamp(nli.getCorrectedTxTimestamp());
        ent.setServerRxTimestamp(nli.getServerRxTimestamp());
        ent.setVehicleMeasuredNetworkLatency((nli.getVehicleMeasuredNetworkLatency() != null ? nli.getVehicleMeasuredNetworkLatency().toMillis() : null));
        ent.setVehicleTxTimestamp(nli.getVehicleTxTimestamp());

        return ent;
    }
}
