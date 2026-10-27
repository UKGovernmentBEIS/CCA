package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunSummary;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.domain.BuyOutSurplusFacilityRunCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.transform.BuyOutSurplusFacilityRunMapper;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityRunService {

	private final RequestService requestService;
	private static final BuyOutSurplusFacilityRunMapper MAPPER = Mappers.getMapper(BuyOutSurplusFacilityRunMapper.class);

    @Transactional
    public void submit(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final BuyOutSurplusFacilityRunRequestPayload requestPayload = (BuyOutSurplusFacilityRunRequestPayload) request.getPayload();

        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);

        requestService.addActionToRequest(request,
                null,
                CcaRequestActionType.BUY_OUT_SURPLUS_FACILITY_RUN_SUBMITTED,
                requestPayload.getSubmitterId());
    }

    @Transactional
	public void complete(String requestId) {
    	final Request request = requestService.findRequestById(requestId);
        BuyOutSurplusFacilityRunRequestPayload requestPayload = (BuyOutSurplusFacilityRunRequestPayload) request.getPayload();
        BuyOutSurplusFacilityRunRequestMetadata metadata = (BuyOutSurplusFacilityRunRequestMetadata) request.getMetadata();
        final Map<Long, BuyOutSurplusFacilityAccountState> accountStates = requestPayload.getAccountStates();

        BuyOutSurplusFacilityRunSummary runSummary = MAPPER.toBuyOutSurplusFacilityRunSummary(accountStates, requestPayload.getTargetPeriodsDetails());
        requestPayload.setRunSummary(runSummary);

        // Update metadata
        metadata.setTotalAccounts(runSummary.getTotalAccounts());
        metadata.setFailedAccounts(runSummary.getFailedAccounts());
        metadata.setTotalFacilities(runSummary.getTotalFacilities());
        metadata.setFailedFacilities(runSummary.getFailedFacilities());

        String actionType = CcaRequestActionType.BUY_OUT_SURPLUS_FACILITY_RUN_COMPLETED;
        // Update status
        if(runSummary.getFailedAccounts() > 0 || runSummary.getFailedFacilities() > 0) {
            requestService.updateRequestStatus(requestId, CcaRequestStatuses.COMPLETED_WITH_FAILURES);
            actionType = CcaRequestActionType.BUY_OUT_SURPLUS_FACILITY_RUN_COMPLETED_WITH_FAILURES;
        }

        // Create timeline
        final BuyOutSurplusFacilityRunCompletedRequestActionPayload requestActionPayload = MAPPER
                .toBuyOutSurplusFacilityRunCompletedRequestActionPayload(requestPayload);
        requestService.addActionToRequest(request, requestActionPayload, actionType, requestPayload.getSubmitterId());
	}
}
