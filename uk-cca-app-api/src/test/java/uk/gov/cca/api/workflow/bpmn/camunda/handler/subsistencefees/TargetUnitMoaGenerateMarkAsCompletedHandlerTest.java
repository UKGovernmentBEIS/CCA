package uk.gov.cca.api.workflow.bpmn.camunda.handler.subsistencefees;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class TargetUnitMoaGenerateMarkAsCompletedHandlerTest {

	@InjectMocks
    private TargetUnitMoaGenerateMarkAsCompletedHandler handler;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
    	handler.execute(execution);
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.TARGET_UNIT_MOA_REQUEST_SUCCEEDED, true);
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.TARGET_UNIT_MOA_REQUEST_ERRORS, List.of());
    }
}
