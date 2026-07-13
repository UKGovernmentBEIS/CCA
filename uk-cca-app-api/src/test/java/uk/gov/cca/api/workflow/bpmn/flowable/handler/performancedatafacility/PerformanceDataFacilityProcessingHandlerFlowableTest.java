package uk.gov.cca.api.workflow.bpmn.flowable.handler.performancedatafacility;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.domain.PerformanceDataFacilityProcessingResults;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service.PerformanceDataFacilityProcessingService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityProcessingHandlerFlowableTest {

    @InjectMocks
    private PerformanceDataFacilityProcessingHandlerFlowable handler;

    @Mock
    private RequestService requestService;

    @Mock
    private PerformanceDataFacilityProcessingService performanceDataFacilityProcessingService;

    @Mock
    private DelegateExecution execution;

    @Test
    void execute() throws BpmnExecutionException {
        final String requestId = "requestId";
        final String assignee = "sectorUserAssignee";
        final FacilityUploadReport facilityReport = FacilityUploadReport.builder().facilityId(1L).build();
        final PerformanceDataFacilityProcessingRequestPayload requestPayload = PerformanceDataFacilityProcessingRequestPayload.builder()
                .sectorUserAssignee(assignee)
                .targetPeriodType(TargetPeriodType.TP7)
                .build();
        final PerformanceDataFacilityProcessingRequestMetadata metadata = PerformanceDataFacilityProcessingRequestMetadata.builder().build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .metadata(metadata)
                .build();
        final PerformanceDataFacilityProcessingResults results = PerformanceDataFacilityProcessingResults.builder()
                .reportVersion(1)
                .build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_REPORT)).thenReturn(facilityReport);
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(performanceDataFacilityProcessingService.doProcess(requestPayload, facilityReport)).thenReturn(results);

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(facilityReport.getErrors()).isEmpty();
        assertThat(metadata.getReportVersion()).isEqualTo(1);
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_REPORT);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(performanceDataFacilityProcessingService, times(1)).doProcess(requestPayload, facilityReport);
        verify(requestService, times(1))
                .addActionToRequest(eq(request), any(), eq(CcaRequestActionType.PERFORMANCE_DATA_FACILITY_PROCESSING_SUBMITTED), eq(assignee));
    }

    @Test
    void execute_throw_exception() throws BpmnExecutionException {
        final String requestId = "requestId";
        final FacilityUploadReport facilityReport = FacilityUploadReport.builder().facilityId(1L).build();
        final PerformanceDataFacilityProcessingRequestPayload requestPayload = PerformanceDataFacilityProcessingRequestPayload.builder()
                .targetPeriodType(TargetPeriodType.TP7)
                .build();
        final Request request = Request.builder().payload(requestPayload).build();

        when(execution.getVariable(BpmnProcessConstants.REQUEST_ID)).thenReturn(requestId);
        when(execution.getVariable(CcaBpmnProcessConstants.FACILITY_REPORT)).thenReturn(facilityReport);
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(performanceDataFacilityProcessingService.doProcess(requestPayload, facilityReport))
                .thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        // Invoke
        handler.execute(execution);

        // Verify
        assertThat(facilityReport.getErrors()).isNotEmpty().containsExactly("Facility process failed");
        verify(execution, times(1)).getVariable(BpmnProcessConstants.REQUEST_ID);
        verify(execution, times(1)).getVariable(CcaBpmnProcessConstants.FACILITY_REPORT);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(performanceDataFacilityProcessingService, times(1)).doProcess(requestPayload, facilityReport);
    }
}
