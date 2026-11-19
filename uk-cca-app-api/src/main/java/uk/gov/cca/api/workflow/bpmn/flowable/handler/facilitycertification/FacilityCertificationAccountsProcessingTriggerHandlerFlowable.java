package uk.gov.cca.api.workflow.bpmn.flowable.handler.facilitycertification;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.service.FacilityCertificationAccountProcessingCreateRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class FacilityCertificationAccountsProcessingTriggerHandlerFlowable implements JavaDelegate {

    private final FacilityCertificationAccountProcessingCreateRequestService facilityCertificationAccountProcessingCreateRequestService;

    @Override
    public void execute(DelegateExecution execution) {
        final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final String requestBusinessKey = (String) execution.getVariable(BpmnProcessConstants.BUSINESS_KEY);

        // Create request
        facilityCertificationAccountProcessingCreateRequestService.createRequest(accountId, requestId, requestBusinessKey);
    }
}
