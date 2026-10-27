package uk.gov.cca.api.workflow.bpmn.flowable.handler.buyoutsurplusfacility;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.service.BuyOutSurplusFacilityRunService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityRunSubmittedHandlerFlowable implements JavaDelegate {

	private final BuyOutSurplusFacilityRunService buyOutSurplusFacilityRunService;
	
    @Override
    public void execute(DelegateExecution delegateExecution) {
    	
    	final String requestId = (String) delegateExecution.getVariable(BpmnProcessConstants.REQUEST_ID);
    	buyOutSurplusFacilityRunService.submit(requestId);
    }
}
