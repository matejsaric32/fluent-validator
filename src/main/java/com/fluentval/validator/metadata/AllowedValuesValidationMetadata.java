package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AllowedValuesValidationMetadata extends ValidationMetadata {

    protected AllowedValuesValidationMetadata(ValidationIdentifier identifier,
                                              DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    @Getter
    public static final class Contains<T> extends AllowedValuesValidationMetadata {
        private final Set<T> allowedValues;
        private final String allowedValuesString;

        private Contains(ValidationIdentifier identifier, Set<T> allowedValues, String allowedValuesString) {
            super(identifier, DefaultValidationCode.ALLOWED_VALUES_CONTAINS);
            this.allowedValues = new HashSet<>(allowedValues);
            this.allowedValuesString = allowedValuesString;

            // Add message parameters
            addMessageParameter(MessageParameter.ALLOWED_VALUES, allowedValuesString);
        }
    }

    @Getter
    public static final class OneOf<T> extends AllowedValuesValidationMetadata {
        private final T[] allowedValues;
        private final String allowedValuesString;

        @SafeVarargs
        private OneOf(ValidationIdentifier identifier, String allowedValuesString, T... allowedValues) {
            super(identifier, DefaultValidationCode.ALLOWED_VALUES_ONE_OF);
            this.allowedValues = allowedValues;
            this.allowedValuesString = allowedValuesString;

            // Add message parameters
            addMessageParameter(MessageParameter.ALLOWED_VALUES, allowedValuesString);
        }

        public T[] getAllowedValues() {
            return allowedValues.clone();
        }
    }

    @Getter
    public static final class NotContains<T> extends AllowedValuesValidationMetadata {
        private final Set<T> disallowedValues;
        private final String disallowedValuesString;

        private NotContains(ValidationIdentifier identifier, Set<T> disallowedValues, String disallowedValuesString) {
            super(identifier, DefaultValidationCode.NOT_CONTAINS);
            this.disallowedValues = new HashSet<>(disallowedValues);
            this.disallowedValuesString = disallowedValuesString;

            // Add message parameters
            addMessageParameter(MessageParameter.ALLOWED_VALUES, disallowedValuesString);
        }

        public Set<T> getDisallowedValues() {
            return new HashSet<>(disallowedValues);
        }
    }

    @Getter
    public static final class NoneOf<T> extends AllowedValuesValidationMetadata {
        private final T[] disallowedValues;
        private final String disallowedValuesString;

        @SafeVarargs
        private NoneOf(ValidationIdentifier identifier, String disallowedValuesString, T... disallowedValues) {
            super(identifier, DefaultValidationCode.NONE_OF);
            this.disallowedValues = disallowedValues;
            this.disallowedValuesString = disallowedValuesString;

            // Add message parameters
            addMessageParameter(MessageParameter.ALLOWED_VALUES, disallowedValuesString);
        }

        public T[] getDisallowedValues() {
            return disallowedValues.clone();
        }
    }

    @Getter
    public static final class IsInEnum<E extends Enum<E>> extends AllowedValuesValidationMetadata {
        private final Class<E> enumClass;
        private final Set<E> enumValues;

        private IsInEnum(ValidationIdentifier identifier, Class<E> enumClass) {
            super(identifier, DefaultValidationCode.IS_IN_ENUM);
            this.enumClass = enumClass;
            this.enumValues = EnumSet.allOf(enumClass);

            // Add message parameters
            String valuesString = createEnumValuesString(enumClass);
            addMessageParameter(MessageParameter.ALLOWED_VALUES, valuesString);
            addMessageParameter(MessageParameter.CLASS_NAME, enumClass.getSimpleName());
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

    // Factory methods
    public static <T> Contains<T> contains(ValidationIdentifier identifier, Set<T> allowedValues, String allowedValuesString) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(allowedValues, "Allowed values set must not be null");
        Objects.requireNonNull(allowedValuesString, "Allowed values string must not be null");

        if (allowedValues.isEmpty()) {
            throw new IllegalArgumentException("Allowed values set must not be empty");
        }

        return new Contains<>(identifier, allowedValues, allowedValuesString);
    }

    @SafeVarargs
    public static <T> OneOf<T> oneOf(ValidationIdentifier identifier, String allowedValuesString, T... allowedValues) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(allowedValuesString, "Allowed values string must not be null");
        Objects.requireNonNull(allowedValues, "Allowed values array must not be null");

        if (allowedValues.length == 0) {
            throw new IllegalArgumentException("Allowed values array must not be empty");
        }

        return new OneOf<>(identifier, allowedValuesString, allowedValues);
    }

    public static <T> NotContains<T> notContains(ValidationIdentifier identifier, Set<T> disallowedValues, String disallowedValuesString) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(disallowedValues, "Disallowed values set must not be null");
        Objects.requireNonNull(disallowedValuesString, "Disallowed values string must not be null");

        if (disallowedValues.isEmpty()) {
            throw new IllegalArgumentException("Disallowed values set must not be empty");
        }

        return new NotContains<>(identifier, disallowedValues, disallowedValuesString);
    }

    @SafeVarargs
    public static <T> NoneOf<T> noneOf(ValidationIdentifier identifier, String disallowedValuesString, T... disallowedValues) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(disallowedValuesString, "Disallowed values string must not be null");
        Objects.requireNonNull(disallowedValues, "Disallowed values array must not be null");

        if (disallowedValues.length == 0) {
            throw new IllegalArgumentException("Disallowed values array must not be empty");
        }

        return new NoneOf<>(identifier, disallowedValuesString, disallowedValues);
    }

    public static <E extends Enum<E>> IsInEnum<E> isInEnum(ValidationIdentifier identifier, Class<E> enumClass) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(enumClass, "Enum class must not be null");

        return new IsInEnum<>(identifier, enumClass);
    }
}