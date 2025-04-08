package uk.gov.cca.api.workflow.bpmn.handler.performancedataupload;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.domain.TargetUnitAccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service.PerformanceDataProcessingCompletedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class PerformanceDataSpreadsheetProcessingCompletedHandler implements JavaDelegate {
	
	private final PerformanceDataProcessingCompletedService performanceDataProcessingCompletedService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
		final TargetUnitAccountUploadReport accountReport = (TargetUnitAccountUploadReport) execution
				.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT);

		performanceDataProcessingCompletedService.completed(requestId, accountId, accountReport);
		
		// Increment completed number var
		final Integer numberOfAccountsCompleted = (Integer) execution
				.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_UPLOAD_PROCESSING_NUMBER_OF_ACCOUNTS_COMPLETED);
		execution.setVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_UPLOAD_PROCESSING_NUMBER_OF_ACCOUNTS_COMPLETED, numberOfAccountsCompleted + 1);
	}
}
