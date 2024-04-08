package uk.gov.cca.api.authorization.rules.services.authorization;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.domain.ResourceType;
import uk.gov.cca.api.authorization.rules.services.authorization.AppAccountAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AppCompAuthAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AppResourceAuthorizationServiceDelegator;
import uk.gov.cca.api.authorization.rules.services.authorization.AppVerificationBodyAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AuthorizationCriteria;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class AppResourceAuthorizationServiceDelegatorTest {

    private AppResourceAuthorizationServiceDelegator appResourceAuthorizationServiceDelegator;
    private AppAccountAuthorizationService appAccountAuthorizationService;
    private AppCompAuthAuthorizationService appCompAuthAuthorizationService;
    private AppVerificationBodyAuthorizationService appVerificationBodyAuthorizationService;

    private final AppUser user = AppUser.builder().userId("user").build();
    private final AuthorizationCriteria criteria = AuthorizationCriteria.builder().build();

    @BeforeAll
    void beforeAll() {
        appAccountAuthorizationService = Mockito.mock(AppAccountAuthorizationService.class);
        appCompAuthAuthorizationService = Mockito.mock(AppCompAuthAuthorizationService.class);
        appVerificationBodyAuthorizationService = Mockito.mock(AppVerificationBodyAuthorizationService.class);
        appResourceAuthorizationServiceDelegator =
            new AppResourceAuthorizationServiceDelegator(List.of(appAccountAuthorizationService,
                    appCompAuthAuthorizationService, appVerificationBodyAuthorizationService));

        when(appAccountAuthorizationService.getResourceType()).thenReturn(ResourceType.ACCOUNT);
        when(appCompAuthAuthorizationService.getResourceType()).thenReturn(ResourceType.CA);
        when(appVerificationBodyAuthorizationService.getResourceType()).thenReturn(ResourceType.VERIFICATION_BODY);
    }

    @Test
    void isAuthorized_resource_account() {
        when(appAccountAuthorizationService.isAuthorized(user, criteria)).thenReturn(true);

        assertTrue(appResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.ACCOUNT, user, criteria));

        verify(appAccountAuthorizationService, times(1)).isAuthorized(user, criteria);
    }

    @Test
    void isAuthorized_resource_competent_authority() {
        when(appCompAuthAuthorizationService.isAuthorized(user, criteria)).thenReturn(true);

        assertTrue(appResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.CA, user, criteria));

        verify(appCompAuthAuthorizationService, times(1)).isAuthorized(user, criteria);
    }

    @Test
    void isAuthorized_resource_verification_body() {
        when(appVerificationBodyAuthorizationService.isAuthorized(user, criteria)).thenReturn(true);

        assertTrue(appResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.VERIFICATION_BODY, user, criteria));

        verify(appVerificationBodyAuthorizationService, times(1)).isAuthorized(user, criteria);
    }

    @Test
    void isAuthorized_resource_null() {
        assertFalse(appResourceAuthorizationServiceDelegator.isAuthorized(null, user, criteria));
    }
}