package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.Getter;

import java.util.Objects;
import java.util.regex.Pattern;

public abstract class StringValidationMetadata extends ValidationMetadata {

    protected StringValidationMetadata(ValidationIdentifier identifier,
                                       DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    public static final class NotBlank extends StringValidationMetadata {
        private NotBlank(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NOT_BLANK);
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class MaxLength extends StringValidationMetadata {
        private final int maxLength;

        private MaxLength(ValidationIdentifier identifier, int maxLength) {
            super(identifier, DefaultValidationCode.MAX_LENGTH);
            this.maxLength = maxLength;

            // Add message parameters
            addMessageParameter(MessageParameter.MAX_LENGTH, String.valueOf(maxLength));
        }
    }

    @Getter
    public static final class MinLength extends StringValidationMetadata {
        private final int minLength;

        private MinLength(ValidationIdentifier identifier, int minLength) {
            super(identifier, DefaultValidationCode.MIN_LENGTH);
            this.minLength = minLength;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN_LENGTH, String.valueOf(minLength));
        }
    }

    @Getter
    public static final class ExactLength extends StringValidationMetadata {
        private final int exactLength;

        private ExactLength(ValidationIdentifier identifier, int exactLength) {
            super(identifier, DefaultValidationCode.EXACT_LENGTH);
            this.exactLength = exactLength;

            // Add message parameters
            addMessageParameter(MessageParameter.EXACT_LENGTH, String.valueOf(exactLength));
        }
    }

    @Getter
    public static final class Matches extends StringValidationMetadata {
        private final Pattern pattern;

        private Matches(ValidationIdentifier identifier, Pattern pattern) {
            super(identifier, DefaultValidationCode.MATCHES);
            this.pattern = pattern;

            // Add message parameters
            addMessageParameter(MessageParameter.PATTERN, pattern.pattern());
        }
    }

    @Getter
    public static final class OneOf extends StringValidationMetadata {
        private final String[] allowedValues;

        private OneOf(ValidationIdentifier identifier, String[] allowedValues) {
            super(identifier, DefaultValidationCode.ONE_OF);
            this.allowedValues = allowedValues;

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

        private OneOfIgnoreCase(ValidationIdentifier identifier, String[] allowedValues) {
            super(identifier, DefaultValidationCode.ONE_OF_IGNORE_CASE);
            this.allowedValues = allowedValues;

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

        private StartsWith(ValidationIdentifier identifier, String prefix) {
            super(identifier, DefaultValidationCode.STARTS_WITH);
            this.prefix = prefix;

            // Add message parameters
            addMessageParameter(MessageParameter.PREFIX, prefix);
        }
    }

    @Getter
    public static final class EndsWith extends StringValidationMetadata {
        private final String suffix;

        private EndsWith(ValidationIdentifier identifier, String suffix) {
            super(identifier, DefaultValidationCode.ENDS_WITH);
            this.suffix = suffix;

            // Add message parameters
            addMessageParameter(MessageParameter.SUFFIX, suffix);
        }
    }

    @Getter
    public static final class Contains extends StringValidationMetadata {
        private final String substring;

        private Contains(ValidationIdentifier identifier, String substring) {
            super(identifier, DefaultValidationCode.CONTAINS);
            this.substring = substring;

            // Add message parameters
            addMessageParameter(MessageParameter.SUBSTRING, substring);
        }
    }

    @Getter
    public static final class Numeric extends StringValidationMetadata {
        private Numeric(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NUMERIC);
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class Alphanumeric extends StringValidationMetadata {
        private Alphanumeric(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.ALPHANUMERIC);
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class Uppercase extends StringValidationMetadata {
        private Uppercase(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.UPPERCASE);
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class Lowercase extends StringValidationMetadata {
        private Lowercase(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.LOWERCASE);
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class NoWhitespace extends StringValidationMetadata {
        private NoWhitespace(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NO_WHITESPACE);
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class NoLeadingWhitespace extends StringValidationMetadata {
        private NoLeadingWhitespace(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NO_LEADING_WHITESPACE);
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class NoTrailingWhitespace extends StringValidationMetadata {
        private NoTrailingWhitespace(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NO_TRAILING_WHITESPACE);
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class NoConsecutiveWhitespace extends StringValidationMetadata {
        private NoConsecutiveWhitespace(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NO_CONSECUTIVE_WHITESPACE);
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class Trimmed extends StringValidationMetadata {
        private Trimmed(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.TRIMMED);
            // Field parameter already added in parent constructor
        }
    }

    @Getter
    public static final class ProperSpacing extends StringValidationMetadata {
        private ProperSpacing(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.PROPER_SPACING);
            // Field parameter already added in parent constructor
        }
    }

    // Factory methods
    public static NotBlank notBlank(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new NotBlank(identifier);
    }

    public static MaxLength maxLength(ValidationIdentifier identifier, int maxLength) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        if (maxLength < 0) {
            throw new IllegalArgumentException("Maximum length cannot be negative");
        }

        return new MaxLength(identifier, maxLength);
    }

    public static MinLength minLength(ValidationIdentifier identifier, int minLength) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        if (minLength < 0) {
            throw new IllegalArgumentException("Minimum length cannot be negative");
        }

        return new MinLength(identifier, minLength);
    }

    public static ExactLength exactLength(ValidationIdentifier identifier, int exactLength) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        if (exactLength < 0) {
            throw new IllegalArgumentException("Exact length cannot be negative");
        }

        return new ExactLength(identifier, exactLength);
    }

    public static Matches matches(ValidationIdentifier identifier, Pattern pattern) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(pattern, "Pattern must not be null");

        return new Matches(identifier, pattern);
    }

    public static Matches matches(ValidationIdentifier identifier, String pattern) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(pattern, "Pattern must not be null");
        if (pattern.isBlank()) {
            throw new IllegalArgumentException("Pattern must not be blank");
        }

        return matches(identifier, Pattern.compile(pattern));
    }

    public static OneOf oneOf(ValidationIdentifier identifier, String... allowedValues) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(allowedValues, "Allowed values must not be null");
        if (allowedValues.length == 0) {
            throw new IllegalArgumentException("At least one allowed value is required");
        }

        return new OneOf(identifier, allowedValues);
    }

    public static OneOfIgnoreCase oneOfIgnoreCase(ValidationIdentifier identifier, String... allowedValues) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(allowedValues, "Allowed values must not be null");
        if (allowedValues.length == 0) {
            throw new IllegalArgumentException("At least one allowed value is required");
        }

        return new OneOfIgnoreCase(identifier, allowedValues);
    }

    public static StartsWith startsWith(ValidationIdentifier identifier, String prefix) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(prefix, "Prefix must not be null");

        return new StartsWith(identifier, prefix);
    }

    public static EndsWith endsWith(ValidationIdentifier identifier, String suffix) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(suffix, "Suffix must not be null");

        return new EndsWith(identifier, suffix);
    }

    public static Contains contains(ValidationIdentifier identifier, String substring) {
        Objects.requireNonNull(identifier, "Identifier must not be null");
        Objects.requireNonNull(substring, "Substring must not be null");

        return new Contains(identifier, substring);
    }

    public static Numeric numeric(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new Numeric(identifier);
    }

    public static Alphanumeric alphanumeric(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new Alphanumeric(identifier);
    }

    public static Uppercase uppercase(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new Uppercase(identifier);
    }

    public static Lowercase lowercase(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new Lowercase(identifier);
    }

    public static NoWhitespace noWhitespace(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new NoWhitespace(identifier);
    }

    public static NoLeadingWhitespace noLeadingWhitespace(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new NoLeadingWhitespace(identifier);
    }

    public static NoTrailingWhitespace noTrailingWhitespace(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new NoTrailingWhitespace(identifier);
    }

    public static NoConsecutiveWhitespace noConsecutiveWhitespace(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new NoConsecutiveWhitespace(identifier);
    }

    public static Trimmed trimmed(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new Trimmed(identifier);
    }

    public static ProperSpacing properSpacing(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, "Identifier must not be null");

        return new ProperSpacing(identifier);
    }
}