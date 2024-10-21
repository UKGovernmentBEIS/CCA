package uk.gov.cca.api.authorization.ccaauth.core.repository;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityWithPermissionDTO;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CcaAuthorityCustomRepository {

    @Transactional(readOnly = true)
    List<CcaAuthorityWithPermissionDTO> findActiveAuthoritiesWithAssignedPermissionsByUserId(String userId);

    @Transactional(readOnly = true)
    Map<String, AuthorityStatus> findStatusByUsersAndSectorAssociationId(List<String> userIds, Long sectorAssociationId);
    
    @Transactional(readOnly = true)
    Map<Long, Set<String>> findSectorUserAssignedResourceSubTypesBySectorAssociations(String userId, Set<Long> sectorAssociationIds,
                                                                              String resourceType, String scope);
}
