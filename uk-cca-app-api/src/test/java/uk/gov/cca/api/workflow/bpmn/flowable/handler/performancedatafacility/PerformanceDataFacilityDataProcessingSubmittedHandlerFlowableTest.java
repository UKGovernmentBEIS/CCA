package uk.gov.cca.api.workflow.bpmn.flowable.handler.performancedatafacility;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service.PerformanceDataFacilityDataProcessingService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDataProcessingSubmittedHandlerFlowableTest {

    @InjectMocks
    private PerformanceDataFacilityDataProcessingSubmittedHandlerFlowable handler;

    @Mock
    private PerformanceDataFacilityDataProcessingService performanceDataFacilityDataProcessingService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final String requestId = "requestId";
        final Map<Long, FacilityUploadReport> facilityReports = Map.of(1L, FacilityUploadReport.builder().build());

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_REPORTS)).thenReturn(facilityReports);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_REPORTS);
        verify(performanceDataFacilityDataProcessingService, times(1))
                .setAdditionalRequestPayloadData(requestId, facilityReports);
    }
}
