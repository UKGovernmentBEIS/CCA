package uk.gov.cca.api.common.validation;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@UtilityClass
public class ValidatorHelper {

    public BiFunctionalValidator<Set<Object>> validateSetsEquals(String errorMessage) {
        return BiFunctionalValidation.from((original, actual) ->
                SetUtils.disjunction(original, actual).isEmpty(), errorMessage);
    }

    public BiFunctionalValidator<Object> validateEquals(String errorMessage) {
        return BiFunctionalValidation.from(org.springframework.util.ObjectUtils::nullSafeEquals, errorMessage);
    }

    public BiFunctionalValidator<BigDecimal> validateBigDecimalEquals(String errorMessage) {
        return BiFunctionalValidation.from((original, actual ) ->
                ObjectUtils.compare(original, actual) == 0, errorMessage);
    }

    public <T extends BusinessViolation> Object[] extractViolations(final List<BusinessValidationResult> results) {
        return results.stream()
                .filter(validationResult -> !validationResult.isValid())
                .flatMap(validationResult -> validationResult.getViolations().stream())
                .toArray();
    }
}
