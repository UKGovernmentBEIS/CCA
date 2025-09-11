package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.service.UnderlyingAgreementVariationActivationService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.validation.UnderlyingAgreementVariationActivationNotifyOperatorValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationActivationNotifyOperatorActionHandler implements
        RequestTaskActionHandler<CcaNotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final UnderlyingAgreementVariationActivationNotifyOperatorValidator underlyingAgreementVariationActivationNotifyOperatorValidator;
    private final UnderlyingAgreementVariationActivationService underlyingAgreementVariationActivationService;
    private final WorkflowService workflowService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
                        CcaNotifyOperatorForDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate
        underlyingAgreementVariationActivationNotifyOperatorValidator.validate(requestTask, payload, appUser);

        // Update Request
        final CcaDecisionNotification decisionNotification = payload.getDecisionNotification();
        underlyingAgreementVariationActivationService.notifyOperator(requestTask, decisionNotification);

        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId())
        );
        
        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
