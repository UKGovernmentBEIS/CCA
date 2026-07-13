package uk.gov.cca.api.workflow.bpmn.flowable.handler.performancedatafacility;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service.PerformanceDataFacilityProcessingService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityProcessingMarkAsCompletedHandlerFlowableTest {

    @InjectMocks
    private PerformanceDataFacilityProcessingMarkAsCompletedHandlerFlowable handler;

    @Mock
    private PerformanceDataFacilityProcessingService performanceDataFacilityProcessingService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final String requestId = "requestId";
        final FacilityUploadReport facilityReport = FacilityUploadReport.builder().facilityId(1L).build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_REPORT)).thenReturn(facilityReport);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_REPORT);
        verify(performanceDataFacilityProcessingService, times(1)).markAsCompleted(requestId, facilityReport);
        verifyNoMoreInteractions(execution);
    }

    @Test
    void execute_facility_with_errors() {
        final String requestId = "requestId";
        final FacilityUploadReport facilityReport = FacilityUploadReport.builder()
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
        verify(performanceDataFacilityProcessingService, times(1)).markAsCompleted(requestId, facilityReport);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }
}
