package uk.gov.cca.api.workflow.bpmn.flowable.handler.performanceaccounttemplatefacility;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.service.FacilityPerformanceAccountTemplateProcessingService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateProcessingMarkAsCompletedHandlerFlowableTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateProcessingMarkAsCompletedHandlerFlowable handler;

    @Mock
    private FacilityPerformanceAccountTemplateProcessingService facilityPerformanceAccountTemplateProcessingService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final String requestId = "requestId";
        final FacilityPerformanceAccountTemplateUploadReport facilityReport = FacilityPerformanceAccountTemplateUploadReport.builder().facilityId(1L).build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_REPORT)).thenReturn(facilityReport);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_REPORT);
        verify(facilityPerformanceAccountTemplateProcessingService, times(1)).markAsCompleted(requestId, facilityReport);
        verifyNoMoreInteractions(execution);
    }

    @Test
    void execute_facility_with_errors() {
        final String requestId = "requestId";
        final FacilityPerformanceAccountTemplateUploadReport facilityReport = FacilityPerformanceAccountTemplateUploadReport.builder()
                .facilityId(1L)
                .errors(List.of("error1", "error2"))
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_REPORT)).thenReturn(facilityReport);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_REPORT);
        verify(facilityPerformanceAccountTemplateProcessingService, times(1)).markAsCompleted(requestId, facilityReport);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }
}
