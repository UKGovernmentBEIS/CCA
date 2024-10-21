package uk.gov.cca.api.account.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.netz.api.common.domain.EmissionTradingScheme;

@Getter
@AllArgsConstructor
public enum CcaEmissionTradingScheme implements EmissionTradingScheme {

    DUMMY_EMISSION_TRADING_SCHEME("DUMMY_EMISSION_TRADING_SCHEME"),
    ;

    private final String description;

    @Override
    public String getName() {
        return null;
    }
}
