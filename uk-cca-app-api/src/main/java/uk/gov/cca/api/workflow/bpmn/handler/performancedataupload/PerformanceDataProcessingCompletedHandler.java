package uk.gov.cca.api.workflow.bpmn.handler.performancedataupload;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service.PerformanceDataProcessingService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerformanceDataProcessingCompletedHandler implements JavaDelegate {

    private final PerformanceDataProcessingService performanceDataProcessingService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);

        // Set account reports
        final Map<Long, TargetUnitAccountUploadReport> accountReports = performanceDataProcessingService.getAccountReports(requestId);
        execution.setVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_UPLOAD_ACCOUNT_REPORTS, accountReports);
    }
}
