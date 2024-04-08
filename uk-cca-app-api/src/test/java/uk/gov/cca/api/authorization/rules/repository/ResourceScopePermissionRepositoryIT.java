package uk.gov.cca.api.authorization.rules.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.cca.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.cca.api.authorization.rules.domain.ResourceType;
import uk.gov.cca.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.domain.RoleType;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class ResourceScopePermissionRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private ResourceScopePermissionRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope() {
        createResourceScopePermission(ResourceType.REQUEST_TASK, "requestTaskType",
            Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR, Permission.PERM_CA_USERS_EDIT);

        Optional<ResourceScopePermission> result = repo.findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK,
                "requestTaskType",
            RoleType.REGULATOR,
            Scope.REQUEST_TASK_EXECUTE);

        assertThat(result).isNotEmpty();
        assertThat(result.get().getResourceType()).isEqualTo(ResourceType.REQUEST_TASK);
        assertThat(result.get().getResourceSubType()).isEqualTo("requestTaskType");
        assertThat(result.get().getScope()).isEqualTo(Scope.REQUEST_TASK_EXECUTE);
    }

    @Test
    void findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope_not_found() {
        createResourceScopePermission(ResourceType.REQUEST_TASK, "requestTaskType",
            Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR, Permission.PERM_CA_USERS_EDIT);

        Optional<ResourceScopePermission> result = repo.findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK,
                "requestTaskType",
            RoleType.REGULATOR,
            Scope.REQUEST_TASK_VIEW);

        assertThat(result).isEmpty();
    }

    @Test
    void findByResourceTypeAndRoleTypeAndScope() {
        createResourceScopePermission(ResourceType.ACCOUNT, null, Scope.EDIT_USER, RoleType.REGULATOR,
            Permission.PERM_ACCOUNT_USERS_EDIT);

        Optional<ResourceScopePermission> result = repo.findByResourceTypeAndRoleTypeAndScope(
            ResourceType.ACCOUNT,
            RoleType.REGULATOR,
            Scope.EDIT_USER);

        assertThat(result).isNotEmpty();
        assertThat(result.get().getResourceType()).isEqualTo(ResourceType.ACCOUNT);
        assertThat(result.get().getScope()).isEqualTo(Scope.EDIT_USER);
    }

    @Test
    void findByResourceTypeAndRoleTypeAndScope_not_found() {
        createResourceScopePermission(ResourceType.ACCOUNT, null,
            Scope.EDIT_USER, RoleType.REGULATOR, Permission.PERM_ACCOUNT_USERS_EDIT);

        Optional<ResourceScopePermission> result = repo.findByResourceTypeAndRoleTypeAndScope(
            ResourceType.ACCOUNT,
            RoleType.REGULATOR,
            Scope.REQUEST_TASK_EXECUTE);

        assertThat(result).isEmpty();
    }

    @Test
    void existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope() {
        createResourceScopePermission(ResourceType.REQUEST_TASK, "requestTaskType",
            Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR, Permission.PERM_CA_USERS_EDIT);

        boolean result = repo.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK,
            "requestTaskType",
            RoleType.REGULATOR,
            Scope.REQUEST_TASK_EXECUTE);

        assertThat(result).isTrue();
    }

    @Test
    void existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope_not_exist() {
        createResourceScopePermission(ResourceType.REQUEST_TASK, "requestTaskType",
            Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR, Permission.PERM_CA_USERS_EDIT);

        boolean result = repo.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK,
            "requestTaskType",
            RoleType.REGULATOR,
            Scope.REQUEST_TASK_VIEW);

        assertThat(result).isFalse();
    }

    @Test
    void findByResourceTypeAndRoleType() {
        createResourceScopePermission(ResourceType.REQUEST_TASK, "requestTaskType",
            Scope.REQUEST_TASK_EXECUTE, RoleType.REGULATOR, Permission.PERM_CA_USERS_EDIT);

        Set<String> resourceSubTypes = repo.findByResourceTypeAndRoleType(ResourceType.REQUEST_TASK, RoleType.REGULATOR).stream()
            .map(ResourceScopePermission::getResourceSubType).collect(
                Collectors.toSet());

        assertThat(resourceSubTypes.size()).isEqualTo(1);
        assertThat(resourceSubTypes).containsAll(Set.of("requestTaskType"));
    }

    private void createResourceScopePermission(
        String resourceType, String resourceSubType, String scope, RoleType roleType, String permission) {
        ResourceScopePermission resource = ResourceScopePermission.builder()
            .resourceType(resourceType)
            .resourceSubType(resourceSubType)
            .roleType(roleType)
            .scope(scope)
            .permission(permission)
            .build();
        entityManager.persist(resource);
    }
}
