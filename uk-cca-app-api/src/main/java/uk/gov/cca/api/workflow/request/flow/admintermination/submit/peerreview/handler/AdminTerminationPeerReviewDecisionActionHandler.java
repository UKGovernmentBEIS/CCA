package uk.gov.cca.api.workflow.request.flow.admintermination.submit.peerreview.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.peerreview.service.AdminTerminationPeerReviewService;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.transform.CcaPeerReviewMapper;
import uk.gov.cca.api.workflow.request.flow.common.validation.peerreview.CcaPeerReviewSubmitValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewDecisionType;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminTerminationPeerReviewDecisionActionHandler implements
        RequestTaskActionHandler<CcaPeerReviewDecisionRequestTaskActionPayload> {

    private static final CcaPeerReviewMapper PEER_REVIEW_MAPPER = Mappers.getMapper(CcaPeerReviewMapper.class);

    private final AdminTerminationPeerReviewService adminTerminationPeerReviewService;
    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final CcaPeerReviewSubmitValidator validator;
    private final WorkflowService workflowService;

    @Override
    public RequestTaskPayload process(Long requestTaskId,
                                      String requestTaskActionType,
                                      AppUser appUser,
                                      CcaPeerReviewDecisionRequestTaskActionPayload taskActionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final String peerReviewer = appUser.getUserId();

        // validate
        validator.validate(requestTask, taskActionPayload);

        // update task payload to submit any attached file before complete the task
        adminTerminationPeerReviewService.submitPeerReview(requestTask, taskActionPayload);

        final CcaPeerReviewDecisionSubmittedRequestActionPayload actionPayload =
                PEER_REVIEW_MAPPER.toPeerReviewDecisionSubmittedRequestActionPayload(
                        (CcaPeerReviewDecisionRequestTaskPayload) requestTask.getPayload(),
                        CcaRequestActionPayloadType.ADMIN_TERMINATION_PEER_REVIEW_SUBMITTED_PAYLOAD);

        final String actionType = actionPayload.getDecision().getDecision().getType() == PeerReviewDecisionType.AGREE ?
                CcaRequestActionType.ADMIN_TERMINATION_APPLICATION_PEER_REVIEWER_ACCEPTED :
                CcaRequestActionType.ADMIN_TERMINATION_APPLICATION_PEER_REVIEWER_REJECTED;

        requestService.addActionToRequest(request,
                actionPayload,
                actionType,
                peerReviewer);

        workflowService.completeTask(requestTask.getProcessTaskId());

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.ADMIN_TERMINATION_SUBMIT_PEER_REVIEW_DECISION);
    }
}
