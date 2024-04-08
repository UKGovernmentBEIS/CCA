package uk.gov.cca.api.web.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.authorization.rules.services.AppUserAuthorizationService;
import uk.gov.netz.api.common.utils.SpELParser;

import java.lang.reflect.Method;

/**
 * Aspect triggered {@link Before} {@link AuthorizedAspect} annotated methods.
 * Retrieves:
 * <ul>
 *     <li>resourceId based on {@link AuthorizedAspect} parameters</li>
 *     <li>resourceSubType on {@link AuthorizedAspect} parameters </li>
 *     <li>serviceName the annotated method name</li>
 *     <li>{@link AppUser} from annotated method parameters</li>
 * </ul>
 * Calls {@link AppUserAuthorizationService} to evaluate authorization.
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizedAspect {

    private final AppUserAuthorizationService appUserAuthorizationService;
    private final AuthorizationAspectUserResolver authorizationAspectUserResolver;

    @Before("@annotation(uk.gov.cca.api.web.security.Authorized)")
    public void authorize(JoinPoint joinPoint) {
        String serviceName = getServiceName(joinPoint);
        String resourceId = getResourceId(joinPoint);
        String resourceSubType = getResourceSubType(joinPoint);
        AppUser user = authorizationAspectUserResolver.getUser(joinPoint);
        
        if (!StringUtils.isEmpty(resourceSubType)) {
            appUserAuthorizationService.authorize(user, serviceName, resourceId, resourceSubType);
        } else if (!StringUtils.isEmpty(resourceId)) {
            appUserAuthorizationService.authorize(user, serviceName, resourceId);
        } else {
            appUserAuthorizationService.authorize(user, serviceName);
        }
    }

    private String getResourceId(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Authorized authorized = method.getAnnotation(Authorized.class);
        return SpELParser.parseExpression(authorized.resourceId(), signature.getParameterNames(), joinPoint.getArgs(), String.class);
    }

    private String getServiceName(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod().getName();
    }

    private String getResourceSubType(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Authorized authorized = method.getAnnotation(Authorized.class);
        return SpELParser.parseExpression(authorized.resourceSubType(), signature.getParameterNames(), joinPoint.getArgs(), String.class);
    }
}
