package uk.gov.cca.api.web.orchestrator.account.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.AccountPerformanceAccountTemplateDataReportInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service.PerformanceAccountTemplateDataAttachmentService;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service.PerformanceAccountTemplateDataQueryService;
import uk.gov.netz.api.token.FileToken;

@Service
@RequiredArgsConstructor
public class TargetUnitAccountPerformanceAccountTemplateDataReportServiceOrchestrator {

	private final PerformanceAccountTemplateDataQueryService performanceAccountTemplateDataQueryService;
	private final PerformanceAccountTemplateDataAttachmentService performanceAccountTemplateDataAttachmentService;

	public AccountPerformanceAccountTemplateDataReportInfoDTO getReportInfoDTO(Long accountId,
			TargetPeriodType targetPeriodType) {
		return performanceAccountTemplateDataQueryService
				.findReportInfoByAccountIdAndTargetPeriod(accountId, targetPeriodType).orElse(null);
	}

	public AccountPerformanceAccountTemplateDataReportDetailsDTO getReportDetailsDTO(Long accountId,
			TargetPeriodType targetPeriodType) {
		return performanceAccountTemplateDataQueryService.getReportDetailsByAccountIdAndTargetPeriod(accountId,
				targetPeriodType);
	}

	public FileToken generateGetReportAttachmentToken(Long accountId,
			TargetPeriodType targetPeriodType, UUID fileAttachmentUuid) {
		return performanceAccountTemplateDataAttachmentService.generateGetAttachmentToken(accountId, targetPeriodType,
				fileAttachmentUuid);
	}
}
