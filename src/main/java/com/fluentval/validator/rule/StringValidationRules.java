package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.StringValidationMetadata;

import java.util.Arrays;
import java.util.regex.Pattern;

public final class StringValidationRules {

    private StringValidationRules() {
        // Utility class
    }

    public static ValidationRule<String> notBlank() {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.notBlank(identifier)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> maxLength(int max) {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (value.trim().length() > max) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.maxLength(identifier, max)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> minLength(int min) {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (value.trim().length() < min) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.minLength(identifier, min)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> exactLength(int length) {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (value.trim().length() != length) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.exactLength(identifier, length)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> matches(Pattern pattern) {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (!pattern.matcher(value).matches()) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.matches(identifier, pattern)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> oneOf(String... allowedValues) {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (Arrays.stream(allowedValues).noneMatch(value::equals)) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.oneOf(identifier, allowedValues)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> oneOfIgnoreCase(String... allowedValues) {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (Arrays.stream(allowedValues).noneMatch(allowed ->
                allowed.equalsIgnoreCase(value))) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.oneOfIgnoreCase(identifier, allowedValues)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> startsWith(String prefix) {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (!value.startsWith(prefix)) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.startsWith(identifier, prefix)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> endsWith(String suffix) {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (!value.endsWith(suffix)) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.endsWith(identifier, suffix)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> contains(String substring) {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (!value.contains(substring)) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.contains(identifier, substring)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> numeric() {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (!value.matches("\\d+")) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.numeric(identifier)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> alphanumeric() {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (!value.matches("[a-zA-Z0-9]+")) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.alphanumeric(identifier)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> uppercase() {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (!value.equals(value.toUpperCase())) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.uppercase(identifier)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> lowercase() {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (!value.equals(value.toLowerCase())) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.lowercase(identifier)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> noWhitespace() {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (value.chars().anyMatch(Character::isWhitespace)) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.noWhitespace(identifier)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> noLeadingWhitespace() {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (Character.isWhitespace(value.charAt(0))) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.noLeadingWhitespace(identifier)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> noTrailingWhitespace() {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (Character.isWhitespace(value.charAt(value.length() - 1))) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.noTrailingWhitespace(identifier)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> noConsecutiveWhitespace() {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (value.matches(".*\\s{2,}.*")) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.noConsecutiveWhitespace(identifier)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> trimmed() {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            if (!value.equals(value.trim())) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.trimmed(identifier)
                    )
                );
            }
        };
    }

    public static ValidationRule<String> properSpacing() {
        return (value, result, identifier) -> {
            if (value == null || value.isBlank()) {
                // Skip validation for null/blank strings
                return;
            }

            String normalized = value.trim().replaceAll("\\s+", " ");
            if (!value.equals(normalized)) {
                result.addFailure(
                    new ValidationResult.Failure(
                        StringValidationMetadata.properSpacing(identifier)
                    )
                );
            }
        };
    }
}