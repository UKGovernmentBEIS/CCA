package uk.gov.cca.api.common.validation;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ValidatorHelper {

    public List<? extends BusinessViolation> extractViolations(final List<BusinessValidationResult> results) {
        return results.stream()
                .filter(validationResult -> !validationResult.isValid())
                .flatMap(validationResult -> validationResult.getViolations().stream())
                .toList();
    }
}
