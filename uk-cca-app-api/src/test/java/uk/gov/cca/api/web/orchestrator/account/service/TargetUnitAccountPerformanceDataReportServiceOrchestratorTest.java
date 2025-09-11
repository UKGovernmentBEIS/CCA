package uk.gov.cca.api.web.orchestrator.account.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataUpdateLockDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusAttachmentService;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.AccountPerformanceDataStatusService;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountPerformanceDataReportServiceOrchestratorTest {

	@Mock
	private AccountPerformanceDataStatusQueryService accountPerformanceDataStatusQueryService;

	@Mock
	private AccountPerformanceDataStatusService accountPerformanceDataStatusService;

	@Mock
	private AccountPerformanceDataStatusAttachmentService accountPerformanceDataStatusAttachmentService;

	@InjectMocks
	TargetUnitAccountPerformanceDataReportServiceOrchestrator orchestrator;

	@Test
	void getAccountPerformanceDataStatusInfo() {
		final long accountId = 1L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		final AppUser currentUser = AppUser.builder().authorities(List
				.of(AppAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).accountId(1L).build()))
				.build();

		// Invoke
		orchestrator.getAccountPerformanceDataStatusInfo(accountId, targetPeriodType, currentUser);

		// Verify
		verify(accountPerformanceDataStatusQueryService, times(1)).getAccountPerformanceDataStatusInfo(accountId,
				targetPeriodType, currentUser);
	}

	@Test
	void getAccountPerformanceDataReportDetails() {
		final long accountId = 1L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;

		// Invoke
		orchestrator.getAccountPerformanceDataReportDetails(accountId, targetPeriodType);

		// Verify
		verify(accountPerformanceDataStatusQueryService, times(1)).getAccountPerformanceDataReportDetails(accountId,
				targetPeriodType);
	}

	@Test
	void generateGetFileAttachmentToken() {
		final long accountId = 1L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		UUID attachmentUuid = UUID.randomUUID();

		// Invoke
		orchestrator.generateGetAccountPerformanceDataReportAttachmentToken(accountId, targetPeriodType, attachmentUuid);

		// Verify
		verify(accountPerformanceDataStatusAttachmentService, times(1)).generateGetAccountPerformanceDataReportAttachmentToken(accountId,
				targetPeriodType, attachmentUuid);
	}

	@Test
	void updateAccountPerformanceDataLock() {
		final long accountId = 1L;
		final TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		AccountPerformanceDataUpdateLockDTO updateLockDTO = AccountPerformanceDataUpdateLockDTO.builder().locked(true)
				.targetPeriodType(targetPeriodType).build();

		// Invoke
		orchestrator.updateAccountPerformanceDataLock(accountId, updateLockDTO);

		// Verify
		verify(accountPerformanceDataStatusService, times(1)).updateAccountPerformanceDataLock(accountId,
				updateLockDTO);
	}

}
