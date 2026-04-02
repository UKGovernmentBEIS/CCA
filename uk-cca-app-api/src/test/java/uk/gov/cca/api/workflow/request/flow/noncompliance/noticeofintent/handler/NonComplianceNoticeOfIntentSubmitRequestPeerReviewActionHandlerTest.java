package uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceOutcome;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NoticeOfIntent;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.domain.NonComplianceNoticeOfIntentSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.service.NoticeOfIntentSubmitService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.noticeofintent.validation.NoticeOfIntentSubmitRequestPeerReviewValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceNoticeOfIntentSubmitRequestPeerReviewActionHandlerTest {

    @InjectMocks
    private NonComplianceNoticeOfIntentSubmitRequestPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private NoticeOfIntentSubmitService noticeOfIntentSubmitService;

    @Mock
    private NoticeOfIntentSubmitRequestPeerReviewValidator validator;

    @Test
    void process() {
        final long requestTaskId = 1L;
        final String requestId = "requestId";
        final Request request = Request.builder().id(requestId).build();
        final String processId = "process";
        final String requestTaskActionType = CcaRequestTaskActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW;
        final AppUser appUser = AppUser.builder().build();
        final String peerReviewer = UUID.randomUUID().toString();
        final UUID fileUuid = UUID.randomUUID();

        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
                .payloadType(CcaRequestTaskActionPayloadType.NON_COMPLIANCE_PEER_REVIEW_REQUEST_PAYLOAD)
                .peerReviewer(peerReviewer)
                .build();

        final NoticeOfIntent noticeOfIntent = NoticeOfIntent.builder()
                .noticeOfIntentFile(fileUuid)
                .comments("bla bla bla")
                .build();
        final NonComplianceNoticeOfIntentSubmitRequestTaskPayload requestTaskPayload = NonComplianceNoticeOfIntentSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PAYLOAD)
                .noticeOfIntent(noticeOfIntent)
                .nonComplianceAttachments(Map.of(fileUuid, "attachment"))
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processId)
                .payload(requestTaskPayload)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        RequestTaskPayload taskPayload = handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        // Verify
        assertThat(taskPayload).isEqualTo(requestTaskPayload);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(validator, times(1)).validate(requestTask, taskActionPayload, appUser);
        verify(noticeOfIntentSubmitService, times(1))
                .requestPeerReview(requestTask, peerReviewer, appUser.getUserId());
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(CcaBpmnProcessConstants.NON_COMPLIANCE_OUTCOME, NonComplianceOutcome.PEER_REVIEW_REQUIRED));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW);
    }
}
