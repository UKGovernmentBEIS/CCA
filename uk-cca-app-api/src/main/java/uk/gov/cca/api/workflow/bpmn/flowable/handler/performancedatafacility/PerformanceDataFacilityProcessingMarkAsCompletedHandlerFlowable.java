package uk.gov.cca.api.workflow.bpmn.flowable.handler.performancedatafacility;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service.PerformanceDataFacilityProcessingService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityProcessingMarkAsCompletedHandlerFlowable implements JavaDelegate {

    private final PerformanceDataFacilityProcessingService performanceDataFacilityProcessingService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        FacilityUploadReport facilityUploadReport = (FacilityUploadReport) execution
                .getVariable(CcaBpmnProcessConstants.FACILITY_REPORT);

        performanceDataFacilityProcessingService.markAsCompleted(requestId, facilityUploadReport);

        // Close request if facility process failed
        if(!facilityUploadReport.getErrors().isEmpty()) {
            execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
        }
    }
}
