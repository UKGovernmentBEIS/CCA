package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.service.SchemeTerminationHelper;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.netz.api.common.exception.BusinessException;

@Log4j2
@Service
@RequiredArgsConstructor
public class UnderlyingAgreementHandleCca2FacilitiesAfterTerminationDateService {

	private final SchemeTerminationHelper schemeTerminationHelper;
	
	public UnderlyingAgreement handleCca2FacilitiesAfterTerminationDate(UnderlyingAgreement underlyingAgreement) {
		if (schemeTerminationHelper.isAfterCca2SchemeTerminationDate()) {
			Set<Facility> facilities = underlyingAgreement.getFacilities();
			
			// User can only cancel the workflow if only CCA2 and no other facilities exist
			boolean onlyCca2 = facilities.stream().allMatch(this::isCca2Only);
			if (onlyCca2) {
				log.error("No facilities found for current scheme {}", facilities);
				throw new BusinessException(CcaErrorCode.NO_FACILITIES_FOUND_FOR_CURRENT_SCHEME);
			}
				
        	// Close CCA2 only facilities that are LIVE, applicable for variation
			facilities.stream()
				.filter(this::isCca2Only)
        	    .filter(f -> FacilityStatus.LIVE.equals(f.getStatus()))
        	    .forEach(f -> {
        	    	f.setExcludedDate(schemeTerminationHelper.getCca2TerminationDate());
        	    	f.setStatus(FacilityStatus.EXCLUDED);
        	    });

        	// Remove CCA2 only facilities that are NEW
			facilities.removeIf(f -> isCca2Only(f) && FacilityStatus.NEW.equals(f.getStatus()));
		}
		return underlyingAgreement;
	}
    
    private boolean isCca2Only(Facility f) {
        Set<SchemeVersion> versions = getSchemeVersions(f);
        return Set.of(SchemeVersion.CCA_2).equals(versions);
    }

    private Set<SchemeVersion> getSchemeVersions(Facility f) {
        return f.getFacilityItem()
                .getFacilityDetails()
                .getParticipatingSchemeVersions();
    }
}
