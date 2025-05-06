package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class StringValidationMetadata extends ValidationMetadata {

    // Error code constants
    public static final String NOT_BLANK_CODE = "VGS01";
    public static final String MAX_LENGTH_CODE = "VGS02";
    public static final String MIN_LENGTH_CODE = "VGS03";
    public static final String EXACT_LENGTH_CODE = "VGS04";
    public static final String MATCHES_CODE = "VGS05";
    public static final String ONE_OF_CODE = "VGS06";
    public static final String ONE_OF_IGNORE_CASE_CODE = "VGS07";
    public static final String STARTS_WITH_CODE = "VGS08";
    public static final String ENDS_WITH_CODE = "VGS09";
    public static final String CONTAINS_CODE = "VGS10";
    public static final String NUMERIC_CODE = "VGS11";
    public static final String ALPHANUMERIC_CODE = "VGS12";
    public static final String UPPERCASE_CODE = "VGS13";
    public static final String LOWERCASE_CODE = "VGS14";
    public static final String NO_WHITESPACE_CODE = "VGS15";
    public static final String NO_LEADING_WHITESPACE_CODE = "VGS16";
    public static final String NO_TRAILING_WHITESPACE_CODE = "VGS17";
    public static final String NO_CONSECUTIVE_WHITESPACE_CODE = "VGS18";
    public static final String TRIMMED_CODE = "VGS19";
    public static final String PROPER_SPACING_CODE = "VGS20";

    // Message templates
    private static final String NOT_BLANK_MESSAGE = "Field '%s' must not be blank.";
    private static final String MAX_LENGTH_MESSAGE = "Field '%s' must not exceed %s characters.";
    private static final String MIN_LENGTH_MESSAGE = "Field '%s' must be at least %s characters long.";
    private static final String EXACT_LENGTH_MESSAGE = "Field '%s' must be exactly %s characters long.";
    private static final String MATCHES_MESSAGE = "Field '%s' must match the pattern: %s.";
    private static final String ONE_OF_MESSAGE = "Field '%s' must be one of the following values: %s.";
    private static final String ONE_OF_IGNORE_CASE_MESSAGE = 
        "Field '%s' must be one of the following values (case-insensitive): %s.";
    private static final String STARTS_WITH_MESSAGE = "Field '%s' must start with '%s'.";
    private static final String ENDS_WITH_MESSAGE = "Field '%s' must end with '%s'.";
    private static final String CONTAINS_MESSAGE = "Field '%s' must contain '%s'.";
    private static final String NUMERIC_MESSAGE = "Field '%s' must contain only digits.";
    private static final String ALPHANUMERIC_MESSAGE = "Field '%s' must contain only letters and digits.";
    private static final String UPPERCASE_MESSAGE = "Field '%s' must be all uppercase.";
    private static final String LOWERCASE_MESSAGE = "Field '%s' must be all lowercase.";
    private static final String NO_WHITESPACE_MESSAGE = "Field '%s' must not contain whitespace.";
    private static final String NO_LEADING_WHITESPACE_MESSAGE = "Field '%s' must not start with whitespace.";
    private static final String NO_TRAILING_WHITESPACE_MESSAGE = "Field '%s' must not end with whitespace.";
    private static final String NO_CONSECUTIVE_WHITESPACE_MESSAGE = "Field '%s' must not contain consecutive spaces.";
    private static final String TRIMMED_MESSAGE = "Field '%s' must not have leading or trailing whitespace.";
    private static final String PROPER_SPACING_MESSAGE = "Field '%s' must use a single space between words.";

    protected StringValidationMetadata(ValidationIdentifier identifier, String errorCode, String message) {
        super(identifier, errorCode, message);
    }

    public static final class NotBlank extends StringValidationMetadata {
        public NotBlank(ValidationIdentifier identifier) {
            super(identifier, NOT_BLANK_CODE, formatMessage(NOT_BLANK_MESSAGE, identifier.value()));
        }
    }

    @Getter
    public static final class MaxLength extends StringValidationMetadata {
        private final int maxLength;
        public MaxLength(ValidationIdentifier identifier, int maxLength) {
            super(identifier, MAX_LENGTH_CODE, formatMessage(MAX_LENGTH_MESSAGE, identifier.value(), String.valueOf(maxLength)));
            if (maxLength < 0) throw new IllegalArgumentException("Maximum length cannot be negative");
            this.maxLength = maxLength;
        }
    }

    @Getter
    public static final class MinLength extends StringValidationMetadata {
        private final int minLength;
        public MinLength(ValidationIdentifier identifier, int minLength) {
            super(identifier, MIN_LENGTH_CODE, formatMessage(MIN_LENGTH_MESSAGE, identifier.value(), String.valueOf(minLength)));
            if (minLength < 0) throw new IllegalArgumentException("Minimum length cannot be negative");
            this.minLength = minLength;
        }
    }

    @Getter
    public static final class ExactLength extends StringValidationMetadata {
        private final int exactLength;
        public ExactLength(ValidationIdentifier identifier, int exactLength) {
            super(identifier, EXACT_LENGTH_CODE, formatMessage(EXACT_LENGTH_MESSAGE, identifier.value(), String.valueOf(exactLength)));
            if (exactLength < 0) throw new IllegalArgumentException("Exact length cannot be negative");
            this.exactLength = exactLength;
        }
    }

    @Getter
    public static final class Matches extends StringValidationMetadata {
        private final Pattern pattern;
        public Matches(ValidationIdentifier identifier, Pattern pattern) {
            super(identifier, MATCHES_CODE, formatMessage(MATCHES_MESSAGE, identifier.value(), pattern.pattern()));
            this.pattern = Objects.requireNonNull(pattern, "Pattern must not be null");
        }
    }

    @Getter
    public static final class OneOf extends StringValidationMetadata {
        private final String[] allowedValues;
        public OneOf(ValidationIdentifier identifier, String[] allowedValues) {
            super(identifier, ONE_OF_CODE, formatMessage(ONE_OF_MESSAGE, identifier.value(), String.join(", ", allowedValues)));
            this.allowedValues = Objects.requireNonNull(allowedValues, "Allowed values must not be null");
            if (allowedValues.length == 0) throw new IllegalArgumentException("At least one allowed value is required");
        }
        public String[] getAllowedValues() {
            return allowedValues.clone();
        }
    }

    @Getter
    public static final class OneOfIgnoreCase extends StringValidationMetadata {
        private final String[] allowedValues;
        public OneOfIgnoreCase(ValidationIdentifier identifier, String[] allowedValues) {
            super(identifier, ONE_OF_IGNORE_CASE_CODE, formatMessage(ONE_OF_IGNORE_CASE_MESSAGE, identifier.value(), String.join(", ", allowedValues)));
            this.allowedValues = Objects.requireNonNull(allowedValues, "Allowed values must not be null");
            if (allowedValues.length == 0) throw new IllegalArgumentException("At least one allowed value is required");
        }
        public String[] getAllowedValues() {
            return allowedValues.clone();
        }
    }

    @Getter
    public static final class StartsWith extends StringValidationMetadata {
        private final String prefix;
        public StartsWith(ValidationIdentifier identifier, String prefix) {
            super(identifier, STARTS_WITH_CODE, formatMessage(STARTS_WITH_MESSAGE, identifier.value(), prefix));
            this.prefix = Objects.requireNonNull(prefix, "Prefix must not be null");
        }
    }

    @Getter
    public static final class EndsWith extends StringValidationMetadata {
        private final String suffix;
        public EndsWith(ValidationIdentifier identifier, String suffix) {
            super(identifier, ENDS_WITH_CODE, formatMessage(ENDS_WITH_MESSAGE, identifier.value(), suffix));
            this.suffix = Objects.requireNonNull(suffix, "Suffix must not be null");
        }
    }

    @Getter
    public static final class Contains extends StringValidationMetadata {
        private final String substring;
        public Contains(ValidationIdentifier identifier, String substring) {
            super(identifier, CONTAINS_CODE, formatMessage(CONTAINS_MESSAGE, identifier.value(), substring));
            this.substring = Objects.requireNonNull(substring, "Substring must not be null");
        }
    }

    @Getter
    public static final class Numeric extends StringValidationMetadata {
        public Numeric(ValidationIdentifier identifier) {
            super(identifier, NUMERIC_CODE, formatMessage(NUMERIC_MESSAGE, identifier.value()));
        }
    }

    @Getter
    public static final class Alphanumeric extends StringValidationMetadata {
        public Alphanumeric(ValidationIdentifier identifier) {
            super(identifier, ALPHANUMERIC_CODE, formatMessage(ALPHANUMERIC_MESSAGE, identifier.value()));
        }
    }

    @Getter
    public static final class Uppercase extends StringValidationMetadata {
        public Uppercase(ValidationIdentifier identifier) {
            super(identifier, UPPERCASE_CODE, formatMessage(UPPERCASE_MESSAGE, identifier.value()));
        }
    }

    @Getter
    public static final class Lowercase extends StringValidationMetadata {
        public Lowercase(ValidationIdentifier identifier) {
            super(identifier, LOWERCASE_CODE, formatMessage(LOWERCASE_MESSAGE, identifier.value()));
        }
    }

    @Getter
    public static final class NoWhitespace extends StringValidationMetadata {
        public NoWhitespace(ValidationIdentifier identifier) {
            super(identifier, NO_WHITESPACE_CODE, formatMessage(NO_WHITESPACE_MESSAGE, identifier.value()));
        }
    }

    @Getter
    public static final class NoLeadingWhitespace extends StringValidationMetadata {
        public NoLeadingWhitespace(ValidationIdentifier identifier) {
            super(identifier, NO_LEADING_WHITESPACE_CODE, formatMessage(NO_LEADING_WHITESPACE_MESSAGE, identifier.value()));
        }
    }

    @Getter
    public static final class NoTrailingWhitespace extends StringValidationMetadata {
        public NoTrailingWhitespace(ValidationIdentifier identifier) {
            super(identifier, NO_TRAILING_WHITESPACE_CODE, formatMessage(NO_TRAILING_WHITESPACE_MESSAGE, identifier.value()));
        }
    }

    @Getter
    public static final class NoConsecutiveWhitespace extends StringValidationMetadata {
        public NoConsecutiveWhitespace(ValidationIdentifier identifier) {
            super(identifier, NO_CONSECUTIVE_WHITESPACE_CODE, formatMessage(NO_CONSECUTIVE_WHITESPACE_MESSAGE, identifier.value()));
        }
    }

    @Getter
    public static final class Trimmed extends StringValidationMetadata {
        public Trimmed(ValidationIdentifier identifier) {
            super(identifier, TRIMMED_CODE, formatMessage(TRIMMED_MESSAGE, identifier.value()));
        }
    }

    @Getter
    public static final class ProperSpacing extends StringValidationMetadata {
        public ProperSpacing(ValidationIdentifier identifier) {
            super(identifier, PROPER_SPACING_CODE, formatMessage(PROPER_SPACING_MESSAGE, identifier.value()));
        }
    }
}
