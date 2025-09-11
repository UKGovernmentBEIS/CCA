package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service.UnderlyingAgreementReviewService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementSaveReviewDeterminationActionHandler
        implements RequestTaskActionHandler<UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final UnderlyingAgreementReviewService underlyingAgreementReviewService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser user,
                        UnderlyingAgreementSaveReviewDeterminationRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        underlyingAgreementReviewService.saveDetermination(payload, requestTask);
        
        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_SAVE_REVIEW_DETERMINATION);
    }
}
