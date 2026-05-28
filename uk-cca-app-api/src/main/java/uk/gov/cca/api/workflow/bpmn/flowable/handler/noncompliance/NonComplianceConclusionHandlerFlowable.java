package uk.gov.cca.api.workflow.bpmn.flowable.handler.noncompliance;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.noncompliance.conclusion.service.NonComplianceConclusionSubmittedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class NonComplianceConclusionHandlerFlowable implements JavaDelegate {

    private final NonComplianceConclusionSubmittedService nonComplianceConclusionSubmittedService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        nonComplianceConclusionSubmittedService.submitConclusion(requestId);
    }
}
