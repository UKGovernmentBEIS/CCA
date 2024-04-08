package uk.gov.cca.api.account.service.validator;

import uk.gov.cca.api.account.domain.enumeration.AccountContactType;

import java.util.Map;

public interface AccountContactTypeDeleteValidator {

    void validateDelete(Map<AccountContactType, String> contactTypes);
}
