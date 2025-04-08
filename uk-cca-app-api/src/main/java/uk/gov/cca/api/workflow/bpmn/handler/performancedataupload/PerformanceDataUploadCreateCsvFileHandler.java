package uk.gov.cca.api.workflow.bpmn.handler.performancedataupload;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service.PerformanceDataUploadService;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PerformanceDataUploadCreateCsvFileHandler implements JavaDelegate {

    private final RequestTaskService requestTaskService;
    private final PerformanceDataUploadService performanceDataUploadService;

    @Override
    @SuppressWarnings("unchecked")
    public void execute(DelegateExecution execution) throws Exception {
        final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
        final Map<Long, TargetUnitAccountUploadReport> accountReports = (Map<Long, TargetUnitAccountUploadReport>) execution
                .getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_UPLOAD_ACCOUNT_REPORTS);
        final RequestTask requestTask = requestTaskService
                .findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_DATA_UPLOAD_SUBMIT, requestId);

        performanceDataUploadService.createCsvFile(requestTask, accountReports);
    }
}
