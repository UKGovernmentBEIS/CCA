package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.service.UnderlyingAgreementVariationApplyService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationSaveActionHandler
        implements RequestTaskActionHandler<UnderlyingAgreementVariationSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final UnderlyingAgreementVariationApplyService underlyingAgreementVariationApplyService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
                        UnderlyingAgreementVariationSaveRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        underlyingAgreementVariationApplyService.applySaveAction(payload, requestTask);
        
        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_SAVE_APPLICATION);
    }
}
