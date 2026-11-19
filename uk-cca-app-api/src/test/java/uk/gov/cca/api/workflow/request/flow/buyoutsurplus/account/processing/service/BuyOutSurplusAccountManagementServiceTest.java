package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreateHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.AccountBuyOutSurplusProcessedDataService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.AccountSurplusService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.TargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.domain.BuyOutSurplusResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.service.BuyOutSurplusCommonManagementService;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusAccountManagementServiceTest {

    @InjectMocks
    private BuyOutSurplusAccountManagementService buyOutSurplusAccountManagementService;

    @Mock
    private AccountSurplusService surplusService;

    @Mock
    private AccountBuyOutSurplusProcessedDataService buyOutSurplusProcessedDataService;

    @Mock
    private BuyOutSurplusCommonManagementService buyOutSurplusCommonManagementService;

    @Test
    void terminateAwaitingPayments() {
        final BuyOutSurplusAccountProcessingRequestMetadata metadata = BuyOutSurplusAccountProcessingRequestMetadata.builder()
                .parentRequestId("runId")
                .build();
        final List<BuyOutSurplusTransactionDetailInfoDTO> previousTransactions = List.of(
                BuyOutSurplusTransactionDetailInfoDTO.builder().id(1L).build());

        // Invoke
        buyOutSurplusCommonManagementService.terminateAwaitingPayments(previousTransactions, metadata);

        // Verify
        verify(buyOutSurplusCommonManagementService, times(1))
                .terminateAwaitingPayments(previousTransactions, metadata);
    }

    @Test
    void generateTransactionCode() {
        final TargetPeriodInfoDTO targetPeriodDetails = TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP6)
                .build();

        when(buyOutSurplusCommonManagementService.generateTransactionCode(TargetPeriodType.TP6))
                .thenReturn("transaction");

        // Invoke
        buyOutSurplusAccountManagementService.generateTransactionCode(targetPeriodDetails);

        // Verify
        verify(buyOutSurplusCommonManagementService, times(1))
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
        buyOutSurplusAccountManagementService.createBankSurplus(accountId, metadata, requestPayload);

        // Verify
        verify(surplusService, times(1)).bankSurplus(surplusCreate);
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
        buyOutSurplusAccountManagementService.createBankSurplus(accountId, metadata, requestPayload);

        // Verify
        verifyNoInteractions(surplusService);
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
        buyOutSurplusAccountManagementService.createBankSurplus(accountId, metadata, requestPayload);

        // Verify
        verify(surplusService, times(1)).bankSurplus(surplusCreate);
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

        when(surplusService.getSurplus(accountId))
                .thenReturn(Optional.of(surplusDTO));

        // Invoke
        buyOutSurplusAccountManagementService.createBankSurplus(accountId, metadata, requestPayload);

        // Verify
        verify(surplusService, times(1)).getSurplus(accountId);
        verify(surplusService, times(1)).bankSurplus(surplusCreate);
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

        when(surplusService.getSurplus(accountId))
                .thenReturn(Optional.empty());

        // Invoke
        buyOutSurplusAccountManagementService.createBankSurplus(accountId, metadata, requestPayload);

        // Verify
        verify(surplusService, times(1)).getSurplus(accountId);
        verify(surplusService, never()).bankSurplus(any());
    }

    @Test
    void saveBuyOutSurplusTransaction() {
        final Long accountId = 1L;
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;
        final TargetPeriodInfoDTO targetPeriod = TargetPeriodInfoDTO.builder()
                .businessId(targetPeriodType)
                .schemeVersion(schemeVersion)
                .build();
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

        final BuyOutSurplusTransactionCreate transactionCreate = BuyOutSurplusTransactionCreate.builder()
                .transactionCode("transaction")
                .accountId(accountId)
                .targetPeriodType(targetPeriodType)
                .chargeType(BuyOutSurplusChargeType.FEE)
                .invoicedPaymentDeadline(LocalDate.of(2020, 1, 1))
                .fileDocumentUuid("notice")
                .details(List.of(BuyOutSurplusTransactionDetailCreate.builder()
                        .performanceDataId(1L)
                        .buyOutFee(BigDecimal.valueOf(995).setScale(2, RoundingMode.HALF_UP))
                        .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                        .buyOutSurplusContainer(container)
                        .build()))
                .build();

        // Invoke
        buyOutSurplusAccountManagementService.saveBuyOutSurplusTransaction(buyOutSurplus, accountId, targetPeriod);

        // Verify
        verify(buyOutSurplusCommonManagementService, times(1))
                .createBuyOutSurplusTransactions(List.of(transactionCreate), schemeVersion);
    }

    @Test
    void saveBuyOutSurplusProcessedData() {
        final Long performanceDataId = 1L;

        // Invoke
        buyOutSurplusAccountManagementService.saveBuyOutSurplusProcessedData(performanceDataId);

        // Verify
        verify(buyOutSurplusProcessedDataService, times(1))
        		.submitBuyOutSurplusProcessedData(performanceDataId);
    }
}
