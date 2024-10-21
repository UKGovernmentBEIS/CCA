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
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaUserRoleType;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class CcaUserRoleTypeRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private CcaUserRoleTypeRepository userRoleTypeRepository;

    @Autowired
    private EntityManager entityManager;
    
    @Test
    void findById_active_sector_user() {
        String userId = "userId";
        CcaAuthority userAuthority =
            CcaAuthority.builder()
                .userId(userId)
                .code("sector_administrator_user")
                .status(AuthorityStatus.ACTIVE)
                .sectorAssociationId(1L)
                .createdBy(userId)
                .build();

        entityManager.persist(userAuthority);

        flushAndClear();

        // Inject
        Optional<CcaUserRoleType> optionalUserRole = userRoleTypeRepository.findById(userId);

        assertTrue(optionalUserRole.isPresent());
        assertEquals(SECTOR_USER, optionalUserRole.get().getRoleType());
        assertEquals(userId, optionalUserRole.get().getUserId());
    }

    @Test
    void findById_disabled_user() {
        String userId = "userId";
        CcaAuthority userAuthority =
            CcaAuthority.builder()
                .userId(userId)
                .code("sector_administrator_user")
                .status(AuthorityStatus.DISABLED)
                .sectorAssociationId(1L)
                .createdBy(userId)
                .build();

        entityManager.persist(userAuthority);

        flushAndClear();

        // Inject
        Optional<CcaUserRoleType> optionalUserRole = userRoleTypeRepository.findById(userId);

        assertTrue(optionalUserRole.isPresent());
        assertEquals(SECTOR_USER, optionalUserRole.get().getRoleType());
        assertEquals(userId, optionalUserRole.get().getUserId());
    }

    @Test
    void findById_pending_user() {
        String userId = "userId";
        CcaAuthority userAuthority =
            CcaAuthority.builder()
                .userId(userId)
                .code("regulator_administrator_user")
                .status(AuthorityStatus.PENDING)
                .sectorAssociationId(1L)
                .createdBy(userId)
                .build();

        entityManager.persist(userAuthority);

        flushAndClear();

        // Inject
        Optional<CcaUserRoleType> optionalUserRole = userRoleTypeRepository.findById(userId);

        assertTrue(optionalUserRole.isPresent());
        assertEquals(SECTOR_USER, optionalUserRole.get().getRoleType());
        assertEquals(userId, optionalUserRole.get().getUserId());
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

}