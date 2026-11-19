package uk.gov.cca.api.workflow.bpmn.flowable.handler.buyoutsurplusfacility;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.service.BuyOutSurplusFacilityRunService;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityRunPopulateNumberOfAccountsCompletedHandlerFlowableTest {

    @InjectMocks
    private BuyOutSurplusFacilityRunPopulateNumberOfAccountsCompletedHandlerFlowable handler;

    @Mock
    private BuyOutSurplusFacilityRunService buyOutSurplusFacilityRunService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        String requestId = "requestId";

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(buyOutSurplusFacilityRunService.getNumberOfAccountsCompleted(requestId)).thenReturn(10L);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(buyOutSurplusFacilityRunService, times(1)).getNumberOfAccountsCompleted(requestId);
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 10L);
    }
}
