package uk.gov.cca.api.common.domain;

@FunctionalInterface
public interface FourFunction<T, U, V, W, R> {
    R apply(T t, U u, V v, W w);
}
