package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.utils.ZipUtils;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.PerformanceDataGenerateRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.common.utils.MimeTypeUtils;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerformanceDataGenerateZipFileService {

	private final RequestService requestService;
	private final FileAttachmentService fileAttachmentService;
	private final CcaFileAttachmentService ccaFileAttachmentService;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public FileInfoDTO generateZipFile(String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final PerformanceDataGenerateRequestPayload requestPayload =
				(PerformanceDataGenerateRequestPayload) request.getPayload();

		// Create file name
		final String zipFileName = resolveFilename(requestPayload);

		// Get excels UUID of all accounts
		final Set<String> accountReportFileUuids = requestPayload.getAccountsReports().values().stream()
				.filter(TargetUnitAccountReport::isSucceeded)
                .map(TargetUnitAccountReport::getFileInfo)
                .map(FileInfoDTO::getUuid)
                .collect(Collectors.toSet());
		
		try {
			// Generate zip file and save to DB
			List<FileDTO> files = fileAttachmentService.getFilesAsStream(accountReportFileUuids).toList();
			byte[] zipFile = ZipUtils.generateZipFile(files);

			FileDTO zipFileDTO = FileDTO.builder()
					.fileContent(zipFile).fileName(zipFileName).fileSize(zipFile.length)
					.fileType(MimeTypeUtils.detect(zipFile, zipFileName))
					.build();
			final String uuid = ccaFileAttachmentService.createSystemFileAttachment(
					zipFileDTO, FileStatus.SUBMITTED, requestPayload.getSectorUserAssignee());

			return FileInfoDTO.builder().uuid(uuid).name(zipFileName).build();
		} catch (Exception e) {
			log.error("Cannot generate zip for request {}", requestId, e);
			throw new BpmnError("zipError");
		}
	}

	private String resolveFilename(final PerformanceDataGenerateRequestPayload payload) {
		// [Sector acronym]_[TPx]_reporting_templates.zip
		return String.format("%s_%s_reporting_templates.zip", payload.getSectorAssociationInfo().getAcronym(),
				payload.getTargetPeriodType().name());
	}
}
