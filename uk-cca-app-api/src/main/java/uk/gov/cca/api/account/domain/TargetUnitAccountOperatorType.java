package uk.gov.cca.api.account.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TargetUnitAccountOperatorType {

    LIMITED_COMPANY("Limited company"),
    PARTNERSHIP("Partnership"),
    SOLE_TRADER("Sole trader"),
    NONE("None of the above");

    private final String name;
}
