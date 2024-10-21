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

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
public class SubsectorAssociationIT extends AbstractContainerBaseTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testSubsectorAssociationPersistence() {
        SubsectorAssociation subsectorAssociation = SubsectorAssociation.builder()
                .name("name")
                .build();

        entityManager.persist(subsectorAssociation);
        entityManager.flush();

        SubsectorAssociation savedSubsectorAssociation = entityManager.find(SubsectorAssociation.class, subsectorAssociation.getId());

        assertThat(savedSubsectorAssociation).isNotNull();
        assertThat(savedSubsectorAssociation.getId()).isNotNull();
        assertThat(savedSubsectorAssociation.getName()).isEqualTo("name");
    }
}
