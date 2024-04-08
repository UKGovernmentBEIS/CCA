package uk.gov.cca.api.account.service.validator;

import uk.gov.cca.api.account.domain.enumeration.AccountContactType;

import java.util.Map;

public interface AccountContactTypeUpdateValidator {
    
    void validateUpdate(Map<AccountContactType, String> contactTypes, Long accountId);
}
