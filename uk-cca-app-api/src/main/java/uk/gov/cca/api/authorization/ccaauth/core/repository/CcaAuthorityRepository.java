package uk.gov.cca.api.authorization.ccaauth.core.repository;

import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority.NAMED_QUERY_FIND_AUTHORITIES_BY_USER_ID;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority.NAMED_QUERY_FIND_AUTHORITIES_WITH_DETAILS_BY_SECTOR_ASSOCIATION_ID;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority.NAMED_QUERY_FIND_AUTHORITIES_WITH_DETAILS_BY_ACCOUNT_ID;
import static uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority.NAMED_QUERY_FIND_SECTOR_USER_AUTHORITY_ROLE_LIST_BY_SECTOR_ASSOCIATION_ID;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.operator.domain.OperatorAuthorityDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityDTO;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityRoleDTO;

@Repository
public interface CcaAuthorityRepository extends JpaRepository<CcaAuthority, Long>, CcaAuthorityCustomRepository {

    @Transactional(readOnly = true)
    @Query(name = NAMED_QUERY_FIND_AUTHORITIES_BY_USER_ID)
    List<Authority> findAuthoritiesByUserId(String userId);

    Optional<CcaAuthority> findByUserIdAndSectorAssociationId(String userid, Long sectorAssociationId);

    @Transactional(readOnly = true)
    @Query(name = NAMED_QUERY_FIND_AUTHORITIES_WITH_DETAILS_BY_SECTOR_ASSOCIATION_ID)
    List<SectorUserAuthorityDTO> findAuthoritiesWithDetailsBySectorAssociationId(Long sectorAssociationId);

    @Transactional(readOnly = true)
    @Query(name =  NAMED_QUERY_FIND_SECTOR_USER_AUTHORITY_ROLE_LIST_BY_SECTOR_ASSOCIATION_ID)
    List<AuthorityRoleDTO> findSectorUserAuthoritiesListBySectorAssociationId(Long sectorId);

    @Transactional(readOnly = true)
    @Query(name = NAMED_QUERY_FIND_AUTHORITIES_WITH_DETAILS_BY_ACCOUNT_ID)
    List<OperatorAuthorityDTO> findAuthoritiesWithDetailsByAccountId(Long accountId);

    @Transactional(readOnly = true)
    @Query(name = CcaAuthority.NAMED_QUERY_EXIST_OTHER_SECTOR_USER_ADMIN)
    boolean existsOtherSectorUserAdmin(String userId);

    @Transactional(readOnly = true)
    Optional<CcaAuthority> findByUuidAndStatus(String uuid, AuthorityStatus status);

    @Transactional(readOnly = true)
    @Query(name = CcaAuthority.NAMED_QUERY_FIND_ACTIVE_SECTOR_USERS_BY_SECTOR_ASSOCIATION_ID_AND_ROLE)
    List<String> findActiveSectorUsersBySectorAndRole(Long sectorId, String roleCode);

    @Transactional(readOnly = true)
    @Query(name = CcaAuthority.NAMED_QUERY_FIND_ACTIVE_SECTOR_USERS_BY_SECTOR_ASSOCIATION_ID)
    List<String> findActiveSectorUsersBySectorAssociationId(Long sectorAssociationId);

    @Transactional(readOnly = true)
    @Query(name =  CcaAuthority.NAMED_QUERY_FIND_SECTOR_USERS_WITH_SCOPE_ON_RESOURCE_TYPE_AND_SUB_TYPE_AND_SECTOR_ASSOCIATION_ID)
    List<String> findSectorUsersWithScopeOnResourceTypeAndSubTypeAndSectorAssociationId(
            String resourceType, String resourceSubType, String scope, Long sectorAssociationId);

    @Transactional(readOnly = true)
    List<CcaAuthority> findByUserId(String userId);
}
