package gov.dot.fhwa.saxton.speedharm.persistence.repositories;

import gov.dot.fhwa.saxton.speedharm.api.models.InfrastructureManager;
import gov.dot.fhwa.saxton.speedharm.persistence.entities.RTMSStatusUpdateEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA Repository for RTMS Status Updates
 */
public interface RTMSStatusUpdateRepository extends CrudRepository<RTMSStatusUpdateEntity, Long> {

    /**
     * Find all RTMSStatusUpdateEntities with a timestamp after the passed parameter. This
     * will be our primary means of interacting with this table, as it allows us to pull in
     * only data we have not seen, allowing us to then manage it internally in the server
     * itself rather than in the db.
     *
     * No changes should be saved to database since we only need to get the updates that occurred
     * since our last query and pass them into the {@link InfrastructureManager}
     * @param rtmsName The string identifier of the RTMS unit in question
     * @param timestamp The timestamp to find data after, exclusively.
     * @return A list of updates that have occurred after that timestamp
     */
    List<RTMSStatusUpdateEntity> findByRtmsNameAndTimestampAfter(String rtmsName, LocalDateTime timestamp);
}
