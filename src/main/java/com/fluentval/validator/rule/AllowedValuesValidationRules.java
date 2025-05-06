package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.AllowedValuesValidationMetadata;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public final class AllowedValuesValidationRules {

    private AllowedValuesValidationRules() {
        // Utility class
    }

    public static <T> ValidationRule<T> contains(String allowedValuesString, Set<T> allowedValues) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value instanceof String str && str.isBlank()) {
                // Skip validation for blank strings
                return;
            }

            if (!allowedValues.contains(value)) {
                result.addFailure(new ValidationResult.Failure(
                    AllowedValuesValidationMetadata.contains(
                        identifier,
                        allowedValues,
                        allowedValuesString
                    )
                ));
            }
        };
    }

    @SafeVarargs
    public static <T> ValidationRule<T> oneOf(String allowedValuesString, T... allowedValues) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value instanceof String str && str.isBlank()) {
                // Skip validation for blank strings
                return;
            }

            boolean isValid = Arrays.stream(allowedValues)
                .anyMatch(value::equals);

            if (!isValid) {
                result.addFailure(new ValidationResult.Failure(
                    AllowedValuesValidationMetadata.oneOf(
                        identifier,
                        allowedValuesString,
                        allowedValues
                    )
                ));
            }
        };
    }

    public static <T> ValidationRule<T> notContains(String disallowedValuesString, Set<T> disallowedValues) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value instanceof String str && str.isBlank()) {
                // Skip validation for blank strings
                return;
            }

            if (disallowedValues.contains(value)) {
                result.addFailure(new ValidationResult.Failure(
                    AllowedValuesValidationMetadata.notContains(
                        identifier,
                        disallowedValues,
                        disallowedValuesString
                    )
                ));
            }
        };
    }

    @SafeVarargs
    public static <T> ValidationRule<T> noneOf(String disallowedValuesString, T... disallowedValues) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            if (value instanceof String str && str.isBlank()) {
                // Skip validation for blank strings
                return;
            }

            boolean isInvalid = Arrays.stream(disallowedValues)
                .anyMatch(value::equals);

            if (isInvalid) {
                result.addFailure(new ValidationResult.Failure(
                    AllowedValuesValidationMetadata.noneOf(
                        identifier,
                        disallowedValuesString,
                        disallowedValues
                    )
                ));
            }
        };
    }

    public static <T, E extends Enum<E>> ValidationRule<T> isInEnum(Class<E> enumClass) {
        return (value, result, identifier) -> {
            if (value == null) {
                // Skip validation for null value
                return;
            }

            Set<E> enumValues = EnumSet.allOf(enumClass);
            boolean isValid;

            if (value instanceof String str) {
                if (str.isBlank()) {
                    // Skip validation for blank strings
                    return;
                }

                isValid = enumValues.stream()
                    .map(Enum::name)
                    .anyMatch(name -> name.equals(str));
            } else {
                isValid = enumValues.contains(value);
            }

            if (!isValid) {
                result.addFailure(new ValidationResult.Failure(
                    AllowedValuesValidationMetadata.isInEnum(
                        identifier,
                        enumClass
                    )
                ));
            }
        };
    }

}