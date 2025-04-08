package uk.gov.cca.api.workflow.bpmn.handler.performancedataupload;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service.PerformanceDataUploadCompletedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerformanceDataUploadCompletedHandler implements JavaDelegate {

    private final PerformanceDataUploadCompletedService performanceDataUploadCompletedService;

    @Override
    @SuppressWarnings("unchecked")
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);

        final Map<Long, TargetUnitAccountUploadReport> accountReports = (Map<Long, TargetUnitAccountUploadReport>) execution
                .getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_UPLOAD_ACCOUNT_REPORTS);
        final String errorMessage = (String) execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ERROR_MESSAGE);

        performanceDataUploadCompletedService.completed(requestId, accountReports, errorMessage);
    }
}
