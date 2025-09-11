package uk.gov.cca.api.workflow.request.flow.admintermination.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationOutcome;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.service.AdminTerminationSubmitService;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.validation.AdminTerminationSubmitRequestPeerReviewValidator;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AdminTerminationSubmitRequestPeerReviewActionHandler implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AdminTerminationSubmitService adminTerminationSubmitService;
    private final AdminTerminationSubmitRequestPeerReviewValidator validator;
    private final RequestService requestService;
    private final WorkflowService workflowService;

    @Override
    public RequestTaskPayload process(Long requestTaskId,
                                      String requestTaskActionType,
                                      AppUser appUser,
                                      PeerReviewRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();

        // validate
        validator.validate(requestTask, actionPayload, appUser);

        final String regulatorReviewer = appUser.getUserId();
        final String peerReviewer = actionPayload.getPeerReviewer();
        adminTerminationSubmitService.requestPeerReview(requestTask, peerReviewer, regulatorReviewer);

        requestService.addActionToRequest(request,
                null,
                CcaRequestActionType.ADMIN_TERMINATION_PEER_REVIEW_REQUESTED,
                regulatorReviewer);

        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(CcaBpmnProcessConstants.ADMIN_TERMINATION_OUTCOME, AdminTerminationOutcome.PEER_REVIEW_REQUIRED));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.ADMIN_TERMINATION_REQUEST_PEER_REVIEW);
    }
}
