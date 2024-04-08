package uk.gov.cca.api.authorization.rules.services.authorization;

import uk.gov.cca.api.authorization.core.domain.AppUser;

import java.util.Set;

public interface VerifierAccountAccessService {
    Set<Long> findAuthorizedAccountIds(AppUser user);
}
