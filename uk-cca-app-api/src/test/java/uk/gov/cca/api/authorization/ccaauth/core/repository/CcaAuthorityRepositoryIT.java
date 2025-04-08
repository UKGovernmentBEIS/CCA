package uk.gov.cca.api.authorization.ccaauth.core.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaPermission;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityWithPermissionDTO;
import uk.gov.netz.api.authorization.core.domain.AuthorityPermission;
import uk.gov.netz.api.authorization.core.domain.Permission;
import uk.gov.netz.api.authorization.core.domain.Role;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

import java.util.List;
import java.util.stream.Collectors;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;
import static uk.gov.netz.api.authorization.core.domain.AuthorityStatus.ACTIVE;
import static uk.gov.netz.api.authorization.core.domain.AuthorityStatus.PENDING;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class CcaAuthorityRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private CcaAuthorityRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findActiveAuthoritiesWithAssignedPermissionsByUserId() {
        String userId = "userId";

        CcaAuthority authority1 = CcaAuthority.builder()
                .userId(userId)
                .code("operator")
                .status(ACTIVE)
                .sectorAssociationId(1L)
                .createdBy(userId)
                .build();
        authority1.addPermission(
                AuthorityPermission.builder().permission(Permission.PERM_ACCOUNT_USERS_EDIT).build());
        authority1.addPermission(
                AuthorityPermission.builder().permission(Permission.PERM_TASK_ASSIGNMENT).build());

        entityManager.persist(authority1);

        CcaAuthority authority2 = CcaAuthority.builder()
                .userId(userId)
                .code("operator")
                .status(ACTIVE)
                .sectorAssociationId(2L)
                .createdBy(userId)
                .build();
        authority2.addPermission(
                AuthorityPermission.builder().permission(Permission.PERM_TASK_ASSIGNMENT).build());

        entityManager.persist(authority2);

        CcaAuthority authority3 = CcaAuthority.builder()
                .userId(userId)
                .code("operator")
                .status(PENDING)
                .sectorAssociationId(3L)
                .createdBy(userId)
                .build();

        entityManager.persist(authority3);

        CcaAuthorityWithPermissionDTO expectedAuthority1 = CcaAuthorityWithPermissionDTO.builder()
                .id(authority1.getId())
                .code(authority1.getCode())
                .status(authority1.getStatus().name())
                .sectorAssociationId(authority1.getSectorAssociationId())
                .permissions(authority1.getAuthorityPermissions().stream().map(AuthorityPermission::getPermission).collect(Collectors.joining(",")))
                .build();

        CcaAuthorityWithPermissionDTO expectedAuthority2 = CcaAuthorityWithPermissionDTO.builder()
                .id(authority2.getId())
                .code(authority2.getCode())
                .status(authority2.getStatus().name())
                .sectorAssociationId(authority2.getSectorAssociationId())
                .permissions(authority2.getAuthorityPermissions().stream().map(AuthorityPermission::getPermission).collect(Collectors.joining(",")))
                .build();

        // Inject

        List<CcaAuthorityWithPermissionDTO> authorities = repo.findActiveAuthoritiesWithAssignedPermissionsByUserId(userId);

        assertThat(authorities).hasSize(2)
                .containsExactlyInAnyOrder(expectedAuthority1, expectedAuthority2);
    }

    @Test
    void existsOtherSectorUserAdmin_True() {
        String userId = "userId";

        CcaAuthority authority1 = CcaAuthority.builder()
                .userId(userId)
                .code("sector_user_administrator")
                .status(ACTIVE)
                .sectorAssociationId(1L)
                .createdBy(userId)
                .build();

        CcaAuthority authority2 = CcaAuthority.builder()
                .userId("userId2")
                .code("sector_user_administrator")
                .status(ACTIVE)
                .sectorAssociationId(1L)
                .createdBy(userId)
                .build();


        authority1.addPermission(
                AuthorityPermission.builder().permission(CcaPermission.PERM_SECTOR_ASSOCIATION_EDIT).build());

        entityManager.persist(authority1);
        entityManager.persist(authority2);
        entityManager.flush();

        boolean exists = repo.existsOtherSectorUserAdmin("userId");
        assertThat(exists).isTrue();
    }

    @Test
    void existsOtherSectorUserAdmin_False() {
        String userId = "userId";

        CcaAuthority authority1 = CcaAuthority.builder()
                .userId(userId)
                .code("sector_user_administrator")
                .status(ACTIVE)
                .sectorAssociationId(1L)
                .createdBy(userId)
                .build();


        authority1.addPermission(
                AuthorityPermission.builder().permission(CcaPermission.PERM_SECTOR_ASSOCIATION_EDIT).build());

        entityManager.persist(authority1);

        boolean exists = repo.existsOtherSectorUserAdmin("userId");
        assertThat(exists).isFalse();
    }

    @Test
    void findActiveSectorUsersBySectorAssociationId() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        Long sectorAssociationId = 1L;

        CcaAuthority authority1 = CcaAuthority.builder()
            .userId("user1")
            .code("regulator_user_administrator")
            .status(ACTIVE)
            .createdBy("creator1")
            .competentAuthority(competentAuthority)
            .build();

        CcaAuthority authority2 = CcaAuthority.builder()
            .userId("user2")
            .code("sector_user_administrator")
            .status(ACTIVE)
            .sectorAssociationId(sectorAssociationId)
            .createdBy("creator2")
            .build();

        CcaAuthority authority3 = CcaAuthority.builder()
            .userId("user3")
            .code("operator_basic_user")
            .status(PENDING)
            .createdBy("creator3")
            .accountId(1L)
            .build();

        Role r1 = Role.builder()
            .name("Administrator User")
            .code("regulator_administrator")
            .type(REGULATOR)
            .build();

        Role r2 = Role.builder()
            .name("Administrator User")
            .code("sector_user_administrator")
            .type(SECTOR_USER)
            .build();

        Role r3 = Role.builder()
            .name("Basic User")
            .code("sector_user_basic_user")
            .type(SECTOR_USER)
            .build();

        Role r4 = Role.builder()
            .name("Operator")
            .code("operator_basic_user")
            .type(OPERATOR)
            .build();

        entityManager.persist(authority1);
        entityManager.persist(authority2);
        entityManager.persist(authority3);
        entityManager.persist(r1);
        entityManager.persist(r2);
        entityManager.persist(r3);
        entityManager.persist(r4);

        entityManager.flush();

        List<String> sectorUsers = repo.findActiveSectorUsersBySectorAssociationId(sectorAssociationId);

        assertThat(sectorUsers).hasSize(1).containsExactlyInAnyOrder("user2");
    }
}