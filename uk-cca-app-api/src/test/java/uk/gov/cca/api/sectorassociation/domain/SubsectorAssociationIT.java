package uk.gov.cca.api.sectorassociation.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class SubsectorAssociationIT extends AbstractContainerBaseTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testSubsectorAssociationPersistence() {
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
    	
    	SectorAssociation sectorAssociation = SectorAssociation.builder()
    			.competentAuthority(CompetentAuthorityEnum.ENGLAND)
    			.name("name")
    			.acronym("acronym")
    			.legalName("legal name")
    			.energyEprFactor("energyEprFactor")
    			.location(location)
    			.sectorAssociationContact(contact)
    			.build();
    	
    	entityManager.persist(sectorAssociation);
    			
    	SubsectorAssociation subsectorAssociation = SubsectorAssociation.builder()
    		.name("name")
    		.sectorAssociation(sectorAssociation)
            .build();

        entityManager.persist(subsectorAssociation);
        entityManager.flush();

        SubsectorAssociation savedSubsectorAssociation = entityManager.find(SubsectorAssociation.class, subsectorAssociation.getId());

        assertThat(savedSubsectorAssociation).isNotNull();
        assertThat(savedSubsectorAssociation.getId()).isNotNull();
        assertThat(savedSubsectorAssociation.getName()).isEqualTo("name");
    }
}
