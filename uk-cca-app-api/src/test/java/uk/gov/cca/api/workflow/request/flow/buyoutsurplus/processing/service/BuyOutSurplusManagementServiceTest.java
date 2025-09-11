package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionCreateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreateHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusProcessedDataService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionIdentifierService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusResult;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusManagementServiceTest {

    @InjectMocks
    private BuyOutSurplusManagementService buyOutSurplusManagementService;

    @Mock
    private SurplusQueryService surplusQueryService;

    @Mock
    private SurplusService surplusService;

    @Mock
    private BuyOutSurplusTransactionService buyOutSurplusTransactionService;

    @Mock
    private BuyOutSurplusProcessedDataService buyOutSurplusProcessedDataService;

    @Mock
    private BuyOutSurplusTransactionIdentifierService buyOutSurplusTransactionIdentifierService;

    @Test
    void terminateAwaitPayments() {
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId("runId")
                .build();
        final List<BuyOutSurplusTransactionInfoDTO> previousTransactions = List.of(
                BuyOutSurplusTransactionInfoDTO.builder().id(1L).buyOutFee(BigDecimal.valueOf(15)).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND).build(),
                BuyOutSurplusTransactionInfoDTO.builder().id(2L).buyOutFee(BigDecimal.valueOf(100)).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                BuyOutSurplusTransactionInfoDTO.builder().id(3L).buyOutFee(BigDecimal.valueOf(20)).paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED).build(),
                BuyOutSurplusTransactionInfoDTO.builder().id(4L).buyOutFee(BigDecimal.valueOf(100)).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build()
        );

        final String submitter = "batch run ID runId";
        final Set<Long> awaitingIds = Set.of(1L, 4L);

        // Invoke
        buyOutSurplusManagementService.terminateAwaitPayments(previousTransactions, metadata);

        // Verify
        verify(buyOutSurplusTransactionService, times(1))
                .terminateBuyOutSurplusTransactions(awaitingIds, submitter);
    }

    @Test
    void terminateAwaitPayments_no_await() {
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId("runId")
                .build();
        final List<BuyOutSurplusTransactionInfoDTO> previousTransactions = List.of(
                BuyOutSurplusTransactionInfoDTO.builder().id(2L).buyOutFee(BigDecimal.valueOf(100)).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                BuyOutSurplusTransactionInfoDTO.builder().id(3L).buyOutFee(BigDecimal.valueOf(20)).paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED).build()
        );

        // Invoke
        buyOutSurplusManagementService.terminateAwaitPayments(previousTransactions, metadata);

        // Verify
        verifyNoInteractions(buyOutSurplusTransactionService);
    }

    @Test
    void generateTransactionCode() {
        final TargetPeriodDTO targetPeriodDetails = TargetPeriodDTO.builder()
                .businessId(TargetPeriodType.TP6)
                .build();

        when(buyOutSurplusTransactionIdentifierService.generateTransactionCode(TargetPeriodType.TP6))
                .thenReturn("transaction");

        // Invoke
        buyOutSurplusManagementService.generateTransactionCode(targetPeriodDetails);

        // Verify
        verify(buyOutSurplusTransactionIdentifierService, times(1))
                .generateTransactionCode(TargetPeriodType.TP6);
    }

    @Test
    void createBankSurplus_PRIMARY_TARGET_MET() {
        final Long accountId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final String runId = "runId";
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId(runId)
                .targetPeriodType(targetPeriodType)
                .build();
        final BuyOutSurplusAccountProcessingRequestPayload requestPayload =
                BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .performanceData(PerformanceDataBuyOutSurplusDetailsDTO.builder()
                                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                                .build())
                        .buyOutSurplus(BuyOutSurplusResult.builder()
                                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                        .invoicedSurplusGained(BigDecimal.ZERO)
                                        .build())
                                .build())
                        .build();

        final SurplusCreate surplusCreate = SurplusCreate.builder()
                .accountId(accountId)
                .targetPeriodType(targetPeriodType)
                .surplusGained(BigDecimal.ZERO)
                .history(SurplusCreateHistory.builder()
                        .submitter("batch run ID " + runId)
                        .build())
                .build();

        // Invoke
        buyOutSurplusManagementService.createBankSurplus(accountId, metadata, requestPayload);

        // Verify
        verify(surplusService, times(1)).bankSurplus(surplusCreate);
        verifyNoInteractions(surplusQueryService);
    }

    @Test
    void createBankSurplus_PRIMARY_BUY_OUT_REQUIRED() {
        final Long accountId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final String runId = "runId";
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId(runId)
                .targetPeriodType(targetPeriodType)
                .build();
        final BuyOutSurplusAccountProcessingRequestPayload requestPayload =
                BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .performanceData(PerformanceDataBuyOutSurplusDetailsDTO.builder()
                                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                                .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                                .build())
                        .buyOutSurplus(BuyOutSurplusResult.builder()
                                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                        .invoicedSurplusGained(BigDecimal.ZERO)
                                        .build())
                                .build())
                        .build();

        // Invoke
        buyOutSurplusManagementService.createBankSurplus(accountId, metadata, requestPayload);

        // Verify
        verifyNoInteractions(surplusService, surplusQueryService);
    }

    @Test
    void createBankSurplus_SECONDARY_TARGET_MET() {
        final Long accountId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final String runId = "runId";
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId(runId)
                .targetPeriodType(targetPeriodType)
                .build();
        final BuyOutSurplusAccountProcessingRequestPayload requestPayload =
                BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .performanceData(PerformanceDataBuyOutSurplusDetailsDTO.builder()
                                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                                .tpOutcome(TargetPeriodResultType.TARGET_MET)
                                .build())
                        .buyOutSurplus(BuyOutSurplusResult.builder()
                                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                        .invoicedSurplusGained(BigDecimal.ZERO)
                                        .build())
                                .build())
                        .build();

        final SurplusCreate surplusCreate = SurplusCreate.builder()
                .accountId(accountId)
                .targetPeriodType(targetPeriodType)
                .surplusGained(BigDecimal.ZERO)
                .history(SurplusCreateHistory.builder()
                        .submitter("batch run ID " + runId)
                        .build())
                .build();

        // Invoke
        buyOutSurplusManagementService.createBankSurplus(accountId, metadata, requestPayload);

        // Verify
        verify(surplusService, times(1)).bankSurplus(surplusCreate);
        verifyNoInteractions(surplusQueryService);
    }

    @Test
    void createBankSurplus_SECONDARY_BUY_OUT_REQUIRED() {
        final Long accountId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final String runId = "runId";
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId(runId)
                .targetPeriodType(targetPeriodType)
                .build();
        final BuyOutSurplusAccountProcessingRequestPayload requestPayload =
                BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .performanceData(PerformanceDataBuyOutSurplusDetailsDTO.builder()
                                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                                .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                                .build())
                        .buyOutSurplus(BuyOutSurplusResult.builder()
                                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                        .invoicedSurplusGained(BigDecimal.ZERO)
                                        .build())
                                .build())
                        .build();

        final SurplusDTO surplusDTO = SurplusDTO.builder().surplusGained(BigDecimal.TEN).build();
        final SurplusCreate surplusCreate = SurplusCreate.builder()
                .accountId(accountId)
                .targetPeriodType(targetPeriodType)
                .surplusGained(BigDecimal.ZERO)
                .history(SurplusCreateHistory.builder()
                        .submitter("batch run ID " + runId)
                        .build())
                .build();

        when(surplusQueryService.getSurplusByAccountIdAndTargetPeriod(accountId, targetPeriodType))
                .thenReturn(Optional.of(surplusDTO));

        // Invoke
        buyOutSurplusManagementService.createBankSurplus(accountId, metadata, requestPayload);

        // Verify
        verify(surplusQueryService, times(1)).getSurplusByAccountIdAndTargetPeriod(accountId, targetPeriodType);
        verify(surplusService, times(1)).bankSurplus(surplusCreate);
    }

    @Test
    void createBankSurplus_SECONDARY_BUY_OUT_REQUIRED_no_recorded_value() {
        final Long accountId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final String runId = "runId";
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId(runId)
                .targetPeriodType(targetPeriodType)
                .build();
        final BuyOutSurplusAccountProcessingRequestPayload requestPayload =
                BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .performanceData(PerformanceDataBuyOutSurplusDetailsDTO.builder()
                                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                                .tpOutcome(TargetPeriodResultType.BUY_OUT_REQUIRED)
                                .build())
                        .buyOutSurplus(BuyOutSurplusResult.builder()
                                .buyOutSurplusContainer(BuyOutSurplusContainer.builder()
                                        .invoicedSurplusGained(BigDecimal.ZERO)
                                        .build())
                                .build())
                        .build();

        when(surplusQueryService.getSurplusByAccountIdAndTargetPeriod(accountId, targetPeriodType))
                .thenReturn(Optional.empty());

        // Invoke
        buyOutSurplusManagementService.createBankSurplus(accountId, metadata, requestPayload);

        // Verify
        verify(surplusQueryService, times(1)).getSurplusByAccountIdAndTargetPeriod(accountId, targetPeriodType);
        verifyNoInteractions(surplusService);
    }

    @Test
    void saveBuyOutSurplusTransaction() {
        final BuyOutSurplusContainer container = BuyOutSurplusContainer.builder()
                .invoicedBuyOutFee(BigDecimal.valueOf(995).setScale(2, RoundingMode.HALF_UP))
                .invoicedSurplusGained(BigDecimal.TWO)
                .priBuyOutCost(BigDecimal.valueOf(150).setScale(2, RoundingMode.HALF_UP))
                .invoicedPreviousPaidFees(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_UP))
                .chargeType(BuyOutSurplusChargeType.FEE)
                .build();
        final BuyOutSurplusResult buyOutSurplus = BuyOutSurplusResult.builder()
                .performanceDataId(1L)
                .buyOutSurplusContainer(container)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .transactionCode("transaction")
                .fileDocumentUuid("notice")
                .build();

        final BuyOutSurplusTransactionCreateDTO transactionCreateDTO = BuyOutSurplusTransactionCreateDTO.builder()
                .performanceDataId(1L)
                .transactionCode("transaction")
                .buyOutFee(BigDecimal.valueOf(995).setScale(2, RoundingMode.HALF_UP))
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .buyOutSurplusContainer(container)
                .fileDocumentUuid("notice")
                .build();

        // Invoke
        buyOutSurplusManagementService.saveBuyOutSurplusTransaction(buyOutSurplus);

        // Verify
        verify(buyOutSurplusTransactionService, times(1))
                .createBuyOutSurplusTransaction(transactionCreateDTO);
    }

    @Test
    void saveBuyOutSurplusProcessedData() {
        final Long performanceDataId = 1L;

        // Invoke
        buyOutSurplusManagementService.saveBuyOutSurplusProcessedData(performanceDataId);

        // Verify
        verify(buyOutSurplusProcessedDataService, times(1))
                .submitBuyOutSurplusProcessedData(performanceDataId);
    }
}
