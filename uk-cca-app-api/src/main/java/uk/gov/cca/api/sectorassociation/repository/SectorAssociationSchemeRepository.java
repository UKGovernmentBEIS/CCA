package uk.gov.cca.api.sectorassociation.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface SectorAssociationSchemeRepository extends JpaRepository<SectorAssociationScheme, Long> {
    
    @EntityGraph(value = "sector-association-scheme-graph", type = EntityGraph.EntityGraphType.FETCH)
    List<SectorAssociationScheme> findSectorAssociationSchemesBySectorAssociationId(Long id);
    
    @EntityGraph(value = "sector-association-scheme-graph", type = EntityGraph.EntityGraphType.FETCH)
    Optional<SectorAssociationScheme> findSectorAssociationSchemeBySectorAssociationIdAndSchemeVersion(Long id, SchemeVersion version);

	@EntityGraph(type = EntityGraph.EntityGraphType.FETCH, attributePaths = {"targetSet", "targetSet.targetCommitments"})
	SectorAssociationScheme findBySchemeVersionAndSectorAssociation_Acronym(SchemeVersion schemeVersion, String acronym);

	@Modifying
	@Query("UPDATE SectorAssociationScheme sas SET sas.umaDate = :umaDate " +
			"WHERE sas.schemeVersion = :schemeVersion AND sas.sectorAssociation.acronym = :acronym")
	void updateUmaDate(SchemeVersion schemeVersion, String acronym, LocalDate umaDate);

	@Modifying
	@Query("UPDATE SectorAssociationScheme sas SET sas.sectorDefinition = :sectorDefinition " +
			"WHERE sas.schemeVersion = :schemeVersion AND sas.sectorAssociation.acronym = :acronym")
	void updateSectorDefinition(SchemeVersion schemeVersion, String acronym, String sectorDefinition);
}
