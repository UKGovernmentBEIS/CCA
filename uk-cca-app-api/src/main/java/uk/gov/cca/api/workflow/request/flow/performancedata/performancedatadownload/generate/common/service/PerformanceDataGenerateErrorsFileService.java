package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.PerformanceDataGenerateRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerformanceDataGenerateErrorsFileService {

	private final RequestService requestService;
	private final CcaFileAttachmentService ccaFileAttachmentService;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Optional<FileInfoDTO> generateErrorsFile(String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final PerformanceDataGenerateRequestPayload requestPayload =
				(PerformanceDataGenerateRequestPayload) request.getPayload();

		// Find erroneous accounts
		final Map<Long, TargetUnitAccountReport> failedAccountsReport = requestPayload.getAccountsReports().entrySet()
	            .stream()
	            .filter(entry -> !entry.getValue().isSucceeded())
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		if(failedAccountsReport.isEmpty()) {
			return Optional.empty();
		}
		
		// Create CSV
		try (StringWriter sw = new StringWriter();
	            CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.DEFAULT.builder()
	                    .setHeader("TU ID", "Error list")
	                    .build())) {

			// Create file name
			final String errorsFileName = resolveFilename(requestPayload);

			for (TargetUnitAccountReport accountReport : failedAccountsReport.values()) {
				for (String error : accountReport.getErrors())
					csvPrinter.printRecord(accountReport.getAccountBusinessId(), error);
			}

			final byte[] generatedFile = sw.toString().getBytes(StandardCharsets.UTF_8);
			FileDTO csvFileDTO = FileDTO.builder()
					.fileContent(generatedFile).fileName(errorsFileName).fileSize(generatedFile.length)
					.fileType(MimeTypeUtils.detect(generatedFile, errorsFileName)).build();

			// Save to DB
			final String uuid = ccaFileAttachmentService.createSystemFileAttachment(
					csvFileDTO, FileStatus.SUBMITTED, requestPayload.getSectorUserAssignee());

			return Optional.of(FileInfoDTO.builder().uuid(uuid).name(errorsFileName).build());
		} catch (Exception e) {
			log.error("Cannot generate errors for request {}", requestId, e);
			throw new BpmnError("csvError");
		}
	}

	private String resolveFilename(final PerformanceDataGenerateRequestPayload payload) {
		// [Sector acronym]_[TPx]_download_errors.csv
		return String.format("%s_%s_download_errors.csv", payload.getSectorAssociationInfo().getAcronym(),
				payload.getTargetPeriodType().name());
	}
}
