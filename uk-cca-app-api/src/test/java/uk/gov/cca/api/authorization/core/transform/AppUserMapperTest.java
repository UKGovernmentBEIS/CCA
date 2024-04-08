package uk.gov.cca.api.authorization.core.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.AccessToken;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.core.domain.AppAuthority;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Map;

import static uk.gov.cca.api.authorization.core.domain.Permission.PERM_ACCOUNT_USERS_EDIT;
import static uk.gov.cca.api.authorization.core.domain.Permission.PERM_TASK_ASSIGNMENT;


@ExtendWith(MockitoExtension.class)
class AppUserMapperTest {

    @Test
    void toappUser() {
        AuthorityDTO accountAuthority = buildAccountAuthority();
        AuthorityDTO caAuthority = buildCaAuthority();
        AccessToken accessToken = buildAccessToken();
        RoleType roleType = RoleType.OPERATOR;

        AppUser expectedUser = getExpectedappUser(accountAuthority, caAuthority, accessToken, roleType);

        //appUser appUser = appUserMapper.toappUser(accessToken, List.of(accountAuthority, caAuthority), roleType);

        //assertEquals(expectedappUser, appUser);
    }

    @Test
    void toappUser_no_authorities() {
        AccessToken accessToken = buildAccessToken();
        RoleType roleType = RoleType.OPERATOR;

        AppUser expectedUser = AppUser.builder()
            .userId(accessToken.getSubject())
            .firstName(accessToken.getGivenName())
            .lastName(accessToken.getFamilyName())
            .email(accessToken.getEmail())
            .roleType(roleType)
            .build();

        //appUser appUser = appUserMapper.toappUser(accessToken, Collections.emptyList(), roleType);

        //assertEquals(expectedappUser, appUser);
    }

    private AppUser getExpectedappUser(AuthorityDTO accountAuthority, AuthorityDTO caAuthority, AccessToken accessToken, RoleType roleType) {
        return AppUser.builder()
                .userId(accessToken.getSubject())
                .firstName(accessToken.getGivenName())
                .lastName(accessToken.getFamilyName())
                .email(accessToken.getEmail())
                .roleType(roleType)
                .authorities(List.of(
                        AppAuthority.builder()
                                .code(accountAuthority.getCode())
                                .accountId(accountAuthority.getAccountId())
                                .permissions(accountAuthority.getAuthorityPermissions())
                                .build(),
                        AppAuthority.builder()
                                .code(caAuthority.getCode())
                                .competentAuthority(caAuthority.getCompetentAuthority())
                                .permissions(caAuthority.getAuthorityPermissions())
                                .build()))
                .build();
    }

    private AuthorityDTO buildCaAuthority() {
        return AuthorityDTO.builder()
                .code("code2")
                .competentAuthority(CompetentAuthorityEnum.SCOTLAND)
                .authorityPermissions(List.of(PERM_ACCOUNT_USERS_EDIT,
                        PERM_TASK_ASSIGNMENT))
                .build();
    }

    private AuthorityDTO buildAccountAuthority() {
        return AuthorityDTO.builder()
                .code("code1")
                .accountId(1L)
                .authorityPermissions(List.of(PERM_ACCOUNT_USERS_EDIT,
                        PERM_TASK_ASSIGNMENT))
                .build();
    }

    private AccessToken buildAccessToken() {
        Map<String, String> token = Map.of("sub", "userId", "email", "user@email.com", "given_name", "name", "family_name", "surname");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(token, AccessToken.class);
    }
}