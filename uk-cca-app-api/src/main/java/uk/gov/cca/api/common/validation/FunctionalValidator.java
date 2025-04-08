package uk.gov.cca.api.common.validation;

@FunctionalInterface
public interface FunctionalValidator<K> {

    BusinessValidationResult process(K param);

    default FunctionalValidator<K> and(FunctionalValidator<K> other) {
        return param -> {
            BusinessValidationResult firstResult = this.process(param);
            return !firstResult.isValid() ? firstResult : other.process(param);
        };
    }

    default FunctionalValidator<K> or(FunctionalValidator<K> other) {
        return param -> {
            BusinessValidationResult firstResult = this.process(param);
            return firstResult.isValid() ? firstResult : other.process(param);
        };
    }
}
