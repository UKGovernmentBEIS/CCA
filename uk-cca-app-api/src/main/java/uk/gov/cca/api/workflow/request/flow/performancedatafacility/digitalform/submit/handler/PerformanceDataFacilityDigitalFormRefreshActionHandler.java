package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.service.PerformanceDataFacilityDigitalFormSubmitService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.validation.PerformanceDataFacilityDigitalFormRefreshValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PerformanceDataFacilityDigitalFormRefreshActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final PerformanceDataFacilityDigitalFormSubmitService performanceDataFacilityDigitalFormSubmitService;
    private final PerformanceDataFacilityDigitalFormRefreshValidator performanceDataFacilityDigitalFormRefreshValidator;

    @Override
    public RequestTaskPayload process(Long requestTaskId, String requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate
        performanceDataFacilityDigitalFormRefreshValidator.validate(requestTask);

        // Update reference data
        performanceDataFacilityDigitalFormSubmitService.refreshBaselineData(requestTask);

        return requestTask.getPayload();
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_REFRESH_APPLICATION);
    }
}
