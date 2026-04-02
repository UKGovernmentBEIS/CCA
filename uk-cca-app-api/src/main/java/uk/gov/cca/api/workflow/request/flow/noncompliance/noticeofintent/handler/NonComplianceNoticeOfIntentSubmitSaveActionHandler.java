package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NoticeOfIntentSubmitSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.service.NoticeOfIntentSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@AllArgsConstructor
public class NonComplianceNoticeOfIntentSubmitSaveActionHandler implements RequestTaskActionHandler<NoticeOfIntentSubmitSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final NoticeOfIntentSubmitService noticeOfIntentSubmitService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, NoticeOfIntentSubmitSaveRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        noticeOfIntentSubmitService.applySaveAction(payload, requestTask);
        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_APPLICATION);
    }
}
