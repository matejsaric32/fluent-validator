package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import lombok.Getter;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AllowedValuesValidationMetadata extends ValidationMetadata {

    // Error code constants
    public static final String CONTAINS_CODE = "VGV01";
    public static final String ONE_OF_CODE = "VGV03";
    public static final String NOT_CONTAINS_CODE = "VGV02";
    public static final String NONE_OF_CODE = "VGV04";
    public static final String IS_IN_ENUM_CODE = "VGV05";
    public static final String IN_RANGE_CODE = "VGV06";

    protected AllowedValuesValidationMetadata(ValidationIdentifier identifier, String errorCode, String message) {
        super(identifier, errorCode, message);
    }

    @Getter
    public static final class Contains<T> extends AllowedValuesValidationMetadata {
        private static final String CONTAINS_MESSAGE = "Field '%s' is not valid, must be one of: %s.";
        private final Set<T> allowedValues;
        private final String allowedValuesString;

        public Contains(ValidationIdentifier identifier, Set<T> allowedValues, String allowedValuesString) {
            this(identifier, CONTAINS_CODE, allowedValues, allowedValuesString);
        }

        public Contains(ValidationIdentifier identifier, String errorCode, Set<T> allowedValues, String allowedValuesString) {
            super(identifier, errorCode, formatMessage(CONTAINS_MESSAGE, identifier.value(), allowedValuesString));
            this.allowedValues = new HashSet<>(Objects.requireNonNull(allowedValues, "Allowed values set must not be null"));
            this.allowedValuesString = allowedValuesString;
            if (allowedValues.isEmpty()) throw new IllegalArgumentException("Allowed values set must not be empty");
        }

        public Contains(ValidationIdentifier identifier, String errorCode, Set<T> allowedValues, String allowedValuesString, String message) {
            super(identifier, errorCode, message);
            this.allowedValues = new HashSet<>(Objects.requireNonNull(allowedValues, "Allowed values set must not be null"));
            this.allowedValuesString = allowedValuesString;
            if (allowedValues.isEmpty()) throw new IllegalArgumentException("Allowed values set must not be empty");
        }

    }

    @Getter
    public static final class OneOf<T> extends AllowedValuesValidationMetadata {
        private static final String ONE_OF_MESSAGE = "Field '%s' must be one of: %s.";
        private final T[] allowedValues;
        private final String allowedValuesString;

        @SafeVarargs
        public OneOf(ValidationIdentifier identifier, String allowedValuesString, T... allowedValues) {
            this(identifier, ONE_OF_CODE, allowedValuesString, allowedValues);
        }

        @SafeVarargs
        public OneOf(ValidationIdentifier identifier, String errorCode, String allowedValuesString, T... allowedValues) {
            super(identifier, errorCode, formatMessage(ONE_OF_MESSAGE, identifier.value(), allowedValuesString));
            this.allowedValues = Objects.requireNonNull(allowedValues, "Allowed values array must not be null");
            this.allowedValuesString = allowedValuesString;
            if (allowedValues.length == 0) throw new IllegalArgumentException("Allowed values array must not be empty");
        }

        @SafeVarargs
        public OneOf(ValidationIdentifier identifier, String errorCode, String allowedValuesString, String message, T... allowedValues) {
            super(identifier, errorCode, message);
            this.allowedValues = Objects.requireNonNull(allowedValues, "Allowed values array must not be null");
            this.allowedValuesString = allowedValuesString;
            if (allowedValues.length == 0) throw new IllegalArgumentException("Allowed values array must not be empty");
        }

        public T[] getAllowedValues() {
            return allowedValues.clone();
        }

    }

    @Getter
    public static final class NotContains<T> extends AllowedValuesValidationMetadata {
        private static final String NOT_CONTAINS_MESSAGE = "Field '%s' is not valid, must NOT be one of: %s.";
        private final Set<T> disallowedValues;
        private final String disallowedValuesString;

        public NotContains(ValidationIdentifier identifier, Set<T> disallowedValues, String disallowedValuesString) {
            this(identifier, NOT_CONTAINS_CODE, disallowedValues, disallowedValuesString);
        }

        public NotContains(ValidationIdentifier identifier, String errorCode, Set<T> disallowedValues, String disallowedValuesString) {
            super(identifier, errorCode, formatMessage(NOT_CONTAINS_MESSAGE, identifier.value(), disallowedValuesString));
            this.disallowedValues = new HashSet<>(Objects.requireNonNull(disallowedValues, "Disallowed values set must not be null"));
            this.disallowedValuesString = disallowedValuesString;
            if (disallowedValues.isEmpty()) throw new IllegalArgumentException("Disallowed values set must not be empty");
        }

        public NotContains(ValidationIdentifier identifier, String errorCode, Set<T> disallowedValues, String disallowedValuesString, String message) {
            super(identifier, errorCode, message);
            this.disallowedValues = new HashSet<>(Objects.requireNonNull(disallowedValues, "Disallowed values set must not be null"));
            this.disallowedValuesString = disallowedValuesString;
            if (disallowedValues.isEmpty()) throw new IllegalArgumentException("Disallowed values set must not be empty");
        }

        public Set<T> getDisallowedValues() {
            return new HashSet<>(disallowedValues);
        }

    }

    @Getter
    public static final class NoneOf<T> extends AllowedValuesValidationMetadata {
        private static final String NONE_OF_MESSAGE = "Field '%s' must NOT be one of: %s.";
        private final T[] disallowedValues;
        private final String disallowedValuesString;

        @SafeVarargs
        public NoneOf(ValidationIdentifier identifier, String disallowedValuesString, T... disallowedValues) {
            this(identifier, NONE_OF_CODE, disallowedValuesString, disallowedValues);
        }

        @SafeVarargs
        public NoneOf(ValidationIdentifier identifier, String errorCode, String disallowedValuesString, T... disallowedValues) {
            super(identifier, errorCode, formatMessage(NONE_OF_MESSAGE, identifier.value(), disallowedValuesString));
            this.disallowedValues = Objects.requireNonNull(disallowedValues, "Disallowed values array must not be null");
            this.disallowedValuesString = disallowedValuesString;
            if (disallowedValues.length == 0) throw new IllegalArgumentException("Disallowed values array must not be empty");
        }

        @SafeVarargs
        public NoneOf(ValidationIdentifier identifier, String errorCode, String disallowedValuesString, String message, T... disallowedValues) {
            super(identifier, errorCode, message);
            this.disallowedValues = Objects.requireNonNull(disallowedValues, "Disallowed values array must not be null");
            this.disallowedValuesString = disallowedValuesString;
            if (disallowedValues.length == 0) throw new IllegalArgumentException("Disallowed values array must not be empty");
        }

        public T[] getDisallowedValues() {
            return disallowedValues.clone();
        }

    }

    @Getter
    public static final class IsInEnum<E extends Enum<E>> extends AllowedValuesValidationMetadata {
        private static final String IS_IN_ENUM_MESSAGE = "Field '%s' must be one of the enum values: %s.";
        private final Class<E> enumClass;
        private final Set<E> enumValues;

        public IsInEnum(ValidationIdentifier identifier, Class<E> enumClass) {
            this(identifier, IS_IN_ENUM_CODE, enumClass);
        }

        public IsInEnum(ValidationIdentifier identifier, String errorCode, Class<E> enumClass) {
            super(identifier, errorCode, formatMessage(IS_IN_ENUM_MESSAGE, identifier.value(), createEnumValuesString(enumClass)));
            this.enumClass = Objects.requireNonNull(enumClass, "Enum class must not be null");
            this.enumValues = EnumSet.allOf(enumClass);
        }

        public IsInEnum(ValidationIdentifier identifier, String errorCode, Class<E> enumClass, String message) {
            super(identifier, errorCode, message);
            this.enumClass = Objects.requireNonNull(enumClass, "Enum class must not be null");
            this.enumValues = EnumSet.allOf(enumClass);
        }


        public Set<E> getEnumValues() {
            return EnumSet.copyOf(enumValues);
        }

        public String getValuesString() {
            return createEnumValuesString(enumClass);
        }

        private static <T extends Enum<T>> String createEnumValuesString(Class<T> enumClass) {
            Set<T> values = EnumSet.allOf(enumClass);
            if (values.size() <= 20) {
                return values.stream().map(Enum::name).collect(Collectors.joining(", "));
            } else {
                return "one of " + values.size() + " possible values";
            }
        }
    }

    @Getter
    public static final class InRange<T extends Comparable<T>> extends AllowedValuesValidationMetadata {
        private static final String IN_RANGE_MESSAGE = "Field '%s' must be within the range: %s.";
        private final T minValue;
        private final T maxValue;
        private final String rangeDescription;

        public InRange(ValidationIdentifier identifier, T minValue, T maxValue) {
            this(identifier, IN_RANGE_CODE, minValue, maxValue);
        }

        public InRange(ValidationIdentifier identifier, String errorCode, T minValue, T maxValue) {
            this(identifier, errorCode, minValue, maxValue,
                minValue + " - " + maxValue,
                formatMessage(IN_RANGE_MESSAGE, identifier.value(), minValue + " - " + maxValue));
        }

        public InRange(ValidationIdentifier identifier, String errorCode, T minValue, T maxValue, String rangeDescription, String message) {
            super(identifier, errorCode, message);
            this.minValue = Objects.requireNonNull(minValue, "Min value must not be null");
            this.maxValue = Objects.requireNonNull(maxValue, "Max value must not be null");
            this.rangeDescription = rangeDescription;
            if (minValue.compareTo(maxValue) > 0) {
                throw new IllegalArgumentException("Min value must be less than or equal to max value");
            }
        }

    }
}
