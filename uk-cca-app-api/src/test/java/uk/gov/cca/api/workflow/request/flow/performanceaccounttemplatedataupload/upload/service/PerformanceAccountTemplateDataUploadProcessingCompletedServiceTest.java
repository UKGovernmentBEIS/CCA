package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.FileReports;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataUploadProcessingCompletedServiceTest {

	@InjectMocks
	private PerformanceAccountTemplateDataUploadProcessingCompletedService cut;

	@Mock
	private RequestTaskService requestTaskService;

	@Test
	void completed() {
		String requestId = "reqId";
		Map<Long, AccountUploadReport> accountReports = Map.of(1L,
				AccountUploadReport.builder().accountId(1L).build());

		PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload
				.builder()
				.fileReports(FileReports.builder().build())
				.processingStatus(PerformanceAccountTemplateDataUploadProcessingStatus.IN_PROGRESS)
				.build();
		
		RequestTask requestTask = RequestTask.builder()
				.payload(requestTaskPayload)
				.build();
		
		when(requestTaskService
				.findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT, requestId))
				.thenReturn(requestTask);
		
		cut.completed(requestId, accountReports);
		
		assertThat(requestTaskPayload.getFileReports().getAccountFileReports()).containsExactlyEntriesOf(accountReports);
		assertThat(requestTaskPayload.getProcessingStatus()).isEqualTo(PerformanceAccountTemplateDataUploadProcessingStatus.COMPLETED);
		
		verify(requestTaskService, times(1)).findByTypeAndRequestId(CcaRequestTaskType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT, requestId);
	}

}
