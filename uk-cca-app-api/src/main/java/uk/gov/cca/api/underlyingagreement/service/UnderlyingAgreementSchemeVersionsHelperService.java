package uk.gov.cca.api.underlyingagreement.service;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.config.Cca2TerminationConfig;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementSchemeVersionsHelperService {

	private final Cca2TerminationConfig cca2TerminationConfig;
	
	public Set<SchemeVersion> calculateSchemeVersionsFromActiveFacilities(Set<Facility> facilities) {
		return calculateSchemeVersionsFromActiveFacilitiesWithDate(facilities, LocalDate.now());
	}

	public Set<SchemeVersion> calculateTerminatedSchemeVersionsFromFacilities(Set<Facility> facilities) {
		Set<SchemeVersion> versionsFromExcluded = facilities.stream()
				.filter(facility -> facility.getStatus().equals(FacilityStatus.EXCLUDED))
				.map(f -> f.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions())
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
		Set<SchemeVersion> versionsFromActive = calculateSchemeVersionsFromActiveFacilitiesWithDate(facilities, LocalDate.now());

		return SetUtils.difference(versionsFromExcluded, versionsFromActive);
	}

	public boolean shouldShowTp5Tp6(UnderlyingAgreementContainer originalContainer, LocalDate requestCreationDate) {
	    final Set<SchemeVersion> schemeVersions = calculateSchemeVersionsFromActiveFacilitiesWithDate(
	    		originalContainer.getUnderlyingAgreement().getFacilities(), requestCreationDate);
	
	    // All live facilities are CCA3-only
	    if (!schemeVersions.contains(SchemeVersion.CCA_2)) {
	        return false;
	    }
	
	    final TargetPeriod5Details originalTp5 = originalContainer.getUnderlyingAgreement().getTargetPeriod5Details();
	    final TargetPeriod6Details originalTp6 = originalContainer.getUnderlyingAgreement().getTargetPeriod6Details();
	    // TP5/TP6 Relevant data exists
	    return originalTp5 != null && originalTp6 != null;
	}
	
	private Set<SchemeVersion> calculateSchemeVersionsFromActiveFacilitiesWithDate(Set<Facility> facilities, LocalDate date) {
		return facilities.stream()
				.filter(facility -> !facility.getStatus().equals(FacilityStatus.EXCLUDED))
				.map(f -> f.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions())
				.flatMap(Set::stream)
		        .filter(version -> !(date.isAfter(cca2TerminationConfig.getTerminationDate()) 
		        		&& version == SchemeVersion.CCA_2))
				.collect(Collectors.toSet());
	}
}
