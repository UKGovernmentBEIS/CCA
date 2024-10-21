package uk.gov.cca.api.sectorassociation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.netz.api.common.AbstractContainerBaseTest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
public class LocationIT extends AbstractContainerBaseTest {

    @Autowired
    private EntityManager em;

    @Test
    void testLocationPersistence() {

        Location location = Location.builder()
            .postcode("12345")
            .line1("123 Main St")
            .city("Springfield")
            .county("CountyName")
            .build();

        em.persist(location);
        em.flush();

        Location savedLocation = em.find(Location.class, location.getId());

        assertThat(savedLocation).isNotNull();
        assertThat(savedLocation.getId()).isNotNull();
        assertThat(savedLocation.getPostcode()).isEqualTo("12345");
    }
}