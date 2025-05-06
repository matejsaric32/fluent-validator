package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.NumberValidationMetadata;

public final class NumberValidationRules {

    private NumberValidationRules() {
        // Utility class
    }

    public static <T extends Number & Comparable<T>> ValidationRule<T> min(T min) {
        return (value, result, identifier) -> {
            if (value != null && value.compareTo(min) < 0) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new NumberValidationMetadata.Min<>(identifier, min)
                    )
                );
            }
        };
    }

    public static <T extends Number & Comparable<T>> ValidationRule<T> max(T max) {
        return (value, result, identifier) -> {
            if (value != null && (value).compareTo(max) > 0) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new NumberValidationMetadata.Max<>(identifier, max)
                    )
                );
            }
        };
    }

    public static <T extends Number & Comparable<T>> ValidationRule<T> range(T min, T max) {
        return (value, result, identifier) -> {
            if (value != null && (value.compareTo(min) < 0 || value.compareTo(max) > 0)) {
                result.addFailure(
                    new ValidationResult.Failure(
                        new NumberValidationMetadata.Range<>(identifier, min, max)
                    )
                );
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number & Comparable<T>> ValidationRule<T> positive() {
        return (value, result, identifier) -> {
            if (value != null) {
                T zero;
                if (value instanceof Integer) {
                    zero = (T) Integer.valueOf(0);
                } else if (value instanceof Long) {
                    zero = (T) Long.valueOf(0);
                } else if (value instanceof Double) {
                    zero = (T) Double.valueOf(0);
                } else if (value instanceof Float) {
                    zero = (T) Float.valueOf(0);
                } else if (value instanceof Short) {
                    zero = (T) Short.valueOf((short) 0);
                } else if (value instanceof Byte) {
                    zero = (T) Byte.valueOf((byte) 0);
                } else {
                    // Default fallback
                    zero = (T) Integer.valueOf(0);
                }

                if (value.compareTo(zero) <= 0) {
                    result.addFailure(
                        new ValidationResult.Failure(
                            new NumberValidationMetadata.Positive(identifier)
                        )
                    );
                }
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number & Comparable<T>> ValidationRule<T> negative() {
        return (value, result, identifier) -> {
            if (value != null) {
                T zero;
                if (value instanceof Integer) {
                    zero = (T) Integer.valueOf(0);
                } else if (value instanceof Long) {
                    zero = (T) Long.valueOf(0);
                } else if (value instanceof Double) {
                    zero = (T) Double.valueOf(0);
                } else if (value instanceof Float) {
                    zero = (T) Float.valueOf(0);
                } else if (value instanceof Short) {
                    zero = (T) Short.valueOf((short) 0);
                } else if (value instanceof Byte) {
                    zero = (T) Byte.valueOf((byte) 0);
                } else {
                    // Default fallback
                    zero = (T) Integer.valueOf(0);
                }

                if (value.compareTo(zero) >= 0) {
                    result.addFailure(
                        new ValidationResult.Failure(
                            new NumberValidationMetadata.Negative(identifier)
                        )
                    );
                }
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number & Comparable<T>> ValidationRule<T> notZero() {
        return (value, result, identifier) -> {
            if (value != null) {
                T zero;
                if (value instanceof Integer) {
                    zero = (T) Integer.valueOf(0);
                } else if (value instanceof Long) {
                    zero = (T) Long.valueOf(0);
                } else if (value instanceof Double) {
                    zero = (T) Double.valueOf(0);
                } else if (value instanceof Float) {
                    zero = (T) Float.valueOf(0);
                } else if (value instanceof Short) {
                    zero = (T) Short.valueOf((short) 0);
                } else if (value instanceof Byte) {
                    zero = (T) Byte.valueOf((byte) 0);
                } else {
                    // Default fallback
                    zero = (T) Integer.valueOf(0);
                }

                if (value.compareTo(zero) == 0) {
                    result.addFailure(
                        new ValidationResult.Failure(
                            new NumberValidationMetadata.NotZero(identifier)
                        )
                    );
                }
            }
        };
    }
}