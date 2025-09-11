package uk.gov.cca.api.sectorassociation.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.cca.api.sectorassociation.domain.Location;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationContact;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class SectorAssociationContactRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private SectorAssociationContactRepository repository;

    @Test
    void whenSave_thenFindById() {

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

        SectorAssociationContact savedContact = repository.save(contact);

        SectorAssociationContact foundContact = repository.findById(savedContact.getId()).orElse(null);

        assertThat(foundContact).isNotNull();
        assertThat(foundContact.getFirstName()).isEqualTo("John");
    }
}
