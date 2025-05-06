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
                        new StringValidationMetadata.NotBlank(identifier)
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
                        new StringValidationMetadata.MaxLength(identifier, max)
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
                        new StringValidationMetadata.MinLength(identifier, min)
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
                        new StringValidationMetadata.ExactLength(identifier, length)
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
                        new StringValidationMetadata.Matches(identifier, pattern)
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
                        new StringValidationMetadata.OneOf(identifier, allowedValues)
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
                        new StringValidationMetadata.OneOfIgnoreCase(identifier, allowedValues)
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
                        new StringValidationMetadata.StartsWith(identifier, prefix)
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
                        new StringValidationMetadata.EndsWith(identifier, suffix)
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
                        new StringValidationMetadata.Contains(identifier, substring)
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
                        new StringValidationMetadata.Numeric(identifier)
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
                        new StringValidationMetadata.Alphanumeric(identifier)
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
                        new StringValidationMetadata.Uppercase(identifier)
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
                        new StringValidationMetadata.Lowercase(identifier)
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
                        new StringValidationMetadata.NoWhitespace(identifier)
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
                        new StringValidationMetadata.NoLeadingWhitespace(identifier)
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
                        new StringValidationMetadata.NoTrailingWhitespace(identifier)
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
                        new StringValidationMetadata.NoConsecutiveWhitespace(identifier)
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
                        new StringValidationMetadata.Trimmed(identifier)
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
                        new StringValidationMetadata.ProperSpacing(identifier)
                    )
                );
            }
        };
    }
}