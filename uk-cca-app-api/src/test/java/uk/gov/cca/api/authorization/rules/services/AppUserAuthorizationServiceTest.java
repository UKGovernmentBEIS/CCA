package uk.gov.cca.api.authorization.rules.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppAuthority;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.AuthorizationRulesService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AppUserAuthorizationServiceTest {

    @InjectMocks
    private AppUserAuthorizationService appUserAuthorizationService;

    @Mock
    private AuthorizationRulesService authorizationRulesService;

    @Test
    void authorize_no_resource() {
        String serviceName = "serviceName";
        AppUser appUser = AppUser.builder().build();
        List<AppAuthority> authorities = List.of(
            AppAuthority.builder().accountId(1L).build()
        );
        appUser.setAuthorities(authorities);

        assertDoesNotThrow(() -> appUserAuthorizationService.authorize(appUser, serviceName));

        verify(authorizationRulesService, times(1)).evaluateRules(appUser, serviceName);
    }

    @Test
    void authorize_no_resource_sub_type() {
        String serviceName = "serviceName";
        String resourceId = "resourceId";
        AppUser appUser = AppUser.builder().build();
        List<AppAuthority> authorities = List.of(
            AppAuthority.builder().accountId(1L).build()
        );
        appUser.setAuthorities(authorities);

        appUserAuthorizationService.authorize(appUser, serviceName, resourceId);

        verify(authorizationRulesService, times(1)).evaluateRules(appUser, serviceName, resourceId);
    }

    @Test
    void authorize_with_resource_and_resource_sub_type() {
        String serviceName = "serviceName";
        String resourceId = "resourceId";
        String resourceSubType = "resourceSubType";

        AppUser appUser = AppUser.builder().build();
        List<AppAuthority> authorities = List.of(
            AppAuthority.builder().accountId(1L).build()
        );
        appUser.setAuthorities(authorities);

        appUserAuthorizationService.authorize(appUser, serviceName, resourceId, resourceSubType);

        verify(authorizationRulesService, times(1))
            .evaluateRules(appUser, serviceName, resourceId, resourceSubType);
    }
    
    @Test
    void authorize_installation_create_request_action() {
        String serviceName = "serviceName";
        String resourceId = null;
        String resourceSubType = "requestCreateActionType";

        AppUser appUser = AppUser.builder().userId("user").build();

        appUserAuthorizationService.authorize(appUser, serviceName, resourceId, resourceSubType);

        verify(authorizationRulesService, times(1))
            .evaluateRules(appUser, serviceName, resourceId, resourceSubType);
    }

}