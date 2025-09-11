package uk.gov.cca.api.subsistencefees.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.subsistencefees.domain.ChargingPeriod;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class ChargingPeriodRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private ChargingPeriodRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        Year y2025 = Year.of(2025);
        ChargingPeriod cp2025 = ChargingPeriod.builder()
                .startDate(LocalDate.of(2025, 4, 1))
                .endDate(LocalDate.of(2026, 3, 31))
                .chargingYear(y2025)
                .build();
        entityManager.persist(cp2025);

        Year y2026 = Year.of(2026);
        ChargingPeriod cp2026 = ChargingPeriod.builder()
                .startDate(LocalDate.of(2026, 4, 1))
                .endDate(LocalDate.of(2027, 3, 31))
                .chargingYear(y2026)
                .build();
        entityManager.persist(cp2026);

        Year y2027 = Year.of(2027);
        ChargingPeriod cp2027 = ChargingPeriod.builder()
                .startDate(LocalDate.of(2027, 4, 1))
                .endDate(LocalDate.of(2028, 3, 31))
                .chargingYear(y2027)
                .build();
        entityManager.persist(cp2027);

        Year y2028 = Year.of(2028);
        ChargingPeriod cp2028 = ChargingPeriod.builder()
                .startDate(LocalDate.of(2028, 4, 1))
                .endDate(LocalDate.of(2029, 3, 31))
                .chargingYear(y2028)
                .build();
        entityManager.persist(cp2028);
    }

    @Test
    void findChargingYear_empty_results() {
        LocalDate currentDate = LocalDate.of(2029, 4, 1);

        Optional<Year> results = repository.findChargingYear(currentDate);

        assertThat(results).isEmpty();
    }

    @Test
    void findChargingYear_empty_results_2025() {
        LocalDate currentDate = LocalDate.of(2025, 1, 1);

        Optional<Year> results = repository.findChargingYear(currentDate);

        assertThat(results).isEmpty();
    }

    @Test
    void findChargingYear_2026() {
        Year y2026 = Year.of(2026);
        LocalDate currentDate = LocalDate.of(2026, 12, 12);

        Optional<Year> results = repository.findChargingYear(currentDate);

        assertThat(results).contains(y2026);
    }

    @Test
    void findChargingYear_2027() {
        Year y2027 = Year.of(2027);
        LocalDate currentDate = LocalDate.of(2027, 4, 1);

        Optional<Year> results = repository.findChargingYear(currentDate);

        assertThat(results).contains(y2027);
    }

    @Test
    void findChargingYear_2028() {
        Year y2028 = Year.of(2028);
        LocalDate currentDate = LocalDate.of(2029, 2, 22);

        Optional<Year> results = repository.findChargingYear(currentDate);

        assertThat(results).contains(y2028);
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
