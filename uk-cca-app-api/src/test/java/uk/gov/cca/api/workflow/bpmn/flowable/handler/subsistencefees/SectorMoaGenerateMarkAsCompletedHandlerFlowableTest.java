package uk.gov.cca.api.workflow.bpmn.flowable.handler.subsistencefees;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class SectorMoaGenerateMarkAsCompletedHandlerFlowableTest {

	@InjectMocks
    private SectorMoaGenerateMarkAsCompletedHandlerFlowable handler;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
    	handler.execute(execution);
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_SUCCEEDED, true);
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.SECTOR_MOA_REQUEST_ERRORS, List.of());
    }
}
