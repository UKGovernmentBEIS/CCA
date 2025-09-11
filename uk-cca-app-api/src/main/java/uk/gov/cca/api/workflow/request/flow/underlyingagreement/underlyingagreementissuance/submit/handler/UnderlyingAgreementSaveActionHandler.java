package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.domain.UnderlyingAgreementSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.service.UnderlyingAgreementApplyService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementSaveActionHandler
        implements RequestTaskActionHandler<UnderlyingAgreementSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final UnderlyingAgreementApplyService underlyingAgreementApplyService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
                        UnderlyingAgreementSaveRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        underlyingAgreementApplyService.applySaveAction(payload, requestTask);
        
        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.UNDERLYING_AGREEMENT_SAVE_APPLICATION);
    }
}
