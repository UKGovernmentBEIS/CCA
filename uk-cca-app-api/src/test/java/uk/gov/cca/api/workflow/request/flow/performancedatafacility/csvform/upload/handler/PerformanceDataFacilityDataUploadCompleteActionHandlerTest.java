package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUpload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUploadResults;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDataUploadCompleteActionHandlerTest {

	@InjectMocks
    private PerformanceDataFacilityDataUploadCompleteActionHandler handler;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private RequestService requestService;

    @Test
    void process_completed() {
        final Long requestTaskId = 1L;
        final String requestId = "requestId";
        final String requestTaskActionType = CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_COMPLETE;
        final RequestTaskActionEmptyPayload taskActionEmptyPayload = RequestTaskActionEmptyPayload.builder().build();
        final AppUser appUser = AppUser.builder().build();

        final String processTaskId = "processTaskId";
        final LocalDateTime submittedDate = LocalDateTime.now();
        final UUID uuid = UUID.randomUUID();
        
        PerformanceDataFacilityUpload upload = PerformanceDataFacilityUpload.builder()
        		.reportType(PerformanceDataReportType.FINAL)
        		.targetPeriodType(TargetPeriodType.TP7)        		
        		.build();
        PerformanceDataFacilityUploadResults results = PerformanceDataFacilityUploadResults.builder()
				.totalFilesUploaded(1)
				.facilitiesFailed(0)
				.facilitiesSucceeded(1)
				.uploadSummaryFile(uuid)
				.submittedDate(submittedDate)
				.build();
        
        Request request = Request.builder().id(requestId).build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processTaskId)
                .payload(PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder()
                		.performanceDataUpload(upload)
                		.results(results)
                        .processingStatus(PerformanceDataFacilityDataUploadProcessingStatus.COMPLETED)
                        .uploadAttachments(Map.of(uuid, "fileName.csv"))
                        .build())
                .build();
        
        PerformanceDataFacilityDataUploadCompletedRequestActionPayload actionPayload = 
        		PerformanceDataFacilityDataUploadCompletedRequestActionPayload.builder()
        		.payloadType(CcaRequestActionPayloadType.PERFORMANCE_DATA_FACILITY_UPLOAD_COMPLETED_PAYLOAD)
				.performanceDataUpload(upload)
				.results(results)
				.uploadAttachments(Map.of(uuid, "fileName.csv"))
				.build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionEmptyPayload);

        // Verify
        assertThat(request.getSubmissionDate()).isNotNull();
        assertThat(request.getStatus()).isEqualTo(RequestStatuses.COMPLETED);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestService, times(1)).addActionToRequest(
        		request, actionPayload, CcaRequestActionType.PERFORMANCE_DATA_FACILITY_UPLOAD_COMPLETED, appUser.getUserId());
        verify(workflowService, times(1)).completeTask(processTaskId);
    }

    @Test
    void process_completed_not_valid() {
        final Long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_COMPLETE;
        final RequestTaskActionEmptyPayload actionPayload = RequestTaskActionEmptyPayload.builder().build();
        final AppUser appUser = AppUser.builder().build();

        final String processTaskId = "processTaskId";
        final RequestTask requestTask = RequestTask.builder()
                .processTaskId(processTaskId)
                .payload(PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder()
                		.processingStatus(PerformanceDataFacilityDataUploadProcessingStatus.IN_PROGRESS)
                		.build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                handler.process(requestTaskId, requestTaskActionType, appUser, actionPayload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_PERFORMANCE_DATA_FACILITY_UPLOAD_PROCESS_STATUS);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verifyNoInteractions(requestService);
        verifyNoInteractions(workflowService);
    }
    
    @Test
    void process_closed() {
        final Long requestTaskId = 1L;
        final String requestId = "requestId";
        final String requestTaskActionType = CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_CLOSE;
        final RequestTaskActionEmptyPayload taskActionEmptyPayload = RequestTaskActionEmptyPayload.builder().build();
        final AppUser appUser = AppUser.builder().build();

        final String processTaskId = "processTaskId";
        final LocalDateTime submittedDate = LocalDateTime.now();
        final UUID uuid = UUID.randomUUID();
        
        PerformanceDataFacilityUpload upload = PerformanceDataFacilityUpload.builder()
        		.reportType(PerformanceDataReportType.FINAL)
        		.targetPeriodType(TargetPeriodType.TP7)        		
        		.build();
        PerformanceDataFacilityUploadResults results = PerformanceDataFacilityUploadResults.builder()
				.totalFilesUploaded(1)
				.facilitiesFailed(0)
				.facilitiesSucceeded(1)
				.uploadSummaryFile(uuid)
				.submittedDate(submittedDate)
				.build();
        
        Request request = Request.builder().id(requestId).build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processTaskId)
                .payload(PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder()
                		.performanceDataUpload(upload)
                		.results(results)
                        .processingStatus(PerformanceDataFacilityDataUploadProcessingStatus.NOT_STARTED_YET)
                        .uploadAttachments(Map.of(uuid, "fileName.csv"))
                        .build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionEmptyPayload);

        // Verify
        assertThat(request.getSubmissionDate()).isNull();
        assertThat(request.getStatus()).isEqualTo(CcaRequestStatuses.CLOSED);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestService, times(1)).addActionToRequest(
        		request, null, CcaRequestActionType.PERFORMANCE_DATA_FACILITY_UPLOAD_CLOSED, appUser.getUserId());
        verify(workflowService, times(1)).completeTask(processTaskId);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(
        		CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_COMPLETE,
        		CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_CLOSE);
    }
}
