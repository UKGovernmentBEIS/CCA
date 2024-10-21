package uk.gov.cca.api.sectorassociation.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.cca.api.sectorassociation.domain.Location;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationContact;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationSiteContactInfoDTO;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
public class SectorAssociationRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private SectorAssociationRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void whenFindDetailedContactInfo_thenReturnPageOfSiteContactInfoDTOs() {
        SectorAssociation savedAssociation = createSectorAssociation(1L, "ADS");
        entityManager.merge(savedAssociation);
        flushAndClear();

        Pageable pageable = PageRequest.of(0, 5);

        Page<SectorAssociationSiteContactInfoDTO> result = repository.findSectorAssociationsSiteContactsByCA(
            CompetentAuthorityEnum.ENGLAND, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getSectorName()).isEqualTo(
            savedAssociation.getAcronym() + " - " + savedAssociation.getName());
    }

    @Test
    void whenFindAllByIdIn_thenReturnListOfSectorAssociations() {
        SectorAssociation savedAssociationOne = createSectorAssociation(1L, "AIC");
        SectorAssociation savedAssociationTwo = createSectorAssociation(2L, "SA");

        entityManager.merge(savedAssociationOne);
        entityManager.merge(savedAssociationTwo);
        flushAndClear();

        List<SectorAssociation> foundAssociations = repository.findAllByIdIn(
            List.of(savedAssociationOne.getId(), savedAssociationTwo.getId()));

        assertThat(foundAssociations).hasSize(2);
    }

    @Test
    void whenFindAllByFacilitatorUserId_thenReturnListOfSectorAssociations() {
        String testUserId = "testFacilitatorId";
        SectorAssociation savedAssociationOne = createSectorAssociation(3L, "AIC");
        savedAssociationOne.setFacilitatorUserId(testUserId);
        SectorAssociation savedAssociationTwo = createSectorAssociation(4L, "SA");
        savedAssociationTwo.setFacilitatorUserId(testUserId);

        entityManager.merge(savedAssociationOne);
        entityManager.merge(savedAssociationTwo);
        flushAndClear();

        List<SectorAssociation> foundAssociations = repository.findAllByFacilitatorUserId(testUserId);

        assertThat(foundAssociations).hasSize(2);
        assertThat(foundAssociations.stream().allMatch(sa -> sa.getFacilitatorUserId().equals(testUserId))).isTrue();
    }

    private SectorAssociation createSectorAssociation(Long id, String acronym) {
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

        return SectorAssociation.builder()
            .id(id)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .legalName("Some Association Legal")
            .name("Some Association")
            .acronym(acronym)
            .facilitatorUserId("Facilitator User Id")
            .energyEprFactor("Energy Factor")
            .location(location)
            .sectorAssociationContact(contact)
            .build();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
