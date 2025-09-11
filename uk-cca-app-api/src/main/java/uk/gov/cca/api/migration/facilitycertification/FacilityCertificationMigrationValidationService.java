package uk.gov.cca.api.migration.facilitycertification;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.facility.domain.dto.FacilityDataDetailsDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.service.FacilityCertificationService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.CertificationPeriodService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class FacilityCertificationMigrationValidationService {
    
    private static final String VALIDATION_ERROR = "Validation Error: ";
    
    private final FacilityDataQueryService facilityDataQueryService;
    private final CertificationPeriodService certificationPeriodService;
    private final FacilityCertificationService facilityCertificationService;
    
    
    private static String validationError(String message, Object... args) {
        return VALIDATION_ERROR + String.format(message, args);
    }
    
    public List<String> validate(FacilityCertificationVO vo) {
        List<String> errors = new ArrayList<>();
        CertificationPeriodDTO certificationPeriod;
        
        // Rule: Certification Period must be valid
        try {
            certificationPeriod = certificationPeriodService
                    .getCertificationPeriodByType(vo.getCertificationPeriodType());
            
            vo.setCertificationPeriodId(certificationPeriod.getId());
        } catch (Exception e) {
            errors.add(validationError("Certification Period %s does not exist.", vo.getCertificationPeriodType()));
            return errors;
        }
        
        // Rule: Facility ID must exist
        try {
            FacilityDataDetailsDTO facilityData = facilityDataQueryService.getFacilityData(vo.getFacilityId());
            vo.setId(facilityData.getId());
        } catch (Exception e) {
            errors.add(validationError("Facility ID %s does not exist.", vo.getFacilityId()));
            return errors;
        }
        
        // Rule: Migrate Facility Certification Status only if not already set
        if (facilityCertificationService.existsFacilityCertificationByFacilityIdAndCertificationPeriodId(vo.getId(), vo.getCertificationPeriodId())) {
            errors.add(validationError("Facility ID %s has already defined Certification Status.", vo.getFacilityId()));
            return errors;
        }
        
        // Rule: Certified status must have a date
        FacilityCertificationStatus status = vo.getCertificationStatus();
        LocalDate startDate = vo.getStartDate();
        
        if (status == FacilityCertificationStatus.CERTIFIED) {
            if (startDate == null) {
                errors.add(validationError(
                        "Facility ID %s is certified but no certification start date is provided.", vo.getFacilityId()));
            } else {
                // Rule: Start Date must be within CP range
                if (startDate.isBefore(certificationPeriod.getStartDate()) || startDate.isAfter(certificationPeriod.getEndDate())) {
                    errors.add(validationError(
                            "Facility ID %s has certification start date out of %s range.",
                            vo.getFacilityId(),
                            vo.getCertificationPeriodType()));
                }
            }
        }
        
        // Rule: Decertified must not have a date
        if (status == FacilityCertificationStatus.DECERTIFIED && startDate != null) {
            errors.add(validationError(
                    "Facility ID %s is decertified but certification start date is present.", vo.getFacilityId()));
        }
        
        return errors;
    }
}
