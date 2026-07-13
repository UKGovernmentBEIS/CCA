package uk.gov.cca.api.workflow.bpmn.flowable.handler.performancedatafacility;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service.PerformanceDataFacilityProcessingCreateRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDataProcessingTriggerHandlerFlowable implements JavaDelegate {

    private final PerformanceDataFacilityProcessingCreateRequestService performanceDataFacilityDataProcessingCreateRequestService;

    @Override
    @SuppressWarnings("unchecked")
    public void execute(DelegateExecution execution) {
        final Long facilityId = (Long) execution.getVariable(CcaBpmnProcessConstants.FACILITY_ID);
        final Map<Long, FacilityUploadReport> facilityReports = (Map<Long, FacilityUploadReport>) execution
                .getVariable(CcaBpmnProcessConstants.FACILITY_REPORTS);
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);

        performanceDataFacilityDataProcessingCreateRequestService.createRequest(facilityReports.get(facilityId), requestId);
    }
}
