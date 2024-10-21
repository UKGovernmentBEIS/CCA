package uk.gov.cca.api.authorization.ccaauth.core.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.stereotype.Repository;

import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityWithPermissionDTO;
import uk.gov.cca.api.authorization.ccaauth.sectoruser.domain.SectorUserAssignedSubResource;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class CcaAuthorityCustomRepositoryImpl implements CcaAuthorityCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings("unchecked")
    public List<CcaAuthorityWithPermissionDTO> findActiveAuthoritiesWithAssignedPermissionsByUserId(String userId) {
        return entityManager.createNativeQuery("select au.id as \"id\", au.code as \"code\", au.status as \"status\", " +
                " au.account_id as \"accountId\", au.competent_authority as \"competentAuthority\", au.verification_body_id as \"verificationBodyId\", " +
                " au.sector_association_id as \"sectorAssociationId\", string_agg(ap.permission, ',') as \"permissions\" " +
                " from au_authority au " +
                " left join au_authority_permission ap on au.id = ap.authority_id " +
                " where au.user_id = :userId " +
                " and au.status = 'ACTIVE' " +
                " group by au.id ")
            .unwrap(NativeQuery.class)
            .addScalar("id", StandardBasicTypes.LONG)
            .addScalar("code", StandardBasicTypes.STRING)
            .addScalar("status", StandardBasicTypes.STRING)
            .addScalar("accountId", StandardBasicTypes.LONG)
            .addScalar("competentAuthority", StandardBasicTypes.STRING)
            .addScalar("verificationBodyId", StandardBasicTypes.LONG)
            .addScalar("sectorAssociationId", StandardBasicTypes.LONG)
            .addScalar("permissions", StandardBasicTypes.STRING)
            .setParameter("userId", userId)
            .setReadOnly(true)
            .setTupleTransformer(Transformers.aliasToBean(CcaAuthorityWithPermissionDTO.class))
            .getResultList();
    }

    @Override
    public Map<String, AuthorityStatus> findStatusByUsersAndSectorAssociationId(List<String> userIds, Long sectorAssociationId) {
        return entityManager.createNamedQuery("CcaAuthority.findStatusByUsersAndSectorAssociationId", Tuple.class)
                .setParameter("userIds", userIds).setParameter("sectorAssociationId", sectorAssociationId).getResultStream()
                .collect(Collectors.toMap((t) -> (String)t.get("userId"), (t) -> (AuthorityStatus)t.get("status")));
    }
    
    @Override
    public Map<Long, Set<String>> findSectorUserAssignedResourceSubTypesBySectorAssociations(String userId, Set<Long> sectorAssociationIds,
                                                                                     String resourceType, String scope) {
        return entityManager.createNamedQuery(CcaAuthority.NAMED_QUERY_FIND_SECTOR_USER_ASSIGNED_RESOURCE_SUB_TYPES_BY_SECTOR_ASSOCIATION, SectorUserAssignedSubResource.class)
            .setParameter("userId", userId)
            .setParameter("sectorAssociationIds", sectorAssociationIds)
            .setParameter("resourceType", resourceType)
            .setParameter("scope", scope)
            .getResultStream()
            .collect(Collectors.groupingBy(SectorUserAssignedSubResource::getSectorAssociationId,
                Collectors.mapping(SectorUserAssignedSubResource::getResourceSubType, Collectors.toSet())));
    }
}
