package uk.gov.cca.api.authorization.core.domain.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uk.gov.cca.api.authorization.core.service.RoleCodeValidator;
import uk.gov.netz.api.common.domain.RoleType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation for role code validation.
 */
@Constraint(validatedBy = RoleCodeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RoleCode {

    String message() default "Invalid role code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    RoleType roleType();
}
