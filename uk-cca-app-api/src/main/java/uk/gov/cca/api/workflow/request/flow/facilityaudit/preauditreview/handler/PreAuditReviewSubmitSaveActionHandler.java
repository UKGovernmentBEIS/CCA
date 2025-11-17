package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.service.PreAuditReviewSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PreAuditReviewSubmitSaveActionHandler implements RequestTaskActionHandler<PreAuditReviewSubmitSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PreAuditReviewSubmitService preAuditReviewSubmitService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, PreAuditReviewSubmitSaveRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        preAuditReviewSubmitService.applySaveAction(payload, requestTask);

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.FACILITY_AUDIT_PRE_AUDIT_REVIEW_SAVE_APPLICATION);
    }
}
