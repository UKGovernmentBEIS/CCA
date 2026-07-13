package uk.gov.cca.api.workflow.bpmn.flowable.handler.performanceaccounttemplatefacility;

import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.service.FacilityPerformanceAccountTemplateProcessingService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class FacilityPerformanceAccountTemplateProcessingMarkAsCompletedHandlerFlowable implements JavaDelegate {

    private final FacilityPerformanceAccountTemplateProcessingService facilityPerformanceAccountTemplateProcessingService;

    @Override
    public void execute(DelegateExecution execution) {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        FacilityPerformanceAccountTemplateUploadReport facilityUploadReport = (FacilityPerformanceAccountTemplateUploadReport) execution
                .getVariable(CcaBpmnProcessConstants.FACILITY_REPORT);

        facilityPerformanceAccountTemplateProcessingService.markAsCompleted(requestId, facilityUploadReport);

        // Close request if facility process failed
        if (!facilityUploadReport.getErrors().isEmpty()) {
            execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
        }
    }
}
