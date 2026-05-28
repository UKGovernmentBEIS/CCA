package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.service.PerformanceDataFacilityDigitalFormSubmitService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PerformanceDataFacilityDigitalFormSaveActionHandler implements
        RequestTaskActionHandler<PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PerformanceDataFacilityDigitalFormSubmitService performanceDataFacilityDigitalFormSubmitService;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser,
                                      PerformanceDataFacilityDigitalFormSaveRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        performanceDataFacilityDigitalFormSubmitService.applySave(payload, requestTask);

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SAVE_APPLICATION);
    }
}
