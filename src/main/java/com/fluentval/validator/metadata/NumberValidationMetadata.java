package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.Getter;

import java.util.Objects;

public abstract class NumberValidationMetadata extends ValidationMetadata {

    protected NumberValidationMetadata(ValidationIdentifier identifier,
                                       DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    @Getter
    public static final class Min<T extends Number & Comparable<T>> extends NumberValidationMetadata {
        private final T min;

        private Min(ValidationIdentifier identifier, T min) {
            super(identifier, DefaultValidationCode.MIN);
            this.min = min;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN, min.toString());
        }
    }

    @Getter
    public static final class Max<T extends Number & Comparable<T>> extends NumberValidationMetadata {
        private final T max;

        private Max(ValidationIdentifier identifier, T max) {
            super(identifier, DefaultValidationCode.MAX);
            this.max = max;

            // Add message parameters
            addMessageParameter(MessageParameter.MAX, max.toString());
        }
    }

    @Getter
    public static final class Range<T extends Number & Comparable<T>> extends NumberValidationMetadata {
        private final T min;
        private final T max;

        private Range(ValidationIdentifier identifier, T min, T max) {
            super(identifier, DefaultValidationCode.RANGE);
            this.min = min;
            this.max = max;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN, min.toString());
            addMessageParameter(MessageParameter.MAX, max.toString());
        }
    }

    public static final class Positive extends NumberValidationMetadata {
        private Positive(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.POSITIVE);
            // No additional parameters needed for this validation
        }
    }

    public static final class Negative extends NumberValidationMetadata {
        private Negative(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NEGATIVE);
            // No additional parameters needed for this validation
        }
    }

    public static final class NotZero extends NumberValidationMetadata {
        private NotZero(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NOT_ZERO);
            // No additional parameters needed for this validation
        }
    }

    // Factory methods
    public static <T extends Number & Comparable<T>> Min<T> min(ValidationIdentifier identifier, T min) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(min, "Minimum value must not be null");

        return new Min<>(identifier, min);
    }

    public static <T extends Number & Comparable<T>> Max<T> max(ValidationIdentifier identifier, T max) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(max, "Maximum value must not be null");

        return new Max<>(identifier, max);
    }

    public static <T extends Number & Comparable<T>> Range<T> range(ValidationIdentifier identifier, T min, T max) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(min, "Minimum value must not be null");
        Objects.requireNonNull(max, "Maximum value must not be null");

        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Minimum value must be less than or equal to maximum value");
        }

        return new Range<>(identifier, min, max);
    }

    public static Positive positive(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new Positive(identifier);
    }

    public static Negative negative(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new Negative(identifier);
    }

    public static NotZero notZero(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new NotZero(identifier);
    }
}