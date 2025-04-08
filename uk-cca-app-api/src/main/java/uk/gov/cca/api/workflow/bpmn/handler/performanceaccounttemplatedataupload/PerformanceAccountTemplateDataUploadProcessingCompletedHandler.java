package uk.gov.cca.api.workflow.bpmn.handler.performanceaccounttemplatedataupload;

import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service.PerformanceAccountTemplateDataUploadProcessingCompletedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateDataUploadProcessingCompletedHandler implements JavaDelegate {

	private final PerformanceAccountTemplateDataUploadProcessingCompletedService service;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		
		@SuppressWarnings("unchecked")
		final Map<Long, AccountUploadReport> accountReports = (Map<Long, AccountUploadReport>) execution
                .getVariable(CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ACCOUNT_REPORTS);
		
		service.completed(requestId, accountReports);
	}

}
