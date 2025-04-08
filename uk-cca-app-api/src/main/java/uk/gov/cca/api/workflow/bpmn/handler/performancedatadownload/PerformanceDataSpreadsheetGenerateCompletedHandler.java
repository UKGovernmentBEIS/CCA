package uk.gov.cca.api.workflow.bpmn.handler.performancedatadownload;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service.PerformanceDataSpreadsheetGenerateCompletedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class PerformanceDataSpreadsheetGenerateCompletedHandler implements JavaDelegate {

	private final PerformanceDataSpreadsheetGenerateCompletedService performanceDataSpreadsheetGenerateCompletedService;
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
		final TargetUnitAccountReport accountReport = (TargetUnitAccountReport) execution
				.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT);

		// Save account report in request
		performanceDataSpreadsheetGenerateCompletedService.completed(requestId, accountId, accountReport);
		
		// Increment completed number var
		final Integer numberOfAccountsCompleted = (Integer) execution
				.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_GENERATE_NUMBER_OF_ACCOUNTS_COMPLETED);
		execution.setVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_GENERATE_NUMBER_OF_ACCOUNTS_COMPLETED, numberOfAccountsCompleted + 1);
	}
}