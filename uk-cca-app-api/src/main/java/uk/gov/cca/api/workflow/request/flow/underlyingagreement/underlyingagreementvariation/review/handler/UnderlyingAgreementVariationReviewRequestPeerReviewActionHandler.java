package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service.UnderlyingAgreementVariationReviewService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.validation.UnderlyingAgreementVariationReviewRequestPeerReviewValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.ReviewOutcome;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationReviewRequestPeerReviewActionHandler implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final WorkflowService workflowService;
    private final UnderlyingAgreementVariationReviewService underlyingAgreementVariationReviewService;
    private final UnderlyingAgreementVariationReviewRequestPeerReviewValidator validator;

    @Override
    public RequestTaskPayload process(Long requestTaskId,
                                      String requestTaskActionType,
                                      AppUser appUser,
                                      PeerReviewRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();

        // Validate
        validator.validate(requestTask, actionPayload, appUser);

        final String regulatorReviewer = appUser.getUserId();
        final String peerReviewer = actionPayload.getPeerReviewer();
        underlyingAgreementVariationReviewService.requestPeerReview(requestTask, peerReviewer, regulatorReviewer);

        requestService.addActionToRequest(request,
                null,
                CcaRequestActionType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW_REQUESTED,
                regulatorReviewer);

        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.PEER_REVIEW_REQUIRED));

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_REQUEST_PEER_REVIEW);
    }
}
