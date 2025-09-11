package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service.UnderlyingAgreementReviewService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementSaveFacilityReviewGroupDecisionActionHandler 
		implements RequestTaskActionHandler<UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;
    private final UnderlyingAgreementReviewService underlyingAgreementReviewService;

    @Override
    public RequestTaskPayload process(final Long requestTaskId,
                        final String requestTaskActionType,
                        final AppUser user,
                        final UnderlyingAgreementSaveFacilityReviewGroupDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        
        underlyingAgreementReviewService.saveFacilityReviewGroupDecision(payload, requestTask);
        
        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_SAVE_FACILITY_REVIEW_GROUP_DECISION);
    }
}
