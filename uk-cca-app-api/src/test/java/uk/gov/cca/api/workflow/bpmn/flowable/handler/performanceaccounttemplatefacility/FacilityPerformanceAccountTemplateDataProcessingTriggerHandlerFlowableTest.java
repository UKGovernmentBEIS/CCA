package uk.gov.cca.api.workflow.bpmn.flowable.handler.performanceaccounttemplatefacility;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.service.FacilityPerformanceAccountTemplateProcessingCreateRequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataProcessingTriggerHandlerFlowableTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataProcessingTriggerHandlerFlowable handler;

    @Mock
    private FacilityPerformanceAccountTemplateProcessingCreateRequestService facilityPerformanceAccountTemplateProcessingCreateRequestService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final Long facilityId = 1L;
        final FacilityPerformanceAccountTemplateUploadReport facilityUploadReport = FacilityPerformanceAccountTemplateUploadReport.builder().facilityId(1L).build();
        final Map<Long, FacilityPerformanceAccountTemplateUploadReport> facilityReports = Map.of(1L, facilityUploadReport);
        final String requestId = "requestId";

        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_ID)).thenReturn(facilityId);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_REPORTS)).thenReturn(facilityReports);
        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_REPORTS);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(facilityPerformanceAccountTemplateProcessingCreateRequestService, times(1))
                .createRequest(facilityUploadReport, requestId);
    }
}
