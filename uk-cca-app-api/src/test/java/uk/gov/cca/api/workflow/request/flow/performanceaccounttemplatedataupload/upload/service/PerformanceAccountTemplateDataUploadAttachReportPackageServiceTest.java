package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataUploadAttachReportPackageServiceTest {

	@InjectMocks
    private PerformanceAccountTemplateDataUploadAttachReportPackageService cut;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Test
	void uploadAttachment() {
		Long requestTaskId = 1L;
		String attachmentUuid = UUID.randomUUID().toString();
		String filename = "filename";
		
		PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload payload = PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder(
				).build();
		
		RequestTask requestTask = RequestTask.builder()
				.payload(payload)
				.build();
		
		when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
		
		cut.uploadAttachment(requestTaskId, attachmentUuid, filename);
		
		verify(requestTaskService, times(1)).findTaskById(requestTaskId);
		
		assertThat(payload.getUploadAttachments()).hasSize(1);
		assertThat(payload.getUploadAttachments().get(UUID.fromString(attachmentUuid))).isEqualTo(filename);
	}
}
