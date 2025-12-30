package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.PerformanceDataGenerateRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.TargetUnitAccountReport;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceDataGenerateCleanUpIndividualFilesService {

	private final RequestService requestService;
	private final FileAttachmentService fileAttachmentService;
	
	@Transactional
	public void cleanupIndividualAccountReports(String requestId) {
		final Request request = requestService.findRequestById(requestId);
		final PerformanceDataGenerateRequestPayload requestPayload =
				(PerformanceDataGenerateRequestPayload) request.getPayload();
		
		final Set<String> accountReportsUUIDs = requestPayload.getAccountsReports().values().stream()
	            .filter(TargetUnitAccountReport::isSucceeded)
	            .map(TargetUnitAccountReport::getFileInfo)
	            .map(FileInfoDTO::getUuid)
	            .collect(Collectors.toSet());

		if(!accountReportsUUIDs.isEmpty()) {
			fileAttachmentService.deleteFileAttachments(accountReportsUUIDs);
		}
	}
}
