package uk.gov.cca.api.authorization.operator.service;

import uk.gov.cca.api.authorization.core.domain.Authority;

public interface OperatorAuthorityDeleteValidator {

    void validateDeletion(Authority authority);
}