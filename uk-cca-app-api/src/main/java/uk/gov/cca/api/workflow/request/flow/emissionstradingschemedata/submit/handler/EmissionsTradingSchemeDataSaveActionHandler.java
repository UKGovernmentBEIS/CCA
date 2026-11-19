package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.domain.EmissionsTradingSchemeDataSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.submit.service.EmissionsTradingSchemeDataSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmissionsTradingSchemeDataSaveActionHandler implements
        RequestTaskActionHandler<EmissionsTradingSchemeDataSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final EmissionsTradingSchemeDataSubmitService emissionsTradingSchemeDataSubmitService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, EmissionsTradingSchemeDataSaveRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        emissionsTradingSchemeDataSubmitService.applySave(payload, requestTask);

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.EMISSIONS_TRADING_SCHEME_DATA_SAVE_APPLICATION);
    }
}
