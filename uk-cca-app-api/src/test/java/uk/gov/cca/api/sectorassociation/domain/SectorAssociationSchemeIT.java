package uk.gov.cca.api.sectorassociation.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.FileStatus;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class SectorAssociationSchemeIT extends AbstractContainerBaseTest {

    @Autowired
    private EntityManager entityManager;

    private SectorAssociationSchemeDocument umbrellaAgreement;
    private TargetSet targetSet;
    private SectorAssociation sectorAssociation;

    @BeforeEach
    void setUp() {
        umbrellaAgreement = SectorAssociationSchemeDocument.builder()
                .uuid("test")
                .fileName("umbrellaAgreement")
                .fileType(".pdf")
                .status(FileStatus.SUBMITTED)
                .fileSize(1)
                .createdBy("test user")
                .build();

        entityManager.persist(umbrellaAgreement);

        targetSet = TargetSet.builder()
                .targetCurrencyType("currency type")
                .energyOrCarbonUnit("carbon")
                .build();

        entityManager.persist(targetSet);

        TargetCommitment targetCommitment = TargetCommitment.builder()
                .targetImprovement(BigDecimal.valueOf(-10.256))
                .targetPeriod("period")
                .targetSet(targetSet)
                .build();

        entityManager.persist(targetCommitment);

        Location location = Location.builder()
                .postcode("12345")
                .line1("123 Main St")
                .city("Springfield")
                .county("CountyName")
                .build();

        SectorAssociationContact contact = SectorAssociationContact.builder()
                .title("Mr.")
                .firstName("John")
                .lastName("Doe")
                .jobTitle("Director")
                .organisationName("Acme Corp")
                .phoneNumber("123456789")
                .email("john.doe@example.com")
                .location(location)
                .build();

        sectorAssociation = SectorAssociation.builder()
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .legalName("Some Association Legal")
                .name("Some Association")
                .acronym("SA")
                .facilitatorUserId("Facilitator User Id")
                .energyEprFactor("Energy Factor")
                .location(location)
                .sectorAssociationContact(contact)
                .build();

        entityManager.persist(sectorAssociation);
        entityManager.flush();
    }
    @Test
    void testSectorAssociationSchemePersistence() {
        SectorAssociationScheme sectorAssociationScheme = SectorAssociationScheme.builder()
                .umbrellaAgreement(umbrellaAgreement)
                .sectorAssociation(sectorAssociation)
                .targetSet(targetSet)
                .build();

        entityManager.persist(sectorAssociationScheme);
        entityManager.flush();

        SectorAssociationScheme savedSectorAssociationScheme = entityManager.find(SectorAssociationScheme.class, sectorAssociationScheme.getId());

        assertThat(savedSectorAssociationScheme).isNotNull();
        assertThat(savedSectorAssociationScheme.getId()).isNotNull();
        assertThat(savedSectorAssociationScheme.getTargetSet().getEnergyOrCarbonUnit()).isEqualTo("carbon");
    }
}
