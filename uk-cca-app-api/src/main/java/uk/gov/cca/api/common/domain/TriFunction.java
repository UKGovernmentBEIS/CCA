package uk.gov.cca.api.common.domain;

@FunctionalInterface
public interface TriFunction<T, U, V, R> {
    public R apply(T t, U u, V v);
}
