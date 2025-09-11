package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service.UnderlyingAgreementReviewService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.validation.UnderlyingAgreementReviewRequestPeerReviewValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.ReviewOutcome;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementReviewRequestPeerReviewActionHandlerTest {

    @InjectMocks
    private UnderlyingAgreementReviewRequestPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private UnderlyingAgreementReviewRequestPeerReviewValidator underlyingAgreementReviewRequestPeerReviewValidator;

    @Mock
    private UnderlyingAgreementReviewService underlyingAgreementReviewService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestService requestService;

    @Test
    void process() {
        final AppUser appUser = AppUser.builder().build();
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.UNDERLYING_AGREEMENT_REQUEST_PEER_REVIEW;
        final String regulatorReviewer = appUser.getUserId();
        final String peerReviewer = UUID.randomUUID().toString();
        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
                .payloadType(CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_PEER_REVIEW_REQUEST_PAYLOAD)
                .peerReviewer(peerReviewer)
                .build();
        final UnderlyingAgreementReviewRequestTaskPayload requestTaskPayload =
                UnderlyingAgreementReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(Determination.builder().type(DeterminationType.ACCEPTED).additionalInformation("text").build())
                        .build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).request(request).payload(requestTaskPayload).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        RequestTaskPayload taskPayload = handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        assertThat(taskPayload).isEqualTo(requestTaskPayload);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(underlyingAgreementReviewRequestPeerReviewValidator, times(1)).validate(requestTask, taskActionPayload, appUser);
        verify(underlyingAgreementReviewService, times(1)).requestPeerReview(requestTask, peerReviewer, regulatorReviewer);
        verify(requestService, times(1))
                .addActionToRequest(request, null, CcaRequestActionType.UNDERLYING_AGREEMENT_APPLICATION_PEER_REVIEW_REQUESTED, regulatorReviewer);
        verify(workflowService, times(1)).completeTask(processTaskId,
                Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.PEER_REVIEW_REQUIRED));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_REQUEST_PEER_REVIEW);
    }
}
