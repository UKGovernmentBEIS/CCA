package uk.gov.cca.api.workflow.bpmn.handler.performanceaccounttemplatedataupload;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service.PerformanceAccountTemplateProcessingCreateRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateProcessingTriggerHandlerTest {

	@InjectMocks
	private PerformanceAccountTemplateProcessingTriggerHandler cut;

	@Mock
	private PerformanceAccountTemplateProcessingCreateRequestService service;

	@Mock
	private DelegateExecution execution;

	@Test
	void execute() throws Exception {
		when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(1L);
		when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn("reqId");
		when(execution.getVariable(BpmnProcessConstants.BUSINESS_KEY)).thenReturn("businessKey");

		Map<Long, AccountUploadReport> accountReports = Map.of(1L,
				AccountUploadReport.builder().accountId(1L).build());
		when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ACCOUNT_REPORTS))
				.thenReturn(accountReports);
		
		cut.execute(execution);
		
		verify(service, times(1)).createRequest(accountReports.get(1L), "reqId", "businessKey");
		

	}
}
