package uk.gov.cca.api.common.validation;

@FunctionalInterface
public interface BiFunctionalValidator<K> {

    BusinessValidationResult process(K param1, K param2);
}
