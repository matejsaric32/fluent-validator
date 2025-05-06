package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class StringValidationMetadata extends ValidationMetadata {

    protected StringValidationMetadata(ValidationIdentifier identifier,
                                       DefaultValidationCode code,
                                       Map<String, String> messageParameters) {
        super(identifier, code.getCode(), messageParameters);

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    protected void addMessageParameter(MessageParameter param, String value) {
        addMessageParameter(param.getKey(), value);
    }

    public static final class NotBlank extends StringValidationMetadata {

        public NotBlank(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NOT_BLANK, new HashMap<>());
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class MaxLength extends StringValidationMetadata {
        private final int maxLength;

        public MaxLength(ValidationIdentifier identifier, int maxLength) {
            super(identifier, DefaultValidationCode.MAX_LENGTH, new HashMap<>());
            if (maxLength < 0) throw new IllegalArgumentException("Maximum length cannot be negative");
            this.maxLength = maxLength;

            // Add message parameters
            addMessageParameter(MessageParameter.MAX_LENGTH, String.valueOf(maxLength));
        }
    }

    @Getter
    public static final class MinLength extends StringValidationMetadata {
        private final int minLength;

        public MinLength(ValidationIdentifier identifier, int minLength) {
            super(identifier, DefaultValidationCode.MIN_LENGTH, new HashMap<>());
            if (minLength < 0) throw new IllegalArgumentException("Minimum length cannot be negative");
            this.minLength = minLength;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN_LENGTH, String.valueOf(minLength));
        }
    }

    @Getter
    public static final class ExactLength extends StringValidationMetadata {
        private final int exactLength;

        public ExactLength(ValidationIdentifier identifier, int exactLength) {
            super(identifier, DefaultValidationCode.EXACT_LENGTH, new HashMap<>());
            if (exactLength < 0) throw new IllegalArgumentException("Exact length cannot be negative");
            this.exactLength = exactLength;

            // Add message parameters
            addMessageParameter(MessageParameter.EXACT_LENGTH, String.valueOf(exactLength));
        }
    }

    @Getter
    public static final class Matches extends StringValidationMetadata {
        private final Pattern pattern;

        public Matches(ValidationIdentifier identifier, Pattern pattern) {
            super(identifier, DefaultValidationCode.MATCHES, new HashMap<>());
            this.pattern = Objects.requireNonNull(pattern, "Pattern must not be null");

            // Add message parameters
            addMessageParameter(MessageParameter.PATTERN, pattern.pattern());
        }
    }

    @Getter
    public static final class OneOf extends StringValidationMetadata {
        private final String[] allowedValues;

        public OneOf(ValidationIdentifier identifier, String[] allowedValues) {
            super(identifier, DefaultValidationCode.ONE_OF, new HashMap<>());
            this.allowedValues = Objects.requireNonNull(allowedValues, "Allowed values must not be null");
            if (allowedValues.length == 0) throw new IllegalArgumentException("At least one allowed value is required");

            // Add message parameters
            addMessageParameter(MessageParameter.ALLOWED_VALUES, String.join(", ", allowedValues));
        }

        public String[] getAllowedValues() {
            return allowedValues.clone();
        }
    }

    @Getter
    public static final class OneOfIgnoreCase extends StringValidationMetadata {
        private final String[] allowedValues;

        public OneOfIgnoreCase(ValidationIdentifier identifier, String[] allowedValues) {
            super(identifier, DefaultValidationCode.ONE_OF_IGNORE_CASE, new HashMap<>());
            this.allowedValues = Objects.requireNonNull(allowedValues, "Allowed values must not be null");
            if (allowedValues.length == 0) throw new IllegalArgumentException("At least one allowed value is required");

            // Add message parameters
            addMessageParameter(MessageParameter.ALLOWED_VALUES, String.join(", ", allowedValues));
        }

        public String[] getAllowedValues() {
            return allowedValues.clone();
        }
    }

    @Getter
    public static final class StartsWith extends StringValidationMetadata {
        private final String prefix;

        public StartsWith(ValidationIdentifier identifier, String prefix) {
            super(identifier, DefaultValidationCode.STARTS_WITH, new HashMap<>());
            this.prefix = Objects.requireNonNull(prefix, "Prefix must not be null");

            // Add message parameters
            addMessageParameter(MessageParameter.PREFIX, prefix);
        }
    }

    @Getter
    public static final class EndsWith extends StringValidationMetadata {
        private final String suffix;

        public EndsWith(ValidationIdentifier identifier, String suffix) {
            super(identifier, DefaultValidationCode.ENDS_WITH, new HashMap<>());
            this.suffix = Objects.requireNonNull(suffix, "Suffix must not be null");

            // Add message parameters
            addMessageParameter(MessageParameter.SUFFIX, suffix);
        }
    }

    @Getter
    public static final class Contains extends StringValidationMetadata {
        private final String substring;

        public Contains(ValidationIdentifier identifier, String substring) {
            super(identifier, DefaultValidationCode.CONTAINS, new HashMap<>());
            this.substring = Objects.requireNonNull(substring, "Substring must not be null");

            // Add message parameters
            addMessageParameter(MessageParameter.SUBSTRING, substring);
        }
    }

    @Getter
    public static final class Numeric extends StringValidationMetadata {

        public Numeric(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NUMERIC, new HashMap<>());
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class Alphanumeric extends StringValidationMetadata {

        public Alphanumeric(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.ALPHANUMERIC, new HashMap<>());
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class Uppercase extends StringValidationMetadata {

        public Uppercase(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.UPPERCASE, new HashMap<>());
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class Lowercase extends StringValidationMetadata {

        public Lowercase(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.LOWERCASE, new HashMap<>());
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class NoWhitespace extends StringValidationMetadata {

        public NoWhitespace(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NO_WHITESPACE, new HashMap<>());
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class NoLeadingWhitespace extends StringValidationMetadata {

        public NoLeadingWhitespace(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NO_LEADING_WHITESPACE, new HashMap<>());
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class NoTrailingWhitespace extends StringValidationMetadata {

        public NoTrailingWhitespace(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NO_TRAILING_WHITESPACE, new HashMap<>());
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class NoConsecutiveWhitespace extends StringValidationMetadata {

        public NoConsecutiveWhitespace(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NO_CONSECUTIVE_WHITESPACE, new HashMap<>());
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class Trimmed extends StringValidationMetadata {

        public Trimmed(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.TRIMMED, new HashMap<>());
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class ProperSpacing extends StringValidationMetadata {

        public ProperSpacing(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.PROPER_SPACING, new HashMap<>());
            // Field parameter already added in parent constructor
        }
    }

    // Factory methods
    public static NotBlank notBlank(ValidationIdentifier identifier) {
        return new NotBlank(identifier);
    }

    public static MaxLength maxLength(ValidationIdentifier identifier, int maxLength) {
        return new MaxLength(identifier, maxLength);
    }

    public static MinLength minLength(ValidationIdentifier identifier, int minLength) {
        return new MinLength(identifier, minLength);
    }

    public static ExactLength exactLength(ValidationIdentifier identifier, int exactLength) {
        return new ExactLength(identifier, exactLength);
    }

    public static Matches matches(ValidationIdentifier identifier, Pattern pattern) {
        return new Matches(identifier, pattern);
    }

    public static Matches matches(ValidationIdentifier identifier, String pattern) {
        return matches(identifier, Pattern.compile(pattern));
    }

    public static OneOf oneOf(ValidationIdentifier identifier, String... allowedValues) {
        return new OneOf(identifier, allowedValues);
    }

    public static OneOfIgnoreCase oneOfIgnoreCase(ValidationIdentifier identifier, String... allowedValues) {
        return new OneOfIgnoreCase(identifier, allowedValues);
    }

    public static StartsWith startsWith(ValidationIdentifier identifier, String prefix) {
        return new StartsWith(identifier, prefix);
    }

    public static EndsWith endsWith(ValidationIdentifier identifier, String suffix) {
        return new EndsWith(identifier, suffix);
    }

    public static Contains contains(ValidationIdentifier identifier, String substring) {
        return new Contains(identifier, substring);
    }

    public static Numeric numeric(ValidationIdentifier identifier) {
        return new Numeric(identifier);
    }

    public static Alphanumeric alphanumeric(ValidationIdentifier identifier) {
        return new Alphanumeric(identifier);
    }

    public static Uppercase uppercase(ValidationIdentifier identifier) {
        return new Uppercase(identifier);
    }

    public static Lowercase lowercase(ValidationIdentifier identifier) {
        return new Lowercase(identifier);
    }

    public static NoWhitespace noWhitespace(ValidationIdentifier identifier) {
        return new NoWhitespace(identifier);
    }

    public static NoLeadingWhitespace noLeadingWhitespace(ValidationIdentifier identifier) {
        return new NoLeadingWhitespace(identifier);
    }

    public static NoTrailingWhitespace noTrailingWhitespace(ValidationIdentifier identifier) {
        return new NoTrailingWhitespace(identifier);
    }

    public static NoConsecutiveWhitespace noConsecutiveWhitespace(ValidationIdentifier identifier) {
        return new NoConsecutiveWhitespace(identifier);
    }

    public static Trimmed trimmed(ValidationIdentifier identifier) {
        return new Trimmed(identifier);
    }

    public static ProperSpacing properSpacing(ValidationIdentifier identifier) {
        return new ProperSpacing(identifier);
    }
}