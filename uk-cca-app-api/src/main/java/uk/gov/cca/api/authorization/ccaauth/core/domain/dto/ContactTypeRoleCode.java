package uk.gov.cca.api.authorization.ccaauth.core.domain.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uk.gov.cca.api.authorization.ccaauth.core.service.ContactTypeValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation for contact type validation.
 */
@Constraint(validatedBy = ContactTypeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ContactTypeRoleCode {

    String message() default "Invalid contact type for the operator user";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String roleType();
}
