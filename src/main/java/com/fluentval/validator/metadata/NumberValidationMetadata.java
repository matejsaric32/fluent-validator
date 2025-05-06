package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class NumberValidationMetadata extends ValidationMetadata {

    protected NumberValidationMetadata(ValidationIdentifier identifier,
                                       DefaultValidationCode code,
                                       Map<String, String> messageParameters) {
        super(identifier, code.getCode(), messageParameters);

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    protected void addMessageParameter(MessageParameter param, String value) {
        addMessageParameter(param.getKey(), value);
    }

    @Getter
    public static final class Min<T extends Number & Comparable<T>> extends NumberValidationMetadata {
        private final T min;

        public Min(ValidationIdentifier identifier, T min) {
            super(identifier, DefaultValidationCode.MIN, new HashMap<>());
            this.min = Objects.requireNonNull(min, "Minimum value must not be null");

            // Add message parameters
            addMessageParameter(MessageParameter.MIN, min.toString());
        }
    }

    @Getter
    public static final class Max<T extends Number & Comparable<T>> extends NumberValidationMetadata {
        private final T max;

        public Max(ValidationIdentifier identifier, T max) {
            super(identifier, DefaultValidationCode.MAX, new HashMap<>());
            this.max = Objects.requireNonNull(max, "Maximum value must not be null");

            // Add message parameters
            addMessageParameter(MessageParameter.MAX, max.toString());
        }
    }

    @Getter
    public static final class Range<T extends Number & Comparable<T>> extends NumberValidationMetadata {
        private final T min;
        private final T max;

        public Range(ValidationIdentifier identifier, T min, T max) {
            super(identifier, DefaultValidationCode.RANGE, new HashMap<>());
            this.min = Objects.requireNonNull(min, "Minimum value must not be null");
            this.max = Objects.requireNonNull(max, "Maximum value must not be null");

            if (min.compareTo(max) > 0) {
                throw new IllegalArgumentException("Minimum value must be less than or equal to maximum value");
            }

            // Add message parameters
            addMessageParameter(MessageParameter.MIN, min.toString());
            addMessageParameter(MessageParameter.MAX, max.toString());
        }
    }

    public static final class Positive extends NumberValidationMetadata {

        public Positive(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.POSITIVE, new HashMap<>());
            // No additional parameters needed for this validation
        }
    }

    public static final class Negative extends NumberValidationMetadata {

        public Negative(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NEGATIVE, new HashMap<>());
            // No additional parameters needed for this validation
        }
    }

    public static final class NotZero extends NumberValidationMetadata {

        public NotZero(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NOT_ZERO, new HashMap<>());
            // No additional parameters needed for this validation
        }
    }

    // Factory methods
    public static <T extends Number & Comparable<T>> Min<T> min(ValidationIdentifier identifier, T min) {
        return new Min<>(identifier, min);
    }

    public static <T extends Number & Comparable<T>> Max<T> max(ValidationIdentifier identifier, T max) {
        return new Max<>(identifier, max);
    }

    public static <T extends Number & Comparable<T>> Range<T> range(ValidationIdentifier identifier, T min, T max) {
        return new Range<>(identifier, min, max);
    }

    public static Positive positive(ValidationIdentifier identifier) {
        return new Positive(identifier);
    }

    public static Negative negative(ValidationIdentifier identifier) {
        return new Negative(identifier);
    }

    public static NotZero notZero(ValidationIdentifier identifier) {
        return new NotZero(identifier);
    }
}