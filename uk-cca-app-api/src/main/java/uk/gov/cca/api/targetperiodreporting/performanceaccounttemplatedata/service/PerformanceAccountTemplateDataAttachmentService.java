package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.attachments.service.FileAttachmentTokenService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.token.FileToken;

@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateDataAttachmentService {

	private final PerformanceAccountTemplateDataQueryService performanceAccountTemplateDataQueryService;
	private final FileAttachmentTokenService fileAttachmentTokenService;

	public FileToken generateGetAttachmentToken(Long accountId, TargetPeriodType targetPeriodType,
			UUID fileAttachmentUuid) {
		final FileInfoDTO fileReport = performanceAccountTemplateDataQueryService
				.getAttachmentReportByAccountIdAndTargetPeriod(accountId, targetPeriodType);
		
		if (fileReport == null || !fileReport.getUuid().equals(fileAttachmentUuid.toString())) {
			throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
		}

		return fileAttachmentTokenService.generateGetFileAttachmentToken(fileReport.getUuid());
	}
}
