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

import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.service.TargetUnitMoaCreateRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesRunTriggerTargetUnitMoasHandlerFlowableTest {

	@InjectMocks
    private SubsistenceFeesRunTriggerTargetUnitMoasHandlerFlowable handler;
	
	@Mock
	private TargetUnitMoaCreateRequestService targetUnitMoaCreateRequestService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
    	final String requestId = "request-id";
    	final Long accountId = 50L;
    	final String requestBusinessKey = "requestBusinessKey";
    	
    	when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
    	when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);
    	when(execution.getVariable(BpmnProcessConstants.BUSINESS_KEY)).thenReturn(requestBusinessKey);
    	
    	handler.execute(execution);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.ACCOUNT_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.BUSINESS_KEY);
        verify(targetUnitMoaCreateRequestService, times(1)).createRequest(accountId, requestId, requestBusinessKey);
    }
}
