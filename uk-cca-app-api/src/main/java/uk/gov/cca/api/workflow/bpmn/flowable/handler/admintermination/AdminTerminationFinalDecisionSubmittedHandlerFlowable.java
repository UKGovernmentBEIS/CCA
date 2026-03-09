package uk.gov.cca.api.workflow.bpmn.flowable.handler.admintermination;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.service.AdminTerminationFinalDecisionSubmittedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class AdminTerminationFinalDecisionSubmittedHandlerFlowable implements JavaDelegate {

    private final AdminTerminationFinalDecisionSubmittedService adminTerminationFinalDecisionSubmittedService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        adminTerminationFinalDecisionSubmittedService.submit(requestId);
    }
}
