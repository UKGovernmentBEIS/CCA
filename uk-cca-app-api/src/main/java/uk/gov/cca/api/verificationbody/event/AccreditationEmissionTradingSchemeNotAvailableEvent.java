package uk.gov.cca.api.verificationbody.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.EmissionTradingScheme;

import java.util.Set;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class AccreditationEmissionTradingSchemeNotAvailableEvent {

    private final Long verificationBodyId;
    private final Set<EmissionTradingScheme> notAvailableAccreditationEmissionTradingSchemes;
}
