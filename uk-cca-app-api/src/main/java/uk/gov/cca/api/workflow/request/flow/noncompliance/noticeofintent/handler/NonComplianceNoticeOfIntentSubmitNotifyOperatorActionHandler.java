package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.service.NoticeOfIntentSubmitService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.validation.NoticeOfIntentSubmitNotifyOperatorValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.netz.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Component
public class NonComplianceNoticeOfIntentSubmitNotifyOperatorActionHandler implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final NoticeOfIntentSubmitService noticeOfIntentSubmitService;
    private final NoticeOfIntentSubmitNotifyOperatorValidator validator;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, NotifyOperatorForDecisionRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate
        validator.validate(requestTask, payload, appUser);

        // Update Request
        final DecisionNotification decisionNotification = payload.getDecisionNotification();
        noticeOfIntentSubmitService.notifyOperator(requestTask, decisionNotification);

        // Complete task
        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId()));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_NOTIFY_OPERATOR);
    }
}
