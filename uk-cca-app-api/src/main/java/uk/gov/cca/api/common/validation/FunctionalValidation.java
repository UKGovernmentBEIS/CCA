package uk.gov.cca.api.common.validation;

import java.util.List;
import java.util.function.Predicate;

public class FunctionalValidation<K> implements FunctionalValidator<K> {

    private final Predicate<K> predicate;
    private final String violationMessage;

    public static <K> FunctionalValidation<K> from(Predicate<K> predicate, String violationMessage) {
        return new FunctionalValidation<>(predicate, violationMessage);
    }

    private FunctionalValidation(Predicate<K> predicate, String violationMessage) {
        this.predicate = predicate;
        this.violationMessage = violationMessage;
    }

    @Override
    public BusinessValidationResult process(K param) {
        return predicate.test(param)
                ? BusinessValidationResult.valid()
                : BusinessValidationResult.invalid(List.of(new BusinessViolation("", violationMessage)));
    }
}
