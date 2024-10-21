package uk.gov.cca.api.sectorassociation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSiteContactInfoDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Repository
public interface SectorAssociationRepository extends JpaRepository<SectorAssociation, Long> {

    @Transactional(readOnly = true)
    @Query(name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATIONS_INFO_BY_COMPETENT_AUTHORITY)
    List<SectorAssociationInfoDTO> findSectorAssociations(CompetentAuthorityEnum competentAuthority);

    @Transactional(readOnly = true)
    @Query(name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATIONS_INFO_BY_SECTORS)
    List<SectorAssociationInfoDTO> findSectorAssociations(Set<Long> sectorIds);

    @Transactional(readOnly = true)
    @Query(name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATIONS_SITE_CONTACTS)
    Page<SectorAssociationSiteContactInfoDTO> findSectorAssociationsSiteContactsByCA(
        @Param("competentAuthority") CompetentAuthorityEnum competentAuthority, Pageable pageable);

    @Transactional(readOnly = true)
    List<SectorAssociation> findAllByIdIn(List<Long> sectorIds);

    @Transactional(readOnly = true)
    List<SectorAssociation> findAllByFacilitatorUserId(String facilitatorUserId);

    @Transactional(readOnly = true)
    @Query(name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATIONS_IDS_BY_COMPETENT_AUTHORITY)
    List<Long> findSectorAssociationsIdsByCompetentAuthority(CompetentAuthorityEnum ca);

    @Transactional(readOnly = true)
    @Query(name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATION_ACRONYM_BY_ID)
    String findSectorAssociationAcronymById(Long id);

    @Transactional(readOnly = true)
    @Query(name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATION_NOTICE_RECIPIENT_BY_ID)
    NoticeRecipientDTO findSectorAssociationNoticeRecipientById(Long id);
    
    @Transactional(readOnly = true)
    @Query(name = SectorAssociation.NAMED_QUERY_FIND_SECTOR_ASSOCIATION_ID_BY_ACRONYM)
    Optional<Long> findSectorAssociationIdByAcronym(String acronym);
}
