package uk.gov.cca.api.workflow.bpmn.camunda.handler.subsistencefees;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class SectorMoaGenerateMarkAsFailedHandlerTest {

	@InjectMocks
    private SectorMoaGenerateMarkAsFailedHandler handler;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
    	handler.execute(execution);
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_SUCCEEDED, false);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }
}
