package uk.gov.cca.api.workflow.bpmn.handler.performanceaccounttemplatedataupload;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service.PerformanceAccountTemplateProcessingCompletedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateProcessingCompletedHandlerTest {
	@InjectMocks
	private PerformanceAccountTemplateProcessingCompletedHandler cut;

	@Mock
	private PerformanceAccountTemplateProcessingCompletedService service;

	@Mock
	private DelegateExecution execution;

	@Test
	void execute() throws Exception {
		String requestId = "reqId";
		Long accountId = 1L;
		AccountUploadReport accountReport = AccountUploadReport.builder().accountId(accountId).build();

		when(execution.getVariable(BpmnProcessConstants.REQUEST_ID))
			.thenReturn(requestId);
		when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID))
			.thenReturn(accountId);
		when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_ACCOUNT_REPORT))
				.thenReturn(accountReport);

		Integer numberOfAccountsCompleted = 2;
		when(execution.getVariable(
				CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED))
				.thenReturn(numberOfAccountsCompleted);

		cut.execute(execution);

		verify(service, times(1)).completed(requestId, accountId, accountReport);
		verify(execution, times(1)).setVariable(
				CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, numberOfAccountsCompleted + 1);
	}
}
