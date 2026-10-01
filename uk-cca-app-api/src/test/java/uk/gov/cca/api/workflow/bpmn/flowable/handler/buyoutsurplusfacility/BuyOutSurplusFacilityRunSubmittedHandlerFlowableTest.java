package uk.gov.cca.api.workflow.bpmn.flowable.handler.buyoutsurplusfacility;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.service.BuyOutSurplusFacilityRunService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityRunSubmittedHandlerFlowableTest {

	@InjectMocks
    private BuyOutSurplusFacilityRunSubmittedHandlerFlowable handler;

    @Mock
    private BuyOutSurplusFacilityRunService buyOutSurplusFacilityRunService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final String requestId = "request-id";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(buyOutSurplusFacilityRunService, times(1)).submit(requestId);
    }
}
