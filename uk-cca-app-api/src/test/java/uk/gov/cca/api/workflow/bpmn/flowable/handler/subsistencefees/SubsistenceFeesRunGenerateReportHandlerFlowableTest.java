package uk.gov.cca.api.workflow.bpmn.flowable.handler.subsistencefees;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service.SubsistenceFeesRunGenerateReportService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesRunGenerateReportHandlerFlowableTest {

	@InjectMocks
    private SubsistenceFeesRunGenerateReportHandlerFlowable handler;
	
	@Mock
	private SubsistenceFeesRunGenerateReportService subsistenceFeesRunGenerateReportService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute(){
    	final String requestId = "request-id";
    	when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
    	
    	handler.execute(execution);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(subsistenceFeesRunGenerateReportService, times(1)).generateReport(requestId);
    }
}
