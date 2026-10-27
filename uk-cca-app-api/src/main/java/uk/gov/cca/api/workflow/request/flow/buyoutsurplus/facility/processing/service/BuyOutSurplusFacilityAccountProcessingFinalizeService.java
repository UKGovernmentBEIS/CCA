package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusCalculationDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.SurplusCreateHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusCalculationService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusProcessedDataService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionIdentifierService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusService;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.validation.BuyOutSurplusFacilityViolation;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.transform.BuyOutSurplusFacilityAccountProcessingMapper;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.utils.BuyOutSurplusFacilityResultsUtil;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Log4j2
@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityAccountProcessingFinalizeService {

    private final RequestService requestService;
    private final BuyOutSurplusProcessedDataService buyOutSurplusProcessedDataService;
    private final BuyOutSurplusTransactionIdentifierService buyOutSurplusTransactionIdentifierService;
    private final PerformanceDataFacilityStatusService performanceDataFacilityStatusService;
    private final BuyOutSurplusCalculationService buyOutSurplusCalculationService;
    private final BuyOutSurplusFacilityAccountProcessingUpdateTransactionService transactionService;
    private final SurplusService surplusService;
    private final BuyOutSurplusFacilityAccountProcessingGenerateDocumentsService generateDocumentsService;
    private final TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;
    private final BuyOutSurplusFacilityAccountProcessingOfficialNoticeService officialNoticeService;
    private static final BuyOutSurplusFacilityAccountProcessingMapper MAPPER = Mappers
            .getMapper(BuyOutSurplusFacilityAccountProcessingMapper.class);

    @Transactional(rollbackFor = BpmnExecutionException.class, propagation = Propagation.REQUIRES_NEW)
    public void finalizeAccountProcessing(Request request,
            List<BuyOutSurplusFacilityPerformanceResult> facilityPerformanceResults) throws BpmnExecutionException {
        final BuyOutSurplusFacilityAccountProcessingRequestPayload payload = (BuyOutSurplusFacilityAccountProcessingRequestPayload) request.getPayload();
        final BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata = (BuyOutSurplusFacilityAccountProcessingRequestMetadata) request.getMetadata();

        try {
            final Long accountId = payload.getAccountDetails().getId();
            final SchemeVersion schemeVersion = payload.getSchemeVersion();
            final boolean requiresTransaction = BuyOutSurplusFacilityResultsUtil.requiresTransaction(facilityPerformanceResults);
            List<Long> targetPeriodIds = payload.getTargetPeriodsDetails().stream()
                    .map(TargetPeriodInfoDTO::getId)
                    .toList();
            
            payload.setFacilityPerformanceResults(facilityPerformanceResults);
            
            if (requiresTransaction) {
                // Generate transaction codes
                Map<TargetPeriodType, String> buyOutTransactionCodes = generateBuyOutTransactionCodes(facilityPerformanceResults);
                Map<TargetPeriodType, String> refundTransactionCodes = generateRefundTransactionCodes(facilityPerformanceResults);

                metadata.setBuyOutTransactionCodes(buyOutTransactionCodes);
                metadata.setRefundTransactionCodes(refundTransactionCodes);

                // Generate MoAs
                generateDocumentsService.generateDocuments(request);
                
                List<DefaultNoticeRecipient> recipients = this.targetUnitAccountNoticeRecipients
                        .getDefaultNoticeRecipients(accountId);
                payload.setDefaultContacts(recipients);
            }

            // Persist latest calculations
            Map<Long, Long> savedCalculationIdsByPerformanceData = submitBuyOutSurplusCalculations(accountId, schemeVersion, facilityPerformanceResults);
            
            // Close awaiting transactions
            transactionService.terminateAwaitingPayments(facilityPerformanceResults, accountId, targetPeriodIds, metadata);

            // Persist new transactions
            if (requiresTransaction) {
                transactionService.persistNewTransactions(payload, metadata, savedCalculationIdsByPerformanceData);
            }

            // Bank surplus
            bankSurplus(facilityPerformanceResults, schemeVersion, metadata);

            // Update processed data
            submitBuyOutSurplusProcessedData(facilityPerformanceResults);
            
            // Add timeline event
            addSubmittedAction(request, payload, metadata);
            
            // Send official notice
            if (requiresTransaction) {
                officialNoticeService.sendOfficialNotice(request);
            }

            // Lock TPR
            lockProcessedTPRs(facilityPerformanceResults);

        } catch (Exception e) {
            final String message = e.getMessage() != null ? e.getMessage()
                    : BuyOutSurplusFacilityViolation.BuyOutSurplusFacilityViolationMessage.DATA_PERSIST_ERROR
                            .getMessage();
            log.error(message, e);
            throw new BpmnExecutionException(message, List.of(message));
        }
    }

    private Map<TargetPeriodType, String> generateBuyOutTransactionCodes(List<BuyOutSurplusFacilityPerformanceResult> facilityPerformanceResults) {
        Map<TargetPeriodType, List<BuyOutSurplusFacilityPerformanceResult>> buyOutResultsPerTP = BuyOutSurplusFacilityResultsUtil
                .getBuyOutResultsPerTP(facilityPerformanceResults);
        return buyOutResultsPerTP.keySet().stream()
                .collect(Collectors.toMap(Function.identity(), buyOutSurplusTransactionIdentifierService::generateTransactionCode));
    }

    private Map<TargetPeriodType, String> generateRefundTransactionCodes(List<BuyOutSurplusFacilityPerformanceResult> facilityPerformanceResults) {
        Map<TargetPeriodType, List<BuyOutSurplusFacilityPerformanceResult>> refundResultsPerTP = BuyOutSurplusFacilityResultsUtil
                .getRefundResultsPerTP(facilityPerformanceResults);
        return refundResultsPerTP.keySet().stream()
                .collect(Collectors.toMap(Function.identity(), buyOutSurplusTransactionIdentifierService::generateTransactionCode));
    }

    private Map<Long, Long> submitBuyOutSurplusCalculations(Long accountId, SchemeVersion schemeVersion, List<BuyOutSurplusFacilityPerformanceResult> facilityPerformanceResults) {
        List<BuyOutSurplusCalculationDetail> details = facilityPerformanceResults.stream()
                .map(result -> MAPPER.toBuyOutSurplusCalculationDetail(result, accountId))
                .toList();
        return buyOutSurplusCalculationService.submitBuyOutCalculations(details, schemeVersion);
    }

    /**
     * Banks the scheme surplus for facilities whose surplus has been updated.
     *
     * Identifies facilities eligible for surplus banking, selects the result for
     * the latest target period for each facility, and creates surplus records using
     * the non-negative scheme total surplus.
     * 
     */
    private void bankSurplus(List<BuyOutSurplusFacilityPerformanceResult> results, SchemeVersion schemeVersion,
            BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata) {
        
        Set<Long> facilitiesWithUpdatedSurplus = results.stream()
                .filter(BuyOutSurplusFacilityResultsUtil::shouldBankSurplus)
                .map(BuyOutSurplusFacilityPerformanceResult::getFacilityId)
                .collect(Collectors.toSet());
        
        if (facilitiesWithUpdatedSurplus.isEmpty()) {
            return;
        }
        
        Map<Long, BuyOutSurplusFacilityPerformanceResult> latestSurplusPerFacility = getTotalSchemeSurplusByFacility(results, facilitiesWithUpdatedSurplus);
            
        List<SurplusCreate> surplusCreateList = latestSurplusPerFacility.values()
                .stream().map(
                        res -> SurplusCreate.builder()
                                .resourceId(res.getFacilityId())
                                .surplusGained(res.getData().getSchemeTotalSurplus().max(BigDecimal.ZERO))
                                .history(SurplusCreateHistory.builder()
                                        .submitter("batch run ID " + metadata.getParentRequestId()).build())
                                .build())
                .toList();

        surplusService.bankSurplus(surplusCreateList, schemeVersion);
    }

    private Map<Long, BuyOutSurplusFacilityPerformanceResult> getTotalSchemeSurplusByFacility(
            List<BuyOutSurplusFacilityPerformanceResult> results, Set<Long> facilitiesWithUpdatedSurplus) {
        return results.stream()
                        .filter(result -> facilitiesWithUpdatedSurplus.contains(result.getFacilityId()))
                        .collect(Collectors.toMap(
                                BuyOutSurplusFacilityPerformanceResult::getFacilityId,
                                Function.identity(),
                                BinaryOperator.maxBy(
                                        Comparator.comparing(result -> result.getTargetPeriodType().getNumber())
                                )
                        ));
    }

    private void submitBuyOutSurplusProcessedData(
            List<BuyOutSurplusFacilityPerformanceResult> facilityPerformanceResults) {

        Set<Long> performanceDataIds = facilityPerformanceResults.stream()
                .filter(res -> !res.isProcessed())
                .map(BuyOutSurplusFacilityPerformanceResult::getPerformanceDataId)
                .collect(Collectors.toSet());

        buyOutSurplusProcessedDataService.submitBuyOutSurplusProcessedDataBatch(performanceDataIds, PerformanceDataResourceType.FACILITY);
    }
    
    private void addSubmittedAction(Request request, BuyOutSurplusFacilityAccountProcessingRequestPayload payload, BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata) {
        final BuyOutSurplusFacilityAccountProcessingSubmittedRequestActionPayload actionPayload = MAPPER
                .toSubmittedAction(payload, metadata);

        requestService.addActionToRequest(request, actionPayload,
                CcaRequestActionType.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_PROCESSING_SUBMITTED, payload.getSubmitterId());
    }

    private void lockProcessedTPRs(List<BuyOutSurplusFacilityPerformanceResult> results) {
        Set<Long> performanceDataIds = results.stream()
                .map(BuyOutSurplusFacilityPerformanceResult::getPerformanceDataId)
                .collect(Collectors.toSet());

        performanceDataFacilityStatusService.lockPerformanceData(performanceDataIds);
    }
}
