package uk.gov.cca.api.sectorassociation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSiteContactInfoDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Transactional(readOnly = true)
public interface SectorAssociationRepository extends JpaRepository<SectorAssociation, Long> {
    
    @Query(name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATIONS_INFO_BY_COMPETENT_AUTHORITY)
    List<SectorAssociationInfoDTO> findSectorAssociations(CompetentAuthorityEnum competentAuthority);

    @Query(name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATIONS_INFO_BY_SECTORS)
    List<SectorAssociationInfoDTO> findSectorAssociations(Set<Long> sectorIds);

    @Query("SELECT new uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSiteContactInfoDTO(sa.id, concat(sa.acronym,' - ', sa.name), sa.facilitatorUserId) "
            + "FROM SectorAssociation sa "
            + "WHERE sa.competentAuthority = :competentAuthority")
    Page<SectorAssociationSiteContactInfoDTO> findSectorAssociationsSiteContactsByCA(
        @Param("competentAuthority") CompetentAuthorityEnum competentAuthority, Pageable pageable);

    List<SectorAssociation> findAllByIdIn(List<Long> sectorIds);

    List<SectorAssociation> findAllByFacilitatorUserId(String facilitatorUserId);

    @Query(name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATIONS_IDS_BY_COMPETENT_AUTHORITY)
    List<Long> findSectorAssociationsIdsByCompetentAuthority(CompetentAuthorityEnum ca);

    @Query(name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATION_ACRONYM_BY_ID)
    String findSectorAssociationAcronymById(Long id);
    
    @Query(name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATION_ID_BY_ACRONYM)
    Optional<Long> findSectorAssociationIdByAcronym(String acronym);
}
