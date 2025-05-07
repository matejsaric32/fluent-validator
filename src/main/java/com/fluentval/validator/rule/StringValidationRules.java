package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.StringValidationMetadata;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.fluentval.validator.rule.ValidationRuleUtils.createRule;
import static com.fluentval.validator.rule.ValidationRuleUtils.createSkipNullRule;

public final class StringValidationRules {

    private StringValidationRules() {
        // Utility class
    }

    // Pure validation functions
    private static class ValidationFunctions {
        static boolean isNotBlank(final String value) {
            return value != null && !value.isBlank();
        }

        static boolean hasMaxLength(final String value, final int maxLength) {
            return value.trim().length() <= maxLength;
        }

        static boolean hasMinLength(final String value, final int minLength) {
            return value.trim().length() >= minLength;
        }

        static boolean hasExactLength(final String value, final int exactLength) {
            return value.trim().length() == exactLength;
        }

        static boolean matchesPattern(final String value, final Pattern pattern) {
            return pattern.matcher(value).matches();
        }

        static boolean isOneOf(final String value, final String[] allowedValues) {
            return Arrays.stream(allowedValues).anyMatch(value::equals);
        }

        static boolean isOneOfIgnoreCase(final String value, final String[] allowedValues) {
            return Arrays.stream(allowedValues).anyMatch(allowed -> allowed.equalsIgnoreCase(value));
        }

        static boolean startsWith(final String value, final String prefix) {
            return value.startsWith(prefix);
        }

        static boolean endsWith(final String value, final String suffix) {
            return value.endsWith(suffix);
        }

        static boolean contains(final String value, final String substring) {
            return value.contains(substring);
        }

        static boolean isNumeric(final String value) {
            return value.matches("\\d+");
        }

        static boolean isAlphanumeric(final String value) {
            return value.matches("[a-zA-Z0-9]+");
        }

        static boolean isUppercase(final String value) {
            return value.equals(value.toUpperCase());
        }

        static boolean isLowercase(final String value) {
            return value.equals(value.toLowerCase());
        }

        static boolean hasNoWhitespace(final String value) {
            return !value.chars().anyMatch(Character::isWhitespace);
        }

        static boolean hasNoLeadingWhitespace(final String value) {
            return !value.isEmpty() && !Character.isWhitespace(value.charAt(0));
        }

        static boolean hasNoTrailingWhitespace(final String value) {
            return !value.isEmpty() && !Character.isWhitespace(value.charAt(value.length() - 1));
        }

        static boolean hasNoConsecutiveWhitespace(final String value) {
            return !value.matches(".*\\s{2,}.*");
        }

        static boolean isTrimmed(final String value) {
            return value.equals(value.trim());
        }

        static boolean hasProperSpacing(final String value) {
            String normalized = value.trim().replaceAll("\\s+", " ");
            return value.equals(normalized);
        }
    }

    public static ValidationRule<String> notBlank() {
        return createRule(
                ValidationFunctions::isNotBlank,
                StringValidationMetadata::notBlank
        );
    }

    public static ValidationRule<String> maxLength(final int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Maximum length cannot be negative");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.hasMaxLength(value, max),
                identifier -> StringValidationMetadata.maxLength(identifier, max)
        );
    }

    public static ValidationRule<String> minLength(final int min) {
        if (min < 0) {
            throw new IllegalArgumentException("Minimum length cannot be negative");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.hasMinLength(value, min),
                identifier -> StringValidationMetadata.minLength(identifier, min)
        );
    }

    public static ValidationRule<String> exactLength(final int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Exact length cannot be negative");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.hasExactLength(value, length),
                identifier -> StringValidationMetadata.exactLength(identifier, length)
        );
    }

    public static ValidationRule<String> matches(final Pattern pattern) {
        Objects.requireNonNull(pattern, "Pattern must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.matchesPattern(value, pattern),
                identifier -> StringValidationMetadata.matches(identifier, pattern)
        );
    }

    public static ValidationRule<String> matches(final String pattern) {
        Objects.requireNonNull(pattern, "Pattern must not be null");
        if (pattern.isBlank()) {
            throw new IllegalArgumentException("Pattern must not be blank");
        }

        return matches(Pattern.compile(pattern));
    }

    public static ValidationRule<String> oneOf(final String... allowedValues) {
        Objects.requireNonNull(allowedValues, "Allowed values must not be null");
        if (allowedValues.length == 0) {
            throw new IllegalArgumentException("At least one allowed value is required");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isOneOf(value, allowedValues),
                identifier -> StringValidationMetadata.oneOf(identifier, allowedValues)
        );
    }

    public static ValidationRule<String> oneOfIgnoreCase(final String... allowedValues) {
        Objects.requireNonNull(allowedValues, "Allowed values must not be null");
        if (allowedValues.length == 0) {
            throw new IllegalArgumentException("At least one allowed value is required");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isOneOfIgnoreCase(value, allowedValues),
                identifier -> StringValidationMetadata.oneOfIgnoreCase(identifier, allowedValues)
        );
    }

    public static ValidationRule<String> startsWith(final String prefix) {
        Objects.requireNonNull(prefix, "Prefix must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.startsWith(value, prefix),
                identifier -> StringValidationMetadata.startsWith(identifier, prefix)
        );
    }

    public static ValidationRule<String> endsWith(final String suffix) {
        Objects.requireNonNull(suffix, "Suffix must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.endsWith(value, suffix),
                identifier -> StringValidationMetadata.endsWith(identifier, suffix)
        );
    }

    public static ValidationRule<String> contains(final String substring) {
        Objects.requireNonNull(substring, "Substring must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.contains(value, substring),
                identifier -> StringValidationMetadata.contains(identifier, substring)
        );
    }

    public static ValidationRule<String> numeric() {
        return createSkipNullRule(
                ValidationFunctions::isNumeric,
                StringValidationMetadata::numeric
        );
    }

    public static ValidationRule<String> alphanumeric() {
        return createSkipNullRule(
                ValidationFunctions::isAlphanumeric,
                StringValidationMetadata::alphanumeric
        );
    }

    public static ValidationRule<String> uppercase() {
        return createSkipNullRule(
                ValidationFunctions::isUppercase,
                StringValidationMetadata::uppercase
        );
    }

    public static ValidationRule<String> lowercase() {
        return createSkipNullRule(
                ValidationFunctions::isLowercase,
                StringValidationMetadata::lowercase
        );
    }

    public static ValidationRule<String> noWhitespace() {
        return createSkipNullRule(
                ValidationFunctions::hasNoWhitespace,
                StringValidationMetadata::noWhitespace
        );
    }

    public static ValidationRule<String> noLeadingWhitespace() {
        return createSkipNullRule(
                ValidationFunctions::hasNoLeadingWhitespace,
                StringValidationMetadata::noLeadingWhitespace
        );
    }

    public static ValidationRule<String> noTrailingWhitespace() {
        return createSkipNullRule(
                ValidationFunctions::hasNoTrailingWhitespace,
                StringValidationMetadata::noTrailingWhitespace
        );
    }

    public static ValidationRule<String> noConsecutiveWhitespace() {
        return createSkipNullRule(
                ValidationFunctions::hasNoConsecutiveWhitespace,
                StringValidationMetadata::noConsecutiveWhitespace
        );
    }

    public static ValidationRule<String> trimmed() {
        return createSkipNullRule(
                ValidationFunctions::isTrimmed,
                StringValidationMetadata::trimmed
        );
    }

    public static ValidationRule<String> properSpacing() {
        return createSkipNullRule(
                ValidationFunctions::hasProperSpacing,
                StringValidationMetadata::properSpacing
        );
    }
}