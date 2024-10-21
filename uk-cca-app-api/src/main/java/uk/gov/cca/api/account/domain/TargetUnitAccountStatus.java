package uk.gov.cca.api.account.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.netz.api.account.domain.enumeration.AccountStatus;

@Getter
@AllArgsConstructor
public enum TargetUnitAccountStatus implements AccountStatus {

    NEW,
    LIVE,
    CANCELLED,
    TERMINATED,
    REJECTED;

    @Override
    public String getName() {
        return this.name();
    }
}
