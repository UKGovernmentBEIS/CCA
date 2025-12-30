package uk.gov.cca.api.web.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.CcaAuthorityDTO;
import uk.gov.cca.api.authorization.ccaauth.core.transform.AppCcaUserMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.netz.api.security.AppSecurityComponentProvider;

import java.util.List;

import static uk.gov.netz.api.common.constants.RoleTypeConstants.OPERATOR;
import static uk.gov.netz.api.security.config.SecurityConstants.CLAIM_ROLE_TYPE;

@Primary
@Component
@RequiredArgsConstructor
public class AppCcaSecurityComponent implements AppSecurityComponentProvider {


    private final AppCcaUserMapper userMapper;

    /**
     * Returns authorities permissions of authenticated user.
     *
     * @return List of {@link AuthorityDTO}
     */
    public AppUser getAuthenticatedUser() {
        Jwt jwt = getToken();
        if (jwt == null) {
            return null;
        }

        String roleType = jwt.getClaim(CLAIM_ROLE_TYPE);
        return userMapper.toAppUser(jwt.getClaimAsString(JwtClaimNames.SUB), jwt.getClaimAsString("email"), jwt.getClaimAsString("given_name"),
                jwt.getClaimAsString("family_name"), getAuthorities(roleType), roleType);
    }

    public String getAccessToken() {
        Jwt jwt = getToken();
        if (jwt == null) {
            return null;
        }

        return jwt.getTokenValue();
    }

    private Jwt getToken() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return (Jwt) authentication.getPrincipal();
    }

    private List<CcaAuthorityDTO> getAuthorities(String roleType) {
        return OPERATOR.equals(roleType) ? getOperatorUserAuthorities() : getUserAuthorities();
    }

    private List<CcaAuthorityDTO> getUserAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .map(CcaAuthorityDTO.class::cast)
            .toList();
    }

    private List<CcaAuthorityDTO> getOperatorUserAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(CcaAuthorityDTO.class::cast)
                .filter(authority -> !ObjectUtils.isEmpty(authority.getAuthorityPermissions()))
                .toList();
    }
}
