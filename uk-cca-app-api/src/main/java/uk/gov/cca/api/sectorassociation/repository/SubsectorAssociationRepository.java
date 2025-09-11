package uk.gov.cca.api.sectorassociation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;

@Repository
@Transactional(readOnly = true)
public interface SubsectorAssociationRepository extends JpaRepository<SubsectorAssociation, Long> {
    
    Optional<SubsectorAssociation> findByName(String name);

	List<SubsectorAssociation> findAllBySectorAssociationId(Long sectorAssociationId);
    
}
