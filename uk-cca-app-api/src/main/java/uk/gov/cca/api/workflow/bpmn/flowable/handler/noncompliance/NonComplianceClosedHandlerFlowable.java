package uk.gov.cca.api.workflow.bpmn.flowable.handler.noncompliance;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.service.NonComplianceCloseService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class NonComplianceClosedHandlerFlowable implements JavaDelegate {

    private final NonComplianceCloseService nonComplianceCloseService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        nonComplianceCloseService.close(requestId);
    }
}
