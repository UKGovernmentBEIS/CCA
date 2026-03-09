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

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service.SubsistenceFeesRunRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesRunPopulateNumberOfSectorsCompletedHandlerFlowableTest {

	@InjectMocks
    private SubsistenceFeesRunPopulateNumberOfSectorsCompletedHandlerFlowable handler;

    @Mock
    private SubsistenceFeesRunRequestService subsistenceFeesRunRequestService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        String requestId = "1";
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(subsistenceFeesRunRequestService.getNumberOfSectorsCompleted(requestId)).thenReturn(10L);

        handler.execute(execution);
        
        verify(subsistenceFeesRunRequestService, times(1)).getNumberOfSectorsCompleted(requestId);
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.NUMBER_OF_SECTORS_COMPLETED, 10L);
    }
}
