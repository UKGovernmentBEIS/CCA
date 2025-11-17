package uk.gov.cca.api.sectorassociation.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociationScheme;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface SubsectorAssociationSchemeRepository extends JpaRepository<SubsectorAssociationScheme, Long> {
    
    @EntityGraph(value = "subsector-association-scheme-graph", type = EntityGraph.EntityGraphType.FETCH)
    List<SubsectorAssociationScheme> findSubsectorAssociationSchemeBySubsectorAssociationId(Long id);

	@EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"targetSet", "targetSet.targetCommitments" })
	SubsectorAssociationScheme findBySchemeVersionAndSubsectorAssociation_NameAndSubsectorAssociation_SectorAssociation_Acronym(SchemeVersion schemeVersion, String name, String acronym);
}
