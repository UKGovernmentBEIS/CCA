package uk.gov.cca.api.authorization.rules.services.authorization;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.domain.ResourceType;

@Service
@RequiredArgsConstructor
public class AppVerificationBodyAuthorizationService implements AppResourceAuthorizationService {

    private final VerificationBodyAuthorizationServiceDelegator verificationBodyAuthorizationService;

    @Override
    public boolean isAuthorized(AppUser user, AuthorizationCriteria criteria) {
        boolean isAuthorized;
        if (ObjectUtils.isEmpty(criteria.getPermission())) {
            isAuthorized = verificationBodyAuthorizationService.isAuthorized(user, criteria.getVerificationBodyId());
        } else {
            isAuthorized = verificationBodyAuthorizationService.isAuthorized(user, criteria.getVerificationBodyId(), criteria.getPermission());
        }

        return isAuthorized;
    }

    @Override
    public String getResourceType() {
        return ResourceType.VERIFICATION_BODY;
    }
}
