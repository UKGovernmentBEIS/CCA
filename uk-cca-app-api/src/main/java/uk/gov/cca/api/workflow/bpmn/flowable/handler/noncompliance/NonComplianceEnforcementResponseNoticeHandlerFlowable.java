package uk.gov.cca.api.workflow.bpmn.flowable.handler.noncompliance;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.service.EnforcementResponseNoticeNotifyOperatorService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class NonComplianceEnforcementResponseNoticeHandlerFlowable implements JavaDelegate {

    private final EnforcementResponseNoticeNotifyOperatorService enforcementResponseNoticeNotifyOperatorService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        enforcementResponseNoticeNotifyOperatorService.sendEnforcementResponseNotice(requestId);
    }
}
