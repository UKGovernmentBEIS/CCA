package uk.gov.cca.api.targetperiodreporting.performancedata.validation;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataStatus;
import uk.gov.netz.api.common.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class AccountPerformanceDataStatusValidationService {

	public void validateAccountUnlocked(Optional<AccountPerformanceDataStatus> accountPerformanceData) {
		accountPerformanceData.filter(AccountPerformanceDataStatus::isLocked).ifPresent(data -> {
			throw new BusinessException(CcaErrorCode.INVALID_PERFORMANCE_DATA_UPDATE_ACCOUNT_LOCKED);
		});
	}

	public void validateReportVersion(Optional<AccountPerformanceDataStatus> accountPerformanceData,
			int reportVersion) {
		boolean isReportVersionInvalid = accountPerformanceData
				.map(data -> data.getLastPerformanceData().getReportVersion() != reportVersion - 1)
				.orElse(reportVersion != 1);

		if (isReportVersionInvalid) {
			throw new BusinessException(CcaErrorCode.INVALID_PERFORMANCE_DATA_UPDATE_INVALID_REPORT_VERSION);
		}
	}

}
