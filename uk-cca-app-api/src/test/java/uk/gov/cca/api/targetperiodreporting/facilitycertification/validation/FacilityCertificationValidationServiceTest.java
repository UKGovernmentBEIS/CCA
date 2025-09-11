package uk.gov.cca.api.targetperiodreporting.facilitycertification.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.CertificationPeriodService;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static uk.gov.cca.api.common.exception.CcaErrorCode.CERT_STATUS_UPDATE_BEFORE_CERT_PERIOD_START_ERROR;
import static uk.gov.cca.api.common.exception.CcaErrorCode.FACILITY_CERTIFICATION_START_DATE_OUTSIDE_PERIOD;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationValidationServiceTest {

	@InjectMocks
	private FacilityCertificationValidationService validationService;

	@Mock
	private CertificationPeriodService certificationPeriodService;

	@Test
	void validateFacilityCertificationByCertificationPeriod() {
		final FacilityCertificationStatusUpdateDTO statusUpdateDTO = FacilityCertificationStatusUpdateDTO.builder()
				.certificationStatus(FacilityCertificationStatus.CERTIFIED)
				.startDate(LocalDate.of(2025,7,3))
				.certificationPeriodId(2L)
				.build();
		final LocalDate submissionDate = LocalDate.of(2025,7,3);

		final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
				.id(2L)
				.targetPeriodType(TargetPeriodType.TP6)
				.certificationPeriodType(CertificationPeriodType.CP7)
				.certificationBatchTriggerDate(LocalDate.of(2025, 7, 2))
				.startDate(LocalDate.of(2025, 7, 1))
				.endDate(LocalDate.of(2027, 3, 31))
				.build();

		when(certificationPeriodService.getCertificationPeriodById(statusUpdateDTO.getCertificationPeriodId()))
				.thenReturn(certificationPeriod);

		assertDoesNotThrow(() -> validationService
				.validateFacilityCertificationByCertificationPeriod(statusUpdateDTO, submissionDate));
	}

	@Test
	void validateFacilityCertificationByCertificationPeriod_throws_Before_Cert_Period() {

		final FacilityCertificationStatusUpdateDTO statusUpdateDTO = FacilityCertificationStatusUpdateDTO.builder()
				.certificationStatus(FacilityCertificationStatus.CERTIFIED)
				.startDate(LocalDate.of(2025,7,3))
				.certificationPeriodId(2L)
				.build();
		final LocalDate currentDate = LocalDate.of(2025, 6, 30);

		final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
				.targetPeriodType(TargetPeriodType.TP6)
				.certificationPeriodType(CertificationPeriodType.CP7)
				.certificationBatchTriggerDate(LocalDate.of(2025, 7, 2))
				.startDate(LocalDate.of(2025, 7, 1))
				.endDate(LocalDate.of(2027, 3, 31))
				.build();

		when(certificationPeriodService.getCertificationPeriodById(2L))
				.thenReturn(certificationPeriod);

		BusinessException result = assertThrows(BusinessException.class, () ->
				validationService
						.validateFacilityCertificationByCertificationPeriod(statusUpdateDTO, 																			currentDate));

		assertEquals(CERT_STATUS_UPDATE_BEFORE_CERT_PERIOD_START_ERROR, result.getErrorCode());
	}

	@Test
	void validateFacilityCertificationByCertificationPeriod_throws_Invalid_Cert_Date() {

		final FacilityCertificationStatusUpdateDTO statusUpdateDTO = FacilityCertificationStatusUpdateDTO.builder()
				.certificationStatus(FacilityCertificationStatus.CERTIFIED)
				.startDate(LocalDate.of(2025,6,3))
				.certificationPeriodId(2L)
				.build();
		final LocalDate currentDate = LocalDate.of(2025, 7, 30);

		final CertificationPeriodDTO certificationPeriod = CertificationPeriodDTO.builder()
				.targetPeriodType(TargetPeriodType.TP6)
				.certificationPeriodType(CertificationPeriodType.CP7)
				.certificationBatchTriggerDate(LocalDate.of(2025, 7, 2))
				.startDate(LocalDate.of(2025, 7, 1))
				.endDate(LocalDate.of(2027, 3, 31))
				.build();

		when(certificationPeriodService.getCertificationPeriodById(2L))
				.thenReturn(certificationPeriod);

		BusinessException result = assertThrows(BusinessException.class, () ->
				validationService
						.validateFacilityCertificationByCertificationPeriod(statusUpdateDTO,currentDate));

		assertEquals(FACILITY_CERTIFICATION_START_DATE_OUTSIDE_PERIOD, result.getErrorCode());
	}
}