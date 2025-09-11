package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.service.UnderlyingAgreementVariationReviewService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionActionHandler
        implements RequestTaskActionHandler<UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final UnderlyingAgreementVariationReviewService underlyingAgreementReviewService;

    @Override
    public RequestTaskPayload process(final Long requestTaskId,
                        final String requestTaskActionType,
                        final AppUser user,
                        final UnderlyingAgreementVariationSaveFacilityReviewGroupDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        underlyingAgreementReviewService.saveFacilityReviewGroupDecision(payload, requestTask);
        
        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_SAVE_FACILITY_REVIEW_GROUP_DECISION);
    }
}
