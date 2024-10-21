package uk.gov.cca.api.authorization.ccaauth.core.service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.core.domain.ContactType;
import uk.gov.cca.api.authorization.ccaauth.core.domain.dto.ContactTypeRoleCode;

import java.util.Arrays;

/**
 * The role code validator.
 */
@RequiredArgsConstructor
public class ContactTypeValidator implements ConstraintValidator<ContactTypeRoleCode, ContactType> {

    private String roleType;

    @Override
    public void initialize(ContactTypeRoleCode constraintAnnotation) {
        this.roleType = constraintAnnotation.roleType();
    }

    @Override
    public boolean isValid(ContactType value, ConstraintValidatorContext context) {
        return Arrays.stream(value.getRoleTypes()).anyMatch(roleCode -> roleCode.equals(roleType));
    }
}
