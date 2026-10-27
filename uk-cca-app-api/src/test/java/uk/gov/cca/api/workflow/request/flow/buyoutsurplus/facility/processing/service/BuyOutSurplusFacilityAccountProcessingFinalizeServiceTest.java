package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
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
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDataContainer;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusCalculationService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusProcessedDataService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionIdentifierService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityAccountProcessingFinalizeServiceTest {
    
    @Mock
    private RequestService requestService;

    @Mock
    private BuyOutSurplusProcessedDataService buyOutSurplusProcessedDataService;

    @Mock
    private BuyOutSurplusTransactionIdentifierService buyOutSurplusTransactionIdentifierService;

    @Mock
    private PerformanceDataFacilityStatusService performanceDataFacilityStatusService;

    @Mock
    private BuyOutSurplusCalculationService buyOutSurplusCalculationService;

    @Mock
    private BuyOutSurplusFacilityAccountProcessingUpdateTransactionService transactionService;

    @Mock
    private SurplusService surplusService;
    
    @Mock
    private BuyOutSurplusFacilityAccountProcessingGenerateDocumentsService generateDocumentsService;
    
    @Mock
    private TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;
    
    @Mock
    private BuyOutSurplusFacilityAccountProcessingOfficialNoticeService officialNoticeService;

    @InjectMocks
    private BuyOutSurplusFacilityAccountProcessingFinalizeService service;
    
    @Test
    void finalizeAccountProcessing_whenRequiresTransaction() throws Exception {
        Request request = createRequest();
        List<BuyOutSurplusFacilityPerformanceResult> results = createPerformanceResults_targetNotMet();

        when(buyOutSurplusTransactionIdentifierService.generateTransactionCode(any()))
                .thenReturn("BOS070001");

        doNothing().when(generateDocumentsService).generateDocuments(request);

        when(buyOutSurplusCalculationService.submitBuyOutCalculations(anyList(), any()))
                .thenReturn(Map.of(100L, 200L));
        
        doNothing().when(officialNoticeService).sendOfficialNotice(request);

        // Act
        service.finalizeAccountProcessing(request, results);

        // Assert
        verify(buyOutSurplusTransactionIdentifierService,
                org.mockito.Mockito.atLeastOnce())
                .generateTransactionCode(any());
        
        verify(generateDocumentsService)
            .generateDocuments(request);

        verify(targetUnitAccountNoticeRecipients)
                .getDefaultNoticeRecipients(any());

        verify(buyOutSurplusCalculationService)
                .submitBuyOutCalculations(anyList(), any());

        verify(transactionService)
                .terminateAwaitingPayments(
                        eq(results),
                        any(),
                        anyList(),
                        any());

        verify(transactionService)
                .persistNewTransactions(
                        any(),
                        any(),
                        any());

        verify(surplusService, never()).bankSurplus(anyList(), any());

        verify(buyOutSurplusProcessedDataService)
                .submitBuyOutSurplusProcessedDataBatch(
                        any(),
                        eq(PerformanceDataResourceType.FACILITY));

        verify(requestService).addActionToRequest(
                eq(request),
                any(),
                eq(CcaRequestActionType
                        .BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_SUBMITTED),
                any());
        
        verify(officialNoticeService)
            .sendOfficialNotice(request);
    }
    
    @Test
    void finalizeAccountProcessing_whenDoNotRequireTransaction() throws Exception {
        Request request = createRequest_targetMet();
        List<BuyOutSurplusFacilityPerformanceResult> results = createPerformanceResults_targetMet();

        when(buyOutSurplusCalculationService.submitBuyOutCalculations(anyList(), any()))
                .thenReturn(Map.of(100L, 200L));

        // Act
        service.finalizeAccountProcessing(request, results);
        
        verifyNoInteractions(generateDocumentsService);

        verifyNoInteractions(targetUnitAccountNoticeRecipients);

        verify(buyOutSurplusCalculationService)
                .submitBuyOutCalculations(anyList(), any());

        verify(transactionService)
                .terminateAwaitingPayments(
                        eq(results),
                        any(),
                        anyList(),
                        any());

        verify(transactionService, never())
        .persistNewTransactions(
                any(),
                any(),
                any());

        verify(surplusService).bankSurplus(anyList(), any());

        verify(buyOutSurplusProcessedDataService)
                .submitBuyOutSurplusProcessedDataBatch(
                        any(),
                        eq(PerformanceDataResourceType.FACILITY));

        verify(requestService).addActionToRequest(
                eq(request),
                any(),
                eq(CcaRequestActionType
                        .BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_SUBMITTED),
                any());
        
        verifyNoInteractions(officialNoticeService);
    }
    
    @Test
    void finalizeAccountProcessing_whenRequiresTransaction_withBankSurplus() throws Exception {
        Request request = createRequest();
        List<BuyOutSurplusFacilityPerformanceResult> results = createPerformanceResults_surplusUsed();

        when(buyOutSurplusTransactionIdentifierService.generateTransactionCode(any()))
                .thenReturn("BOS070001");

        doNothing().when(generateDocumentsService).generateDocuments(request);

        when(buyOutSurplusCalculationService.submitBuyOutCalculations(anyList(), any()))
                .thenReturn(Map.of(100L, 200L));
        
        doNothing().when(officialNoticeService).sendOfficialNotice(request);

        // Act
        service.finalizeAccountProcessing(request, results);

        // Assert
        verify(buyOutSurplusTransactionIdentifierService,
                org.mockito.Mockito.atLeastOnce())
                .generateTransactionCode(any());
        
        verify(generateDocumentsService)
            .generateDocuments(request);

        verify(targetUnitAccountNoticeRecipients)
                .getDefaultNoticeRecipients(any());

        verify(buyOutSurplusCalculationService)
                .submitBuyOutCalculations(anyList(), any());

        verify(transactionService)
                .terminateAwaitingPayments(
                        eq(results),
                        any(),
                        anyList(),
                        any());

        verify(transactionService)
                .persistNewTransactions(
                        any(),
                        any(),
                        any());

        verify(surplusService).bankSurplus(anyList(), any());

        verify(buyOutSurplusProcessedDataService)
                .submitBuyOutSurplusProcessedDataBatch(
                        any(),
                        eq(PerformanceDataResourceType.FACILITY));

        verify(requestService).addActionToRequest(
                eq(request),
                any(),
                eq(CcaRequestActionType
                        .BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_SUBMITTED),
                any());
        
        verify(officialNoticeService)
            .sendOfficialNotice(request);
    }
    
    private Request createRequest() {
        final TargetPeriodInfoDTO targetPeriodDetails = TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP7)
                .build();
        return Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .targetPeriodsDetails(List.of(targetPeriodDetails))
                        .accountDetails(new TargetUnitAccountDetailsDTO())
                        .invoicedPaymentDeadlinePerTP(Map.of(TargetPeriodType.TP7, LocalDate.of(2026,01,01)))
                        .buyOutOfficialNotices(Map.of(TargetPeriodType.TP7, FileInfoDTO.builder().uuid("uuid").build()))
                        .build())
                .metadata(BuyOutSurplusFacilityAccountProcessingRequestMetadata.builder().buyOutTransactionCodes(Map.of(TargetPeriodType.TP7, "uuid")).build())
                .build();
    }
    
    private List<BuyOutSurplusFacilityPerformanceResult> createPerformanceResults_targetNotMet() {
        return List.of(BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .processed(false)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(1)
                .performanceDataId(1L)
                .targetPeriodBuyOutCost(BigDecimal.valueOf(10))
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
                        .schemeTotalSurplus(BigDecimal.TEN)
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.valueOf(100))
                        .build())
                .succeeded(true)
                .build());
    }
    
    private List<BuyOutSurplusFacilityPerformanceResult> createPerformanceResults_surplusUsed() {
        return List.of(BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .processed(false)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(1)
                .performanceDataId(1L)
                .targetPeriodBuyOutCost(BigDecimal.valueOf(10))
                .submissionType(PerformanceDataSubmissionType.SECONDARY)
                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_NOT_MET)
                        .buyOutRequired(BigDecimal.TEN)
                        .surplusGained(BigDecimal.ZERO)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.valueOf(2))
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.valueOf(2))
                        .chargeType(BuyOutSurplusChargeType.FEE)
                        .invoicedBuyOutFee(BigDecimal.valueOf(100))
                        .build())
                .succeeded(true)
                .build());
    }
    
    private Request createRequest_targetMet() {
        final TargetPeriodInfoDTO targetPeriodDetails = TargetPeriodInfoDTO.builder()
                .businessId(TargetPeriodType.TP7)
                .build();
        return Request.builder()
                .payload(BuyOutSurplusFacilityAccountProcessingRequestPayload.builder()
                        .targetPeriodsDetails(List.of(targetPeriodDetails))
                        .accountDetails(new TargetUnitAccountDetailsDTO())
                        .invoicedPaymentDeadlinePerTP(Map.of(TargetPeriodType.TP7, LocalDate.of(2026,01,01)))
                        .build())
                .metadata(new BuyOutSurplusFacilityAccountProcessingRequestMetadata())
                .build();
    }
    
    private List<BuyOutSurplusFacilityPerformanceResult> createPerformanceResults_targetMet() {
        return List.of(BuyOutSurplusFacilityPerformanceResult.builder()
                .facilityBusinessId("businessId_1")
                .siteName("siteName_1")
                .processed(false)
                .targetPeriodType(TargetPeriodType.TP7)
                .performanceDataReportVersion(1)
                .performanceDataId(1L)
                .targetPeriodBuyOutCost(BigDecimal.valueOf(10))
                .submissionType(PerformanceDataSubmissionType.PRIMARY)
                .data(BuyOutSurplusCalculationDataContainer.builder()
                        .performanceOutcome(PerformanceDataFacilityTargetPeriodResultType.TARGET_MET)
                        .buyOutRequired(BigDecimal.ZERO)
                        .surplusGained(BigDecimal.TEN)
                        .surplusUsed(BigDecimal.ZERO)
                        .previousPaidFees(BigDecimal.ZERO)
                        .previousSurplusUsed(BigDecimal.ZERO)
                        .previousSurplusGained(BigDecimal.ZERO)
                        .schemeTotalSurplus(BigDecimal.TEN)
                        .invoicedBuyOutFee(BigDecimal.ZERO)
                        .build())
                .succeeded(true)
                .build());
    }

}
