package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataUploadCompleteActionHandlerTest {

	@InjectMocks
    private PerformanceAccountTemplateDataUploadCompleteActionHandler cut;
	
	@Mock
    private WorkflowService workflowService;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Test
    void process() {
        final Long requestTaskId = 1L;
        final String requestTaskActionType = "requestTaskActionType";
        final RequestTaskActionEmptyPayload actionPayload = RequestTaskActionEmptyPayload.builder().build();
        final AppUser appUser = AppUser.builder().build();

        final String processTaskId = "processTaskId";
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().build())
                .processTaskId(processTaskId)
                .payload(PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
                        .processingStatus(PerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED)
                        .build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        cut.process(requestTaskId, requestTaskActionType, appUser, actionPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(workflowService, times(1)).completeTask(processTaskId);
    }

    @Test
    void process_not_valid() {
        final Long requestTaskId = 1L;
        final String requestTaskActionType = "requestTaskActionType";
        final RequestTaskActionEmptyPayload actionPayload = RequestTaskActionEmptyPayload.builder().build();
        final AppUser appUser = AppUser.builder().build();

        final String processTaskId = "processTaskId";
        final RequestTask requestTask = RequestTask.builder()
                .processTaskId(processTaskId)
				.payload(PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
						.processingStatus(PerformanceAccountTemplateDataUploadProcessingStatus.IN_PROGRESS).build())
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                cut.process(requestTaskId, requestTaskActionType, appUser, actionPayload));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.PERFORMANCE_ACCOUNT_TEMPLATE_NOT_COMPLETED_YET);
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verifyNoInteractions(workflowService);
    }
	
	@Test
	void getType() {
		assertThat(cut.getTypes()).containsExactlyElementsOf(
				List.of(CcaRequestTaskActionType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_COMPLETE));
	}
	
}
