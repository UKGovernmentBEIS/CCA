package uk.gov.cca.api.targetperiodreporting.performancedata.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import uk.gov.cca.api.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiod.repository.TargetPeriodRepository;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataStatus;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataEntity;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceDataUpdateLockDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.repository.AccountPerformanceDataStatusRepository;
import uk.gov.cca.api.targetperiodreporting.performancedata.repository.PerformanceDataRepository;
import uk.gov.cca.api.targetperiodreporting.performancedata.service.validator.AccountPerformanceDataStatusValidatorService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.Optional;

@Validated
@RequiredArgsConstructor
@Service
public class AccountPerformanceDataStatusService {

	private final AccountPerformanceDataStatusRepository accountPerformanceDataStatusRepository;
	private final TargetPeriodRepository targetPeriodRepository;
	private final PerformanceDataRepository performanceDataRepository;
	private final AccountPerformanceDataStatusValidatorService validatorService;

	@Transactional
	public void updateAccountPerformanceDataLock(Long accountId, AccountPerformanceDataUpdateLockDTO updateLockDTO) {

		AccountPerformanceDataStatus accountPerformanceDataStatus = getAccountPerformanceDataStatus(accountId,
				updateLockDTO.getTargetPeriodType());

		accountPerformanceDataStatus.setLocked(updateLockDTO.getLocked());
	}

	@Transactional
	public void submitAccountPerformanceData(@Valid PerformanceDataContainer container, Long accountId, TargetPeriodType targetPeriodType,
			int reportVersion, @NotNull PerformanceDataSubmissionType submissionType) {

		Optional<AccountPerformanceDataStatus> accountPerformanceData = accountPerformanceDataStatusRepository
				.findByAccountIdAndTargetPeriodBusinessIdForUpdate(accountId, targetPeriodType);

		validatorService.validateAccountUnlocked(accountPerformanceData);
		validatorService.validateReportVersion(accountPerformanceData, reportVersion);

		TargetPeriod targetPeriod = targetPeriodRepository.findByBusinessId(targetPeriodType)
				.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

		PerformanceDataEntity newPerformanceData = createPerformanceData(container, accountId, targetPeriod,
				reportVersion, submissionType);

		accountPerformanceData.ifPresentOrElse(
				existingAccountPerformanceData -> updateExistingAccountPerformanceData(existingAccountPerformanceData,
						newPerformanceData),
				() -> createAccountPerformanceData(accountId, targetPeriod, newPerformanceData));

	}

	private PerformanceDataEntity createPerformanceData(PerformanceDataContainer container, Long accountId,
			TargetPeriod targetPeriod, int reportVersion, PerformanceDataSubmissionType submissionType) {

		PerformanceDataEntity performanceDataEntity = PerformanceDataEntity.builder().data(container)
				.targetPeriod(targetPeriod).accountId(accountId).reportVersion(reportVersion)
				.submissionType(submissionType).build();

		return performanceDataRepository.save(performanceDataEntity);
	}

	private void createAccountPerformanceData(Long accountId, TargetPeriod targetPeriod,
			PerformanceDataEntity newPerformanceData) {

		AccountPerformanceDataStatus newAccountPerformanceData = new AccountPerformanceDataStatus();
		newAccountPerformanceData.setAccountId(accountId);
		newAccountPerformanceData.setTargetPeriod(targetPeriod);
		newAccountPerformanceData.setLastPerformanceData(newPerformanceData);
		newAccountPerformanceData.setLocked(true);

		accountPerformanceDataStatusRepository.save(newAccountPerformanceData);
	}

	private void updateExistingAccountPerformanceData(AccountPerformanceDataStatus accountPerformanceData,
			PerformanceDataEntity newPerformanceData) {

		accountPerformanceData.setLocked(true);
		accountPerformanceData.setLastPerformanceData(newPerformanceData);
	}

	private AccountPerformanceDataStatus getAccountPerformanceDataStatus(Long accountId,
			TargetPeriodType targetPeriodType) {
		return accountPerformanceDataStatusRepository
				.findByAccountIdAndTargetPeriodBusinessId(accountId, targetPeriodType)
				.orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
	}

}
