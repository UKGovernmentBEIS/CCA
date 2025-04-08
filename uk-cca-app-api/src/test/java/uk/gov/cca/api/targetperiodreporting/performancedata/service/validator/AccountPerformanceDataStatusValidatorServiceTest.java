package uk.gov.cca.api.targetperiodreporting.performancedata.service.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.AccountPerformanceDataStatus;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataEntity;
import uk.gov.netz.api.common.exception.BusinessException;

public class AccountPerformanceDataStatusValidatorServiceTest {

	private AccountPerformanceDataStatusValidatorService validatorService;

	@BeforeEach
	void setUp() {
		validatorService = new AccountPerformanceDataStatusValidatorService();
	}

	@Test
	void testValidateAccountUnlocked_accountLocked() {
		Optional<AccountPerformanceDataStatus> lockedOptional = Optional
				.of(AccountPerformanceDataStatus.builder().locked(true).build());

		BusinessException exception = assertThrows(BusinessException.class,
				() -> validatorService.validateAccountUnlocked(lockedOptional));

		assertEquals(CcaErrorCode.INVALID_PERFORMANCE_DATA_UPDATE_ACCOUNT_LOCKED, exception.getErrorCode());
	}

	@Test
	void testValidateAccountUnlocked() {
		// Arrange
		Optional<AccountPerformanceDataStatus> unlockedOptional = Optional
				.of(AccountPerformanceDataStatus.builder().locked(false).build());

		assertDoesNotThrow(() -> validatorService.validateAccountUnlocked(unlockedOptional));
	}

	@Test
	void testValidateAccountUnlocked_accountIsEmpty() {
		assertDoesNotThrow(() -> validatorService.validateAccountUnlocked(Optional.empty()));
	}

	@Test
	void testValidateReportVersion_reportVersionIsInvalid() {
		AccountPerformanceDataStatus accountPerformanceDataStatus = AccountPerformanceDataStatus.builder().locked(false)
				.lastPerformanceData(PerformanceDataEntity.builder().reportVersion(2).build()).build();

		BusinessException exception = assertThrows(BusinessException.class,
				() -> validatorService.validateReportVersion(Optional.of(accountPerformanceDataStatus), 4));

		assertEquals(CcaErrorCode.INVALID_PERFORMANCE_DATA_UPDATE_INVALID_REPORT_VERSION, exception.getErrorCode());
	}

	@Test
	void testValidateReportVersion_reportVersionIsValid() {
		AccountPerformanceDataStatus accountPerformanceDataStatus = AccountPerformanceDataStatus.builder().locked(false)
				.lastPerformanceData(PerformanceDataEntity.builder().reportVersion(2).build()).build();

		assertDoesNotThrow(() -> validatorService.validateReportVersion(Optional.of(accountPerformanceDataStatus), 3));
	}

	@Test
	void testValidateReportVersion_firstReportVersionIsNotOne() {

		BusinessException exception = assertThrows(BusinessException.class,
				() -> validatorService.validateReportVersion(Optional.empty(), 2));

		assertEquals(CcaErrorCode.INVALID_PERFORMANCE_DATA_UPDATE_INVALID_REPORT_VERSION, exception.getErrorCode());
	}

	@Test
	void testValidateReportVersion_firstReportVersionIsOne() {

		assertDoesNotThrow(() -> validatorService.validateReportVersion(Optional.empty(), 1));
	}
}
