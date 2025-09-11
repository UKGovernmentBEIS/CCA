package uk.gov.cca.api.targetperiodreporting.facilitycertification.repository;

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
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertification;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class FacilityCertificationRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private FacilityCertificationRepository repository;

    @Autowired
    private EntityManager entityManager;

    private final LocalDate startDate = LocalDate.of(2025, 4, 1);
    private final Set<Long> facilityIds = Set.of(1L, 2L, 3L, 4L, 5L);

    @BeforeEach
    void setUp() {
        FacilityCertification facilityCertification1 = FacilityCertification.builder()
                .facilityId(1L)
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .certificationPeriodId(2L)
                .startDate(startDate)
                .build();
        entityManager.persist(facilityCertification1);

        FacilityCertification facilityCertification2 = FacilityCertification.builder()
                .facilityId(2L)
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .certificationPeriodId(2L)
                .startDate(startDate)
                .build();
        entityManager.persist(facilityCertification2);

        FacilityCertification facilityCertification3 = FacilityCertification.builder()
                .facilityId(3L)
                .certificationStatus(FacilityCertificationStatus.DECERTIFIED)
                .certificationPeriodId(2L)
                .startDate(startDate)
                .build();
        entityManager.persist(facilityCertification3);

        FacilityCertification facilityCertification4 = FacilityCertification.builder()
                .facilityId(4L)
                .certificationStatus(FacilityCertificationStatus.NOT_YET_DEFINED)
                .certificationPeriodId(2L)
                .startDate(startDate)
                .build();
        entityManager.persist(facilityCertification4);

        FacilityCertification facilityCertification5 = FacilityCertification.builder()
                .facilityId(5L)
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .certificationPeriodId(1L)
                .startDate(startDate)
                .build();
        entityManager.persist(facilityCertification5);

        FacilityCertification facilityCertification6 = FacilityCertification.builder()
                .facilityId(5L)
                .certificationStatus(FacilityCertificationStatus.NOT_YET_DEFINED)
                .certificationPeriodId(2L)
                .startDate(startDate)
                .build();
        entityManager.persist(facilityCertification6);
    }

    @Test
    void findAllByFacilityIdInAndCertificationPeriodId() {

        List<FacilityCertification> result =
                repository.findAllByFacilityIdInAndCertificationPeriodId(facilityIds, 1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getFacilityId()).isEqualTo(5L);
    }

    @Test
    void findAllByFacilityId() {
        List<FacilityCertification> facilityCertifications = repository.findAllByFacilityId(5L);

        assertThat(facilityCertifications).hasSize(2);
    }

    @AfterEach
    void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
