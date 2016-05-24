package gov.dot.fhwa.saxton.speedharm.persistence.repositories;

import gov.dot.fhwa.saxton.speedharm.persistence.entities.AlgorithmEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data JPA Repository for AlgorithmEntity Objects
 */
public interface AlgorithmRepository extends CrudRepository<AlgorithmEntity, Long> {
}
