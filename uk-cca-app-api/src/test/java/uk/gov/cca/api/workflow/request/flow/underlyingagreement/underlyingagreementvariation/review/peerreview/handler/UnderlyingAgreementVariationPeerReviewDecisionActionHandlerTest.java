package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.peerreview.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.peerreview.CcaPeerReviewSubmitValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.peerreview.domain.UnderlyingAgreementVariationPeerReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.peerreview.service.UnderlyingAgreementVariationPeerReviewService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewDecision;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewDecisionType;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationPeerReviewDecisionActionHandlerTest {

    @InjectMocks
    private UnderlyingAgreementVariationPeerReviewDecisionActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private CcaPeerReviewSubmitValidator validator;

    @Mock
    private UnderlyingAgreementVariationPeerReviewService underlyingAgreementVariationPeerReviewService;

    @Test
    void process() {

        final PeerReviewDecision decision = PeerReviewDecision.builder()
                .type(PeerReviewDecisionType.DISAGREE)
                .notes("not approved")
                .build();
        final CcaPeerReviewDecision peerReviewDecision = CcaPeerReviewDecision.builder()
                .decision(decision)
                .files(Set.of())
                .build();
        final CcaPeerReviewDecisionRequestTaskActionPayload payload =
                CcaPeerReviewDecisionRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD)
                        .decision(peerReviewDecision)
                        .build();
        final String userId = "userId";
        final AppUser appUser = AppUser.builder().userId(userId).build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final UnderlyingAgreementVariationPeerReviewRequestTaskPayload requestTaskPayload = UnderlyingAgreementVariationPeerReviewRequestTaskPayload.builder()
                .peerReviewAttachments(Map.of())
                .decision(peerReviewDecision)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(2L)
                .request(request)
                .processTaskId(processTaskId)
                .payload(requestTaskPayload)
                .build();

        when(requestTaskService.findTaskById(2L)).thenReturn(requestTask);

        handler.process(requestTask.getId(),
                CcaRequestTaskActionType.UNDERLYING_AGREEMENT_SUBMIT_PEER_REVIEW_DECISION,
                appUser,
                payload);

        final ArgumentCaptor<CcaPeerReviewDecisionSubmittedRequestActionPayload> actionPayloadArgumentCaptor =
                ArgumentCaptor.forClass(CcaPeerReviewDecisionSubmittedRequestActionPayload.class);

        verify(requestService, times(1)).addActionToRequest(
                eq(request),
                actionPayloadArgumentCaptor.capture(),
                eq(CcaRequestActionType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEWER_REJECTED),
                eq(userId));

        final CcaPeerReviewDecisionSubmittedRequestActionPayload captorValue = actionPayloadArgumentCaptor.getValue();
        assertThat(captorValue.getDecision()).isEqualTo(peerReviewDecision);
        assertThat(captorValue.getPayloadType()).isEqualTo(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_PEER_REVIEW_SUBMITTED_PAYLOAD);

        verify(workflowService, times(1)).completeTask(processTaskId);
        verify(validator, times(1)).validate(requestTask, payload);
        verify(underlyingAgreementVariationPeerReviewService, times(1)).submitPeerReview(requestTask, payload);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_SUBMIT_PEER_REVIEW_DECISION);
    }

}
