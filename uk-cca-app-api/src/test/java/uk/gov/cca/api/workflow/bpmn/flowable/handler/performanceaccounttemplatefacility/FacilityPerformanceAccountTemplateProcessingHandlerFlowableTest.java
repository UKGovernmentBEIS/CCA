package uk.gov.cca.api.workflow.bpmn.flowable.handler.performanceaccounttemplatefacility;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.domain.FacilityPerformanceAccountTemplateProcessingResults;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.service.FacilityPerformanceAccountTemplateProcessingService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateProcessingHandlerFlowableTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateProcessingHandlerFlowable handler;

    @Mock
    private RequestService requestService;

    @Mock
    private FacilityPerformanceAccountTemplateProcessingService facilityPerformanceAccountTemplateProcessingService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws BpmnExecutionException {
        //TODO: enhance
        final String requestId = "requestId";
        final String assignee = "sectorUserAssignee";
        final FacilityPerformanceAccountTemplateUploadReport facilityReport = FacilityPerformanceAccountTemplateUploadReport.builder().facilityId(1L).build();
        final FacilityPerformanceAccountTemplateProcessingRequestPayload requestPayload = FacilityPerformanceAccountTemplateProcessingRequestPayload.builder()
                .sectorUserAssignee(assignee)
                .targetYear(Year.of(2026))
                .build();
        final FacilityPerformanceAccountTemplateProcessingRequestMetadata metadata = FacilityPerformanceAccountTemplateProcessingRequestMetadata.builder().build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .metadata(metadata)
                .build();
        final FacilityPerformanceAccountTemplateProcessingResults results = FacilityPerformanceAccountTemplateProcessingResults.builder()
                .reportVersion(1)
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_REPORT)).thenReturn(facilityReport);
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(facilityPerformanceAccountTemplateProcessingService.doProcess(requestPayload, facilityReport)).thenReturn(results);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(facilityReport.getErrors()).isEmpty();
        assertThat(metadata.getReportVersion()).isEqualTo(1);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_REPORT);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(facilityPerformanceAccountTemplateProcessingService, times(1)).doProcess(requestPayload, facilityReport);
    }

    @Test
    void execute_throw_exception() throws BpmnExecutionException {
        final String requestId = "requestId";
        final String assignee = "sectorUserAssignee";
        final FacilityPerformanceAccountTemplateUploadReport facilityReport = FacilityPerformanceAccountTemplateUploadReport.builder().facilityId(1L).build();
        final FacilityPerformanceAccountTemplateProcessingRequestPayload requestPayload = FacilityPerformanceAccountTemplateProcessingRequestPayload.builder()
                .sectorUserAssignee(assignee)
                .targetYear(Year.of(2026))
                .build();
        final Request request = Request.builder().payload(requestPayload).build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_REPORT)).thenReturn(facilityReport);
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(facilityPerformanceAccountTemplateProcessingService.doProcess(requestPayload, facilityReport))
                .thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(facilityReport.getErrors()).isNotEmpty().containsExactly("Facility process failed");
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_REPORT);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(facilityPerformanceAccountTemplateProcessingService, times(1)).doProcess(requestPayload, facilityReport);
    }
}
