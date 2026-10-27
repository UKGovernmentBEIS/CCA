package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailSummary;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.service.BuyOutSurplusManagementService;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountProcessingBaseRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityAccountProcessingUpdateTransactionServiceTest {

    @InjectMocks
    private BuyOutSurplusFacilityAccountProcessingUpdateTransactionService persistTransactionService;

    @Mock
    private BuyOutSurplusTransactionService buyOutSurplusTransactionService;

    @Mock
    private BuyOutSurplusQueryService buyOutSurplusQueryService;

    @Mock
    private BuyOutSurplusManagementService buyOutSurplusManagementService;

    @Test
    void terminateAwaitingPayments_shouldTerminateNothing_whenNoTransactions() {
        Long accountId = 123L;
        List<Long> targetPeriodIds = getTargetPeriodDetailsWithTP8Current().stream()
                .map(TargetPeriodInfoDTO::getId)
                .toList();

        BuyOutSurplusAccountProcessingBaseRequestMetadata metadata = mock(
                BuyOutSurplusAccountProcessingBaseRequestMetadata.class);

        BuyOutSurplusFacilityPerformanceResult performanceResult = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("facility1")
                .targetPeriodType(TargetPeriodType.TP7)
                .build();

        when(buyOutSurplusQueryService.getTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, targetPeriodIds))
            .thenReturn(List.of());

        persistTransactionService.terminateAwaitingPayments(List.of(performanceResult), accountId, targetPeriodIds, metadata);

        verifyNoInteractions(buyOutSurplusManagementService);
    }
    
    @Test
    void shouldTerminateMatchingAwaitingPayments() {
        Long accountId = 123L;
        String facilityBusinessId = "facility1";
        List<Long> targetPeriodIds = getTargetPeriodDetailsWithTP8Current().stream()
                .map(TargetPeriodInfoDTO::getId)
                .toList();
        BuyOutSurplusAccountProcessingBaseRequestMetadata metadata = mock(BuyOutSurplusAccountProcessingBaseRequestMetadata.class);

        BuyOutSurplusFacilityPerformanceResult performanceResult = BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP7)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .build();

        BuyOutSurplusTransactionDetailSummary transaction =
                BuyOutSurplusTransactionDetailSummary.builder()
                .facilityBusinessId(facilityBusinessId)
                .targetPeriodType(TargetPeriodType.TP7)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .build();

        when(buyOutSurplusQueryService.getTransactionDetailsSummaryByAccountIdAndTargetPeriodIds(accountId, targetPeriodIds))
                .thenReturn(List.of(transaction));

        persistTransactionService.terminateAwaitingPayments(
                List.of(performanceResult), accountId, targetPeriodIds, metadata);

        verify(buyOutSurplusManagementService)
                .terminateAwaitingPayments(anyList(), same(metadata));
    }
    
    @Test
    void shouldIgnoreResults_onlySurplus() {

        List<BuyOutSurplusFacilityPerformanceResult> results = List.of(
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .targetPeriodType(TargetPeriodType.TP7)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .invoicedBuyOutFee(BigDecimal.ZERO)
                        .build())
                .succeeded(true)
                .build(),
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_2")
                .siteName("siteName_2")
                .targetPeriodType(TargetPeriodType.TP7)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .invoicedBuyOutFee(BigDecimal.ZERO)
                        .build())
                .succeeded(true)
                .build());
        
        BuyOutSurplusFacilityAccountProcessingRequestPayload payload = BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                .facilityPerformanceResults(results)
                .build();
        BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata = new BuyOutSurplusFacilityAccountProcessingRequestMetadata();

        persistTransactionService.persistNewTransactions(
                payload,
                metadata,
                Map.of()
        );

        verifyNoInteractions(buyOutSurplusTransactionService);
    }
    
    @Test
    void shouldCreateSingleBuyOutTransaction() {
        // Given
        final Long accountId = 123L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final TargetPeriodType tp7 = TargetPeriodType.TP7;
        BigDecimal tp7BuyOutCost = getTargetPeriodDetailsByBusinessId(tp7).getBuyOutCost();

        List<BuyOutSurplusFacilityPerformanceResult> results = List.of(
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .processed(false)
                .targetPeriodType(tp7)
                .performanceDataReportVersion(1)
                .performanceDataId(1L)
                .targetPeriodBuyOutCost(tp7BuyOutCost)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.TEN)
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.TEN.multiply(tp7BuyOutCost))
                        .build())
                .succeeded(true)
                .build(),
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_2")
                .siteName("siteName_2")
                .processed(false)
                .targetPeriodType(tp7)
                .performanceDataReportVersion(1)
                .performanceDataId(2L)
                .targetPeriodBuyOutCost(tp7BuyOutCost)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.valueOf(40))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.valueOf(40).multiply(tp7BuyOutCost))
                        .build())
                .succeeded(true)
                .build());
        
        BuyOutSurplusTransactionCreate transactionCreate = BuyOutSurplusTransactionCreate.builder()
                .accountId(accountId)
                .targetPeriodType(tp7)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .invoicedPaymentDeadline(LocalDate.of(2026, 10, 10))
                .transactionCode("CCA070001")
                .fileDocumentUuid("uuid")
                .details(List.of(BuyOutSurplusTransactionDetailCreate.builder()
                        .performanceDataId(1L)
                        .buyOutFee(BigDecimal.TEN.multiply(tp7BuyOutCost))
                        .calculationId(1L)
                        .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                        .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                .invoicedBuyOutFee(BigDecimal.TEN.multiply(tp7BuyOutCost))
                                .invoicedSurplusGained(BigDecimal.ZERO)
                                .priBuyOutCost(BigDecimal.TEN.multiply(tp7BuyOutCost))
                                .invoicedPreviousPaidFees(BigDecimal.ZERO)
                                .build())
                        .build(),
                        BuyOutSurplusTransactionDetailCreate.builder()
                        .performanceDataId(2L)
                        .buyOutFee(BigDecimal.valueOf(40).multiply(tp7BuyOutCost))
                        .calculationId(2L)
                        .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                        .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                .invoicedBuyOutFee(BigDecimal.valueOf(40).multiply(tp7BuyOutCost))
                                .invoicedSurplusGained(BigDecimal.ZERO)
                                .priBuyOutCost(BigDecimal.valueOf(40).multiply(tp7BuyOutCost))
                                .invoicedPreviousPaidFees(BigDecimal.ZERO)
                                .build())
                        .build()))
                .build();
        
        BuyOutSurplusFacilityAccountProcessingRequestPayload payload = BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                .facilityPerformanceResults(results)
                .invoicedPaymentDeadlinePerTP(Map.of(tp7, LocalDate.of(2026, 10, 10)))
                .accountDetails(TargetUnitAccountDetailsDTO.builder().id(accountId).build())
                .buyOutOfficialNotices(Map.of(tp7, FileInfoDTO.builder().uuid("uuid").build()))
                .schemeVersion(schemeVersion)
                .build();
        BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata = BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder()
                .buyOutTransactionCodes(Map.of(tp7, "CCA070001"))
                .build();

        persistTransactionService.persistNewTransactions(
                payload,
                metadata,
                Map.of(1L, 1L, 2L, 2L)
        );

        verify(buyOutSurplusTransactionService).createBuyOutSurplusTransactions(
                argThat(actual -> actual != null
                        && actual.equals(List.of(transactionCreate))),
                eq(schemeVersion)
        );
    }
    
    @Test
    void shouldCreateMultipleBuyOutTransactionsPerTargetPeriodType() {
        final Long accountId = 123L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final TargetPeriodType tp7 = TargetPeriodType.TP7;
        final TargetPeriodType tp8 = TargetPeriodType.TP8;
        BigDecimal tp7BuyOutCost = getTargetPeriodDetailsByBusinessId(tp7).getBuyOutCost();
        BigDecimal tp8BuyOutCost = getTargetPeriodDetailsByBusinessId(tp8).getBuyOutCost();

        List<BuyOutSurplusFacilityPerformanceResult> results = List.of(
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .processed(false)
                .targetPeriodType(tp7)
                .performanceDataReportVersion(1)
                .performanceDataId(1L)
                .targetPeriodBuyOutCost(tp7BuyOutCost)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.TEN)
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.TEN.multiply(tp7BuyOutCost))
                        .build())
                .succeeded(true)
                .build(),
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_2")
                .siteName("siteName_2")
                .processed(false)
                .targetPeriodType(tp7)
                .performanceDataReportVersion(1)
                .performanceDataId(2L)
                .targetPeriodBuyOutCost(tp7BuyOutCost)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.valueOf(40))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.valueOf(40).multiply(tp7BuyOutCost))
                        .build())
                .succeeded(true)
                .build(),
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_2")
                .siteName("siteName_2")
                .processed(false)
                .targetPeriodType(tp8)
                .performanceDataReportVersion(1)
                .performanceDataId(3L)
                .targetPeriodBuyOutCost(tp8BuyOutCost)
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.valueOf(60))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.valueOf(60).multiply(tp8BuyOutCost))
                        .build())
                .succeeded(true)
                .build());
        
        BuyOutSurplusTransactionCreate transactionCreateTp7 = BuyOutSurplusTransactionCreate.builder()
                .accountId(accountId)
                .targetPeriodType(tp7)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .invoicedPaymentDeadline(LocalDate.of(2026, 10, 10))
                .transactionCode("CCA070001")
                .fileDocumentUuid("uuid")
                .details(List.of(BuyOutSurplusTransactionDetailCreate.builder()
                        .performanceDataId(1L)
                        .buyOutFee(BigDecimal.TEN.multiply(tp7BuyOutCost))
                        .calculationId(1L)
                        .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                        .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                .invoicedBuyOutFee(BigDecimal.TEN.multiply(tp7BuyOutCost))
                                .invoicedSurplusGained(BigDecimal.ZERO)
                                .priBuyOutCost(BigDecimal.TEN.multiply(tp7BuyOutCost))
                                .invoicedPreviousPaidFees(BigDecimal.ZERO)
                                .build())
                        .build(),
                        BuyOutSurplusTransactionDetailCreate.builder()
                        .performanceDataId(2L)
                        .buyOutFee(BigDecimal.valueOf(40).multiply(tp7BuyOutCost))
                        .calculationId(2L)
                        .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                        .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                .invoicedBuyOutFee(BigDecimal.valueOf(40).multiply(tp7BuyOutCost))
                                .invoicedSurplusGained(BigDecimal.ZERO)
                                .priBuyOutCost(BigDecimal.valueOf(40).multiply(tp7BuyOutCost))
                                .invoicedPreviousPaidFees(BigDecimal.ZERO)
                                .build())
                        .build()))
                .build();
        
        
        BuyOutSurplusTransactionCreate transactionCreateTp8 = BuyOutSurplusTransactionCreate.builder()
                .accountId(accountId)
                .targetPeriodType(tp8)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .invoicedPaymentDeadline(LocalDate.of(2026, 12, 12))
                .transactionCode("CCA080001")
                .fileDocumentUuid("uuid1")
                .details(List.of(
                        BuyOutSurplusTransactionDetailCreate.builder()
                        .performanceDataId(3L)
                        .buyOutFee(BigDecimal.valueOf(60).multiply(tp8BuyOutCost))
                        .calculationId(3L)
                        .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                        .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                .invoicedBuyOutFee(BigDecimal.valueOf(60).multiply(tp8BuyOutCost))
                                .invoicedSurplusGained(BigDecimal.ZERO)
                                .priBuyOutCost(BigDecimal.valueOf(60).multiply(tp8BuyOutCost))
                                .invoicedPreviousPaidFees(BigDecimal.ZERO)
                                .build())
                        .build()))
                .build();
        
        BuyOutSurplusFacilityAccountProcessingRequestPayload payload = BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                .facilityPerformanceResults(results)
                .invoicedPaymentDeadlinePerTP(Map.of(tp7, LocalDate.of(2026, 10, 10), tp8, LocalDate.of(2026, 12, 12)))
                .accountDetails(TargetUnitAccountDetailsDTO.builder().id(accountId).build())
                .buyOutOfficialNotices(Map.of(tp7, FileInfoDTO.builder().uuid("uuid").build(), tp8, FileInfoDTO.builder().uuid("uuid1").build()))
                .schemeVersion(schemeVersion)
                .build();
        BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata = BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder()
                .buyOutTransactionCodes(Map.of(tp7, "CCA070001", tp8, "CCA080001"))
                .build();

        persistTransactionService.persistNewTransactions(
                payload,
                metadata,
                Map.of(1L, 1L, 2L, 2L, 3L, 3L)
        );
        
        verify(buyOutSurplusTransactionService).createBuyOutSurplusTransactions(
                argThat(actual -> actual != null
                        && actual.size() == 2
                        && actual.containsAll(List.of(transactionCreateTp7, transactionCreateTp8))),
                eq(schemeVersion)
        );
    }
    
    @Test
    void shouldCreateMultipleBuyOutTransactionsPerChangeType() {
        final Long accountId = 123L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final TargetPeriodType tp8 = TargetPeriodType.TP8;
        BigDecimal tp8BuyOutCost = getTargetPeriodDetailsByBusinessId(tp8).getBuyOutCost();

        List<BuyOutSurplusFacilityPerformanceResult> results = List.of(
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .processed(false)
                .targetPeriodType(tp8)
                .performanceDataReportVersion(2)
                .performanceDataId(1L)
                .targetPeriodBuyOutCost(getTargetPeriodDetailsByBusinessId(tp8).getBuyOutCost())
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.TEN)
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.TEN.multiply(tp8BuyOutCost))
                        .build())
                .succeeded(true)
                .build(),
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_2")
                .siteName("siteName_2")
                .processed(false)
                .targetPeriodType(tp8)
                .performanceDataReportVersion(2)
                .performanceDataId(2L)
                .targetPeriodBuyOutCost(tp8BuyOutCost)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.valueOf(40))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.valueOf(40).multiply(tp8BuyOutCost))
                        .build())
                .succeeded(true)
                .build(),
                BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_3")
                .siteName("siteName_3")
                .processed(false)
                .targetPeriodType(tp8)
                .performanceDataReportVersion(2)
                .performanceDataId(3L)
                .targetPeriodBuyOutCost(tp8BuyOutCost)
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .buyOutRequired(BigDecimal.valueOf(10))
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.valueOf(50).multiply(tp8BuyOutCost))
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.ZERO)
                        .chargeType(BuyOutSurplusChargeType.REFUND)
                        .invoicedBuyOutFee(BigDecimal.valueOf(10).multiply(tp8BuyOutCost))
                        .build())
                .succeeded(true)
                .build());
        
        BuyOutSurplusTransactionCreate transactionCreateTp8a = BuyOutSurplusTransactionCreate.builder()
                .accountId(accountId)
                .targetPeriodType(tp8)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .invoicedPaymentDeadline(LocalDate.of(2026, 12, 12))
                .transactionCode("CCA080001")
                .fileDocumentUuid("uuid")
                .details(List.of(BuyOutSurplusTransactionDetailCreate.builder()
                        .performanceDataId(1L)
                        .buyOutFee(BigDecimal.TEN.multiply(tp8BuyOutCost))
                        .calculationId(1L)
                        .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                        .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                .invoicedBuyOutFee(BigDecimal.TEN.multiply(tp8BuyOutCost))
                                .invoicedSurplusGained(BigDecimal.ZERO)
                                .priBuyOutCost(BigDecimal.TEN.multiply(tp8BuyOutCost))
                                .invoicedPreviousPaidFees(BigDecimal.ZERO)
                                .build())
                        .build(),
                        BuyOutSurplusTransactionDetailCreate.builder()
                        .performanceDataId(2L)
                        .buyOutFee(BigDecimal.valueOf(40).multiply(tp8BuyOutCost))
                        .calculationId(2L)
                        .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                        .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                .invoicedBuyOutFee(BigDecimal.valueOf(40).multiply(tp8BuyOutCost))
                                .invoicedSurplusGained(BigDecimal.ZERO)
                                .priBuyOutCost(BigDecimal.valueOf(40).multiply(tp8BuyOutCost))
                                .invoicedPreviousPaidFees(BigDecimal.ZERO)
                                .build())
                        .build()))
                .build();
        
        
        BuyOutSurplusTransactionCreate transactionCreateTp8b = BuyOutSurplusTransactionCreate.builder()
                .accountId(accountId)
                .targetPeriodType(tp8)
                .chargeType(BuyOutSurplusChargeType.REFUND)
                .invoicedPaymentDeadline(null)
                .transactionCode("CCA080002")
                .fileDocumentUuid("uuid2")
                .details(List.of(
                        BuyOutSurplusTransactionDetailCreate.builder()
                        .performanceDataId(3L)
                        .buyOutFee(BigDecimal.valueOf(10).multiply(tp8BuyOutCost))
                        .calculationId(3L)
                        .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND)
                        .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                .invoicedBuyOutFee(BigDecimal.valueOf(10).multiply(tp8BuyOutCost))
                                .invoicedSurplusGained(BigDecimal.ZERO)
                                .priBuyOutCost(BigDecimal.valueOf(10).multiply(tp8BuyOutCost))
                                .invoicedPreviousPaidFees(BigDecimal.valueOf(50).multiply(tp8BuyOutCost))
                                .build())
                        .build()))
                .build();
        

        
        BuyOutSurplusFacilityAccountProcessingRequestPayload payload = BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                .facilityPerformanceResults(results)
                .invoicedPaymentDeadlinePerTP(Map.of(tp8, LocalDate.of(2026, 12, 12)))
                .accountDetails(TargetUnitAccountDetailsDTO.builder().id(accountId).build())
                .buyOutOfficialNotices(Map.of(tp8, FileInfoDTO.builder().uuid("uuid").build()))
                .refundOfficialNotices(Map.of(tp8, FileInfoDTO.builder().uuid("uuid2").build()))
                .schemeVersion(schemeVersion)
                .build();
        BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata = BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder()
                .buyOutTransactionCodes(Map.of(tp8, "CCA080001"))
                .refundTransactionCodes(Map.of(tp8, "CCA080002"))
                .build();

        persistTransactionService.persistNewTransactions(
                payload,
                metadata,
                Map.of(1L, 1L, 2L, 2L, 3L, 3L)
        );
        
        verify(buyOutSurplusTransactionService).createBuyOutSurplusTransactions(
                argThat(actual -> actual != null
                        && actual.size() == 2
                        && actual.containsAll(List.of(transactionCreateTp8a, transactionCreateTp8b))),
                eq(schemeVersion)
        );
    }
    
    static List<TargetPeriodInfoDTO> getTargetPeriodDetailsWithTP8Current() {
        return List.of(
            TargetPeriodInfoDTO.builder()
                .id(1L)
                .businessId(TargetPeriodType.TP7)
                .buyOutCost(BigDecimal.valueOf(10))
                .startDate(LocalDate.of(2026, 1, 1))
                .schemeVersion(SchemeVersion.CCA_3)
                .build(),
            TargetPeriodInfoDTO.builder()
                .id(2L)
                .businessId(TargetPeriodType.TP8)
                .buyOutCost(BigDecimal.valueOf(100))
                .startDate(LocalDate.of(2027, 1, 1))
                .schemeVersion(SchemeVersion.CCA_3)
                .build()
        );
    }
    
    static TargetPeriodInfoDTO getTargetPeriodDetailsByBusinessId(TargetPeriodType businessId) {
        return getTargetPeriodDetailsWithTP8Current().stream()
            .filter(dto -> dto.getBusinessId().equals(businessId))
            .findFirst()
            .orElse(null);
    }

}
