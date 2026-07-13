package uk.gov.cca.api.workflow.bpmn.flowable.handler.performancedatafacility;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDataProcessingCompletedHandlerFlowableTest {

    @InjectMocks
    private PerformanceDataFacilityDataProcessingCompletedHandlerFlowable handler;

    @Mock
    private RequestService requestService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() {
        final String requestId = "requestId";
        final Request request = Request.builder()
                .payload(PerformanceDataFacilityDataProcessingRequestPayload.builder()
                        .facilityReports(Map.of(1L, FacilityUploadReport.builder().facilityId(1L).build()))
                        .build())
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        handler.execute(execution);

        // Verify
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(execution, times(1)).setVariable(eq(CcaBpmnProcessConstants.FACILITY_REPORTS), anyString());
        verify(execution, times(1)).setVariable(CcaBpmnProcessConstants.PERFORMANCE_DATA_FACILITY_DATA_PROCESSING_MESSAGE_FAILED, false);
        verify(execution, times(1)).setVariable(BpmnProcessConstants.REQUEST_DELETE_UPON_TERMINATE, true);
    }
}
