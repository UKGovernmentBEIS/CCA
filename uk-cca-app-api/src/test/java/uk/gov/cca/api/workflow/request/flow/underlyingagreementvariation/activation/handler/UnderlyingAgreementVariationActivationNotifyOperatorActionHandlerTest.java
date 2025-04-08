package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.domain.UnderlyingAgreementActivationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.service.UnderlyingAgreementVariationActivationService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.validation.UnderlyingAgreementVariationActivationNotifyOperatorValidator;
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
public class UnderlyingAgreementVariationActivationNotifyOperatorActionHandlerTest {

    @InjectMocks
    private UnderlyingAgreementVariationActivationNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private UnderlyingAgreementVariationActivationNotifyOperatorValidator underlyingAgreementVariationActivationNotifyOperatorValidator;

    @Mock
    private UnderlyingAgreementVariationActivationService underlyingAgreementVariationActivationService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final long requestTaskId = 1L;
        final String requestId = "requestId";
        final String processId = "process";
        final AppUser appUser = AppUser.builder().build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();
        final CcaNotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload =
                CcaNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();

        final UnderlyingAgreementActivationRequestTaskPayload requestTaskPayload = UnderlyingAgreementActivationRequestTaskPayload.builder()
        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_PAYLOAD)
        .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().id(requestId).build())
                .processTaskId(processId)
                .payload(requestTaskPayload)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        RequestTaskPayload taskPayload = handler.process(requestTaskId, CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_NOTIFY_OPERATOR_FOR_DECISION,
                appUser, taskActionPayload);

        // Verify
        assertThat(taskPayload).isEqualTo(requestTaskPayload);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(underlyingAgreementVariationActivationNotifyOperatorValidator, times(1))
                .validate(requestTask, taskActionPayload, appUser);
        verify(underlyingAgreementVariationActivationService, times(1))
                .notifyOperator(requestTask, decisionNotification);
        verify(workflowService, times(1)).completeTask(processId,
                Map.of(BpmnProcessConstants.REQUEST_ID, requestId));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
