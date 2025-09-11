package uk.gov.cca.api.sectorassociation.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface SectorAssociationSchemeRepository extends JpaRepository<SectorAssociationScheme, Long> {
    
    @EntityGraph(value = "sector-association-scheme-graph", type = EntityGraph.EntityGraphType.FETCH)
    List<SectorAssociationScheme> findSectorAssociationSchemesBySectorAssociationId(Long id);
    
    @EntityGraph(value = "sector-association-scheme-graph", type = EntityGraph.EntityGraphType.FETCH)
    Optional<SectorAssociationScheme> findSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion(Long id, SchemeVersion version);

}
