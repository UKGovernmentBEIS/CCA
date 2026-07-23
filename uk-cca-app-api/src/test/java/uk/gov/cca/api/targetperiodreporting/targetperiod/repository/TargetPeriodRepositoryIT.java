package uk.gov.cca.api.targetperiodreporting.targetperiod.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class TargetPeriodRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private TargetPeriodRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldFindTargetPeriodByBusinessId() {
        TargetPeriodType businessId = TargetPeriodType.TP7;

        TargetPeriod targetPeriod = createTargetPeriod(businessId, SchemeVersion.CCA_3,
                LocalDate.of(2026, 1, 1));

        entityManager.persist(targetPeriod);
        flushAndClear();

        Optional<TargetPeriod> result = repository.findByBusinessId(businessId);

        assertThat(result).isPresent();
        assertThat(result.get().getBusinessId()).isEqualTo(businessId);
    }

    @Test
    void shouldReturnEmptyWhenTargetPeriodByBusinessIdNotFound() {
        Optional<TargetPeriod> result = repository.findByBusinessId(TargetPeriodType.TP6);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindAllBySchemeVersion() {
        TargetPeriod differentTargetPeriod = createTargetPeriod(
                TargetPeriodType.TP6,
                SchemeVersion.CCA_2,
                LocalDate.of(2024, 1, 1));

        TargetPeriod matchingTargetPeriod = createTargetPeriod(
                TargetPeriodType.TP7,
                SchemeVersion.CCA_3,
                LocalDate.of(2024, 1, 1));

        entityManager.persist(matchingTargetPeriod);
        entityManager.persist(differentTargetPeriod);
        flushAndClear();

        List<TargetPeriod> result = repository.findAllBySchemeVersion(SchemeVersion.CCA_3);

        assertThat(result)
                .hasSize(1)
                .contains(matchingTargetPeriod);
    }

    @Test
    void shouldFindByBusinessIdIn() {
        TargetPeriod targetPeriod1 = createTargetPeriod(
                TargetPeriodType.TP6,
                SchemeVersion.CCA_2,
                LocalDate.of(2024, 1, 1));

        TargetPeriod targetPeriod2 = createTargetPeriod(
                TargetPeriodType.TP7,
                SchemeVersion.CCA_3,
                LocalDate.of(2024, 1, 1));

        entityManager.persist(targetPeriod1);
        entityManager.persist(targetPeriod2);
        flushAndClear();

        List<TargetPeriod> result = repository.findByBusinessIdIn(Set.of(TargetPeriodType.TP6));

        assertThat(result)
                .hasSize(1)
                .contains(targetPeriod1);
    }

    @Test
    void shouldFindBuyOutCurrentAndPreviousOrderedByBuyOutStartDateDesc() {
        TargetPeriod older = createTargetPeriod(
                TargetPeriodType.TP6,
                SchemeVersion.CCA_2,
                LocalDate.of(2023, 1, 1));

        TargetPeriod newer = createTargetPeriod(
                TargetPeriodType.TP7,
                SchemeVersion.CCA_3,
                LocalDate.of(2024, 1, 1));

        TargetPeriod future = createTargetPeriod(
                TargetPeriodType.TP8,
                SchemeVersion.CCA_3,
                LocalDate.of(2030, 1, 1));

        entityManager.persist(older);
        entityManager.persist(newer);
        entityManager.persist(future);
        flushAndClear();

        List<TargetPeriod> result = repository
                .findByBuyOutStartDateLessThanEqualOrderByBuyOutStartDateDesc(LocalDate.of(2025, 1, 1));

        assertThat(result).containsExactly(newer, older);
    }

    private TargetPeriod createTargetPeriod(TargetPeriodType businessId,
                                             SchemeVersion schemeVersion,
                                             LocalDate buyOutStartDate) {
        return TargetPeriod.builder()
                .businessId(businessId)
                .name("Test Target Period " + businessId)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2030, 12, 31))
                .performanceDataTemplateVersion("1")
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                		.targetPeriodYears(List.of(TargetPeriodYear.builder()
                				.targetYear(Year.of(2026))
                				.reportingStartDate(buyOutStartDate)
                				.endDate(buyOutStartDate)
                				.startDate(buyOutStartDate)
                				.build()))
                		.build())
                .buyOutStartDate(buyOutStartDate)
                .buyOutPrimaryPaymentDeadline(buyOutStartDate.plusYears(1))
                .secondaryReportingStartDate(buyOutStartDate.plusYears(2))
                .schemeVersion(schemeVersion)
                .build();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}