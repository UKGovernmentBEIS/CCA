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
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service.PerformanceAccountTemplateProcessingMarkAsFailedService;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateProcessingMarkAsFailedHandlerTest {

	@InjectMocks
	private PerformanceAccountTemplateProcessingMarkAsFailedHandler cut;

	@Mock
	private PerformanceAccountTemplateProcessingMarkAsFailedService service;

	@Mock
	private DelegateExecution execution;

	@Test
	void execute() throws Exception {
		AccountUploadReport accountUploadReport = AccountUploadReport.builder().accountId(1L).build();
		
		when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_ACCOUNT_REPORT))
				.thenReturn(accountUploadReport);
		
		cut.execute(execution);
		
		verify(service, times(1)).markAsFailed(accountUploadReport);
	}
}
