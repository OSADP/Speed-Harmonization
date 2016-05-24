package gov.dot.fhwa.saxton.speedharm.persistence.repositories;

import gov.dot.fhwa.saxton.speedharm.persistence.entities.VehicleCommandEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data JPA Repository for VehicleCommandEntity Objects
 */
public interface VehicleCommandRepository extends CrudRepository<VehicleCommandEntity, Long> {
}
