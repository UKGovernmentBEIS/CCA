package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service.PerformanceAccountTemplateDataUploadAttachReportPackageService;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataUploadAttachReportPackageHandlerTest {

	@InjectMocks
    private PerformanceAccountTemplateDataUploadAttachReportPackageHandler cut;
	
	@Mock
    private PerformanceAccountTemplateDataUploadAttachReportPackageService performanceAccountTemplateDataUploadAttachReportPackageService;
	
	@Test
	void uploadAttachment() {
		Long requestTaskId = 1L;
		String attachmentUuid = "uuid";
		String filename = "filename";
		
		cut.uploadAttachment(requestTaskId, attachmentUuid, filename);
		
		verify(performanceAccountTemplateDataUploadAttachReportPackageService, times(1)).uploadAttachment(requestTaskId, attachmentUuid, filename);
		
	}
	
	@Test
	void getType() {
		assertThat(cut.getType()).isEqualTo(CcaRequestTaskActionType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_ATTACH_REPORT_PACKAGE);
	}
}
