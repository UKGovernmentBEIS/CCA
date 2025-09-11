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
class TargetSetIT extends AbstractContainerBaseTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void testSchemePersistence() {
        TargetSet targetSet = TargetSet.builder()
                .targetCurrencyType("currency type")
                .energyOrCarbonUnit("carbon")
                .build();

        entityManager.persist(targetSet);
        entityManager.flush();

        TargetSet savedScheme = entityManager.find(TargetSet.class, targetSet.getId());

        assertThat(savedScheme).isNotNull();
        assertThat(savedScheme.getId()).isNotNull();
        assertThat(savedScheme.getEnergyOrCarbonUnit()).isEqualTo("carbon");
    }
}
