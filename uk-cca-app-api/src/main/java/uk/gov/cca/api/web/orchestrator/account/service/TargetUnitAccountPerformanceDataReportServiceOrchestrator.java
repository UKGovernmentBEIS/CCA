package uk.gov.cca.api.web.orchestrator.account.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataStatusInfoDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataUpdateLockDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataReportDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusAttachmentService;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.token.FileToken;

@Service
@RequiredArgsConstructor
public class TargetUnitAccountPerformanceDataReportServiceOrchestrator {
	
	private final AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;
	private final AccountPerformanceDataStatusService accountPerformanceDataStatusService;
	private final AccountPerformanceDataStatusAttachmentService accountPerformanceDataStatusAttachmentService;

	public AccountPerformanceDataStatusInfoDTO getAccountPerformanceDataStatusInfo(Long accountId,
			TargetPeriodType targetPeriodType, AppUser currentUser) {
		return accountPerformanceDataStatusQueryService
				.getAccountPerformanceDataStatusInfo(accountId, targetPeriodType, currentUser);
	}

	public AccountPerformanceDataReportDetailsDTO getAccountPerformanceDataReportDetails(Long accountId,
			TargetPeriodType targetPeriodType) {
		return accountPerformanceDataStatusQueryService.getAccountPerformanceDataReportDetails(accountId, targetPeriodType);
	}

	public FileToken generateGetAccountPerformanceDataReportAttachmentToken(Long accountId,
			TargetPeriodType targetPeriodType, UUID fileAttachmentUuid) {
		return accountPerformanceDataStatusAttachmentService
				.generateGetAccountPerformanceDataReportAttachmentToken(accountId, targetPeriodType, fileAttachmentUuid);
	}

	public void updateAccountPerformanceDataLock(Long accountId, AccountPerformanceDataUpdateLockDTO updateLockDTO) {
		accountPerformanceDataStatusService.updateAccountPerformanceDataLock(accountId, updateLockDTO);
	}
}
