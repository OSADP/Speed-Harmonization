package gov.dot.fhwa.saxton.speedharm.persistence.repositories;

import gov.dot.fhwa.saxton.speedharm.persistence.entities.VehicleStatusUpdateEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data JPA Repository for VehicleStatusUpdateEntity Objects
 */
public interface VehicleStatusUpdateRepository extends CrudRepository<VehicleStatusUpdateEntity, Long> {
}
