package uk.gov.cca.api.authorization.ccaauth.core.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.cca.api.sectorassociation.domain.Location;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
public class CcaAuthorityDetailsIT extends AbstractContainerBaseTest {

    @Autowired
    private EntityManager em;

    @Test
    void testCcaAuthorityDetailsPersistence() {
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

        em.persist(authorityDetails);
        em.flush();

        CcaAuthorityDetails savedAuthorityDetails = em.find(CcaAuthorityDetails.class, authorityDetails.getId());

        assertThat(savedAuthorityDetails).isNotNull();
        assertThat(savedAuthorityDetails.getId()).isNotNull();
        assertThat(savedAuthorityDetails.getContactType()).isEqualTo(ContactType.SECTOR_ASSOCIATION);
        assertThat(savedAuthorityDetails.getOrganisationName()).isEqualTo("Test Org Name");
    }
}
