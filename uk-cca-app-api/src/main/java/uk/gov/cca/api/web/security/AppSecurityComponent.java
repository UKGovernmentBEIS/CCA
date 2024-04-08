package uk.gov.cca.api.web.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.cca.api.authorization.core.transform.AppUserMapper;
import uk.gov.netz.api.common.domain.RoleType;

import java.util.List;

import static uk.gov.netz.api.common.domain.RoleType.OPERATOR;
import static uk.gov.cca.api.web.constants.SecurityConstants.CLAIM_ROLE_TYPE;

/**
 * The AppSecurity extracting security acknowledge objects.
 */
@Component
@RequiredArgsConstructor
public class AppSecurityComponent {

    private final AppUserMapper userMapper;

    /**
     * Returns authorities permissions of authenticated user.
     *
     * @return List of {@link AuthorityDTO}
     */
    public AppUser getAuthenticatedUser() {
        Jwt jwt = getToken();
        RoleType roleType = jwt.getClaim(CLAIM_ROLE_TYPE);
        return userMapper.toAppUser(jwt.getClaimAsString(JwtClaimNames.SUB), jwt.getClaimAsString("email"), jwt.getClaimAsString("given_name"),
                jwt.getClaimAsString("family_name"), getAuthorities(roleType), roleType);
    }

    public String getAccessToken() {
        return getToken().getTokenValue();
    }

    private Jwt getToken() {
        return (Jwt)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private List<AuthorityDTO> getAuthorities(RoleType roleType) {
        return OPERATOR.equals(roleType) ? getOperatorUserAuthorities() : getUserAuthorities();
    }

    private List<AuthorityDTO> getUserAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(AuthorityDTO.class::cast)
                .toList();
    }

    private List<AuthorityDTO> getOperatorUserAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(AuthorityDTO.class::cast)
                .filter(authority -> !ObjectUtils.isEmpty(authority.getAuthorityPermissions()))
                .toList();
    }
}
