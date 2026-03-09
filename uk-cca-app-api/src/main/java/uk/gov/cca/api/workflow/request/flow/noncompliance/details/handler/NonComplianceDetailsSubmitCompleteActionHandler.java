package uk.gov.cca.api.workflow.request.flow.noncompliance.details.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.service.NonComplianceDetailsSubmitService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.transform.NonComplianceDetailsSubmitMapper;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.validation.NonComplianceDetailsSubmitValidator;
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

@RequiredArgsConstructor
@Component
public class NonComplianceDetailsSubmitCompleteActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final NonComplianceDetailsSubmitService nonComplianceDetailsSubmitService;
    private final NonComplianceDetailsSubmitValidator nonComplianceDetailsSubmitValidator;

    private static final NonComplianceDetailsSubmitMapper NON_COMPLIANCE_DETAILS_SUBMIT_MAPPER = Mappers.getMapper(NonComplianceDetailsSubmitMapper.class);

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final NonComplianceDetailsSubmitRequestTaskPayload taskPayload = (NonComplianceDetailsSubmitRequestTaskPayload) requestTask.getPayload();

        // Validate data
        nonComplianceDetailsSubmitValidator.validate(taskPayload);

        // Update Request
        nonComplianceDetailsSubmitService.submitDetails(requestTask);

        // Add submit action request
        addCompletedRequestAction(appUser, taskPayload, requestTask.getRequest());

        // Complete task
        final Boolean isEnforcementResponseNoticeRequired =
                taskPayload.getNonComplianceDetails().getIsEnforcementResponseNoticeRequired();

        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        CcaBpmnProcessConstants.NON_COMPLIANCE_OUTCOME, "",
                        CcaBpmnProcessConstants.IS_NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_NEEDED, isEnforcementResponseNoticeRequired));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.NON_COMPLIANCE_DETAILS_SUBMIT_APPLICATION);
    }

    private void addCompletedRequestAction(AppUser user, NonComplianceDetailsSubmitRequestTaskPayload taskPayload, Request request) {

        NonComplianceDetailsSubmittedRequestActionPayload actionPayload =
                NON_COMPLIANCE_DETAILS_SUBMIT_MAPPER.toNonComplianceDetailsSubmittedRequestActionPayload(taskPayload);

        requestService.addActionToRequest(
                request,
                actionPayload,
                CcaRequestActionType.NON_COMPLIANCE_DETAILS_SUBMITTED,
                user.getUserId());
    }
}
