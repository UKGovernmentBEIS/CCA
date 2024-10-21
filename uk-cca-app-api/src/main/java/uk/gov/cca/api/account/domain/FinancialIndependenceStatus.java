package uk.gov.cca.api.account.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FinancialIndependenceStatus {

    NON_FINANCIALLY_INDEPENDENT("Non-financially independent"),
    FINANCIALLY_INDEPENDENT("Financially independent");

    private final String name;
}
