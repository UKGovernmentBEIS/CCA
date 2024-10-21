package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewOutcome;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.service.UnderlyingAgreementVariationReviewService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.validation.UnderlyingAgreementVariationReviewValidatorService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationReviewNotifyOperatorActionHandler
        implements RequestTaskActionHandler<CcaNotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final UnderlyingAgreementVariationReviewValidatorService underlyingAgreementVariationReviewValidatorService;
    private final UnderlyingAgreementVariationReviewService underlyingAgreementVariationReviewService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
                        CcaNotifyOperatorForDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload = (UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload();

        // Validate
        underlyingAgreementVariationReviewValidatorService.validate(requestTask, payload, appUser);

        // Update Request
        final CcaDecisionNotification decisionNotification = payload.getDecisionNotification();
        final DeterminationType determinationType = taskPayload.getDetermination().getType();
        underlyingAgreementVariationReviewService.notifyOperator(requestTask, decisionNotification, appUser);

        // Complete task
        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.REVIEW_DETERMINATION, determinationType,
                        BpmnProcessConstants.REVIEW_OUTCOME, CcaReviewOutcome.NOTIFY_OPERATOR)
        );
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
