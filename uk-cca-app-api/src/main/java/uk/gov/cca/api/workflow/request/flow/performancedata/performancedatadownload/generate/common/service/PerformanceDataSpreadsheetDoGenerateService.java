package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.validation.PerformanceDataDownloadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestPayload;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerformanceDataSpreadsheetDoGenerateService {

	private final RequestService requestService;
	private final FileAttachmentService fileAttachmentService;
	private final List<PerformanceDataSpreadsheetGenerateExcelService> performanceDataSpreadsheetGenerateExcelServices;
	
	@Transactional(rollbackFor = BpmnExecutionException.class, propagation = Propagation.REQUIRES_NEW)
	public FileInfoDTO doGenerate(final String requestId, final Long accountId) throws BpmnExecutionException {
		try {
			final Request request = requestService.findRequestById(requestId);
			final PerformanceDataSpreadsheetGenerateRequestPayload requestPayload =
					(PerformanceDataSpreadsheetGenerateRequestPayload) request.getPayload();
			final PerformanceDataSpreadsheetGenerateRequestMetadata metadata =
					(PerformanceDataSpreadsheetGenerateRequestMetadata) request.getMetadata();

			// Get proper generator service
			PerformanceDataSpreadsheetGenerateExcelService excelService = performanceDataSpreadsheetGenerateExcelServices.stream()
					.filter(service -> service.getTemplateType().equals(metadata.getTargetPeriodDocument()))
					.findFirst().orElseThrow();

			// Generate Excel file
			final FileDTO report = excelService.generate(metadata, accountId);

			// Persist to DB
			final String uuid = fileAttachmentService.createFileAttachment(report, FileStatus.SUBMITTED, requestPayload.getSectorUserAssignee());

			return FileInfoDTO.builder().name(report.getFileName()).uuid(uuid).build();
        } catch (Exception e) {
			log.error(e.getMessage(), e);
            throw new BpmnExecutionException(e.getMessage(), List.of(
					PerformanceDataDownloadViolation.PerformanceDataDownloadViolationMessage.GENERATE_EXCEL_FAILED.getMessage()));
        }
	}
}
