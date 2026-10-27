package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityPerformanceResult;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.validation.BuyOutSurplusFacilityViolation;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingData;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityProcessingContext;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.utils.BuyOutSurplusFacilityResultsUtil;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Log4j2
@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityAccountProcessingService {

	private final RequestService requestService;
	private final BuyOutSurplusFacilityAccountDataLoader dataLoader;
    private final BuyOutSurplusFacilityProcessingContextFactory facilityProcessingContextFactory;
	private final BuyOutSurplusFacilityProcessor facilityProcessor;
	
	public void doProcess(String requestId, BuyOutSurplusFacilityAccountState accountState) {
		final Request request = requestService.findRequestById(requestId);
        final BuyOutSurplusFacilityAccountProcessingRequestPayload payload = (BuyOutSurplusFacilityAccountProcessingRequestPayload) request.getPayload();
        
		final List<TargetPeriodInfoDTO> targetPeriods = payload.getTargetPeriodsDetails().stream()
				.sorted(Comparator.comparing(TargetPeriodInfoDTO::getStartDate))
				.toList();
        
        BuyOutSurplusFacilityAccountProcessingData processingData = dataLoader.load(accountState.getAccountId(), targetPeriods);
        
        /*
         * Process each facility independently and sequentially
         * processFacility() is intentionally isolated/stateless so that this can later become:
         * facilitiesTprMap.entrySet().parallelStream() to test parallel processing performance
         * without changing the calculation logic.
         */
        processingData.getPerformanceDataReports().forEach((facilityBusinessId, tprs) -> {
        	        	
        	BuyOutSurplusFacilityProcessingContext facilityContext =
        	        facilityProcessingContextFactory.create(facilityBusinessId, 
                            targetPeriods, 
                            payload.isApplyPrimaryRules(),
        	                tprs,
							processingData.getPaidOrRefundedTransactions().getOrDefault(facilityBusinessId, Map.of()),
							processingData.getBuyOutSurplusCalculations().getOrDefault(facilityBusinessId, Map.of()),
							processingData.getSurplusBuckets().get(facilityBusinessId));


        	try {
        		facilityProcessor.doProcessFacility(facilityContext, accountState);
        	} catch (Exception e) {
                log.error("Error processing buy-out surplus for facility {}", facilityBusinessId, e);
                BuyOutSurplusFacilityResultsUtil.markTPRsAsFailed(facilityBusinessId,
						tprs,
						BuyOutSurplusFacilityViolation.BuyOutSurplusFacilityViolationMessage.PROCESSING_ERROR.getMessage(),
						accountState.getFacilityPerformanceResults());
			}
        });
    }

	@Transactional
	public void complete(String requestId, BuyOutSurplusFacilityAccountState accountState) {
		final Request request = requestService.findRequestById(requestId);
        final BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata =
                (BuyOutSurplusFacilityAccountProcessingRequestMetadata) request.getMetadata();

        final Request parentRequest = requestService.findRequestById(metadata.getParentRequestId());
        BuyOutSurplusFacilityRunRequestPayload parentRequestPayload = 
        		(BuyOutSurplusFacilityRunRequestPayload) parentRequest.getPayload();
        accountState.setSucceeded(accountState.getErrors().isEmpty() && hasSuccessfulFacility(accountState));
        parentRequestPayload.getAccountStates().put(accountState.getAccountId(), accountState);	
	}

	private boolean hasSuccessfulFacility(BuyOutSurplusFacilityAccountState accountState) {
		return accountState.getFacilityPerformanceResults().stream().anyMatch(BuyOutSurplusFacilityPerformanceResult::isSucceeded);
	}
}
