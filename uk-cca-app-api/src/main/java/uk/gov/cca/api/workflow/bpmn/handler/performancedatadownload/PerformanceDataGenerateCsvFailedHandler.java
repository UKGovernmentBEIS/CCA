package uk.gov.cca.api.workflow.bpmn.handler.performancedatadownload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.validation.PerformanceDataDownloadViolation;

@Service
public class PerformanceDataGenerateCsvFailedHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        execution.setVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ERROR_MESSAGE,
                PerformanceDataDownloadViolation.PerformanceDataDownloadViolationMessage.GENERATE_CSV_FAILED.name());

        // Set workflow outputs to null
        execution.setVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_DOWNLOAD_ERRORS_FILE, null);
    }
}
