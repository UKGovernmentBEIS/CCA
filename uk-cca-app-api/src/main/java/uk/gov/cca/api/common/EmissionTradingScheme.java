package uk.gov.cca.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmissionTradingScheme {
    DUMMY_EMISSION_TRADING_SCHEME("DUMMY"),
    DUMMY_EMISSION_TRADING_SCHEME_2("DUMMY2")
    ;

    /** The description */
    private final String description;

}
