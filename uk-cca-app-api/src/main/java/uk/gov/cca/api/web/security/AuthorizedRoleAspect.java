package uk.gov.cca.api.web.security;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.netz.api.common.domain.RoleType;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizedRoleAspect {

    private final RoleAuthorizationService roleAuthorizationService;
    private final AuthorizationAspectUserResolver authorizationAspectUserResolver;

    @Before("@annotation(uk.gov.cca.api.web.security.AuthorizedRole)")
    public void authorize(JoinPoint joinPoint) {
        RoleType[] roleTypes = getRoleTypes(joinPoint);
        AppUser user = authorizationAspectUserResolver.getUser(joinPoint);
        roleAuthorizationService.evaluate(user, roleTypes);
    }

    private RoleType[] getRoleTypes(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        AuthorizedRole authorizedRole = method.getAnnotation(AuthorizedRole.class);
        return authorizedRole.roleType();
    }
}
