package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.service.UnderlyingAgreementReviewService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementSaveReviewGroupDecisionActionHandler 
		implements RequestTaskActionHandler<UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
    private final UnderlyingAgreementReviewService underlyingAgreementReviewService;

    @Override
    public void process(final Long requestTaskId,
                        final String requestTaskActionType,
                        final AppUser user,
                        final UnderlyingAgreementSaveReviewGroupDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        
        underlyingAgreementReviewService.saveReviewGroupDecision(payload, requestTask);
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_SAVE_REVIEW_GROUP_DECISION);
    }
}
