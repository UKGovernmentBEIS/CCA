package uk.gov.cca.api.workflow.bpmn.handler.performanceaccounttemplatedataupload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service.PerformanceAccountTemplateProcessingException;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service.PerformanceAccountTemplateProcessingService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateProcessingHandlerTest {

	@InjectMocks
	private PerformanceAccountTemplateProcessingHandler cut;

	@Mock
	private PerformanceAccountTemplateProcessingService service;
	
	@Mock
	private DelegateExecution execution;

	@Test
	void execute() throws Exception {
		String requestId = "ReqId";
		AccountUploadReport accountUploadReport = AccountUploadReport.builder().accountId(1L).build();
		
		when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
		when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_ACCOUNT_REPORT)).thenReturn(accountUploadReport);
	
		cut.execute(execution);
		
		verify(service, times(1)).doProcess(requestId, accountUploadReport);
	}
	
	@Test
	void execute_throw_error() throws Exception {
		String requestId = "ReqId";
		AccountUploadReport accountUploadReport = AccountUploadReport.builder().accountId(1L).build();
		
		when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
		when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_ACCOUNT_TEMPLATE_ACCOUNT_REPORT)).thenReturn(accountUploadReport);
	
		doThrow(new PerformanceAccountTemplateProcessingException((List.of("error1")))).when(service)
        	.doProcess(requestId, accountUploadReport);
		
		assertThrows(BpmnError.class,
                () -> cut.execute(execution));
		
		verify(service, times(1)).doProcess(requestId, accountUploadReport);
		assertThat(accountUploadReport.getErrors()).containsExactly("error1");
	}
	
}
