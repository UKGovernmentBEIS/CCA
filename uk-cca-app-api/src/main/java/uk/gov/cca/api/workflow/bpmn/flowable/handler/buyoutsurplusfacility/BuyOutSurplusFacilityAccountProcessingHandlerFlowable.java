package uk.gov.cca.api.workflow.bpmn.flowable.handler.buyoutsurplusfacility;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.common.utils.StringSafetyUtils;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service.BuyOutSurplusFacilityAccountProcessingFinalizeService;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service.BuyOutSurplusFacilityAccountProcessingService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Log4j2
@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityAccountProcessingHandlerFlowable implements JavaDelegate {

    private final RequestService requestService;
	private final BuyOutSurplusFacilityAccountProcessingService processingService;
	private final BuyOutSurplusFacilityAccountProcessingFinalizeService finalizeService;

	@Override
	public void execute(DelegateExecution execution) {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final BuyOutSurplusFacilityAccountState accountState = (BuyOutSurplusFacilityAccountState) execution
				.getVariable(CcaBpmnProcessConstants.BUY_OUT_SURPLUS_FACILITY_ACCOUNT_STATE);

		try {
		    final Request request = requestService.findRequestById(requestId);
		    
			processingService.doProcess(requestId, accountState);
			
            if (!accountState.getSuccessfulFacilityPerformanceResults().isEmpty()) {
                finalizeService.finalizeAccountProcessing(request, accountState.getSuccessfulFacilityPerformanceResults());
            }
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			accountState.getFacilityPerformanceResults().clear();
			accountState.getErrors().add(StringSafetyUtils.safeErrorMessage(e));
		}
	}

}
