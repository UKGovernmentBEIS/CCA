package uk.gov.cca.api.authorization.rules.services.authorization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.domain.ResourceType;
import uk.gov.cca.api.authorization.rules.services.authorization.AppAuthorizationService;
import uk.gov.cca.api.authorization.rules.services.authorization.AppResourceAuthorizationServiceDelegator;
import uk.gov.cca.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppAuthorizationServiceTest {
    @InjectMocks
    private AppAuthorizationService appAuthorizationService;

    @Mock
    private AppResourceAuthorizationServiceDelegator appResourceAuthorizationServiceDelegator;

    @Test
    void isAuthorized_criteria_with_account_only() {
        AppUser user = AppUser.builder().roleType(RoleType.OPERATOR).build();
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .accountId(1L)
            .build();

        when(appResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.ACCOUNT, user, criteria))
            .thenReturn(true);

        appAuthorizationService.authorize(user, criteria);

        verify(appResourceAuthorizationServiceDelegator, times(1))
            .isAuthorized(ResourceType.ACCOUNT, user, criteria);
    }

    @Test
    void isAuthorized_criteria_with_competent_authority_only() {
        AppUser user = AppUser.builder().roleType(RoleType.REGULATOR).build();
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .build();

        when(appResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.CA, user, criteria))
            .thenReturn(true);

        appAuthorizationService.authorize(user, criteria);

        verify(appResourceAuthorizationServiceDelegator, times(1))
            .isAuthorized(ResourceType.CA, user, criteria);
    }

    @Test
    void isAuthorized_creiteria_with_verification_body_only() {
        AppUser user = AppUser.builder().roleType(RoleType.VERIFIER).build();
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .verificationBodyId(1L)
            .build();

        when(appResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.VERIFICATION_BODY, user, criteria))
            .thenReturn(true);

        appAuthorizationService.authorize(user, criteria);

        verify(appResourceAuthorizationServiceDelegator, times(1))
            .isAuthorized(ResourceType.VERIFICATION_BODY, user, criteria);
    }

    @Test
    void isAuthorized_operator_user() {
        AppUser user = AppUser.builder().roleType(RoleType.OPERATOR).build();
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .accountId(1L)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .build();

        when(appResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.ACCOUNT, user, criteria))
            .thenReturn(true);

        appAuthorizationService.authorize(user, criteria);

        verify(appResourceAuthorizationServiceDelegator, times(1))
            .isAuthorized(ResourceType.ACCOUNT, user, criteria);
    }

    @Test
    void isAuthorized_regulator_user() {
        AppUser user = AppUser.builder().roleType(RoleType.REGULATOR).build();
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .accountId(1L)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .build();

        when(appResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.CA, user, criteria))
            .thenReturn(true);

        appAuthorizationService.authorize(user, criteria);

        verify(appResourceAuthorizationServiceDelegator, times(1))
            .isAuthorized(ResourceType.CA, user, criteria);
    }

    @Test
    void isAuthorized_verifier_user() {
        AppUser user = AppUser.builder().roleType(RoleType.VERIFIER).build();
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .accountId(1L)
            .verificationBodyId(2L)
            .build();

        when(appResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.VERIFICATION_BODY, user, criteria))
            .thenReturn(true);

        appAuthorizationService.authorize(user, criteria);

        verify(appResourceAuthorizationServiceDelegator, times(1))
            .isAuthorized(ResourceType.VERIFICATION_BODY, user, criteria);
    }

    @Test
    void isAuthorized_throws_forbidden() {
        AppUser user = AppUser.builder().roleType(RoleType.OPERATOR).build();
        AuthorizationCriteria criteria = AuthorizationCriteria
            .builder()
            .accountId(1L)
            .build();

        when(appResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.ACCOUNT, user, criteria))
            .thenReturn(false);

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> appAuthorizationService.authorize(user, criteria));

        assertEquals(ErrorCode.FORBIDDEN, businessException.getErrorCode());

        verify(appResourceAuthorizationServiceDelegator, times(1))
            .isAuthorized(ResourceType.ACCOUNT, user, criteria);
    }

}