package uk.gov.cca.api.authorization.rules.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;

@Service
@RequiredArgsConstructor
public class AppUserAuthorizationService {

    private final AuthorizationRulesService authorizationRulesService;

    public void authorize(AppUser appUser, String serviceName) {
        authorizationRulesService.evaluateRules(appUser, serviceName);
    }

    public void authorize(AppUser appUser, String serviceName, String resourceId) {
        authorizationRulesService.evaluateRules(appUser, serviceName, resourceId);
    }

    public void authorize(AppUser appUser, String serviceName, String resourceId, String resourceSubType) {
        authorizationRulesService.evaluateRules(appUser, serviceName, resourceId, resourceSubType);
    }
}
