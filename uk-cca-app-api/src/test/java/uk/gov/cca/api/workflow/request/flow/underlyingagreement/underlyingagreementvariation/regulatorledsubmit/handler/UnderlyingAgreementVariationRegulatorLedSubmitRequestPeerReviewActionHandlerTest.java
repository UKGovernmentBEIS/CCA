package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewOutcome;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationOutcome;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.service.UnderlyingAgreementVariationRegulatorLedSubmitService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.validation.UnderlyingAgreementVariationRegulatorLedSubmitRequestPeerReviewValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedSubmitRequestPeerReviewActionHandlerTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedSubmitRequestPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private UnderlyingAgreementVariationRegulatorLedSubmitRequestPeerReviewValidator underlyingAgreementVariationRegulatorLedSubmitRequestPeerReviewValidator;

    @Mock
    private UnderlyingAgreementVariationRegulatorLedSubmitService underlyingAgreementVariationRegulatorLedSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final Long requestTaskId = 1L;
        final String requestId = "requestId";
        final String processTaskId = "processTaskId";
        final String peerReviewer = "peerReviewer";
        final String regulatorReviewer = "regulatorReviewer";
        final PeerReviewRequestTaskActionPayload actionPayload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer(peerReviewer)
                .build();
        final AppUser user = AppUser.builder().userId(regulatorReviewer).build();

        final Request request = Request.builder().id(requestId).build();
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .request(request)
                .processTaskId(processTaskId)
                .payload(UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder().build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_REQUEST_PEER_REVIEW, user, actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(underlyingAgreementVariationRegulatorLedSubmitRequestPeerReviewValidator, times(1))
                .validate(requestTask, actionPayload, user);
        verify(underlyingAgreementVariationRegulatorLedSubmitService, times(1))
                .requestPeerReview(requestTask, peerReviewer, regulatorReviewer);
        verify(requestService, times(1)).addActionToRequest(request, null,
                CcaRequestActionType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW_REQUESTED, regulatorReviewer);
        verify(workflowService, times(1)).completeTask(
                processTaskId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        CcaBpmnProcessConstants.UNDERLYING_AGREEMENT_VARIATION_OUTCOME, UnderlyingAgreementVariationOutcome.SUBMITTED,
                        BpmnProcessConstants.REVIEW_OUTCOME, CcaReviewOutcome.PEER_REVIEW_REQUIRED));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_REQUEST_PEER_REVIEW);
    }
}
