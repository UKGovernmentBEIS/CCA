package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.service.UnderlyingAgreementVariationActivationService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationActivationSaveActionHandler implements RequestTaskActionHandler<UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final UnderlyingAgreementVariationActivationService underlyingAgreementVariationActivationService;
    
    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
                        UnderlyingAgreementVariationActivationSaveRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        underlyingAgreementVariationActivationService.applySaveAction(payload, requestTask);
        
        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATION_SAVE_APPLICATION);
    }
}
