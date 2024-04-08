package uk.gov.cca.api.account;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.account.domain.Account;
import uk.gov.cca.api.account.domain.enumeration.AccountStatus;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class TestAccount extends Account {
    @Override
    public AccountStatus getStatus() {
        return null;
    }
}
