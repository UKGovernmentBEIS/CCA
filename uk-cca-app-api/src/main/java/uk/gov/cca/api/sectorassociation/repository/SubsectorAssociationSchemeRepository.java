package uk.gov.cca.api.sectorassociation.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;

import java.util.Optional;

@Repository
public interface SubsectorAssociationSchemeRepository extends JpaRepository<SubsectorAssociationScheme, Long> {

    @Transactional(readOnly = true)
    @EntityGraph(value = "subsector-association-scheme-graph", type = EntityGraph.EntityGraphType.FETCH)
    Optional<SubsectorAssociationScheme> findSubsectorAssociationSchemesById(Long id);

    @Transactional(readOnly = true)
    @EntityGraph(value = "subsector-association-scheme-graph", type = EntityGraph.EntityGraphType.FETCH)
    Optional<SubsectorAssociationScheme> findSubsectorAssociationSchemesBySubsectorAssociationId(Long subsectorAssociationId);

}
