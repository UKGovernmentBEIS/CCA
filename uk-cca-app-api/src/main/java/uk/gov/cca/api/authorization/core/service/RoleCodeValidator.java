package uk.gov.cca.api.authorization.core.service;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.authorization.core.domain.dto.RoleCode;
import uk.gov.netz.api.common.domain.RoleType;

import java.util.Set;

/**
 * The role code validator.
 */
@Log4j2
@RequiredArgsConstructor
public class RoleCodeValidator implements ConstraintValidator<RoleCode, String> {

    private final RoleService roleService;

    private RoleType roleType;

    @Override
    public void initialize(RoleCode constraintAnnotation) {
        this.roleType = constraintAnnotation.roleType();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Set<String> codesByType = roleService.getCodesByType(roleType);
        return codesByType.contains(value);
    }
}
