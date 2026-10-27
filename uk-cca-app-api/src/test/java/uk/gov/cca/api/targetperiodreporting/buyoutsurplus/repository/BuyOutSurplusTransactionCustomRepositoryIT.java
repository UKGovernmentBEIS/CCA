package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import jakarta.persistence.EntityManager;

import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.CcaAccountContactType;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityAddress;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionListItemDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionsListSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityCalculatedResults;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityContainer;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEnergyFuelDetails;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityEntity;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityThroughputDetails;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@EnableAutoConfiguration
@EnableJpaAuditing
@ContextConfiguration(classes = BuyOutSurplusTransactionCustomRepository.class)
@DataJpaTest
@Import({ObjectMapper.class})
class BuyOutSurplusTransactionCustomRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private BuyOutSurplusTransactionCustomRepository repository;

    @Autowired
    private EntityManager entityManager;

    private BuyOutSurplusTransaction transactionAccount11;
    private BuyOutSurplusTransaction transactionAccount12;
    private BuyOutSurplusTransaction transactionAccount21;
    private BuyOutSurplusTransaction transactionAccount22;
    private BuyOutSurplusTransaction transactionAccount31;
    private BuyOutSurplusTransaction transactionAccount41;
    private BuyOutSurplusTransaction transactionAccount42;
    private BuyOutSurplusTransaction transactionAccount51;

    @BeforeEach
    void setup() {
        TargetUnitAccount account1 = createAccount(1L, 1L);
        TargetUnitAccount account2 = createAccount(2L, 1L);
        TargetUnitAccount account3 = createAccount(3L, 2L);
        TargetUnitAccount account4 = createAccount(4L, 1L);
        TargetUnitAccount account5 = createAccount(5L, 1L);

        TargetPeriod tp6 = createTargetPeriod(TargetPeriodType.TP6, SchemeVersion.CCA_2);
        TargetPeriod tp7 = createTargetPeriod(TargetPeriodType.TP7, SchemeVersion.CCA_3);
        TargetPeriod tp8 = createTargetPeriod(TargetPeriodType.TP8, SchemeVersion.CCA_3);

        // Account1 - Contains both Schemes
        BuyOutSurplusTransactionDetail detail11 = createTransactionDetail(1L, BuyOutSurplusPaymentStatus.AWAITING_PAYMENT);
        transactionAccount11 = createTransaction(11, account1.getId(), tp6, List.of(detail11));

        FacilityData facility12 = createFacility(12, account1.getId());
        PerformanceDataFacilityEntity performanceData12 = createPerformanceDataFacility(tp7, facility12.getId());
        BuyOutSurplusTransactionDetail detail12 = createTransactionDetail(performanceData12.getId(), BuyOutSurplusPaymentStatus.AWAITING_PAYMENT);
        FacilityData facility13 = createFacility(13, account1.getId());
        PerformanceDataFacilityEntity performanceData13 = createPerformanceDataFacility(tp7, facility13.getId());
        BuyOutSurplusTransactionDetail detail13 = createTransactionDetail(performanceData13.getId(), BuyOutSurplusPaymentStatus.UNDER_APPEAL);
        transactionAccount12 = createTransaction(12, account1.getId(), tp7, List.of(detail12, detail13));

        // Account2 - Contains only CCA3 Scheme
        FacilityData facility21 = createFacility(21, account2.getId());
        PerformanceDataFacilityEntity performanceData21 = createPerformanceDataFacility(tp7, facility21.getId());
        BuyOutSurplusTransactionDetail detail21 = createTransactionDetail(performanceData21.getId(), BuyOutSurplusPaymentStatus.AWAITING_PAYMENT);
        FacilityData facility22 = createFacility(22, account2.getId());
        PerformanceDataFacilityEntity performanceData22 = createPerformanceDataFacility(tp7, facility22.getId());
        BuyOutSurplusTransactionDetail detail22 = createTransactionDetail(performanceData22.getId(), BuyOutSurplusPaymentStatus.PAID);
        transactionAccount21 = createTransaction(21, account2.getId(), tp7, List.of(detail21, detail22));

        FacilityData facility23 = createFacility(23, account2.getId());
        PerformanceDataFacilityEntity performanceData23 = createPerformanceDataFacility(tp7, facility23.getId());
        BuyOutSurplusTransactionDetail detail23 = createTransactionDetail(performanceData23.getId(), BuyOutSurplusPaymentStatus.TERMINATED);
        FacilityData facility24 = createFacility(24, account2.getId());
        PerformanceDataFacilityEntity performanceData24 = createPerformanceDataFacility(tp7, facility24.getId());
        BuyOutSurplusTransactionDetail detail24 = createTransactionDetail(performanceData24.getId(), BuyOutSurplusPaymentStatus.AWAITING_REFUND);
        transactionAccount22 = createTransaction(22, account2.getId(), tp7, List.of(detail23, detail24));

        // Account3 - SectorAssociation 2 - Contains only CCA3 Scheme
        FacilityData facility31 = createFacility(31, account3.getId());
        PerformanceDataFacilityEntity performanceData31 = createPerformanceDataFacility(tp7, facility31.getId());
        BuyOutSurplusTransactionDetail detail31 = createTransactionDetail(performanceData31.getId(), BuyOutSurplusPaymentStatus.TERMINATED);
        FacilityData facility32 = createFacility(32, account3.getId());
        PerformanceDataFacilityEntity performanceData32 = createPerformanceDataFacility(tp7, facility32.getId());
        BuyOutSurplusTransactionDetail detail32 = createTransactionDetail(performanceData32.getId(), BuyOutSurplusPaymentStatus.REFUNDED);
        transactionAccount31 = createTransaction(31, account3.getId(), tp7, List.of(detail31, detail32));

        // Account4 - Contains only CCA2 Scheme
        BuyOutSurplusTransactionDetail detail41 = createTransactionDetail(2L, BuyOutSurplusPaymentStatus.PAID);
        transactionAccount41 = createTransaction(41, account4.getId(), tp6, List.of(detail41));

        BuyOutSurplusTransactionDetail detail42 = createTransactionDetail(3L, BuyOutSurplusPaymentStatus.REFUNDED);
        transactionAccount42 = createTransaction(42, account4.getId(), tp6, List.of(detail42));

        // Account5 - TP8 - Contains only CCA3 Scheme
        FacilityData facility51 = createFacility(51, account5.getId());
        PerformanceDataFacilityEntity performanceData51 = createPerformanceDataFacility(tp8, facility51.getId());
        BuyOutSurplusTransactionDetail detail51 = createTransactionDetail(performanceData51.getId(), BuyOutSurplusPaymentStatus.TERMINATED);
        FacilityData facility52 = createFacility(52, account5.getId());
        PerformanceDataFacilityEntity performanceData52 = createPerformanceDataFacility(tp8, facility52.getId());
        BuyOutSurplusTransactionDetail detail52 = createTransactionDetail(performanceData52.getId(), BuyOutSurplusPaymentStatus.NOT_REQUIRED);
        transactionAccount51 = createTransaction(51, account5.getId(), tp8, List.of(detail51, detail52));

        flushAndClear();
    }

    @Test
    void findBuyOutSurplusTransactions_CCA_2() {
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final BuyOutSurplusTransactionsListSearchCriteria criteria = BuyOutSurplusTransactionsListSearchCriteria.builder()
                .schemeVersion(SchemeVersion.CCA_2)
                .paging(PagingRequest.builder()
                        .pageNumber(0)
                        .pageSize(25)
                        .build())
                .build();

        Set<Long> expected = Set.of(transactionAccount11, transactionAccount41, transactionAccount42).stream()
                .map(BuyOutSurplusTransaction::getId).collect(Collectors.toSet());

        // Invoke
        BuyOutSurplusTransactionsListDTO result = repository
                .findBuyOutSurplusTransactions(competentAuthority, criteria);

        // Verify
        assertThat(result.getTotal()).isEqualTo(expected.size());
        assertThat(result.getTransactions())
                .extracting(BuyOutSurplusTransactionListItemDTO::getId).containsExactlyInAnyOrderElementsOf(expected);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount11))
                .isEqualTo(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount41))
                .isEqualTo(BuyOutSurplusPaymentStatus.PAID);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount42))
                .isEqualTo(BuyOutSurplusPaymentStatus.REFUNDED);

        assertThat(getTransactionBuyOutFee(result.getTransactions(), transactionAccount11))
                .isEqualByComparingTo(BigDecimal.valueOf(10));
        assertThat(getTransactionBuyOutFee(result.getTransactions(), transactionAccount41))
                .isEqualByComparingTo(BigDecimal.valueOf(10));
        assertThat(getTransactionBuyOutFee(result.getTransactions(), transactionAccount42))
                .isEqualByComparingTo(BigDecimal.valueOf(10));
    }

    @Test
    void findBuyOutSurplusTransactions_CCA_3() {
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final BuyOutSurplusTransactionsListSearchCriteria criteria = BuyOutSurplusTransactionsListSearchCriteria.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .paging(PagingRequest.builder()
                        .pageNumber(0)
                        .pageSize(25)
                        .build())
                .build();

        Set<Long> expected = Set.of(transactionAccount12, transactionAccount21, transactionAccount22, transactionAccount31, transactionAccount51).stream()
                .map(BuyOutSurplusTransaction::getId).collect(Collectors.toSet());

        // Invoke
        BuyOutSurplusTransactionsListDTO result = repository
                .findBuyOutSurplusTransactions(competentAuthority, criteria);

        // Verify
        assertThat(result.getTotal()).isEqualTo(expected.size());
        assertThat(result.getTransactions())
                .extracting(BuyOutSurplusTransactionListItemDTO::getId).containsExactlyInAnyOrderElementsOf(expected);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount12))
                .isEqualTo(BuyOutSurplusPaymentStatus.UNDER_APPEAL);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount21))
                .isEqualTo(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount22))
                .isEqualTo(BuyOutSurplusPaymentStatus.AWAITING_REFUND);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount31))
                .isEqualTo(BuyOutSurplusPaymentStatus.REFUNDED);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount51))
                .isEqualTo(BuyOutSurplusPaymentStatus.NOT_REQUIRED);

        assertThat(getTransactionBuyOutFee(result.getTransactions(), transactionAccount12))
                .isEqualByComparingTo(BigDecimal.valueOf(20));
        assertThat(getTransactionBuyOutFee(result.getTransactions(), transactionAccount21))
                .isEqualByComparingTo(BigDecimal.valueOf(20));
        assertThat(getTransactionBuyOutFee(result.getTransactions(), transactionAccount22))
                .isEqualByComparingTo(BigDecimal.valueOf(20));
        assertThat(getTransactionBuyOutFee(result.getTransactions(), transactionAccount31))
                .isEqualByComparingTo(BigDecimal.valueOf(20));
        assertThat(getTransactionBuyOutFee(result.getTransactions(), transactionAccount51))
                .isEqualByComparingTo(BigDecimal.valueOf(20));
    }

    @Test
    void findBuyOutSurplusTransactions_CCA_3_payment_status() {
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final BuyOutSurplusTransactionsListSearchCriteria criteria = BuyOutSurplusTransactionsListSearchCriteria.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .paging(PagingRequest.builder()
                        .pageNumber(0)
                        .pageSize(25)
                        .build())
                .build();

        Set<Long> expected = Set.of(transactionAccount22).stream()
                .map(BuyOutSurplusTransaction::getId).collect(Collectors.toSet());

        // Invoke
        BuyOutSurplusTransactionsListDTO result = repository
                .findBuyOutSurplusTransactions(competentAuthority, criteria);

        // Verify
        assertThat(result.getTotal()).isEqualTo(expected.size());
        assertThat(result.getTransactions())
                .extracting(BuyOutSurplusTransactionListItemDTO::getId).containsExactlyInAnyOrderElementsOf(expected);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount22))
                .isEqualTo(BuyOutSurplusPaymentStatus.AWAITING_REFUND);

        assertThat(getTransactionBuyOutFee(result.getTransactions(), transactionAccount22))
                .isEqualByComparingTo(BigDecimal.valueOf(20));
    }

    @Test
    void findBuyOutSurplusTransactions_CCA_3_with_paging() {
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final BuyOutSurplusTransactionsListSearchCriteria criteria = BuyOutSurplusTransactionsListSearchCriteria.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .paging(PagingRequest.builder()
                        .pageNumber(1)
                        .pageSize(4)
                        .build())
                .build();

        Set<Long> expected = Set.of(transactionAccount51).stream()
                .map(BuyOutSurplusTransaction::getId).collect(Collectors.toSet());

        // Invoke
        BuyOutSurplusTransactionsListDTO result = repository
                .findBuyOutSurplusTransactions(competentAuthority, criteria);

        // Verify
        assertThat(result.getTotal()).isEqualTo(5);
        assertThat(result.getTransactions())
                .extracting(BuyOutSurplusTransactionListItemDTO::getId).containsExactlyInAnyOrderElementsOf(expected);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount51))
                .isEqualTo(BuyOutSurplusPaymentStatus.NOT_REQUIRED);

        assertThat(getTransactionBuyOutFee(result.getTransactions(), transactionAccount51))
                .isEqualByComparingTo(BigDecimal.valueOf(20));
    }

    @Test
    void findBuyOutSurplusTransactions_TP8() {
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final BuyOutSurplusTransactionsListSearchCriteria criteria = BuyOutSurplusTransactionsListSearchCriteria.builder()
                .schemeVersion(SchemeVersion.CCA_3)
                .targetPeriodType(TargetPeriodType.TP8)
                .paging(PagingRequest.builder()
                        .pageNumber(0)
                        .pageSize(25)
                        .build())
                .build();

        Set<Long> expected = Set.of(transactionAccount51).stream()
                .map(BuyOutSurplusTransaction::getId).collect(Collectors.toSet());

        // Invoke
        BuyOutSurplusTransactionsListDTO result = repository
                .findBuyOutSurplusTransactions(competentAuthority, criteria);

        // Verify
        assertThat(result.getTotal()).isEqualTo(expected.size());
        assertThat(result.getTransactions())
                .extracting(BuyOutSurplusTransactionListItemDTO::getId).containsExactlyInAnyOrderElementsOf(expected);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount51))
                .isEqualTo(BuyOutSurplusPaymentStatus.NOT_REQUIRED);
    }

    @Test
    void findBuyOutSurplusTransactions_term_Target_Unit_id() {
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final BuyOutSurplusTransactionsListSearchCriteria criteria = BuyOutSurplusTransactionsListSearchCriteria.builder()
                .term("businessId5")
                .schemeVersion(SchemeVersion.CCA_3)
                .paging(PagingRequest.builder()
                        .pageNumber(0)
                        .pageSize(25)
                        .build())
                .build();

        Set<Long> expected = Set.of(transactionAccount51).stream()
                .map(BuyOutSurplusTransaction::getId).collect(Collectors.toSet());

        // Invoke
        BuyOutSurplusTransactionsListDTO result = repository
                .findBuyOutSurplusTransactions(competentAuthority, criteria);

        // Verify
        assertThat(result.getTotal()).isEqualTo(expected.size());
        assertThat(result.getTransactions())
                .extracting(BuyOutSurplusTransactionListItemDTO::getId).containsExactlyInAnyOrderElementsOf(expected);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount51))
                .isEqualTo(BuyOutSurplusPaymentStatus.NOT_REQUIRED);
    }

    @Test
    void findBuyOutSurplusTransactions_term_Operator_name() {
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final BuyOutSurplusTransactionsListSearchCriteria criteria = BuyOutSurplusTransactionsListSearchCriteria.builder()
                .term("name4")
                .schemeVersion(SchemeVersion.CCA_2)
                .paging(PagingRequest.builder()
                        .pageNumber(0)
                        .pageSize(25)
                        .build())
                .build();

        Set<Long> expected = Set.of(transactionAccount41, transactionAccount42).stream()
                .map(BuyOutSurplusTransaction::getId).collect(Collectors.toSet());

        // Invoke
        BuyOutSurplusTransactionsListDTO result = repository
                .findBuyOutSurplusTransactions(competentAuthority, criteria);

        // Verify
        assertThat(result.getTotal()).isEqualTo(expected.size());
        assertThat(result.getTransactions())
                .extracting(BuyOutSurplusTransactionListItemDTO::getId).containsExactlyInAnyOrderElementsOf(expected);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount41))
                .isEqualTo(BuyOutSurplusPaymentStatus.PAID);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount42))
                .isEqualTo(BuyOutSurplusPaymentStatus.REFUNDED);
    }

    @Test
    void findBuyOutSurplusTransactions_term_Transaction_id() {
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final BuyOutSurplusTransactionsListSearchCriteria criteria = BuyOutSurplusTransactionsListSearchCriteria.builder()
                .term("CCA060041")
                .schemeVersion(SchemeVersion.CCA_2)
                .paging(PagingRequest.builder()
                        .pageNumber(0)
                        .pageSize(25)
                        .build())
                .build();

        Set<Long> expected = Set.of(transactionAccount41).stream()
                .map(BuyOutSurplusTransaction::getId).collect(Collectors.toSet());

        // Invoke
        BuyOutSurplusTransactionsListDTO result = repository
                .findBuyOutSurplusTransactions(competentAuthority, criteria);

        // Verify
        assertThat(result.getTotal()).isEqualTo(expected.size());
        assertThat(result.getTransactions())
                .extracting(BuyOutSurplusTransactionListItemDTO::getId).containsExactlyInAnyOrderElementsOf(expected);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount41))
                .isEqualTo(BuyOutSurplusPaymentStatus.PAID);
    }

    @Test
    void findBuyOutSurplusTransactions_term_facility_id() {
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final BuyOutSurplusTransactionsListSearchCriteria criteria = BuyOutSurplusTransactionsListSearchCriteria.builder()
                .term("FAC31")
                .schemeVersion(SchemeVersion.CCA_3)
                .paging(PagingRequest.builder()
                        .pageNumber(0)
                        .pageSize(25)
                        .build())
                .build();

        Set<Long> expected = Set.of(transactionAccount31).stream()
                .map(BuyOutSurplusTransaction::getId).collect(Collectors.toSet());

        // Invoke
        BuyOutSurplusTransactionsListDTO result = repository
                .findBuyOutSurplusTransactions(competentAuthority, criteria);

        // Verify
        assertThat(result.getTotal()).isEqualTo(expected.size());
        assertThat(result.getTransactions())
                .extracting(BuyOutSurplusTransactionListItemDTO::getId).containsExactlyInAnyOrderElementsOf(expected);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount31))
                .isEqualTo(BuyOutSurplusPaymentStatus.REFUNDED);
    }

    @Test
    void findBuyOutSurplusTransactions_term_facility_site_name() {
        final CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        final BuyOutSurplusTransactionsListSearchCriteria criteria = BuyOutSurplusTransactionsListSearchCriteria.builder()
                .term("facilityName31")
                .schemeVersion(SchemeVersion.CCA_3)
                .paging(PagingRequest.builder()
                        .pageNumber(0)
                        .pageSize(25)
                        .build())
                .build();

        Set<Long> expected = Set.of(transactionAccount31).stream()
                .map(BuyOutSurplusTransaction::getId).collect(Collectors.toSet());

        // Invoke
        BuyOutSurplusTransactionsListDTO result = repository
                .findBuyOutSurplusTransactions(competentAuthority, criteria);

        // Verify
        assertThat(result.getTotal()).isEqualTo(expected.size());
        assertThat(result.getTransactions())
                .extracting(BuyOutSurplusTransactionListItemDTO::getId).containsExactlyInAnyOrderElementsOf(expected);
        assertThat(getTransactionPaymentStatus(result.getTransactions(), transactionAccount31))
                .isEqualTo(BuyOutSurplusPaymentStatus.REFUNDED);
    }

    private TargetUnitAccount createAccount(Long id, Long sectorId) {
        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(id)
                .businessId("businessId" + id)
                .name("name" + id)
                .sectorAssociationId(sectorId)
                .status(TargetUnitAccountStatus.LIVE)
                .acceptedDate(LocalDate.of(2023, 11, 3).atStartOfDay())
                .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .createdBy("user1")
                .address(createAddress())
                .contacts(Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"))
                .build();
        entityManager.persist(account);
        return account;
    }

    private TargetPeriod createTargetPeriod(TargetPeriodType targetPeriodType, SchemeVersion schemeVersion) {
        TargetPeriod targetPeriod = TargetPeriod.builder()
                .businessId(targetPeriodType)
                .name(targetPeriodType.name())
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
                .schemeVersion(schemeVersion)
                .build();
        entityManager.persist(targetPeriod);
        return targetPeriod;
    }

    private BuyOutSurplusTransactionDetail createTransactionDetail(long performanceDataId, BuyOutSurplusPaymentStatus paymentStatus) {
        return BuyOutSurplusTransactionDetail.builder()
                .performanceDataId(performanceDataId)
                .buyOutFee(BigDecimal.valueOf(10))
                .paymentStatus(paymentStatus)
                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                        .invoicedBuyOutFee(BigDecimal.ONE)
                        .invoicedSurplusGained(BigDecimal.TWO)
                        .priBuyOutCost(BigDecimal.TEN)
                        .invoicedPreviousPaidFees(BigDecimal.TEN).build())
                .paymentDate(LocalDate.of(2022, 1, 1))
                .build();
    }

    private BuyOutSurplusTransaction createTransaction(long identity, long accountId, TargetPeriod targetPeriod, List<BuyOutSurplusTransactionDetail> details) {
        BuyOutSurplusTransaction transaction = BuyOutSurplusTransaction.builder()
                .transactionCode("CCA0600" + identity)
                .accountId(accountId)
                .targetPeriod(targetPeriod)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .invoicedBuyOutFee(BigDecimal.valueOf(identity))
                .fileDocumentUuid("uuid")
                .performanceDataResourceType(targetPeriod.getBusinessId().equals(TargetPeriodType.TP6)
                        ? PerformanceDataResourceType.ACCOUNT
                        : PerformanceDataResourceType.FACILITY)
                .build();
        details.forEach(transaction::addDetail);
        entityManager.persist(transaction);
        return transaction;
    }

    private FacilityData createFacility(long identity, Long accountId) {

        FacilityAddress address = FacilityAddress.builder()
                .line1("123 Test Street")
                .city("Test City")
                .postcode("12345")
                .country("Test Country")
                .build();
        entityManager.persist(address);

        FacilityData facility = FacilityData.builder()
                .facilityBusinessId("FAC" + identity)
                .accountId(accountId)
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                .siteName("facilityName" + identity)
                .address(address)
                .createdDate(LocalDateTime.now())
                .build();

        entityManager.persist(facility);

        return facility;
    }

    private PerformanceDataFacilityEntity createPerformanceDataFacility(TargetPeriod targetPeriod, Long facilityId) {
        PerformanceDataFacilityEntity facility = PerformanceDataFacilityEntity.builder()
                .facilityId(facilityId)
                .reportVersion(10)
                .targetPeriod(targetPeriod)
                .targetPeriodYear(Year.of(2026))
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .data(PerformanceDataFacilityContainer.builder()
                        .energyFuelDetails(PerformanceDataFacilityEnergyFuelDetails.builder()
                                .atLeastSeventyPercentEnergyUsed(Boolean.FALSE)
                                .build())
                        .baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder().build())
                        .throughputDetails(PerformanceDataFacilityThroughputDetails.builder()
                                .totalTargetVariableEnergy(BigDecimal.ONE)
                                .build())
                        .calculatedResults(PerformanceDataFacilityCalculatedResults.builder()
                                .actualEnergyCarbon(BigDecimal.valueOf(1.99999))
                                .actualImprovement(BigDecimal.valueOf(0.99999))
                                .targetImprovement(BigDecimal.ONE)
                                .targetEnergyCarbon(BigDecimal.ONE)
                                .targetCo2Emissions(BigDecimal.ONE)
                                .energyCarbonDifference(BigDecimal.valueOf(10.15))
                                .weightedConversionFactor(BigDecimal.ONE)
                                .co2EmissionsDifference(BigDecimal.ONE)
                                .actualCo2Emissions(BigDecimal.ONE)
                                .targetPeriodResultType(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                                .buyOutRequired(BigDecimal.ZERO)
                                .surplusGained(BigDecimal.valueOf(5))
                                .build())
                        .build())
                .build();
        entityManager.persist(facility);
        return facility;
    }

    private AccountAddress createAddress() {
        AccountAddress address = AccountAddress.builder().line1("123 Test Street").city("Test City").postcode("12345")
                .country("Test Country").build();
        entityManager.persist(address);
        return address;
    }

    private BuyOutSurplusPaymentStatus getTransactionPaymentStatus(List<BuyOutSurplusTransactionListItemDTO> result,
                                                                   BuyOutSurplusTransaction transaction) {
        return result.stream()
                .filter(r -> r.getId().equals(transaction.getId())).findFirst()
                .map(BuyOutSurplusTransactionListItemDTO::getPaymentStatus).orElse(null);
    }

    private BigDecimal getTransactionBuyOutFee(List<BuyOutSurplusTransactionListItemDTO> result,
                                                                   BuyOutSurplusTransaction transaction) {
        return result.stream()
                .filter(r -> r.getId().equals(transaction.getId())).findFirst()
                .map(BuyOutSurplusTransactionListItemDTO::getBuyOutFee).orElse(null);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
