package uk.gov.cca.api.workflow.bpmn.handler.subsistencefees;

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

import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.service.TargetUnitMoaGenerateService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class TargetUnitMoaGenerateHandlerTest {

	@InjectMocks
    private TargetUnitMoaGenerateHandler handler;

    @Mock
    private TargetUnitMoaGenerateService service;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        handler.execute(execution);
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(service, times(1)).generateMoa(requestId);
    }
    
    @Test
    void execute_error_occurred() throws Exception {
        String requestId = "1";
        List<String> errors = List.of();
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        doThrow(new BpmnExecutionException(errors)).when(service).generateMoa(requestId);

        assertThrows(BpmnError.class, () -> handler.execute(execution));
        
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.TARGET_UNIT_MOA_REQUEST_ERRORS, errors);
        verify(service, times(1)).generateMoa(requestId);
    }
}
