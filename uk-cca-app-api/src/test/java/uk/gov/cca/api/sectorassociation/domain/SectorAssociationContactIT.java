package uk.gov.cca.api.sectorassociation.domain;

import jakarta.persistence.EntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class SectorAssociationContactIT extends AbstractContainerBaseTest {

    @Autowired
    private EntityManager em;

    @Test
    void testSectorAssociationContactPersistence() {

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

        em.persist(contact);
        em.flush();

        SectorAssociationContact savedContact = em.find(SectorAssociationContact.class, contact.getId());

        assertThat(savedContact).isNotNull();
        assertThat(savedContact.getId()).isNotNull();
        assertThat(savedContact.getFirstName()).isEqualTo("John");
    }
}
