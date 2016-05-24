package gov.dot.fhwa.saxton.speedharm.persistence.repositories;

import gov.dot.fhwa.saxton.speedharm.persistence.entities.VehicleSessionEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data JPA Repository for VehicleSessionEntity Objects
 */
public interface VehicleSessionRepository extends CrudRepository<VehicleSessionEntity, Long> {

    VehicleSessionEntity findByUniqVehId(String uniqVehId);
}
