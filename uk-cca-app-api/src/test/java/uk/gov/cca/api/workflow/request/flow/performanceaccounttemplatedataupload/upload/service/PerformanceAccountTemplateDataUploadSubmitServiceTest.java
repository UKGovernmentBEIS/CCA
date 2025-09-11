package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.FileReports;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUpload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataUploadSubmitServiceTest {
	
	@InjectMocks
    private PerformanceAccountTemplateDataUploadSubmitService cut;

	@Test
	void submitUpload() {
		PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload
				.builder().build();
		
		RequestTask requestTask = RequestTask.builder()
				.payload(requestTaskPayload)
				.build();
		
		PerformanceAccountTemplateDataUpload performanceAccountTemplateDataUpload = PerformanceAccountTemplateDataUpload
				.builder().targetPeriodType(TargetPeriodType.TP6).build();
		
		PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload requestTaskActionPayload = PerformanceAccountTemplateDataUploadProcessingRequestTaskActionPayload
				.builder()
				.performanceAccountTemplateDataUpload(performanceAccountTemplateDataUpload)
				.build();
		
		FileReports fileReports = FileReports.builder()
				.build();
		
		cut.submitUpload(requestTask, requestTaskActionPayload, fileReports);
		
		assertThat(requestTaskPayload.getPerformanceAccountTemplateDataUpload())
				.isEqualTo(performanceAccountTemplateDataUpload);
		assertThat(requestTaskPayload.getFileReports())
				.isEqualTo(fileReports);
		assertThat(requestTaskPayload.getProcessingStatus()).isEqualTo(PerformanceAccountTemplateDataUploadProcessingStatus.IN_PROGRESS);
	}
}
