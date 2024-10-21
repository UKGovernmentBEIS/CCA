package uk.gov.cca.api.sectorassociation.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationInfoDTO;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectorAssociationSchemeRepository extends JpaRepository<SectorAssociationScheme, Long> {

    @Transactional(readOnly = true)
    @EntityGraph(value = "sector-association-scheme-graph", type = EntityGraph.EntityGraphType.FETCH)
    Optional<SectorAssociationScheme> findSectorAssociationSchemeBySectorAssociationId(Long id);

    @Transactional(readOnly = true)
    @Query(name = SectorAssociationScheme.NAMED_QUERY_FIND_SUBSECTOR_ASSOCIATIONS_IDS_BY_SECTOR_ASSOCIATIONS_ID)
    List<Long> findSubsectorAssociationIdsBySectorAssociationId(Long sectorAssociationId);

    @Transactional(readOnly = true)
    @Query(name = SectorAssociationScheme.NAMED_QUERY_FIND_SUBSECTOR_ASSOCIATIONS_BY_SECTOR_ASSOCIATION_ID)
    List<SubsectorAssociationInfoDTO> findSubsectorAssociationsBySectorAssociationId(Long sectorAssociationId);
}
