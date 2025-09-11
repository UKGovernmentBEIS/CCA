package uk.gov.cca.api.targetperiodreporting.facilitycertification.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.CertificationPeriodService;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FacilityCertificationValidationService {

	private final CertificationPeriodService certificationPeriodService;

	public void validateFacilityCertificationByCertificationPeriod(FacilityCertificationStatusUpdateDTO statusUpdateDTO,
	                                                               LocalDate submissionDate) {
		final CertificationPeriodDTO certificationPeriod = certificationPeriodService
				.getCertificationPeriodById(statusUpdateDTO.getCertificationPeriodId());

		this.validateSubmissionDateIsAfterCertificationPeriodStart(certificationPeriod, submissionDate);
		if(statusUpdateDTO.getCertificationStatus().equals(FacilityCertificationStatus.CERTIFIED)) {
			this.validateCertifiedStartDateWithinCertificationPeriod(certificationPeriod, statusUpdateDTO.getStartDate());
		}
	}

	private void validateSubmissionDateIsAfterCertificationPeriodStart(CertificationPeriodDTO certificationPeriod,
	                                                                   LocalDate submissionDate) {
		// the current date must be after or equal CP start date
		if(submissionDate.isBefore(certificationPeriod.getStartDate())) {
			throw new BusinessException(CcaErrorCode.CERT_STATUS_UPDATE_BEFORE_CERT_PERIOD_START_ERROR);
		}
	}

	private void validateCertifiedStartDateWithinCertificationPeriod(CertificationPeriodDTO certificationPeriod,
	                                                                 LocalDate submittedStartDate) {
		// the submittedStartDate must fall within the CP deadlines
		if (submittedStartDate.isBefore(certificationPeriod.getStartDate()) ||
				submittedStartDate.isAfter(certificationPeriod.getEndDate())) {
			throw new BusinessException(CcaErrorCode.FACILITY_CERTIFICATION_START_DATE_OUTSIDE_PERIOD);
		}

	}
}
