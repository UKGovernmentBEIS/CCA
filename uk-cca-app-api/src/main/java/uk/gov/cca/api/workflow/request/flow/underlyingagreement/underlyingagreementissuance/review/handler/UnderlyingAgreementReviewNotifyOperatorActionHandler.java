package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewOutcome;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationOutcome;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service.UnderlyingAgreementReviewService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.validation.UnderlyingAgreementReviewNotifyOperatorValidator;
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
public class UnderlyingAgreementReviewNotifyOperatorActionHandler
        implements RequestTaskActionHandler<UnderlyingAgreementNotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final UnderlyingAgreementReviewNotifyOperatorValidator validator;
    private final UnderlyingAgreementReviewService underlyingAgreementReviewService;
    private final WorkflowService workflowService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
                                      UnderlyingAgreementNotifyOperatorForDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload = (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();

        // Update taskPayload with proposed UNA from action payload
        underlyingAgreementReviewService.saveProposedUnderlyingAgreement(payload, requestTask);

        // Validate
        validator.validate(requestTask, payload, appUser);

        // Update Request
        final CcaDecisionNotification decisionNotification = payload.getDecisionNotification();
        final DeterminationType determinationType = taskPayload.getDetermination().getType();
        underlyingAgreementReviewService.notifyOperator(requestTask, decisionNotification, appUser);

        // Complete task
        workflowService.completeTask(
                requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.REVIEW_DETERMINATION, DeterminationOutcome.fromDeterminationType(determinationType, true),
                        BpmnProcessConstants.REVIEW_OUTCOME, CcaReviewOutcome.NOTIFY_OPERATOR)
        );

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
