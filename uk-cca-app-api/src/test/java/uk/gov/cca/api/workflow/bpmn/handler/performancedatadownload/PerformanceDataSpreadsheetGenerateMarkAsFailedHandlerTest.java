package uk.gov.cca.api.workflow.bpmn.handler.performancedatadownload;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataSpreadsheetGenerateMarkAsFailedHandlerTest {

    @InjectMocks
    private PerformanceDataSpreadsheetGenerateMarkAsFailedHandler handler;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws Exception {
        TargetUnitAccountReport accountReport = TargetUnitAccountReport.builder().build();

        when(execution.getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT))
                .thenReturn(accountReport);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(accountReport.isSucceeded()).isFalse();
        verify(execution, times(1))
                .getVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_ACCOUNT_REPORT);
        verify(execution, times(1))
                .setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }
}
