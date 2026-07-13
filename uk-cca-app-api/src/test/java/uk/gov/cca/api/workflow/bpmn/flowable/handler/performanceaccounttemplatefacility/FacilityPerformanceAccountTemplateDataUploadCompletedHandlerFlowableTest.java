package uk.gov.cca.api.workflow.bpmn.flowable.handler.performanceaccounttemplatefacility;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service.FacilityPerformanceAccountTemplateDataUploadCompleteService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataUploadCompletedHandlerFlowableTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataUploadCompletedHandlerFlowable handler;

    @Mock
    private FacilityPerformanceAccountTemplateDataUploadCompleteService facilityPerformanceAccountTemplateDataUploadCompleteService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final String requestId = "1";
        Map<String, Map<String, String>> rawMap = Map.of("1", Map.of("facilityId", "1"));
        final Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReports = Map.of(1L, FacilityPerformanceAccountTemplateUploadReport.builder().facilityId(1L).build());

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID))
                .thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_REPORTS))
                .thenReturn(rawMap);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_REPORTS);
        verify(facilityPerformanceAccountTemplateDataUploadCompleteService, times(1))
                .processCompleted(requestId, facilityReports);
    }
}
