package uk.gov.cca.api.workflow.bpmn.flowable.handler.noncompliance;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.service.NoticeOfIntentNotifyOperatorService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class NonComplianceNoticeOfIntentHandlerFlowable implements JavaDelegate {

    private final NoticeOfIntentNotifyOperatorService notifyOperatorService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        notifyOperatorService.sendNoticeOfIntent(requestId);
    }
}
