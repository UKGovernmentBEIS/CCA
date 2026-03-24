package uk.gov.cca.api.underlyingagreement.service;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.SetUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.service.SchemeTerminationHelper;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementSchemeVersionsHelperService {

	private final SchemeTerminationHelper schemeTerminationHelper;
	
	public Set<SchemeVersion> calculateSchemeVersionsFromActiveFacilities(Set<Facility> facilities) {
		return facilities.stream()
				.filter(facility -> !facility.getStatus().equals(FacilityStatus.EXCLUDED))
				.map(f -> f.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions())
				.flatMap(Set::stream)
		        .filter(version -> !(schemeTerminationHelper.isCca2Terminated(Set.of(version))))
				.collect(Collectors.toSet());
	}

	public Set<SchemeVersion> calculateTerminatedSchemeVersionsFromFacilities(Set<Facility> facilities) {
		Set<SchemeVersion> versionsFromExcluded = calculateSchemeVersionsFromExcludedFacilities(facilities);
		Set<SchemeVersion> versionsFromActive = calculateSchemeVersionsFromActiveFacilities(facilities);

		return SetUtils.difference(versionsFromExcluded, versionsFromActive);
	}

	public boolean shouldShowCCA2BaselineAndTargets(UnderlyingAgreementContainer originalContainer, LocalDate requestCreationDate) {
	    final Set<SchemeVersion> schemeVersions = originalContainer.getUnderlyingAgreement().getFacilities().stream()
				.filter(facility -> !facility.getStatus().equals(FacilityStatus.EXCLUDED))
				.map(f -> f.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions())
				.flatMap(Set::stream)
		        .filter(version -> !(schemeTerminationHelper.isCca2Terminated(Set.of(version), requestCreationDate)))
				.collect(Collectors.toSet());
	
	    // All live facilities are CCA3-only
	    if (!schemeVersions.contains(SchemeVersion.CCA_2)) {
	        return false;
	    }
	
	    final TargetPeriod5Details originalTp5 = originalContainer.getUnderlyingAgreement().getTargetPeriod5Details();
	    final TargetPeriod6Details originalTp6 = originalContainer.getUnderlyingAgreement().getTargetPeriod6Details();
	    // TP5/TP6 Relevant data exists
	    return originalTp5 != null && originalTp6 != null;
	}
	
	private Set<SchemeVersion> calculateSchemeVersionsFromExcludedFacilities(Set<Facility> facilities) {
		return facilities.stream()
				.filter(facility -> facility.getStatus().equals(FacilityStatus.EXCLUDED))
				.map(f -> f.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions())
				.flatMap(Set::stream)
				.filter(version -> !(schemeTerminationHelper.isCca2Terminated(Set.of(version))))
				.collect(Collectors.toSet());
	}
}
