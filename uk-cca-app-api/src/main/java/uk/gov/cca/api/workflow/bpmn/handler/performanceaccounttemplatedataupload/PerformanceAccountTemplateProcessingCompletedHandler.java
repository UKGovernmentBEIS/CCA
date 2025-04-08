package uk.gov.cca.api.workflow.bpmn.handler.performanceaccounttemplatedataupload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service.PerformanceAccountTemplateProcessingCompletedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateProcessingCompletedHandler implements JavaDelegate {

	private final PerformanceAccountTemplateProcessingCompletedService service;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		final String requestId = (String) execution.getVariable(BpmnProcessConstants.REQUEST_ID);
		final Long accountId = (Long) execution.getVariable(BpmnProcessConstants.ACCOUNT_ID);
		final AccountUploadReport accountReport = (AccountUploadReport) execution
				.getVariable(CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_ACCOUNT_REPORT);

		service.completed(requestId, accountId, accountReport);

		// Increment completed number var
		final Integer numberOfAccountsCompleted = (Integer) execution.getVariable(
				CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED);
		execution.setVariable(
				CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED,
				numberOfAccountsCompleted + 1);
	}

}
