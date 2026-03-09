package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.peerreview.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecision;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.peerreview.CcaPeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.peerreview.CcaPeerReviewSubmitValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.peerreview.domain.UnderlyingAgreementVariationRegulatorLedPeerReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.peerreview.service.UnderlyingAgreementVariationRegulatorLedPeerReviewService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewDecision;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewDecisionType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedPeerReviewDecisionActionHandlerTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedPeerReviewDecisionActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private UnderlyingAgreementVariationRegulatorLedPeerReviewService underlyingAgreementVariationRegulatorLedPeerReviewService;

    @Mock
    private CcaPeerReviewSubmitValidator validator;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final long taskId = 1L;
        final String userId = "userId";
        final AppUser appUser = AppUser.builder().userId(userId).build();
        final CcaPeerReviewDecision decision = CcaPeerReviewDecision.builder()
                .decision(PeerReviewDecision.builder().type(PeerReviewDecisionType.AGREE).build())
                .build();
        final CcaPeerReviewDecisionRequestTaskActionPayload taskActionPayload =
                CcaPeerReviewDecisionRequestTaskActionPayload.builder()
                        .decision(decision)
                        .build();

        final String processTaskId = "processTaskId";
        final Request request = Request.builder().build();
        final RequestTask requestTask = RequestTask.builder()
                .processTaskId(processTaskId)
                .request(request)
                .payload(UnderlyingAgreementVariationRegulatorLedPeerReviewRequestTaskPayload.builder()
                        .decision(decision)
                        .build())
                .build();
        final CcaPeerReviewDecisionSubmittedRequestActionPayload actionPayload =
                CcaPeerReviewDecisionSubmittedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_PEER_REVIEW_SUBMITTED_PAYLOAD)
                        .decision(decision)
                        .peerReviewAttachments(Map.of())
                        .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        // Invoke
        handler.process(taskId, CcaRequestTaskActionType.UNDERLYING_AGREEMENT_SUBMIT_PEER_REVIEW_DECISION, appUser, taskActionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(validator, times(1)).validate(requestTask, taskActionPayload);
        verify(underlyingAgreementVariationRegulatorLedPeerReviewService, times(1)).submitPeerReview(requestTask, taskActionPayload);
        verify(requestService, times(1)).addActionToRequest(request, actionPayload,
                CcaRequestActionType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEWER_ACCEPTED, userId);
        verify(workflowService, times(1)).completeTask(processTaskId);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_SUBMIT_PEER_REVIEW_DECISION);
    }
}
