package uk.gov.cca.api.workflow.request.flow.admintermination.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationOutcome;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReason;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
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
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTerminationSubmitRequestPeerReviewActionHandlerTest {

    @InjectMocks
    private AdminTerminationSubmitRequestPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private AdminTerminationSubmitRequestPeerReviewValidator validator;

    @Mock
    private AdminTerminationSubmitService adminTerminationSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestService requestService;

    @Test
    void process() {
        final long requestTaskId = 1L;
        final String requestId = "requestId";
        final Request request = Request.builder().id(requestId).build();
        final String processId = "process";
        final String requestTaskActionType = CcaRequestTaskActionType.ADMIN_TERMINATION_REQUEST_PEER_REVIEW;
        final AppUser appUser = AppUser.builder().build();
        final String regulatorReviewer = appUser.getUserId();
        final String peerReviewer = UUID.randomUUID().toString();
        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
                .payloadType(CcaRequestTaskActionPayloadType.ADMIN_TERMINATION_PEER_REVIEW_REQUEST_PAYLOAD)
                .peerReviewer(peerReviewer)
                .build();
        final AdminTerminationSubmitRequestTaskPayload requestTaskPayload = AdminTerminationSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.ADMIN_TERMINATION_SUBMIT_PAYLOAD)
                .adminTerminationReasonDetails(AdminTerminationReasonDetails.builder()
                        .reason(AdminTerminationReason.DATA_NOT_PROVIDED)
                        .build())
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
        verify(adminTerminationSubmitService, times(1))
                .requestPeerReview(requestTask, peerReviewer, appUser.getUserId());
        verify(requestService, times(1))
                .addActionToRequest(request, null, CcaRequestActionType.ADMIN_TERMINATION_PEER_REVIEW_REQUESTED, regulatorReviewer);
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(CcaBpmnProcessConstants.ADMIN_TERMINATION_OUTCOME, AdminTerminationOutcome.PEER_REVIEW_REQUIRED));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.ADMIN_TERMINATION_REQUEST_PEER_REVIEW);
    }
}
