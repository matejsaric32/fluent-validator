package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.AllowedValuesValidationMetadata;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import static com.fluentval.validator.rule.ValidationRuleUtils.createSkipNullRule;

public final class AllowedValuesValidationRules {

    private AllowedValuesValidationRules() {
        // Utility class
    }

    // Pure validation functions
    private static class ValidationFunctions {
        static <T> boolean containsValue(final T value, final Set<T> allowedValues) {
            return allowedValues.contains(value);
        }

        static <T> boolean isOneOf(final T value, final T[] allowedValues) {
            return Arrays.stream(allowedValues).anyMatch(value::equals);
        }

        static <T> boolean notContainsValue(final T value, final Set<T> disallowedValues) {
            return !disallowedValues.contains(value);
        }

        static <T> boolean isNoneOf(final T value, final T[] disallowedValues) {
            return Arrays.stream(disallowedValues).noneMatch(value::equals);
        }

        static <T, E extends Enum<E>> boolean isInEnum(final T value, final Class<E> enumClass) {
            final Set<E> enumValues = EnumSet.allOf(enumClass);

            if (value instanceof String str) {
                return enumValues.stream()
                        .map(Enum::name)
                        .anyMatch(name -> name.equals(str));
            } else {
                return enumValues.contains(value);
            }
        }
    }

    public static <T> ValidationRule<T> contains(final String allowedValuesString, final Set<T> allowedValues) {
        Objects.requireNonNull(allowedValues, "Allowed values set must not be null");
        Objects.requireNonNull(allowedValuesString, "Allowed values string must not be null");

        if (allowedValues.isEmpty()) {
            throw new IllegalArgumentException("Allowed values set must not be empty");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.containsValue(value, allowedValues),
                identifier -> AllowedValuesValidationMetadata.contains(identifier, allowedValues, allowedValuesString)
        );
    }

    @SafeVarargs
    public static <T> ValidationRule<T> oneOf(final String allowedValuesString, final T... allowedValues) {
        Objects.requireNonNull(allowedValuesString, "Allowed values string must not be null");
        Objects.requireNonNull(allowedValues, "Allowed values array must not be null");

        if (allowedValues.length == 0) {
            throw new IllegalArgumentException("Allowed values array must not be empty");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isOneOf(value, allowedValues),
                identifier -> AllowedValuesValidationMetadata.oneOf(identifier, allowedValuesString, allowedValues)
        );
    }

    public static <T> ValidationRule<T> notContains(final String disallowedValuesString, final Set<T> disallowedValues) {
        Objects.requireNonNull(disallowedValues, "Disallowed values set must not be null");
        Objects.requireNonNull(disallowedValuesString, "Disallowed values string must not be null");

        if (disallowedValues.isEmpty()) {
            throw new IllegalArgumentException("Disallowed values set must not be empty");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.notContainsValue(value, disallowedValues),
                identifier -> AllowedValuesValidationMetadata.notContains(identifier, disallowedValues, disallowedValuesString)
        );
    }

    @SafeVarargs
    public static <T> ValidationRule<T> noneOf(final String disallowedValuesString, final T... disallowedValues) {
        Objects.requireNonNull(disallowedValuesString, "Disallowed values string must not be null");
        Objects.requireNonNull(disallowedValues, "Disallowed values array must not be null");

        if (disallowedValues.length == 0) {
            throw new IllegalArgumentException("Disallowed values array must not be empty");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isNoneOf(value, disallowedValues),
                identifier -> AllowedValuesValidationMetadata.noneOf(identifier, disallowedValuesString, disallowedValues)
        );
    }

    public static <T, E extends Enum<E>> ValidationRule<T> isInEnum(final Class<E> enumClass) {
        Objects.requireNonNull(enumClass, "Enum class must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isInEnum(value, enumClass),
                identifier -> AllowedValuesValidationMetadata.isInEnum(identifier, enumClass)
        );
    }
}