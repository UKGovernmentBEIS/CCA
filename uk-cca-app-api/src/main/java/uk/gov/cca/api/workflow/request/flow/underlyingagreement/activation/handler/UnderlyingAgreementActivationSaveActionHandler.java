package uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.handler;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.domain.UnderlyingAgreementActivationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.activation.service.UnderlyingAgreementActivationService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementActivationSaveActionHandler implements RequestTaskActionHandler<UnderlyingAgreementActivationSaveRequestTaskActionPayload> {

	private final RequestTaskService requestTaskService;

    private final UnderlyingAgreementActivationService underlyingAgreementActivationService;


    @Override
    public RequestTaskPayload process(Long requestTaskId,
                        String requestTaskActionType,
                        AppUser appUser,
                        UnderlyingAgreementActivationSaveRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        underlyingAgreementActivationService.applySaveAction(payload, requestTask);
        
        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_ACTIVATION_SAVE_APPLICATION);
    }
}
