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

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class TargetCommitmentIT extends AbstractContainerBaseTest {

    @Autowired
    private EntityManager entityManager;

    private TargetSet targetSet;

    @BeforeEach
    void setUp() {
        targetSet = TargetSet.builder()
                .targetCurrencyType("currency type")
                .energyOrCarbonUnit("carbon")
                .build();

        entityManager.persist(targetSet);
        entityManager.flush();
    }

    @Test
    void testSchemeTargetCommitmentPersistence() {
        TargetCommitment targetCommitment = TargetCommitment.builder()
                .targetImprovement(BigDecimal.valueOf(15.000))
                .targetPeriod("period")
                .targetSet(targetSet)
                .build();

        entityManager.persist(targetCommitment);
        entityManager.flush();

        TargetCommitment savedTargetCommitment = entityManager.find(TargetCommitment.class, targetCommitment.getId());

        assertThat(savedTargetCommitment).isNotNull();
        assertThat(savedTargetCommitment.getId()).isNotNull();
        assertThat(savedTargetCommitment.getTargetPeriod()).isEqualTo("period");
    }
}
