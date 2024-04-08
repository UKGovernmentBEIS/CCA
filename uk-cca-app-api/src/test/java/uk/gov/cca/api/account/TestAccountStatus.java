package uk.gov.cca.api.account;

import uk.gov.cca.api.account.domain.enumeration.AccountStatus;

public enum TestAccountStatus implements AccountStatus {
    DUMMY,
    DUMMY2,
    ;

    @Override
    public String getName() {
        return this.name();
    }
}
