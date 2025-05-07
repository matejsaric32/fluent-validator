package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.NumberValidationMetadata;

import java.util.Objects;

public final class NumberValidationRules {

    private NumberValidationRules() {
        // Utility class
    }

    // Pure validation functions
    private static class ValidationFunctions {
        static <T extends Number & Comparable<T>> boolean isGreaterThanOrEqualTo(final T value, final T min) {
            return value.compareTo(min) >= 0;
        }

        static <T extends Number & Comparable<T>> boolean isLessThanOrEqualTo(final T value, final T max) {
            return value.compareTo(max) <= 0;
        }

        static <T extends Number & Comparable<T>> boolean isInRange(final T value, final T min, final T max) {
            return value.compareTo(min) >= 0 && value.compareTo(max) <= 0;
        }

        static <T extends Number & Comparable<T>> boolean isPositive(final T value) {
            return value.compareTo(getZero(value)) > 0;
        }

        static <T extends Number & Comparable<T>> boolean isNegative(final T value) {
            return value.compareTo(getZero(value)) < 0;
        }

        static <T extends Number & Comparable<T>> boolean isNotZero(final T value) {
            return value.compareTo(getZero(value)) != 0;
        }

        @SuppressWarnings("unchecked")
        static <T extends Number & Comparable<T>> T getZero(final T value) {
            if (value instanceof Integer) {
                return (T) Integer.valueOf(0);
            } else if (value instanceof Long) {
                return (T) Long.valueOf(0L);
            } else if (value instanceof Double) {
                return (T) Double.valueOf(0.0);
            } else if (value instanceof Float) {
                return (T) Float.valueOf(0.0f);
            } else if (value instanceof Short) {
                return (T) Short.valueOf((short) 0);
            } else if (value instanceof Byte) {
                return (T) Byte.valueOf((byte) 0);
            } else {
                // Default fallback
                return (T) Integer.valueOf(0);
            }
        }
    }

    public static <T extends Number & Comparable<T>> ValidationRule<T> min(final T min) {
        Objects.requireNonNull(min, "Minimum value must not be null");

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!ValidationFunctions.isGreaterThanOrEqualTo(value, min)) {
                result.addFailure(
                        new ValidationResult.Failure(
                                NumberValidationMetadata.min(identifier, min)
                        )
                );
            }
        };
    }

    public static <T extends Number & Comparable<T>> ValidationRule<T> max(final T max) {
        Objects.requireNonNull(max, "Maximum value must not be null");

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!ValidationFunctions.isLessThanOrEqualTo(value, max)) {
                result.addFailure(
                        new ValidationResult.Failure(
                                NumberValidationMetadata.max(identifier, max)
                        )
                );
            }
        };
    }

    public static <T extends Number & Comparable<T>> ValidationRule<T> range(final T min, final T max) {
        Objects.requireNonNull(min, "Minimum value must not be null");
        Objects.requireNonNull(max, "Maximum value must not be null");

        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Minimum value must be less than or equal to maximum value");
        }

        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!ValidationFunctions.isInRange(value, min, max)) {
                result.addFailure(
                        new ValidationResult.Failure(
                                NumberValidationMetadata.range(identifier, min, max)
                        )
                );
            }
        };
    }

    public static <T extends Number & Comparable<T>> ValidationRule<T> positive() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!ValidationFunctions.isPositive(value)) {
                result.addFailure(
                        new ValidationResult.Failure(
                                NumberValidationMetadata.positive(identifier)
                        )
                );
            }
        };
    }

    public static <T extends Number & Comparable<T>> ValidationRule<T> negative() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!ValidationFunctions.isNegative(value)) {
                result.addFailure(
                        new ValidationResult.Failure(
                                NumberValidationMetadata.negative(identifier)
                        )
                );
            }
        };
    }

    public static <T extends Number & Comparable<T>> ValidationRule<T> notZero() {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (!ValidationFunctions.isNotZero(value)) {
                result.addFailure(
                        new ValidationResult.Failure(
                                NumberValidationMetadata.notZero(identifier)
                        )
                );
            }
        };
    }
}