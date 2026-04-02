package uk.gov.cca.api.underlyingagreement.validation;

import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_AFTER_SCHEME_END_DATE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_PREVIOUS_FACILITY_ID;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_PREVIOUS_FACILITY_ID_CCA2_ONLY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.service.SchemeTerminationHelper;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.facility.domain.FacilityValidationContext;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementFacilitiesFinalizationValidatorService {

	private final FacilityDataQueryService facilityDataQueryService;
	private final SchemeTerminationHelper schemeTerminationHelper;
	
	public BusinessValidationResult validate(Set<Facility> facilities) {
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();
        Set<String> previousFacilityIds = facilities.stream()
				.map(facility -> facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId())
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
        Map<String, FacilityValidationContext> previousFacilityIdsContextMap = !previousFacilityIds.isEmpty() 
        		? facilityDataQueryService.getFacilityValidationContextByFacilityBusinessIds(previousFacilityIds) 
        				: new HashMap<>();
        
        facilities.forEach(facility -> {
			validateFacilitySchemeAfterCca2EndDate(violations, facility.getStatus(), facility.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions());
			validateExistingFacilityIds(facility, previousFacilityIdsContextMap, violations);
		});
        
        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
	
	private void validateFacilitySchemeAfterCca2EndDate(List<UnderlyingAgreementViolation> violations,
			FacilityStatus status, Set<SchemeVersion> schemeVersions) {
		if (schemeTerminationHelper.isCca2Terminated(schemeVersions)
				&& !FacilityStatus.EXCLUDED.equals(status)) {
        	violations.add(new UnderlyingAgreementViolation(Facility.class.getName(), INVALID_FACILITY_PARTICIPATING_SCHEME_VERSIONS_AFTER_SCHEME_END_DATE, schemeVersions));
        }
	}
	
	private void validateExistingFacilityIds(Facility facility, Map<String, FacilityValidationContext> facilityValidationContextMap, List<UnderlyingAgreementViolation> violations) {
		
		Optional.ofNullable(facility.getFacilityItem().getFacilityDetails().getPreviousFacilityId())
        .ifPresent(id -> {
        	boolean previousFacilityActive = (facilityValidationContextMap.get(id) != null && facilityValidationContextMap.get(id).getClosedDate() == null);
        	Set<SchemeVersion> previousFacilityParticipatingSchemeVersions = facilityValidationContextMap.get(id) != null 
        			? facilityValidationContextMap.get(id).getParticipatingSchemeVersions()
        					: Collections.emptySet();
            if (FacilityStatus.NEW.equals(facility.getStatus())) {
            	if (!previousFacilityActive) {
                	violations.add(new UnderlyingAgreementViolation(Facility.class.getName(), INVALID_PREVIOUS_FACILITY_ID, id));
                }
            	else if (schemeTerminationHelper.isCca2Terminated(previousFacilityParticipatingSchemeVersions)) {
            		violations.add(new UnderlyingAgreementViolation(Facility.class.getName(), INVALID_PREVIOUS_FACILITY_ID_CCA2_ONLY, id));
            	}
            }	
        });
    }
}
