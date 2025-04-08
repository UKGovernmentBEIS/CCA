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
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service.PerformanceAccountTemplateDataProcessingQueryService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataProcessingCompletedHandlerTest {

	@InjectMocks
	private PerformanceAccountTemplateDataProcessingCompletedHandler cut;

	@Mock
	private PerformanceAccountTemplateDataProcessingQueryService performanceAccountTemplateDataProcessingQueryService;

	@Mock
	private DelegateExecution execution;

	@Test
	void execute() throws Exception {
		String requestId = "reqId";
		Map<Long, AccountUploadReport> accountReports = Map.of(1L,
				AccountUploadReport.builder().accountId(1L).build());

		when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
		when(performanceAccountTemplateDataProcessingQueryService.getAccountReports(requestId)).thenReturn(accountReports);

		cut.execute(execution);

		verify(execution, times(1)).setVariable(
				CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ACCOUNT_REPORTS, accountReports);
	}

}
