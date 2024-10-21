package uk.gov.cca.api.underlyingagreement.domain.baselinetargets;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AgreementCompositionType {
    ABSOLUTE("Absolute"),
    RELATIVE("Relative"),
    NOVEM("Novem");

    private final String description;
}
