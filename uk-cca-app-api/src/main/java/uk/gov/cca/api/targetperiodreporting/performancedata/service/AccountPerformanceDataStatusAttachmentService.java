package uk.gov.cca.api.targetperiodreporting.performancedata.service;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.attachments.service.FileAttachmentTokenService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.token.FileToken;

@Service
@RequiredArgsConstructor
public class AccountPerformanceDataStatusAttachmentService {
	private final AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;
	private final FileAttachmentTokenService fileAttachmentTokenService;

	public FileToken generateGetFileAttachmentToken(final Long accountId, final TargetPeriodType targetPeriodType,
			final UUID fileAttachmentUuid) {

		FileInfoDTO performanceReport = accountPerformanceDataStatusQueryService
				.getAccountPerformanceReportAttachment(accountId, targetPeriodType);

		if (performanceReport == null || !performanceReport.getUuid().equals(fileAttachmentUuid.toString())) {
			throw new BusinessException(RESOURCE_NOT_FOUND);
		}

		return fileAttachmentTokenService.generateGetFileAttachmentToken(fileAttachmentUuid.toString());
	}
}