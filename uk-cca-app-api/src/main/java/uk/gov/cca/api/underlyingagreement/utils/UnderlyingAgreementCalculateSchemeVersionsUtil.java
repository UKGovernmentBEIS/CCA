package uk.gov.cca.api.underlyingagreement.utils;


import java.util.Set;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.SetUtils;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;

@UtilityClass
public class UnderlyingAgreementCalculateSchemeVersionsUtil {

	public Set<SchemeVersion> calculateSchemeVersionsFromActiveFacilities(Set<Facility> facilities) {
		return facilities.stream()
				.filter(facility -> !facility.getStatus().equals(FacilityStatus.EXCLUDED))
				.map(f -> f.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions())
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
	}

	public Set<SchemeVersion> calculateTerminatedSchemeVersionsFromFacilities(Set<Facility> facilities) {
		Set<SchemeVersion> versionsFromExcluded = facilities.stream()
				.filter(facility -> facility.getStatus().equals(FacilityStatus.EXCLUDED))
				.map(f -> f.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions())
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
		Set<SchemeVersion> versionsFromActive = calculateSchemeVersionsFromActiveFacilities(facilities);

		return SetUtils.difference(versionsFromExcluded, versionsFromActive);
	}
}
