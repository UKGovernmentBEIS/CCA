package uk.gov.cca.api.common.validation;

import java.util.List;
import java.util.function.BiPredicate;

public class BiFunctionalValidation<K> implements BiFunctionalValidator<K> {

    private final BiPredicate<K, K> predicate;
    private final String violationMessage;

    public static <K> BiFunctionalValidation<K> from(BiPredicate<K, K> predicate, String violationMessage) {
        return new BiFunctionalValidation<>(predicate, violationMessage);
    }

    private BiFunctionalValidation(BiPredicate<K, K> predicate, String violationMessage) {
        this.predicate = predicate;
        this.violationMessage = violationMessage;
    }

    @Override
    public BusinessValidationResult process(K expected, K actual) {
        return predicate.test(expected, actual)
                ? BusinessValidationResult.valid()
                : BusinessValidationResult.invalid(List.of(new BusinessViolation("", violationMessage)));
    }
}
