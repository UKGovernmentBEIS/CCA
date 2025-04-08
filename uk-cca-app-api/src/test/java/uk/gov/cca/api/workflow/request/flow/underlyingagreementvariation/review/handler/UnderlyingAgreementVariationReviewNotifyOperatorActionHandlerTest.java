package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewOutcome;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.service.UnderlyingAgreementVariationReviewService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.validation.UnderlyingAgreementVariationReviewValidatorService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnderlyingAgreementVariationReviewNotifyOperatorActionHandlerTest {

    @InjectMocks
    private UnderlyingAgreementVariationReviewNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private UnderlyingAgreementVariationReviewValidatorService underlyingAgreementVariationReviewValidatorService;

    @Mock
    private UnderlyingAgreementVariationReviewService underlyingAgreementVariationReviewService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final AppUser appUser = AppUser.builder().build();
        final long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.UNDERLYING_AGREEMENT_SUBMIT_APPLICATION;
        final UnderlyingAgreementVariationReviewRequestTaskPayload requestTaskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(Determination.builder().type(DeterminationType.ACCEPTED).additionalInformation("text").build())

                        .build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).request(request).payload(requestTaskPayload).processTaskId(processTaskId).build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();
        final UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload payload =
                UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        RequestTaskPayload taskPayload = handler.process(requestTaskId, requestTaskActionType, appUser, payload);

        // Verify
        assertThat(taskPayload).isEqualTo(requestTaskPayload);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(underlyingAgreementVariationReviewValidatorService, times(1)).validate(requestTask, payload, appUser);
        verify(underlyingAgreementVariationReviewService, times(1)).notifyOperator(requestTask, decisionNotification, appUser);
        verify(workflowService, times(1)).completeTask(processTaskId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.REVIEW_DETERMINATION, DeterminationType.ACCEPTED,
                        BpmnProcessConstants.REVIEW_OUTCOME, CcaReviewOutcome.NOTIFY_OPERATOR));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
