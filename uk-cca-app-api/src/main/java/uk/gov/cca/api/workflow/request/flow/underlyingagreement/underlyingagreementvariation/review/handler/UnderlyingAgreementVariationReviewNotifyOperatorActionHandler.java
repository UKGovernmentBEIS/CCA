package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewOutcome;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationOutcome;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service.UnderlyingAgreementVariationReviewService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.validation.UnderlyingAgreementVariationReviewNotifyOperatorValidator;
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
public class UnderlyingAgreementVariationReviewNotifyOperatorActionHandler
        implements RequestTaskActionHandler<UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final UnderlyingAgreementVariationReviewNotifyOperatorValidator validator;
    private final UnderlyingAgreementVariationReviewService underlyingAgreementVariationReviewService;
    private final WorkflowService workflowService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
                                      UnderlyingAgreementVariationNotifyOperatorForDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Update taskPayload with proposed UNA from action payload
        underlyingAgreementVariationReviewService.saveProposedUnderlyingAgreement(payload, requestTask);

        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload = (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();

        // Validate
        validator.validate(requestTask, payload, appUser);

        // Update Request
        final CcaDecisionNotification decisionNotification = payload.getDecisionNotification();
        final DeterminationType determinationType = taskPayload.getDetermination().getDetermination().getType();
        final Boolean hasChanges = taskPayload.getDetermination().getVariationImpactsAgreement();
        underlyingAgreementVariationReviewService.notifyOperator(requestTask, decisionNotification, appUser);

        // Complete task
        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.REVIEW_DETERMINATION, DeterminationOutcome.fromDeterminationType(determinationType, hasChanges),
                        BpmnProcessConstants.REVIEW_OUTCOME, CcaReviewOutcome.NOTIFY_OPERATOR)
        );

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
