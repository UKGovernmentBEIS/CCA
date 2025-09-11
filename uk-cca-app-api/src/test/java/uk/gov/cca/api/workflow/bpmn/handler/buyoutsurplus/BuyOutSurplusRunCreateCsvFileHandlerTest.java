package uk.gov.cca.api.workflow.bpmn.handler.buyoutsurplus;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.service.BuyOutSurplusRunService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusRunCreateCsvFileHandlerTest {

    @InjectMocks
    private BuyOutSurplusRunCreateCsvFileHandler handler;

    @Mock
    private BuyOutSurplusRunService buyOutSurplusRunService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "requestId";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(buyOutSurplusRunService, times(1)).createCsvFile(requestId);
    }
}
