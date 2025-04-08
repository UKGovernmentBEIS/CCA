package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;

@Service
@RequiredArgsConstructor
class PerformanceAccountTemplateDataReportPackagesValidateExistenceService {
	
	private final FileAttachmentService fileAttachmentService;
	
	public void validateReportPackagesExistence(Set<UUID> reportPackages) throws ReportPackageMissingException {
		if (!fileAttachmentService
				.fileAttachmentsExist(reportPackages.stream().map(UUID::toString).collect(Collectors.toSet()))) {
			throw new ReportPackageMissingException("One or more report packages are missing");
		}
	}
}
