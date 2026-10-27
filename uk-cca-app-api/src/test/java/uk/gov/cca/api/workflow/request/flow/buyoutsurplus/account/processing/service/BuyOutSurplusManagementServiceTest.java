package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.AccountBuyOutSurplusTransactionCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreateHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusProcessedDataService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionIdentifierService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.domain.BuyOutSurplusResult;

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
    void terminateAwaitingPayments() {
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId("runId")
                .build();
        final List<BuyOutSurplusTransactionDetailInfoDTO> previousTransactions = List.of(
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(1L).buyOutFee(BigDecimal.valueOf(15)).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND).build(),
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(2L).buyOutFee(BigDecimal.valueOf(100)).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(3L).buyOutFee(BigDecimal.valueOf(20)).paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED).build(),
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(4L).buyOutFee(BigDecimal.valueOf(100)).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).build()
        );

        final String submitter = "batch run ID runId";
        final Set<Long> awaitingIds = Set.of(1L, 4L);

        // Invoke
        buyOutSurplusManagementService.terminateAwaitingPayments(previousTransactions, metadata);

        // Verify
        verify(buyOutSurplusTransactionService, times(1))
                .terminateBuyOutSurplusTransactionDetails(awaitingIds, submitter);
    }

    @Test
    void terminateAwaitingPayments_noAvailableAwaitingPayments() {
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId("runId")
                .build();
        final List<BuyOutSurplusTransactionDetailInfoDTO> previousTransactions = List.of(
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(2L).buyOutFee(BigDecimal.valueOf(100)).paymentStatus(BuyOutSurplusPaymentStatus.PAID).build(),
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(3L).buyOutFee(BigDecimal.valueOf(20)).paymentStatus(BuyOutSurplusPaymentStatus.REFUNDED).build()
        );

        // Invoke
        buyOutSurplusManagementService.terminateAwaitingPayments(previousTransactions, metadata);

        // Verify
        verifyNoInteractions(buyOutSurplusTransactionService);
    }

    @Test
    void generateTransactionCode() {
        final TargetPeriodInfoDTO targetPeriodDetails = TargetPeriodInfoDTO.builder()
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
        final String runId = "runId";
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId(runId)
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
                        .targetPeriodDetails(TargetPeriodInfoDTO.builder().schemeVersion(schemeVersion).build())
                        .build();

        final SurplusCreate surplusCreate = SurplusCreate.builder()
                .resourceId(accountId)
                .surplusGained(BigDecimal.ZERO)
                .history(SurplusCreateHistory.builder()
                        .submitter("batch run ID " + runId)
                        .build())
                .build();

        // Invoke
        buyOutSurplusManagementService.createBankSurplus(accountId, metadata, requestPayload);

        // Verify
        verify(surplusService, times(1)).bankSurplus(surplusCreate, schemeVersion);
        verifyNoInteractions(surplusQueryService);
    }

    @Test
    void createBankSurplus_PRIMARY_BUY_OUT_REQUIRED() {
        final Long accountId = 1L;
        final String runId = "runId";
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId(runId)
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
                        .targetPeriodDetails(TargetPeriodInfoDTO.builder().schemeVersion(schemeVersion).build())
                        .build();

        // Invoke
        buyOutSurplusManagementService.createBankSurplus(accountId, metadata, requestPayload);

        // Verify
        verifyNoInteractions(surplusService, surplusQueryService);
    }

    @Test
    void createBankSurplus_SECONDARY_TARGET_MET() {
        final Long accountId = 1L;
        final String runId = "runId";
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId(runId)
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
                        .targetPeriodDetails(TargetPeriodInfoDTO.builder().schemeVersion(schemeVersion).build())
                        .build();

        final SurplusCreate surplusCreate = SurplusCreate.builder()
                .resourceId(accountId)
                .surplusGained(BigDecimal.ZERO)
                .history(SurplusCreateHistory.builder()
                        .submitter("batch run ID " + runId)
                        .build())
                .build();

        // Invoke
        buyOutSurplusManagementService.createBankSurplus(accountId, metadata, requestPayload);

        // Verify
        verify(surplusService, times(1)).bankSurplus(surplusCreate, schemeVersion);
        verifyNoInteractions(surplusQueryService);
    }

    @Test
    void createBankSurplus_SECONDARY_BUY_OUT_REQUIRED() {
        final Long accountId = 1L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;
        final String runId = "runId";
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId(runId)
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
                        .targetPeriodDetails(TargetPeriodInfoDTO.builder().schemeVersion(schemeVersion).build())
                        .build();

        final SurplusDTO surplusDTO = SurplusDTO.builder().surplusGained(BigDecimal.TEN).build();
        final SurplusCreate surplusCreate = SurplusCreate.builder()
                .resourceId(accountId)
                .surplusGained(BigDecimal.ZERO)
                .history(SurplusCreateHistory.builder()
                        .submitter("batch run ID " + runId)
                        .build())
                .build();

        when(surplusQueryService.getSurplusByResourceIdAndSchemeVersion(accountId, schemeVersion))
                .thenReturn(Optional.of(surplusDTO));

        // Invoke
        buyOutSurplusManagementService.createBankSurplus(accountId, metadata, requestPayload);

        // Verify
        verify(surplusQueryService, times(1)).getSurplusByResourceIdAndSchemeVersion(accountId, schemeVersion);
        verify(surplusService, times(1)).bankSurplus(surplusCreate, schemeVersion);
    }

    @Test
    void createBankSurplus_SECONDARY_BUY_OUT_REQUIRED_no_recorded_value() {
        final Long accountId = 1L;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;
        final String runId = "runId";
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId(runId)
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
                        .targetPeriodDetails(TargetPeriodInfoDTO.builder().schemeVersion(schemeVersion).build())
                        .build();

        when(surplusQueryService.getSurplusByResourceIdAndSchemeVersion(accountId, schemeVersion))
                .thenReturn(Optional.empty());

        // Invoke
        buyOutSurplusManagementService.createBankSurplus(accountId, metadata, requestPayload);

        // Verify
        verify(surplusQueryService, times(1)).getSurplusByResourceIdAndSchemeVersion(accountId, schemeVersion);
        verifyNoInteractions(surplusService);
    }

    @Test
    void saveBuyOutSurplusTransaction() {
        final Long accountId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final BuyOutSurplusContainer container = BuyOutSurplusContainer.builder()
                .invoicedBuyOutFee(BigDecimal.valueOf(995).setScale(2, RoundingMode.HALF_UP))
                .invoicedSurplusGained(BigDecimal.TWO)
                .priBuyOutCost(BigDecimal.valueOf(150).setScale(2, RoundingMode.HALF_UP))
                .invoicedPreviousPaidFees(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_UP))
                .build();
        final BuyOutSurplusResult buyOutSurplus = BuyOutSurplusResult.builder()
                .performanceDataId(1L)
                .buyOutSurplusContainer(container)
                .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                .chargeType(BuyOutSurplusChargeType.FEE)
                .transactionCode("transaction")
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .fileDocumentUuid("notice")
                .build();

        final AccountBuyOutSurplusTransactionCreate transactionCreateDTO = AccountBuyOutSurplusTransactionCreate.builder()
                .transactionCode("transaction")
                .accountId(accountId)
                .targetPeriodType(targetPeriodType)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                .fileDocumentUuid("notice")
                .performanceDataId(1L)
                .buyOutFee(BigDecimal.valueOf(995).setScale(2, RoundingMode.HALF_UP))
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .buyOutSurplusContainer(container)
                .build();

        // Invoke
        buyOutSurplusManagementService.saveBuyOutSurplusTransaction(buyOutSurplus, accountId, targetPeriodType);

        // Verify
        verify(buyOutSurplusTransactionService, times(1))
                .createAccountBuyOutSurplusTransaction(transactionCreateDTO);
    }

    @Test
    void saveBuyOutSurplusProcessedData() {
        final Long performanceDataId = 1L;
        final PerformanceDataResourceType resourceType = PerformanceDataResourceType.ACCOUNT;

        // Invoke
        buyOutSurplusManagementService.saveBuyOutSurplusProcessedData(performanceDataId, resourceType);

        // Verify
        verify(buyOutSurplusProcessedDataService, times(1))
        		.submitBuyOutSurplusProcessedData(performanceDataId, resourceType);
    }
}
