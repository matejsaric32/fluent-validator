package com.fluentval.validator;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class PropertyValidator<T, V> {

    private final Validator<T> parent;
    private final ValidationIdentifier identifier;
    private final V value;
    private boolean shortCircuit = false;

    PropertyValidator(final Validator<T> parent,
        final ValidationIdentifier identifier,
        final V value) {
        this.parent = parent;
        this.identifier = identifier;
        this.value = value;
    }

    public PropertyValidator<T, V> validate(final ValidationRule<V> rule) {
        if (!shortCircuit && !parent.isShortCircuited()) {
            rule.validate(value, parent.getResult(), identifier);
        }
        return this;
    }

    public PropertyValidator<T, V> validateWhen(final Predicate<V> condition,
        final ValidationRule<V> rule) {
        if (!shortCircuit && !parent.isShortCircuited() && condition.test(value)) {
            rule.validate(value, parent.getResult(), identifier);
        }
        return this;
    }

    public PropertyValidator<T, V> validateIfNoError(final ValidationRule<V> rule) {
        if (!shortCircuit && !parent.isShortCircuited() &&
            !parent.getResult().hasErrorForIdentifier(identifier)) {
            rule.validate(value, parent.getResult(), identifier);
        }
        return this;
    }

    public PropertyValidator<T, V> validateIfNoErrorFor(final ValidationIdentifier otherIdentifier,
        final ValidationRule<V> rule) {
        if (!shortCircuit && !parent.isShortCircuited() &&
            !parent.getResult().hasErrorForIdentifier(otherIdentifier)) {
            rule.validate(value, parent.getResult(), identifier);
        }
        return this;
    }

    public PropertyValidator<T, V> peek(final Consumer<V> consumer) {
        if (!shortCircuit && !parent.isShortCircuited() && value != null) {
            consumer.accept(value);
        }
        return this;
    }

    public PropertyValidator<T, V> shortCircuitIf(final Predicate<ValidationResult> condition) {
        if (condition.test(parent.getResult())) {
            shortCircuit = true;
        }
        return this;
    }

    public PropertyValidator<T, V> shortCircuitIfErrors() {
        return shortCircuitIf(r -> r.hasErrorForIdentifier(identifier));
    }

    public <K> PropertyValidator<V, K> property(final ValidationIdentifier nestedIdentifier,
        final Function<V, K> extractor) {
        return new PropertyValidator<>(
            new Validator<>(value, parent.getResult(), shortCircuit),
            nestedIdentifier,
            value != null ? extractor.apply(value) : null
        );
    }

    public Validator<T> end() {
        return parent;
    }
}