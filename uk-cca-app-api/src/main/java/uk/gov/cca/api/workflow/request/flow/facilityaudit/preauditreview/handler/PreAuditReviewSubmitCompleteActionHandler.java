package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.service.PreAuditReviewSubmitService;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.transform.PreAuditReviewSubmitMapper;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.validation.PreAuditReviewSubmitValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PreAuditReviewSubmitCompleteActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final PreAuditReviewSubmitService preAuditReviewSubmitService;
    private final WorkflowService workflowService;
    private final PreAuditReviewSubmitValidator preAuditReviewSubmitValidator;

    private static final PreAuditReviewSubmitMapper PRE_AUDIT_REVIEW_SUBMIT_MAPPER = Mappers.getMapper(PreAuditReviewSubmitMapper.class);

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final PreAuditReviewSubmitRequestTaskPayload taskPayload = (PreAuditReviewSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate data
        preAuditReviewSubmitValidator.validate(taskPayload);

        // Update Request
        preAuditReviewSubmitService.submitPreAuditReview(requestTask);

        // Add submit action request
        addCompletedRequestAction(appUser, taskPayload, requestTask.getRequest());

        // Complete task
        final Boolean isFurtherAuditNeeded =
                taskPayload.getPreAuditReviewDetails().getAuditDetermination().getFurtherAuditNeeded();

        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        CcaBpmnProcessConstants.FACILITY_AUDIT_OUTCOME, "",
                        CcaBpmnProcessConstants.IS_FURTHER_AUDIT_NEEDED, isFurtherAuditNeeded));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMIT_APPLICATION);
    }

    private void addCompletedRequestAction(AppUser user, PreAuditReviewSubmitRequestTaskPayload taskPayload, Request request) {

        PreAuditReviewSubmittedRequestActionPayload actionPayload =
                PRE_AUDIT_REVIEW_SUBMIT_MAPPER.toPreAuditReviewSubmittedRequestActionPayload(taskPayload);

        requestService.addActionToRequest(
                request,
                actionPayload,
                CcaRequestActionType.FACILITY_AUDIT_PRE_AUDIT_REVIEW_SUBMITTED,
                user.getUserId());
    }
}
