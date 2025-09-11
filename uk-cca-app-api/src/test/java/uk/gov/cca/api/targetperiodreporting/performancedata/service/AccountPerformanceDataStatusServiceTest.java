package uk.gov.cca.api.targetperiodreporting.performancedata.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.targetperiodreporting.performancedata.validation.AccountPerformanceDataStatusValidationService;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataStatus;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataEntity;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataUpdateLockDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.repository.AccountPerformanceDataStatusRepository;
import uk.gov.cca.api.targetperiodreporting.performancedata.repository.PerformanceDataRepository;
import uk.gov.netz.api.common.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class AccountPerformanceDataStatusServiceTest {

	@InjectMocks
	private AccountPerformanceDataStatusService accountPerformanceDataStatusService;

	@Mock
	private AccountPerformanceDataStatusRepository accountPerformanceDataStatusRepository;

	@Mock
	private PerformanceDataRepository performanceDataRepository;

	@Mock
	private TargetPeriodService targetPeriodService;

	@Mock
	private AccountPerformanceDataStatusValidationService validatorService;

	@Test
	void updateAccountPerformanceDataStatusLock() {
		Long accountId = 912L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		AccountPerformanceDataStatus entity = AccountPerformanceDataStatus.builder().locked(true)
				.lastPerformanceData(PerformanceDataEntity.builder().reportVersion(2).id(226L).build())
				.targetPeriod(TargetPeriod.builder().name("TP6 (2024)").build()).build();
		AccountPerformanceDataUpdateLockDTO updateLockDTO = AccountPerformanceDataUpdateLockDTO.builder().locked(true)
				.targetPeriodType(targetPeriodType).build();

		when(accountPerformanceDataStatusRepository.findByAccountIdAndTargetPeriodBusinessId(accountId,
				updateLockDTO.getTargetPeriodType())).thenReturn(Optional.of(entity));

		// invoke
		accountPerformanceDataStatusService.updateAccountPerformanceDataLock(accountId, updateLockDTO);

		verify(accountPerformanceDataStatusRepository, times(1)).findByAccountIdAndTargetPeriodBusinessId(accountId,
				targetPeriodType);

	}

	@Test
	void submitAccountPerformanceData_newRecord() {
		Long accountId = 912L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		TargetPeriod tp6TargetPeriod = TargetPeriod.builder().businessId(TargetPeriodType.TP6).name("TP6 (2024)")
				.build();
		PerformanceDataSubmissionType submissionType = PerformanceDataSubmissionType.PRIMARY;
		int reportVersion = 1;

		PerformanceDataContainer performanceDataContainer = new PerformanceDataContainer();
		PerformanceDataEntity performanceDataEntity = new PerformanceDataEntity();

		when(accountPerformanceDataStatusRepository.findByAccountIdAndTargetPeriodBusinessIdForUpdate(accountId,
				targetPeriodType)).thenReturn(Optional.empty());

		when(targetPeriodService.findByTargetPeriodType(targetPeriodType)).thenReturn(tp6TargetPeriod);

		when(performanceDataRepository.save(any(PerformanceDataEntity.class))).thenReturn(performanceDataEntity);

		accountPerformanceDataStatusService.submitAccountPerformanceData(performanceDataContainer, accountId,
				targetPeriodType, reportVersion, submissionType);

		// Verify behavior
		verify(accountPerformanceDataStatusRepository, times(1))
				.findByAccountIdAndTargetPeriodBusinessIdForUpdate(accountId, targetPeriodType);
		verify(performanceDataRepository, times(1)).save(any(PerformanceDataEntity.class));
		verify(targetPeriodService, times(1)).findByTargetPeriodType(targetPeriodType);
		verify(accountPerformanceDataStatusRepository, times(1)).save(any(AccountPerformanceDataStatus.class));

	}

	@Test
	void submitAccountPerformanceData_existingRecord() {
		Long accountId = 912L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		PerformanceDataSubmissionType submissionType = PerformanceDataSubmissionType.PRIMARY;
		TargetPeriod tp6TargetPeriod = TargetPeriod.builder().businessId(TargetPeriodType.TP6).name("TP6 (2024)")
				.build();

		PerformanceDataContainer performanceDataContainer = new PerformanceDataContainer();
		PerformanceDataEntity performanceDataEntity = new PerformanceDataEntity();
		performanceDataEntity.setReportVersion(2);
		AccountPerformanceDataStatus existingAccountPerformanceDataStatus = AccountPerformanceDataStatus.builder()
				.accountId(accountId).locked(false)
				.lastPerformanceData(PerformanceDataEntity.builder().reportVersion(1).build()).build();

		when(accountPerformanceDataStatusRepository.findByAccountIdAndTargetPeriodBusinessIdForUpdate(accountId,
				targetPeriodType)).thenReturn(Optional.of(existingAccountPerformanceDataStatus));

		when(targetPeriodService.findByTargetPeriodType(targetPeriodType)).thenReturn(tp6TargetPeriod);

		when(performanceDataRepository.save(any(PerformanceDataEntity.class))).thenReturn(performanceDataEntity);

		accountPerformanceDataStatusService.submitAccountPerformanceData(performanceDataContainer, accountId,
				targetPeriodType, 2, submissionType);

		// Verify behavior
		assertTrue(existingAccountPerformanceDataStatus.isLocked());
		verify(accountPerformanceDataStatusRepository, times(1))
				.findByAccountIdAndTargetPeriodBusinessIdForUpdate(accountId, targetPeriodType);
		verify(targetPeriodService, times(1)).findByTargetPeriodType(targetPeriodType);
		verify(performanceDataRepository, times(1)).save(any(PerformanceDataEntity.class));
		verify(accountPerformanceDataStatusRepository, never()).save(any(AccountPerformanceDataStatus.class));

	}

	@Test
	void submitAccountPerformanceData_AccountLocked() {
		Long accountId = 912L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		PerformanceDataSubmissionType submissionType = PerformanceDataSubmissionType.PRIMARY;
		int reportVersion = 1;

		PerformanceDataContainer performanceDataContainer = new PerformanceDataContainer();

		when(accountPerformanceDataStatusRepository.findByAccountIdAndTargetPeriodBusinessIdForUpdate(accountId,
				targetPeriodType)).thenReturn(Optional.empty());

		doThrow(new BusinessException(CcaErrorCode.INVALID_PERFORMANCE_DATA_UPDATE_ACCOUNT_LOCKED))
				.when(validatorService).validateAccountUnlocked(any());

		assertThrows(BusinessException.class,
				() -> accountPerformanceDataStatusService.submitAccountPerformanceData(performanceDataContainer,
						accountId, targetPeriodType, reportVersion, submissionType));

		// Verify behavior
		verify(accountPerformanceDataStatusRepository, times(1))
				.findByAccountIdAndTargetPeriodBusinessIdForUpdate(accountId, targetPeriodType);
		verify(performanceDataRepository, times(0)).save(any(PerformanceDataEntity.class));
		verify(accountPerformanceDataStatusRepository, times(0)).save(any(AccountPerformanceDataStatus.class));

	}

	@Test
	void submitAccountPerformanceData_invalid_reportVersion() {
		Long accountId = 912L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		PerformanceDataSubmissionType submissionType = PerformanceDataSubmissionType.PRIMARY;
		int reportVersion = 1;

		PerformanceDataContainer performanceDataContainer = new PerformanceDataContainer();

		when(accountPerformanceDataStatusRepository.findByAccountIdAndTargetPeriodBusinessIdForUpdate(accountId,
				targetPeriodType)).thenReturn(Optional.empty());

		doThrow(new BusinessException(CcaErrorCode.INVALID_PERFORMANCE_DATA_UPDATE_INVALID_REPORT_VERSION))
				.when(validatorService).validateReportVersion(any(), eq(reportVersion));

		assertThrows(BusinessException.class,
				() -> accountPerformanceDataStatusService.submitAccountPerformanceData(performanceDataContainer,
						accountId, targetPeriodType, reportVersion, submissionType));

		// Verify behavior
		verify(accountPerformanceDataStatusRepository, times(1))
				.findByAccountIdAndTargetPeriodBusinessIdForUpdate(accountId, targetPeriodType);
		verify(performanceDataRepository, times(0)).save(any(PerformanceDataEntity.class));
		verify(accountPerformanceDataStatusRepository, times(0)).save(any(AccountPerformanceDataStatus.class));

	}
}