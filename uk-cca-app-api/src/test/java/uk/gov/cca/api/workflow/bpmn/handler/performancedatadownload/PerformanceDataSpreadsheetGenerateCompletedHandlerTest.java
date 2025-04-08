package uk.gov.cca.api.workflow.bpmn.handler.performancedatadownload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service.PerformanceDataSpreadsheetGenerateCompletedService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataSpreadsheetGenerateCompletedHandlerTest {

    @InjectMocks
    private PerformanceDataSpreadsheetGenerateCompletedHandler handler;

    @Mock
    private PerformanceDataSpreadsheetGenerateCompletedService performanceDataSpreadsheetGenerateCompletedService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        final String requestId = "request-id";
        final long accountId = 1L;
        final TargetUnitAccountReport accountReport = TargetUnitAccountReport.builder().accountId(accountId).build();
        final int numberOfAccountsCompleted = 1;

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(BpmnProcessConstants.ACCOUNT_ID)).thenReturn(accountId);
        when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT))
                .thenReturn(accountReport);
        when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_GENERATE_NUMBER_OF_ACCOUNTS_COMPLETED))
                .thenReturn(numberOfAccountsCompleted);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.ACCOUNT_ID);
        verify(execution, times(1))
                .getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT);
        verify(execution, times(1))
                .getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_GENERATE_NUMBER_OF_ACCOUNTS_COMPLETED);
        verify(performanceDataSpreadsheetGenerateCompletedService, times(1))
                .completed(requestId, accountId, accountReport);
        verify(execution, times(1))
                .setVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_GENERATE_NUMBER_OF_ACCOUNTS_COMPLETED, 2);
    }
}
