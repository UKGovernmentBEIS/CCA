package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewOutcome;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationOutcome;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.domain.UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationRegulatorLedDetermination;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.service.UnderlyingAgreementVariationRegulatorLedSubmitService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.validation.UnderlyingAgreementVariationRegulatorLedSubmitNotifyOperatorValidatorService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedSubmitNotifyOperatorActionHandlerTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedSubmitNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private UnderlyingAgreementVariationRegulatorLedSubmitNotifyOperatorValidatorService underlyingAgreementVariationRegulatorLedSubmitNotifyOperatorValidatorService;

    @Mock
    private UnderlyingAgreementVariationRegulatorLedSubmitService underlyingAgreementVariationRegulatorLedSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final Long requestTaskId = 1L;
        final String requestId = "requestId";
        final String processTaskId = "processTaskId";
        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder()
                        .determination(VariationRegulatorLedDetermination.builder().variationImpactsAgreement(true).build())
                        .build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("user"))
                .build();
        final CcaNotifyOperatorForDecisionRequestTaskActionPayload actionPayload =
                CcaNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .request(Request.builder().id(requestId).build())
                .processTaskId(processTaskId)
                .payload(taskPayload)
                .build();
       final AppUser user = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_NOTIFY_OPERATOR_FOR_DECISION, user, actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(underlyingAgreementVariationRegulatorLedSubmitNotifyOperatorValidatorService, times(1))
                .validate(requestTask, actionPayload, user);
        verify(underlyingAgreementVariationRegulatorLedSubmitService, times(1))
                .notifyOperator(requestTask, decisionNotification);
        verify(workflowService, times(1)).completeTask(
                processTaskId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        CcaBpmnProcessConstants.UNDERLYING_AGREEMENT_VARIATION_OUTCOME, UnderlyingAgreementVariationOutcome.SUBMITTED,
                        BpmnProcessConstants.REVIEW_OUTCOME, CcaReviewOutcome.NOTIFY_OPERATOR));
    }

    @Test
    void process_COMPLETED() {
        final Long requestTaskId = 1L;
        final String requestId = "requestId";
        final String processTaskId = "processTaskId";
        final UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationRegulatorLedSubmitRequestTaskPayload.builder()
                        .determination(VariationRegulatorLedDetermination.builder().variationImpactsAgreement(false).build())
                        .build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("user"))
                .build();
        final CcaNotifyOperatorForDecisionRequestTaskActionPayload actionPayload =
                CcaNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .request(Request.builder().id(requestId).build())
                .processTaskId(processTaskId)
                .payload(taskPayload)
                .build();
        final AppUser user = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_NOTIFY_OPERATOR_FOR_DECISION, user, actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(underlyingAgreementVariationRegulatorLedSubmitNotifyOperatorValidatorService, times(1))
                .validate(requestTask, actionPayload, user);
        verify(underlyingAgreementVariationRegulatorLedSubmitService, times(1))
                .notifyOperator(requestTask, decisionNotification);
        verify(workflowService, times(1)).completeTask(
                processTaskId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId,
                        CcaBpmnProcessConstants.UNDERLYING_AGREEMENT_VARIATION_OUTCOME, UnderlyingAgreementVariationOutcome.COMPLETED,
                        BpmnProcessConstants.REVIEW_OUTCOME, CcaReviewOutcome.NOTIFY_OPERATOR));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
