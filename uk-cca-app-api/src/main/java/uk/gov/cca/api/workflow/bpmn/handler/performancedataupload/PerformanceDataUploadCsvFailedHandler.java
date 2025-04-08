package uk.gov.cca.api.workflow.bpmn.handler.performancedataupload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;

@Service
public class PerformanceDataUploadCsvFailedHandler implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        execution.setVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ERROR_MESSAGE,
                PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.GENERATE_CSV_FAILED.name());
    }
}
