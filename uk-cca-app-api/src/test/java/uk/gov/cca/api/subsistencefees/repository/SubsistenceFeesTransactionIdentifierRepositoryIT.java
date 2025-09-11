package uk.gov.cca.api.subsistencefees.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesTransactionIdentifier;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class SubsistenceFeesTransactionIdentifierRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private SubsistenceFeesTransactionIdentifierRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        SubsistenceFeesTransactionIdentifier transactionIdentifier1 = SubsistenceFeesTransactionIdentifier.builder()
                .id(1)
                .transactionId(1199L)
                .moaType(MoaType.SECTOR_MOA)
                .build();
        entityManager.persist(transactionIdentifier1);

        SubsistenceFeesTransactionIdentifier transactionIdentifier2 = SubsistenceFeesTransactionIdentifier.builder()
                .id(2)
                .transactionId(1199L)
                .moaType(MoaType.TARGET_UNIT_MOA)
                .build();
        entityManager.persist(transactionIdentifier2);
    }

    @Test
    void findByMoaType_when_SECTOR() {
        // invoke
        Optional<SubsistenceFeesTransactionIdentifier> transactionIdentifier = repository.findByMoaType(MoaType.SECTOR_MOA);
        // verify
        assertThat(transactionIdentifier).isPresent();
        assertThat(transactionIdentifier.get().getMoaType()).isEqualTo(MoaType.SECTOR_MOA);
        assertThat(transactionIdentifier.get().getTransactionId()).isEqualTo(1199L);
    }

    @Test
    void findByMoaType_when_TARGET_UNIT() {
        // invoke
        Optional<SubsistenceFeesTransactionIdentifier> transactionIdentifier = repository.findByMoaType(MoaType.TARGET_UNIT_MOA);
        // verify
        assertThat(transactionIdentifier).isPresent();
        assertThat(transactionIdentifier.get().getMoaType()).isEqualTo(MoaType.TARGET_UNIT_MOA);
        assertThat(transactionIdentifier.get().getTransactionId()).isEqualTo(1199L);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
        flushAndClear();
    }


    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
