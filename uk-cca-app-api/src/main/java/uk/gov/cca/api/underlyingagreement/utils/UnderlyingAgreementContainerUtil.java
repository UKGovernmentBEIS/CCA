package uk.gov.cca.api.underlyingagreement.utils;

import lombok.experimental.UtilityClass;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

@UtilityClass
public class UnderlyingAgreementContainerUtil {

    public Optional<Cca3FacilityBaselineAndTargets> getFacilityBaselineAndTargets(final String facilityBusinessId, final UnderlyingAgreementContainer una) {
        return Stream.of(
                        una.getUnderlyingAgreement().getFacilities(),
                        una.getExcludedFacilities()
                ).flatMap(Collection::stream)
                .filter(f -> f.getFacilityItem().getFacilityId().equals(facilityBusinessId)
                        && f.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions().contains(SchemeVersion.CCA_3)
                )
                .findFirst().map(f -> f.getFacilityItem().getCca3BaselineAndTargets());
    }
}
