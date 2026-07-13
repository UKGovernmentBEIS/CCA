package uk.gov.cca.api.workflow.bpmn.flowable.handler.performancedatafacility;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service.PerformanceDataFacilityDataProcessingService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDataProcessingSubmittedHandlerFlowable implements JavaDelegate {

    private final PerformanceDataFacilityDataProcessingService performanceDataFacilityDataProcessingService;

    @Override
    @SuppressWarnings("unchecked")
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Map<Long, FacilityUploadReport> facilityReports = (Map<Long, FacilityUploadReport>) execution
                .getVariable(CcaBpmnProcessConstants.FACILITY_REPORTS);

        performanceDataFacilityDataProcessingService.setAdditionalRequestPayloadData(requestId, facilityReports);
    }
}
