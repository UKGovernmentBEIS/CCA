package uk.gov.cca.api.authorization.ccaauth.core.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthority;
import uk.gov.cca.api.authorization.ccaauth.core.domain.CcaAuthorityDetails;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class CcaAuthorityDetailsRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private CcaAuthorityDetailsRepository ccaAuthorityDetailsRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findCcaAuthorityDetailsByAuthority() {
        String userId = "userId";
        CcaAuthority userAuthority =
                CcaAuthority.builder()
                        .userId(userId)
                        .code("sector user")
                        .status(AuthorityStatus.PENDING)
                        .sectorAssociationId(1L)
                        .competentAuthority(null)
                        .createdBy(userId)
                        .build();

        final CcaAuthorityDetails authorityDetails = CcaAuthorityDetails.builder()
                .contactType(ContactType.SECTOR_ASSOCIATION)
                .organisationName("Test Org Name")
                .authority(userAuthority)
                .build();

        entityManager.persist(authorityDetails);
        flushAndClear();

        Optional<CcaAuthorityDetails> savedAuthorityDetailsOpt = ccaAuthorityDetailsRepository.findById(userAuthority.getId());

        assertThat(savedAuthorityDetailsOpt).isNotEmpty();
        assertThat(savedAuthorityDetailsOpt.get().getId()).isNotNull();
        assertThat(savedAuthorityDetailsOpt.get().getContactType()).isEqualTo(ContactType.SECTOR_ASSOCIATION);
        assertThat(savedAuthorityDetailsOpt.get().getOrganisationName()).isEqualTo("Test Org Name");
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

}
