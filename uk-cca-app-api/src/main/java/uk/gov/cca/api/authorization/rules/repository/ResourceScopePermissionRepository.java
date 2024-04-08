package uk.gov.cca.api.authorization.rules.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.netz.api.common.domain.RoleType;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ResourceScopePermissionRepository extends JpaRepository<ResourceScopePermission, Long> {

    @Transactional(readOnly = true)
    Optional<ResourceScopePermission> findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(
        String resourceType, String resourceSubType, RoleType roleType, String scope);
    
    @Transactional(readOnly = true)
    Optional<ResourceScopePermission> findByResourceTypeAndRoleTypeAndScope(
        String resourceType, RoleType roleType, String scope);
    
    @Transactional(readOnly = true)
    boolean existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(String resourceType, String resourceSubType,
            RoleType roleType, String scope);

    @Transactional(readOnly = true)
    Set<ResourceScopePermission> findByResourceTypeAndRoleType(String resourceType, RoleType roleType);
    
}
