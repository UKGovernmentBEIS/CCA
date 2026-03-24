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
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
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
@ContextConfiguration(classes = AccountPerformanceDataStatusRepository.class)
@DataJpaTest
@Import(ObjectMapper.class)
class AccountPerformanceDataStatusRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AccountPerformanceDataStatusRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        createTargetPeriod();
        createAccount(-1L, TargetUnitAccountStatus.LIVE, LocalDate.of(2023, 11, 3), null);
        createAccount(-2L, TargetUnitAccountStatus.LIVE, LocalDate.of(2023, 12, 3), null);
        createAccount(-3L, TargetUnitAccountStatus.TERMINATED, LocalDate.of(2023, 1, 3), LocalDate.of(2023, 2, 3));
        createAccount(-4L, TargetUnitAccountStatus.TERMINATED, LocalDate.of(2022, 1, 3), LocalDate.of(2024, 1, 3));

        flushAndClear();
    }

    @Test
    void findEligibleAccountsForPerformanceDataReportingBySector() {
        final Long sectorAssociationId = 1L;
        final Long targetPeriodId = 1L;

        // Invoke
        List<TargetUnitAccountBusinessInfoDTO> result = repository
                .findEligibleAccountsForPerformanceDataReportingBySector(sectorAssociationId, targetPeriodId);

        // Verify
        assertThat(result).hasSize(2).containsExactlyInAnyOrder(
                TargetUnitAccountBusinessInfoDTO.builder().accountId(-1L).businessId("businessId-1").name("name-1").build(),
                TargetUnitAccountBusinessInfoDTO.builder().accountId(-4L).businessId("businessId-4").name("name-4").build()
        );
    }

    private void createTargetPeriod() {
        TargetPeriod targetPeriod = TargetPeriod.builder()
                .businessId(TargetPeriodType.TP6)
                .name(TargetPeriodType.TP6.name())
                .startDate(LocalDate.now())
                .endDate(LocalDate.of(2023, 11, 3))
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(TargetPeriodYear.builder()
                                .targetYear(Year.now())
                                .startDate(LocalDate.now())
                                .endDate(LocalDate.of(2023, 11, 3))
                                .reportingStartDate(LocalDate.now())
                                .build()))
                        .build())
                .buyOutStartDate(LocalDate.now())
                .buyOutPrimaryPaymentDeadline(LocalDate.now())
                .secondaryReportingStartDate(LocalDate.now())
                .build();
        entityManager.persist(targetPeriod);
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
