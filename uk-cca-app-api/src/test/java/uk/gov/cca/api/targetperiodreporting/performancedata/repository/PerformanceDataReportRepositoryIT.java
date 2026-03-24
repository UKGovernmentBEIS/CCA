package uk.gov.cca.api.targetperiodreporting.performancedata.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.CcaAccountContactType;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.SectorAccountPerformanceDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@EnableAutoConfiguration
@EnableJpaAuditing
@ContextConfiguration(classes = PerformanceDataReportRepository.class)
@DataJpaTest
@Import(ObjectMapper.class)
class PerformanceDataReportRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private PerformanceDataReportRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        createTargetPeriods();
        createAccount(-1L, TargetUnitAccountStatus.LIVE, LocalDate.of(2023, 11, 3), null);
        createAccount(-2L, TargetUnitAccountStatus.LIVE, LocalDate.of(2024, 12, 31), null);
        createAccount(-3L, TargetUnitAccountStatus.LIVE, LocalDate.of(2025, 1, 1), null);

        createAccount(-4L, TargetUnitAccountStatus.TERMINATED, LocalDate.of(2023, 1, 3), LocalDate.of(2025, 2, 3));
        createAccount(-5L, TargetUnitAccountStatus.TERMINATED, LocalDate.of(2022, 1, 3), LocalDate.of(2024, 12, 31));

        flushAndClear();
    }

    @Test
    void findEligibleAccountsForPerformanceDataReportingBySector() {
        final Long sectorAssociationId = 1L;
        final SectorAccountPerformanceDataReportSearchCriteria criteria =
                SectorAccountPerformanceDataReportSearchCriteria.builder()
                        .targetPeriodType(TargetPeriodType.TP6)
                        .paging(PagingRequest.builder().pageNumber(0).pageSize(10).build())
                        .build();
        // Invoke
        SectorAccountPerformanceDataReportListDTO result = repository
                .getSectorAccountPerformanceDataReportListBySearchCriteria(sectorAssociationId, criteria);

        // Verify
        assertThat(result.getTotal()).isEqualTo(3L);
        assertThat(result.getPerformanceDataReportItems())
                .extracting(SectorAccountPerformanceDataReportItemDTO::getAccountId)
                .containsExactlyInAnyOrder(-1L, -2L, -4L);
    }

    private void createTargetPeriods() {
        TargetPeriod tp6 = TargetPeriod.builder()
                .businessId(TargetPeriodType.TP6)
                .name(TargetPeriodType.TP6.name())
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 12, 31))
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(TargetPeriodYear.builder()
                                .targetYear(Year.of(2024))
                                .startDate(LocalDate.of(2024, 1, 1))
                                .endDate(LocalDate.of(2024, 12, 31))
                                .reportingStartDate(LocalDate.of(2025, 1, 1))
                                .reportingEndDate(LocalDate.of(2027, 6, 30))
                                .build()))
                        .build())
                .buyOutStartDate(LocalDate.now())
                .buyOutPrimaryPaymentDeadline(LocalDate.now())
                .secondaryReportingStartDate(LocalDate.now())
                .build();
        entityManager.persist(tp6);

        TargetPeriod tp7 = TargetPeriod.builder()
                .businessId(TargetPeriodType.TP7)
                .name(TargetPeriodType.TP7.name())
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 12, 31))
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(TargetPeriodYear.builder()
                                .targetYear(Year.of(2026))
                                .startDate(LocalDate.of(2026, 1, 1))
                                .endDate(LocalDate.of(2026, 12, 31))
                                .reportingStartDate(LocalDate.of(2027, 1, 1))
                                .build()))
                        .build())
                .buyOutStartDate(LocalDate.now())
                .buyOutPrimaryPaymentDeadline(LocalDate.now())
                .secondaryReportingStartDate(LocalDate.now())
                .build();
        entityManager.persist(tp7);
    }

    private void createAccount(Long id, TargetUnitAccountStatus status, LocalDate acceptedDate, LocalDate terminatedDate) {
        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(id)
                .businessId("businessId" + id)
                .name("name" + id)
                .sectorAssociationId(1L)
                .status(status)
                .acceptedDate(acceptedDate.atTime(13, 0))
                .terminatedDate(terminatedDate == null ? null : terminatedDate.atStartOfDay())
                .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .createdBy("user1")
                .address(createAddress())
                .contacts(Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"))
                .build();
        entityManager.persist(account);
    }

    private AccountAddress createAddress() {
        AccountAddress address = AccountAddress.builder().line1("123 Test Street").city("Test City").postcode("12345")
                .country("Test Country").build();
        entityManager.persist(address);
        return address;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
