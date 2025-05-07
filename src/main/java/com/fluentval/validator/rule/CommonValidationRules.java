package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.CommonValidationMetadata;
import com.fluentval.validator.metadata.ValidationMetadata;

import java.util.function.Function;
import java.util.function.Predicate;

public final class CommonValidationRules {

    private CommonValidationRules() {
        // Utility class
    }

    // Pure validation functions
    private static class ValidationFunctions {
        static boolean isNull(final Object value) {
            return value == null;
        }

        static boolean isNotNull(final Object value) {
            return value != null;
        }

        static <T> boolean isEqual(final T value, final T target) {
            return value != null && value.equals(target);
        }

        static <T> boolean isNotEqual(final T value, final T target) {
            return value != null && !value.equals(target);
        }

        static <T> boolean satisfiesCondition(final T value, final Predicate<T> predicate) {
            return value != null && predicate.test(value);
        }

        static boolean isInstanceOf(final Object value, final Class<?> clazz) {
            return value != null && clazz.isInstance(value);
        }

        static boolean isNotInstanceOf(final Object value, final Class<?> clazz) {
            return value != null && !clazz.isInstance(value);
        }

        static boolean isSameObject(final Object value, final Object target) {
            return value != null && value == target;
        }

        static boolean isNotSameObject(final Object value, final Object target) {
            return value != null && value != target;
        }
    }

    private static <T> ValidationRule<T> createRule(
            final Predicate<T> validationFunction,
            final Function<ValidationIdentifier, ValidationMetadata> metadataFactory) {

        return (value, result, identifier) -> {
            if (!validationFunction.test(value)) {
                result.addFailure(new ValidationResult.Failure(
                        metadataFactory.apply(identifier)
                ));
            }
        };
    }

    private static <T> ValidationRule<T> createSkipNullRule(
            final Predicate<T> validationFunction,
            final Function<ValidationIdentifier, ValidationMetadata> metadataFactory) {

        return (value, result, identifier) -> {
            if (value == null) {
                return; // Skip validation for null value
            }

            if (!validationFunction.test(value)) {
                result.addFailure(new ValidationResult.Failure(
                        metadataFactory.apply(identifier)
                ));
            }
        };
    }

    // Public API
    public static <T> ValidationRule<T> notNull() {
        return createRule(
                ValidationFunctions::isNotNull,
                CommonValidationMetadata::notNull
        );
    }

    public static <T> ValidationRule<T> mustBeNull() {
        return createRule(
                ValidationFunctions::isNull,
                CommonValidationMetadata::mustBeNull
        );
    }

    public static <T> ValidationRule<T> isEqual(final T object) {
        if (object == null) {
            throw new IllegalArgumentException("Reference object cannot be null");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isEqual(value, object),
                identifier -> CommonValidationMetadata.equal(identifier, object)
        );
    }

    public static <T> ValidationRule<T> isNotEqual(final T object) {
        if (object == null) {
            throw new IllegalArgumentException("Reference object cannot be null");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isNotEqual(value, object),
                identifier -> CommonValidationMetadata.notEqual(identifier, object)
        );
    }

    public static <T> ValidationRule<T> satisfies(final Predicate<T> predicate, final String message) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate cannot be null");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Predicate description cannot be null or empty");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.satisfiesCondition(value, predicate),
                identifier -> CommonValidationMetadata.satisfies(identifier, predicate, message)
        );
    }

    public static <T> ValidationRule<T> isInstanceOf(final Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isInstanceOf(value, clazz),
                identifier -> CommonValidationMetadata.instanceOf(identifier, clazz)
        );
    }

    public static <T> ValidationRule<T> isNotInstanceOf(final Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isNotInstanceOf(value, clazz),
                identifier -> CommonValidationMetadata.notInstanceOf(identifier, clazz)
        );
    }

    public static <T> ValidationRule<T> isSameAs(final T object) {
        if (object == null) {
            throw new IllegalArgumentException("Reference object cannot be null");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isSameObject(value, object),
                identifier -> CommonValidationMetadata.sameAs(identifier, object)
        );
    }

    public static <T> ValidationRule<T> isNotSameAs(final T object) {
        if (object == null) {
            throw new IllegalArgumentException("Reference object cannot be null");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isNotSameObject(value, object),
                identifier -> CommonValidationMetadata.notSameAs(identifier, object)
        );
    }
}