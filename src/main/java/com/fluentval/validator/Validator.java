package com.fluentval.validator;

import lombok.Getter;

import java.util.function.Function;
import java.util.function.Predicate;

@Getter
public class Validator<T> {
    private final T target;
    private final ValidationResult result;
    private boolean shortCircuit = false;

    protected Validator(T target) {
        this(target, new ValidationResult(), false);
    }

    protected Validator(T target, ValidationResult result, boolean shortCircuit) {
        this.target = target;
        this.result = result;
        this.shortCircuit = shortCircuit;
    }

    public static <T> Validator<T> of(final T target) {
        return new Validator<>(target);
    }

    public static <T> Validator<T> withExistingResult(final T target, final ValidationResult parentResult) {
        if (parentResult == null) {
            throw new IllegalArgumentException("ValidationResult cannot be null");
        }

        ScopedValidationResult childResult = new ScopedValidationResult(parentResult);
        return new Validator<>(target, childResult, false);
    }

    public <V> PropertyValidator<T, V> property(final ValidationIdentifier identifier, final Function<T, V> extractor) {
        return new PropertyValidator<>(this, identifier, target != null ? extractor.apply(target) : null);
    }

    public <V> PropertyValidator<T, V> property(final ValidationIdentifier identifier, final V value) {
        return new PropertyValidator<>(this, identifier, target != null ? value : null);
    }

    public Validator<T> validate(final ObjectValidationRule<T> rule) {
        if (!shortCircuit) {
            rule.validate(target, result);
        }
        return this;
    }

    public <V> Validator<T> validateAs(Function<T, V> converter, ObjectValidationRule<V> rule) {
        if (!shortCircuit && target != null) {
            V convertedTarget = converter.apply(target);
            rule.validate(convertedTarget, result);
        }
        return this;
    }

    public Validator<T> validateWhen(final Predicate<T> condition, final ObjectValidationRule<T> rule) {
        if (!shortCircuit && condition.test(target)) {
            rule.validate(target, result);
        }
        return this;
    }

    public <V> Validator<T> validateWithCircuitBreaker(final V value, final ValidationIdentifier identifier, final ValidationRule<V> rule) {
        ValidationResult tempResult = new ValidationResult();
        rule.validate(value, tempResult, identifier);

        if (tempResult.hasErrors()) {
            tempResult.getFailures().forEach(result::addFailure);
            shortCircuit = true;
        }

        return this;
    }

    public Validator<T> validateIfNoErrorFor(final ValidationIdentifier identifier, final ObjectValidationRule<T> rule) {
        if (!shortCircuit && !result.hasErrorForIdentifier(identifier)) {
            rule.validate(target, result);
        }
        return this;
    }

    public Validator<T> shortCircuitIf(final Predicate<ValidationResult> condition) {
        if (condition.test(result)) {
            shortCircuit = true;
        }
        return this;
    }

    public Validator<T> shortCircuitIfErrors() {
        return shortCircuitIf(ValidationResult::hasErrors);
    }

    public Validator<T> mergeScopedFailures(Validator<T> otherValidator) {
        if (otherValidator.getResult() instanceof ScopedValidationResult scopedResult) {
            scopedResult.getScopedFailures().forEach(this.result::addFailure);
        }
        return this;
    }

    boolean isShortCircuited() {
        return shortCircuit;
    }

}