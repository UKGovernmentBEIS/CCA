package uk.gov.cca.api.authorization.rules.services.authorization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.Permission;
import uk.gov.cca.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.common.domain.RoleType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppVerificationBodyAuthorizationServiceTest {

    @InjectMocks
    private AppVerificationBodyAuthorizationService appVerificationBodyAuthorizationService;

    @Mock
    private VerificationBodyAuthorizationServiceDelegator verificationBodyAuthorizationServiceDelegator;

    private final AppUser user = AppUser.builder().roleType(RoleType.VERIFIER).build();

    @Test
    void isAuthorized_no_permission() {
        Long verificationBodyId = 1L;
        AuthorizationCriteria criteria = AuthorizationCriteria.builder().verificationBodyId(verificationBodyId).build();

        when(verificationBodyAuthorizationServiceDelegator.isAuthorized(user, verificationBodyId)).thenReturn(true);

        assertTrue(appVerificationBodyAuthorizationService.isAuthorized(user, criteria));
        verify(verificationBodyAuthorizationServiceDelegator, times(1)).isAuthorized(user, verificationBodyId);
    }

    @Test
    void isAuthorized_with_permission() {
        Long verificationBodyId = 1L;
        String permission = Permission.PERM_CA_USERS_EDIT;
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .verificationBodyId(verificationBodyId)
            .permission(permission)
            .build();

        when(verificationBodyAuthorizationServiceDelegator.isAuthorized(user, verificationBodyId, permission))
            .thenReturn(true);

        assertTrue(appVerificationBodyAuthorizationService.isAuthorized(user, criteria));
        verify(verificationBodyAuthorizationServiceDelegator, times(1))
            .isAuthorized(user, verificationBodyId, permission);
    }

    @Test
    void getResourceType() {
        assertEquals(ResourceType.VERIFICATION_BODY, appVerificationBodyAuthorizationService.getResourceType());
    }
}