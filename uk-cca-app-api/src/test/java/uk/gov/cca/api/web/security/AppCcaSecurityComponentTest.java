package uk.gov.cca.api.web.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityDTO;
import uk.gov.cca.api.authorization.ccaauth.core.transform.AppCcaUserMapper;
import uk.gov.cca.api.common.domain.CcaRoleTypeConstants;
import uk.gov.netz.api.authorization.core.domain.Permission;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.security.config.SecurityConstants.CLAIM_ROLE_TYPE;

@ExtendWith(MockitoExtension.class)
class AppCcaSecurityComponentTest {

    @InjectMocks
    private AppCcaSecurityComponent appSecurityComponent;

    @Mock
    private AppCcaUserMapper userMapper;

    private SecurityContext securityContext;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        securityContext = mock(SecurityContext.class);
    }

    @Test
    void getAuthenticatedUser_regulator() {
        List<CcaAuthorityDTO> CcaAuthorityDTOS = List.of(CcaAuthorityDTO.builder()
                .code("code")
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build());

        Map<String, Object> claims = Map.of(JwtClaimNames.SUB, "userId",
                "email", "email",
                "given_name", "firstName",
                "family_name", "lastName",
                CLAIM_ROLE_TYPE, RoleTypeConstants.REGULATOR);
        authentication = new JwtAuthenticationToken(new Jwt("tokenValue", Instant.now(), Instant.MAX, Map.of("header", "header"), claims), CcaAuthorityDTOS);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
        appSecurityComponent.getAuthenticatedUser();

        verify(userMapper, times(1)).toAppUser("userId", "email", "firstName", "lastName", CcaAuthorityDTOS, RoleTypeConstants.REGULATOR);
    }


    @Test
    void getAuthenticatedUser_operator() {
        List<CcaAuthorityDTO> CcaAuthorityDTOS = List.of(CcaAuthorityDTO.builder()
                .code("code")
                .accountId(1L)
                .authorityPermissions(List.of(Permission.PERM_ACCOUNT_USERS_EDIT))
                .build());

        Map<String, Object> claims = Map.of(JwtClaimNames.SUB, "userId",
                "email", "email",
                "given_name", "firstName",
                "family_name", "lastName",
                CLAIM_ROLE_TYPE, RoleTypeConstants.OPERATOR);
        authentication = new JwtAuthenticationToken(new Jwt("tokenValue", Instant.now(), Instant.MAX, Map.of("header", "header"), claims), CcaAuthorityDTOS);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        appSecurityComponent.getAuthenticatedUser();

        verify(userMapper, times(1)).toAppUser("userId", "email", "firstName", "lastName", CcaAuthorityDTOS, RoleTypeConstants.OPERATOR);
    }

    @Test
    void getAuthenticatedUser_sectorUser() {
        List<CcaAuthorityDTO> CcaAuthorityDTOS = List.of(CcaAuthorityDTO.builder()
                .code("code")
                .accountId(1L)
                .sectorAssociationId(2L)
                .authorityPermissions(List.of(Permission.PERM_ACCOUNT_USERS_EDIT))
                .build());

        Map<String, Object> claims = Map.of(JwtClaimNames.SUB, "userId",
                "email", "email",
                "given_name", "firstName",
                "family_name", "lastName",
                CLAIM_ROLE_TYPE, CcaRoleTypeConstants.SECTOR_USER);
        authentication = new JwtAuthenticationToken(new Jwt("tokenValue", Instant.now(), Instant.MAX, Map.of("header", "header"), claims), CcaAuthorityDTOS);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        appSecurityComponent.getAuthenticatedUser();

        verify(userMapper, times(1)).toAppUser("userId", "email", "firstName", "lastName", CcaAuthorityDTOS, CcaRoleTypeConstants.SECTOR_USER);
    }

    @Test
    void getAuthenticatedUser_operator_authority_without_permission() {
        List<CcaAuthorityDTO> CcaAuthorityDTOS = List.of(CcaAuthorityDTO.builder()
                .code("code")
                .accountId(1L)
                .build());

        Map<String, Object> claims = Map.of(JwtClaimNames.SUB, "userId",
                "email", "email",
                "given_name", "firstName",
                "family_name", "lastName",
                CLAIM_ROLE_TYPE, RoleTypeConstants.OPERATOR);
        authentication = new JwtAuthenticationToken(new Jwt("tokenValue", Instant.now(), Instant.MAX, Map.of("header", "header"), claims), CcaAuthorityDTOS);

        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        appSecurityComponent.getAuthenticatedUser();

        verify(userMapper, times(1)).toAppUser("userId", "email", "firstName", "lastName", List.of(), RoleTypeConstants.OPERATOR);
    }

    @Test
    void getAuthenticatedUser_null_authenticated_user() {
        when(securityContext.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(securityContext);

        var result = appSecurityComponent.getAuthenticatedUser();
        assertThat(result).isNull();
        verifyNoInteractions(userMapper);
    }

    @Test
    void getAuthenticatedUser_unauthenticated_user() {
        Authentication authenticationMock = Mockito.mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.isAuthenticated()).thenReturn(false);

        SecurityContextHolder.setContext(securityContext);

        var result = appSecurityComponent.getAuthenticatedUser();
        assertThat(result).isNull();
        verifyNoInteractions(userMapper);
    }

    @Test
    void getAuthenticatedUser_anonymous_authenticated_user() {
        Authentication authenticationMock = Mockito.mock(AnonymousAuthenticationToken.class);
        when(securityContext.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.isAuthenticated()).thenReturn(true);

        SecurityContextHolder.setContext(securityContext);

        var result = appSecurityComponent.getAuthenticatedUser();
        assertThat(result).isNull();
        verifyNoInteractions(userMapper);
    }
}
