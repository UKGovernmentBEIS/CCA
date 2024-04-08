package uk.gov.cca.api.authorization.rules.services.authorization;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.function.BiPredicate;

import static uk.gov.cca.api.authorization.rules.domain.ResourceType.ACCOUNT;
import static uk.gov.cca.api.authorization.rules.domain.ResourceType.CA;
import static uk.gov.cca.api.authorization.rules.domain.ResourceType.VERIFICATION_BODY;

@Service
@RequiredArgsConstructor
public class AppAuthorizationService {

    private final AppResourceAuthorizationServiceDelegator resourceAuthorizationServiceDelegator;

    /**
     * Authorizes user based on {@link AuthorizationCriteria} and {@link RoleType}.
     * @param user the authenticated user
     * @param authorizationCriteria the {@link AuthorizationCriteria} based on which criteria the authorization is performed on.
     * @throws BusinessException {@link ErrorCode} FORBIDDEN if authorization fails.
     */
    public void authorize(AppUser user, AuthorizationCriteria authorizationCriteria) {
        boolean isAuthorized = resourceAuthorizationServiceDelegator
            .isAuthorized(getResourceType(user, authorizationCriteria), user, authorizationCriteria);

        if (!isAuthorized) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    private String getResourceType(AppUser user, AuthorizationCriteria authorizationCriteria) {
        String resourceType = null;
        if (checkAccountAccess().test(user, authorizationCriteria)) {
            resourceType = ACCOUNT;
        } else if (checkCompetentAuthorityAccess().test(user, authorizationCriteria)) {
            resourceType = CA;
        } else if (checkVerificationBodyAccess().test(user, authorizationCriteria)) {
            resourceType = VERIFICATION_BODY;
        }

        return resourceType;
    }

    private BiPredicate<AppUser, AuthorizationCriteria> checkAccountAccess() {
        return accountOnlyCriteria().or(operatorUserRole());
    }

    private BiPredicate<AppUser, AuthorizationCriteria> checkCompetentAuthorityAccess() {
        return competentAuthorityOnlyCriteria().or(regulatorUserRole());
    }

    private BiPredicate<AppUser, AuthorizationCriteria> checkVerificationBodyAccess() {
        return verificationBodyOnlyCriteria().or(verifierUserRole());
    }

    private BiPredicate<AppUser, AuthorizationCriteria> accountOnlyCriteria() {
        return (user, criteria) -> ObjectUtils.isNotEmpty(criteria.getAccountId())
            && criteria.getCompetentAuthority() == null
            && ObjectUtils.isEmpty(criteria.getVerificationBodyId());
    }

    private BiPredicate<AppUser, AuthorizationCriteria> competentAuthorityOnlyCriteria() {
        return (user, criteria) -> ObjectUtils.isNotEmpty(criteria.getAccountId())
            && criteria.getCompetentAuthority() != null
            && ObjectUtils.isEmpty(criteria.getVerificationBodyId());
    }

    private BiPredicate<AppUser, AuthorizationCriteria> verificationBodyOnlyCriteria() {
        return (user, criteria) -> ObjectUtils.isEmpty(criteria.getAccountId())
            && criteria.getCompetentAuthority() == null
            && ObjectUtils.isNotEmpty(criteria.getVerificationBodyId());
    }

    private BiPredicate<AppUser, AuthorizationCriteria> operatorUserRole() {
        return (user, criteria) -> user.getRoleType() == RoleType.OPERATOR;
    }

    private BiPredicate<AppUser, AuthorizationCriteria> regulatorUserRole() {
        return (user, criteria) -> user.getRoleType() == RoleType.REGULATOR;
    }

    private BiPredicate<AppUser, AuthorizationCriteria> verifierUserRole() {
        return (user, criteria) -> user.getRoleType() == RoleType.VERIFIER;
    }
}
