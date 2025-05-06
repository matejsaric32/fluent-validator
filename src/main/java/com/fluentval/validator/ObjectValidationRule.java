package com.fluentval.validator;

@FunctionalInterface
public interface ObjectValidationRule<T> {
    void validate(final T value, final ValidationResult result);

    default ObjectValidationRule<T> and(final ObjectValidationRule<T> other) {
        return (value, result) -> {
            validate(value, result);
            other.validate(value, result);
        };
    }

    default ObjectValidationRule<T> named(final String name) {
        return (value, result) -> {
            try {
                validate(value, result);
            } catch (Exception e) {
                throw new RuntimeException("Error in object validation rule '" + name + "'", e);
            }
        };
    }
}