package uk.gov.cca.api.workflow.bpmn.handler.performancedataupload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service.PerformanceDataSpreadsheetProcessingService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class PerformanceDataSpreadsheetProcessingMarkAsCompletedHandler implements JavaDelegate {

    private final PerformanceDataSpreadsheetProcessingService performanceDataSpreadsheetProcessingService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final TargetUnitAccountUploadReport accountReport = (TargetUnitAccountUploadReport) execution
                .getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT);

        if(accountReport.getErrors().isEmpty()) {
            accountReport.setSucceeded(true);
        }
        else{
            // Set succeeded to false and close request
            accountReport.setSucceeded(false);
            performanceDataSpreadsheetProcessingService.cleanupFailed(accountReport);
            execution.setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
        }
    }
}
