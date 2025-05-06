package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.CommonValidationMetadata;

import java.util.function.Predicate;

public final class CommonValidationRules {

    private CommonValidationRules() {
        // Utility class
    }

    public static <T> ValidationRule<T> notNull() {
        return (value, result, identifier) -> {
            if (value == null) {
                result.addFailure(new ValidationResult.Failure(
                        CommonValidationMetadata.notNull(identifier)
                ));
            }
        };
    }

    public static <T> ValidationRule<T> mustBeNull() {
        return (value, result, identifier) -> {
            if (value != null) {
                result.addFailure(new ValidationResult.Failure(
                        CommonValidationMetadata.mustBeNull(identifier)
                ));
            }
        };
    }

    public static <T> ValidationRule<T> isEqual(final T object) {
        if (object == null) {
            throw new IllegalArgumentException("Reference object cannot be null");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!value.equals(object)) {
                result.addFailure(new ValidationResult.Failure(
                        CommonValidationMetadata.equal(identifier, object)
                ));
            }
        };
    }

    public static <T> ValidationRule<T> isNotEqual(final T object) {
        if (object == null) {
            throw new IllegalArgumentException("Reference object cannot be null");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value.equals(object)) {
                result.addFailure(new ValidationResult.Failure(
                        CommonValidationMetadata.notEqual(identifier, object)
                ));
            }
        };
    }

    public static <T> ValidationRule<T> satisfies(final Predicate<T> predicate, final String message) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate cannot be null");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Predicate description cannot be null or empty");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!predicate.test(value)) {
                result.addFailure(new ValidationResult.Failure(
                        CommonValidationMetadata.satisfies(identifier, predicate, message)
                ));
            }
        };
    }

    public static <T> ValidationRule<T> isInstanceOf(final Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!clazz.isInstance(value)) {
                result.addFailure(new ValidationResult.Failure(
                        CommonValidationMetadata.instanceOf(identifier, clazz)
                ));
            }
        };
    }

    public static <T> ValidationRule<T> isNotInstanceOf(final Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (clazz.isInstance(value)) {
                result.addFailure(new ValidationResult.Failure(
                        CommonValidationMetadata.notInstanceOf(identifier, clazz)
                ));
            }
        };
    }

    public static <T> ValidationRule<T> isSameAs(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Reference object cannot be null");
        }

        return (value, result, identifier) -> {
            if (value != object) {
                result.addFailure(new ValidationResult.Failure(
                        CommonValidationMetadata.sameAs(identifier, object)
                ));
            }
        };
    }

    public static <T> ValidationRule<T> isNotSameAs(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Reference object cannot be null");
        }

        return (value, result, identifier) -> {
            if (value == object) {
                result.addFailure(new ValidationResult.Failure(
                        CommonValidationMetadata.notSameAs(identifier, object)
                ));
            }
        };
    }
}