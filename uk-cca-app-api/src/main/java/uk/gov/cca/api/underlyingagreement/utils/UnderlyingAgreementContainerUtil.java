package uk.gov.cca.api.underlyingagreement.utils;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

@UtilityClass
public class UnderlyingAgreementContainerUtil {

    public Optional<Cca3FacilityBaselineAndTargets> getFacilityBaselineAndTargets(final String facilityBusinessId, final UnderlyingAgreementContainer una) {
        return getUnaFacility(facilityBusinessId, una).map(FacilityItem::getCca3BaselineAndTargets);
    }

    public Optional<FacilityItem> getUnaFacility(final String facilityBusinessId, final UnderlyingAgreementContainer una) {
        return Stream.of(
                        una.getUnderlyingAgreement().getFacilities(),
                        una.getExcludedFacilities()
                ).flatMap(Collection::stream)
                .filter(f -> f.getFacilityItem().getFacilityId().equals(facilityBusinessId)
                        && f.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions().contains(SchemeVersion.CCA_3)
                )
                .findFirst().map(Facility::getFacilityItem);
    }
}
