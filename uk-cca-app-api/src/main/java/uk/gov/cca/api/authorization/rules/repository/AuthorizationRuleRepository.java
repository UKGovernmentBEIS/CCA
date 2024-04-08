package uk.gov.cca.api.authorization.rules.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.rules.domain.AuthorizationRule;
import uk.gov.cca.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.netz.api.common.domain.RoleType;

import java.util.List;

@Repository
public interface AuthorizationRuleRepository extends JpaRepository<AuthorizationRule, Long>, AuthorizationRuleCustomRepository {

    @Transactional(readOnly = true)
    @Query(name =  AuthorizationRule.NAMED_QUERY_FIND_RULE_SCOPE_PERMISSIONS_BY_SERVICE_AND_ROLE_TYPE)
    List<AuthorizationRuleScopePermission> findRulePermissionsByServiceAndRoleType(String serviceName,
                                                                                   RoleType roleType);

    @Transactional(readOnly = true)
    @Query(name =  AuthorizationRule.NAMED_QUERY_FIND_RULE_SCOPE_PERMISSIONS_BY_SERVICE_AND_ROLE_TYPE_AND_RESOURCE_SUB_TYPE)
    List<AuthorizationRuleScopePermission> findRulePermissionsByServiceAndRoleTypeAndResourceSubType(String serviceName,
                                                                                                     RoleType roleType, String resourceSubType);

    @Transactional(readOnly = true)
    @Query(name =  AuthorizationRule.NAMED_QUERY_FIND_RULE_SCOPE_PERMISSIONS_BY_RESOURCE_TYPE_SCOPE_AND_ROLE_TYPE)
    List<AuthorizationRuleScopePermission> findRulePermissionsByResourceTypeScopeAndRoleType(
        String resourceType, String scope, RoleType roleType);

}
