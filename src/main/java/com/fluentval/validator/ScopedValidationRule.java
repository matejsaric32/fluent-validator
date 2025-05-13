package com.fluentval.validator;

@FunctionalInterface
public interface ScopedValidationRule<T> {

    void validate(final T value, final Validator<?> validator);
    
    default ScopedValidationRule<T> and(final ScopedValidationRule<T> other) {
        return (value, validator) -> {
            validate(value, validator);
            other.validate(value, validator);
        };
    }
    
}