package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import lombok.Getter;

import java.util.Objects;


public abstract class NumberValidationMetadata extends ValidationMetadata {

    // Error code constants
    public static final String MIN_CODE = "VGN01";
    public static final String MAX_CODE = "VGN02";
    public static final String RANGE_CODE = "VGN03";
    public static final String POSITIVE_CODE = "VGN04";
    public static final String NEGATIVE_CODE = "VGN05";
    public static final String NOT_ZERO_CODE = "VGN06";

    // Message templates
    private static final String MIN_MESSAGE = "Field '%s' must have a minimum value of %s.";
    private static final String MAX_MESSAGE = "Field '%s' must have a maximum value of %s.";
    private static final String RANGE_MESSAGE = "Field '%s' must be between %s and %s.";
    private static final String POSITIVE_MESSAGE = "Field '%s' must be a positive number.";
    private static final String NEGATIVE_MESSAGE = "Field '%s' must be a negative number.";
    private static final String NOT_ZERO_MESSAGE = "Field '%s' must not be zero.";

    protected NumberValidationMetadata(ValidationIdentifier identifier, String errorCode, String message) {
        super(identifier, errorCode, message);
    }

    @Getter
    public static final class Min<T extends Number & Comparable<T>> extends NumberValidationMetadata {
        private final T min;

        public Min(ValidationIdentifier identifier, T min) {
            super(identifier, MIN_CODE, formatMessage(MIN_MESSAGE, identifier.value(), min.toString()));
            this.min = Objects.requireNonNull(min, "Minimum value must not be null");
        }

    }

    @Getter
    public static final class Max<T extends Number & Comparable<T>> extends NumberValidationMetadata {
        private final T max;

        public Max(ValidationIdentifier identifier, T max) {
            super(identifier, MAX_CODE, formatMessage(MAX_MESSAGE, identifier.value(), max.toString()));
            this.max = Objects.requireNonNull(max, "Maximum value must not be null");
        }

    }

    @Getter
    public static final class Range<T extends Number & Comparable<T>> extends NumberValidationMetadata {
        private final T min;
        private final T max;

        public Range(ValidationIdentifier identifier, T min, T max) {
            super(identifier, RANGE_CODE, formatMessage(RANGE_MESSAGE, identifier.value(), min.toString(), max.toString()));
            this.min = Objects.requireNonNull(min, "Minimum value must not be null");
            this.max = Objects.requireNonNull(max, "Maximum value must not be null");

            if (min.compareTo(max) > 0) {
                throw new IllegalArgumentException("Minimum value must be less than or equal to maximum value");
            }
        }

    }

    public static final class Positive extends NumberValidationMetadata {
        public Positive(ValidationIdentifier identifier) {
            super(identifier, POSITIVE_CODE, formatMessage(POSITIVE_MESSAGE, identifier.value()));
        }
    }

    public static final class Negative extends NumberValidationMetadata {
        public Negative(ValidationIdentifier identifier) {
            super(identifier, NEGATIVE_CODE, formatMessage(NEGATIVE_MESSAGE, identifier.value()));
        }
    }

    public static final class NotZero extends NumberValidationMetadata {
        public NotZero(ValidationIdentifier identifier) {
            super(identifier, NOT_ZERO_CODE, formatMessage(NOT_ZERO_MESSAGE, identifier.value()));
        }
    }
}
