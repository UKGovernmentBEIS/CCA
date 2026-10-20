package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.FileReports;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.NotAccountRelatedUploadErrorReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateDataGenerateCsvReportServiceTest {
	
	@InjectMocks
	private PerformanceAccountTemplateDataGenerateCsvReportService cut;
	
	@Mock
	private RequestTaskService requestTaskService;
	
	@Mock
	private CcaFileAttachmentService ccaFileAttachmentService;

	@Test
	void generateCsvReport() throws IOException {
		Long requestTaskId = 1L;
		FileReports fileReports = FileReports.builder()
				.accountFileReports(Map.of(
						1L, AccountUploadReport.builder()
								.accountBusinessId("ac1Bus")
								.succeeded(true)
								.file(FileInfoDTO.builder().name("acc1FileName").build())
								.build(),
						2L, AccountUploadReport.builder()
								.accountBusinessId("acc2Bus")
								.succeeded(false)
								.errorFilenames(List.of("acc2FileName"))
								.errors(List.of("Error for acc 2"))
								.build()
						))
				.notAccountRelatedFileErrors(List.of(
						NotAccountRelatedUploadErrorReport.builder()
							.fileName("fileName3")
							.error("Error with file name 3")
							.build()
						))
				.build();
		
		PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.builder()
				.build();
		
		PerformanceAccountTemplateDataUploadRequestPayload requestPayload = PerformanceAccountTemplateDataUploadRequestPayload.builder()
				.sectorUserAssignee("sectorUserAsignee")
				.build();
		
		Request request = Request.builder().id("req1").payload(requestPayload).build();
		RequestTask requestTask = RequestTask.builder()
				.request(request)
				.payload(requestTaskPayload)
				.build();
		
		when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
		
		String csvUuid = UUID.randomUUID().toString();
		when(ccaFileAttachmentService.createSystemFileAttachment(Mockito.any(FileDTO.class),
				Mockito.eq(FileStatus.SUBMITTED), Mockito.eq("sectorUserAsignee"))).thenReturn(csvUuid);
		
		cut.generateCsvReport(requestTaskId, fileReports);
		
		assertThat(requestTaskPayload.getCsvReportFile()).isNotNull();
		
		
		verify(requestTaskService, times(1)).findTaskById(requestTaskId);
		
		ArgumentCaptor<FileDTO> fileDTOCaptor = ArgumentCaptor.forClass(FileDTO.class);
		ArgumentCaptor<FileStatus> fileStatusCaptor = ArgumentCaptor.forClass(FileStatus.class);
		ArgumentCaptor<String> assigneeCaptor = ArgumentCaptor.forClass(String.class);
		verify(ccaFileAttachmentService, times(1)).createSystemFileAttachment(fileDTOCaptor.capture(),
				fileStatusCaptor.capture(), assigneeCaptor.capture());
		FileDTO csvFile = fileDTOCaptor.getValue();
		assertThat(csvFile.getFileName()).isEqualTo("req1_Summary.csv");
		String csvContent = new String(csvFile.getFileContent(), StandardCharsets.UTF_8);
		assertThat(csvContent).contains("ac1Bus,acc1FileName,Success",
				"acc2Bus,acc2FileName,Error,Error for acc 2",
				",fileName3,Error,Error with file name 3");
		
		assertThat(fileStatusCaptor.getValue()).isEqualTo(FileStatus.SUBMITTED);
		assertThat(assigneeCaptor.getValue()).isEqualTo("sectorUserAsignee");
	}
	
}
