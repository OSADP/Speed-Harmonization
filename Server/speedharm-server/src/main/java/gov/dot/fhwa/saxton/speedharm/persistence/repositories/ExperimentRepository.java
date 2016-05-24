package gov.dot.fhwa.saxton.speedharm.persistence.repositories;

import gov.dot.fhwa.saxton.speedharm.persistence.entities.ExperimentEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data JPA Repository for ExperimentEntity Objects
 */
public interface ExperimentRepository extends CrudRepository<ExperimentEntity, Long> {
}
