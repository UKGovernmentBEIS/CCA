package uk.gov.cca.api.authorization.ccaauth.core.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.authorization.core.domain.Authority;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@NamedQuery(
        name = CcaAuthority.NAMED_QUERY_FIND_AUTHORITIES_WITH_DETAILS_BY_SECTOR_ASSOCIATION_ID,
        query = "select new uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAuthorityDTO(au.userId AS userId, r.name AS roleName, r.code as roleCode, au.status as authorityStatus, au.creationDate as authorityCreationDate, det.contactType as contactType) from CcaAuthority au join Role r on r.code = au.code join CcaAuthorityDetails det on au.id = det.authority.id where au.sectorAssociationId = :sectorAssociationId"
)
@NamedQuery(
        name = CcaAuthority.NAMED_QUERY_FIND_AUTHORITIES_WITH_DETAILS_BY_ACCOUNT_ID,
        query = "select new uk.gov.cca.api.authorization.ccaauth.operator.domain.OperatorAuthorityDTO(au.userId AS userId, r.name AS roleName, r.code as roleCode, au.status as authorityStatus, au.creationDate as authorityCreationDate, det.contactType as contactType) from Authority au join Role r on r.code = au.code join CcaAuthorityDetails det on au.id = det.authority.id where au.accountId = :accountId"
)
@NamedQuery(
        name = CcaAuthority.NAMED_QUERY_EXIST_OTHER_SECTOR_USER_ADMIN,
        query = "select count(au.userId) > 0 from CcaAuthority au where au.userId <> :userId and au.status = 'ACTIVE' and au.code = 'sector_user_administrator'"
)
@NamedQuery(
        name = CcaAuthority.NAMED_QUERY_FIND_AUTHORITIES_BY_USER_ID,
        query = "select au from Authority au where au.userId = :userId"
)
@NamedQuery(
        name = CcaAuthority.NAMED_QUERY_FIND_ACTIVE_SECTOR_USERS_BY_SECTOR_ASSOCIATION_ID_AND_ROLE,
        query = "select distinct(au.userId) from CcaAuthority au where au.sectorAssociationId = :sectorId and au.code = :roleCode and au.status = 'ACTIVE' "
)
@NamedQuery(
        name = CcaAuthority.NAMED_QUERY_FIND_STATUS_BY_USERS_AND_SECTOR_ASSOCIATION_ID,
        query = "select au.userId as userId, au.status as status from CcaAuthority au where au.userId in (:userIds) and au.sectorAssociationId = :sectorAssociationId "
)
@NamedQuery(
    name = CcaAuthority.NAMED_QUERY_FIND_ACTIVE_SECTOR_USERS_BY_SECTOR_ASSOCIATION_ID,
    query = "select au.userId from CcaAuthority au where au.sectorAssociationId = :sectorAssociationId and au.status = 'ACTIVE'"
)
@NamedQuery(
        name = CcaAuthority.NAMED_QUERY_FIND_SECTOR_USER_ASSIGNED_RESOURCE_SUB_TYPES_BY_SECTOR_ASSOCIATION,
        query = "select new uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAssignedSubResource("
                + "au.sectorAssociationId, sc.resourceSubType) "
                + "from CcaAuthority au "
                + "join au.authorityPermissions ap "
                + "join ResourceScopePermission sc on sc.permission = ap.permission "
                + "where au.userId = :userId "
                + "and au.sectorAssociationId in (:sectorAssociationIds) "
                + "and sc.resourceType = :resourceType "
                + "and sc.scope = :scope "
                + "and au.status = 'ACTIVE' ")
@NamedQuery(
        name = CcaAuthority.NAMED_QUERY_FIND_SECTOR_USERS_WITH_SCOPE_ON_RESOURCE_TYPE_AND_SUB_TYPE_AND_SECTOR_ASSOCIATION_ID,
        query = "select distinct(au.userId) "
                + "from CcaAuthority au "
                + "join au.authorityPermissions ap "
                + "join ResourceScopePermission sc on sc.permission = ap.permission "
                + "where sc.resourceType = :resourceType "
                + "and sc.resourceSubType = :resourceSubType "
                + "and sc.scope = :scope "
                + "and au.sectorAssociationId = :sectorAssociationId "
                + "and au.status = 'ACTIVE' ")
@NamedQuery(
        name = CcaAuthority.NAMED_QUERY_FIND_SECTOR_USER_AUTHORITY_ROLE_LIST_BY_SECTOR_ASSOCIATION_ID,
        query = "select new uk.gov.netz.api.authorization.core.domain.dto.AuthorityRoleDTO("
                + "au.userId, au.status, r.name, r.code, au.creationDate) "
                + "from CcaAuthority au "
                + "join Role r on r.code = au.code "
                + "where au.sectorAssociationId = :sectorId ")
public class CcaAuthority extends Authority {

    public static final String NAMED_QUERY_FIND_AUTHORITIES_WITH_DETAILS_BY_SECTOR_ASSOCIATION_ID = "CcaAuthority.findAuthoritiesWithDetailsBySectorAssociationId";
    public static final String NAMED_QUERY_FIND_AUTHORITIES_WITH_DETAILS_BY_ACCOUNT_ID = "CcaAuthority.findAuthoritiesWithDetailsByAccountId";
    public static final String NAMED_QUERY_EXIST_OTHER_SECTOR_USER_ADMIN = "CcaAuthority.existsOtherSectorUserAdmin";
    public static final String NAMED_QUERY_FIND_AUTHORITIES_BY_USER_ID = "CcaAuthority.findAuthoritiesByUserId";
    public static final String NAMED_QUERY_FIND_ACTIVE_SECTOR_USERS_BY_SECTOR_ASSOCIATION_ID_AND_ROLE = "CcaAuthority.findActiveSectorUsersBySectorAssociationIdAndRole";
    public static final String NAMED_QUERY_FIND_STATUS_BY_USERS_AND_SECTOR_ASSOCIATION_ID = "CcaAuthority.findStatusByUsersAndSectorAssociationId";
    public static final String NAMED_QUERY_FIND_ACTIVE_SECTOR_USERS_BY_SECTOR_ASSOCIATION_ID = "CcaAuthority.findActiveSectorUsersBySectorAssociationId";
    public static final String NAMED_QUERY_FIND_SECTOR_USER_ASSIGNED_RESOURCE_SUB_TYPES_BY_SECTOR_ASSOCIATION = "CcaAuthority.findSectorUserAssignedResourceSubTypesBySectorAssociation";
    public static final String NAMED_QUERY_FIND_SECTOR_USERS_WITH_SCOPE_ON_RESOURCE_TYPE_AND_SUB_TYPE_AND_SECTOR_ASSOCIATION_ID = "CcaAuthority.findSectorUsersWithScopeOnResourceTypeAndSubTypeAndSectorAssociationId";
    public static final String NAMED_QUERY_FIND_SECTOR_USER_AUTHORITY_ROLE_LIST_BY_SECTOR_ASSOCIATION_ID = "CcaAuthority.findSectorUserAuthorityRoleListBySectorAssociationId";

    @Column(name = "sector_association_id")
    private Long sectorAssociationId;
}
