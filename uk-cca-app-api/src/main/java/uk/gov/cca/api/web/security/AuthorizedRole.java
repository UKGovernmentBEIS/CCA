package uk.gov.cca.api.web.security;

import uk.gov.netz.api.common.domain.RoleType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for authorization based on the {@link RoleType}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthorizedRole {

    /**
     * The permitted {@link RoleType}.
     */
    RoleType[] roleType();
}
