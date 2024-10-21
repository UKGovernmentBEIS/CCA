package uk.gov.cca.api.authorization.ccaauth.rules.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.Permission;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorAssociationAuthorizationServiceTest {

    @InjectMocks
    private SectorAssociationAuthorizationService sectorAssociationAuthorizationService;

    @Mock
    private SectorAssociationAuthorizationServiceDelegator authorizationServiceDelegator;

    @Test
    void authorize_does_not_throw_exception_when_no_permission() {
        Long sectorAssociationId = 1L;
        AppUser user = AppUser.builder().build();

        when(authorizationServiceDelegator.isAuthorized(user, sectorAssociationId)).thenReturn(true);

        assertDoesNotThrow(() -> sectorAssociationAuthorizationService.authorize(user, sectorAssociationId, null));
        verify(authorizationServiceDelegator, times(1)).isAuthorized(user, sectorAssociationId);
    }

    @Test
    void authorize_throws_exception_when_no_permission() {
        Long sectorAssociationId = 1L;
        AppUser user = AppUser.builder().build();

        when(authorizationServiceDelegator.isAuthorized(user, sectorAssociationId)).thenReturn(false);

        BusinessException be = assertThrows(BusinessException.class,
                () -> sectorAssociationAuthorizationService.authorize(user, sectorAssociationId, null));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);

        verify(authorizationServiceDelegator, times(1)).isAuthorized(user, sectorAssociationId);
    }

    @Test
    void authorize_does_not_throw_exception_when_permission_provided() {
        Long sectorAssociationId = 1L;
        String permission = Permission.PERM_TASK_ASSIGNMENT;
        AppUser user = AppUser.builder().build();

        when(authorizationServiceDelegator.isAuthorized(user, sectorAssociationId, permission)).thenReturn(true);

        assertDoesNotThrow(() -> sectorAssociationAuthorizationService.authorize(user, sectorAssociationId, permission));
        verify(authorizationServiceDelegator, times(1)).isAuthorized(user, sectorAssociationId, permission);
    }
}
