package uk.gov.cca.api.workflow.bpmn.flowable.handler.noncompliance;

import lombok.AllArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.service.NonComplianceCancelledService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@AllArgsConstructor
public class NonComplianceCancelledHandlerFlowable implements JavaDelegate {

    private final NonComplianceCancelledService nonComplianceCancelledService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        nonComplianceCancelledService.cancel(requestId);
    }
}
