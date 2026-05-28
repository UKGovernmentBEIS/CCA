package uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.peerreview.handler;

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
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.peerreview.domain.NonComplianceEnforcementResponseNoticePeerReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.enforcementresponsenotice.peerreview.service.EnforcementResponseNoticePeerReviewService;
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
class NonComplianceEnforcementResponseNoticePeerReviewDecisionActionHandlerTest {

    @InjectMocks
    private NonComplianceEnforcementResponseNoticePeerReviewDecisionActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private CcaPeerReviewSubmitValidator validator;

    @Mock
    private EnforcementResponseNoticePeerReviewService enforcementResponseNoticePeerReviewService;

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
        final CcaPeerReviewDecisionRequestTaskActionPayload taskActionPayload =
                CcaPeerReviewDecisionRequestTaskActionPayload.builder()
                        .payloadType(CcaRequestTaskActionPayloadType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_PEER_REVIEW_DECISION_PAYLOAD)
                        .decision(peerReviewDecision)
                        .build();
        final String userId = "userId";
        final AppUser appUser = AppUser.builder().userId(userId).build();
        final String peerReviewer = appUser.getUserId();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final NonComplianceEnforcementResponseNoticePeerReviewRequestTaskPayload requestTaskPayload = NonComplianceEnforcementResponseNoticePeerReviewRequestTaskPayload.builder()
                .peerReviewAttachments(Map.of())
                .decision(peerReviewDecision)
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(2L)
                .request(request)
                .processTaskId(processTaskId)
                .payload(requestTaskPayload)
                .build();
        final CcaPeerReviewDecisionSubmittedRequestActionPayload requestActionPayload = CcaPeerReviewDecisionSubmittedRequestActionPayload.builder()
                .payloadType(CcaRequestActionPayloadType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_PEER_REVIEW_SUBMITTED_PAYLOAD)
                .decision(peerReviewDecision)
                .peerReviewAttachments(Map.of())
                .build();

        when(requestTaskService.findTaskById(2L)).thenReturn(requestTask);

        handler.process(requestTask.getId(),
                CcaRequestTaskActionType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PEER_REVIEW_DECISION,
                appUser,
                taskActionPayload);

        final ArgumentCaptor<CcaPeerReviewDecisionSubmittedRequestActionPayload> actionPayloadArgumentCaptor =
                ArgumentCaptor.forClass(CcaPeerReviewDecisionSubmittedRequestActionPayload.class);

        verify(requestService, times(1)).addActionToRequest(
                eq(request),
                actionPayloadArgumentCaptor.capture(),
                eq(CcaRequestActionType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_PEER_REVIEWER_REJECTED),
                eq(userId));

        final CcaPeerReviewDecisionSubmittedRequestActionPayload captorValue = actionPayloadArgumentCaptor.getValue();
        assertThat(captorValue.getDecision()).isEqualTo(peerReviewDecision);
        assertThat(captorValue.getPayloadType()).isEqualTo(CcaRequestActionPayloadType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_PEER_REVIEW_SUBMITTED_PAYLOAD);

        verify(requestService, times(1)).addActionToRequest(request, requestActionPayload,
                CcaRequestActionType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_PEER_REVIEWER_REJECTED, peerReviewer);
        verify(workflowService, times(1)).completeTask(processTaskId);
        verify(enforcementResponseNoticePeerReviewService, times(1)).submitPeerReview(requestTask, taskActionPayload);
        verify(validator, times(1)).validate(requestTask, taskActionPayload);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.NON_COMPLIANCE_ENFORCEMENT_RESPONSE_NOTICE_SUBMIT_PEER_REVIEW_DECISION);
    }
}
