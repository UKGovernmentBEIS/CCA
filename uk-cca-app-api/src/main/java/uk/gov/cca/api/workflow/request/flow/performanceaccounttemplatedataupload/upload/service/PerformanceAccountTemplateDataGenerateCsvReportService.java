package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.FileReports;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.NotAccountRelatedUploadErrorReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateDataGenerateCsvReportService {

	private final RequestTaskService requestTaskService;
	private final CcaFileAttachmentService ccaFileAttachmentService;

	@Transactional
	public void generateCsvReport(Long requestTaskId, FileReports fileReports) throws IOException {
		final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
		final PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload requestTaskPayload = (PerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTask
				.getPayload();
		final Request request = requestTask.getRequest();
		final PerformanceAccountTemplateDataUploadRequestPayload requestPayload = (PerformanceAccountTemplateDataUploadRequestPayload) request.getPayload();

		final String fileName = String.format("%s_Summary.csv", requestTask.getRequest().getId());

		// Create CSV
		try (StringWriter sw = new StringWriter();
				CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT.builder()
						.setHeader("TU ID", "Upload file name", "Status", "Error description").build())) {
			for(AccountUploadReport report : fileReports.getAccountFileReports().values()) {
				if (Boolean.TRUE.equals(report.getSucceeded())) {
					csvPrinter.printRecord(report.getAccountBusinessId(), report.getFile().getName(), "Success");
				} else {
					for (String error : report.getErrors()) {
						csvPrinter.printRecord(report.getAccountBusinessId(), String.join(" - ", report.getErrorFilenames()), "Error",
								error);
					}
				}
			}
			
			for (NotAccountRelatedUploadErrorReport nonAccountRelatedErrorReport : fileReports
					.getNotAccountRelatedFileErrors()) {
				csvPrinter.printRecord(null, nonAccountRelatedErrorReport.getFileName(), "Error",
						nonAccountRelatedErrorReport.getError());
			}

			final byte[] generatedFile = sw.toString().getBytes(StandardCharsets.UTF_8);

			FileDTO reportFile = FileDTO.builder().fileContent(generatedFile).fileName(fileName)
					.fileSize(generatedFile.length).fileType(MimeTypeUtils.detect(generatedFile, fileName)).build();
			
            final String uuid = ccaFileAttachmentService.createSystemFileAttachment(
            		reportFile, FileStatus.SUBMITTED, requestPayload.getSectorUserAssignee());
			
			requestTaskPayload.setCsvReportFile(FileInfoDTO.builder().uuid(uuid).name(reportFile.getFileName()).build());
		}
	}
}
