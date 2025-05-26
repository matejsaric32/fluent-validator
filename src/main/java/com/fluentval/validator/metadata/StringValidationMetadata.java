package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Abstract base class for validation metadata related to string constraints and validations.
 * This class serves as the foundation for all validation metadata types that deal with string
 * length validation, pattern matching, character composition, whitespace handling, and content
 * validation, providing common infrastructure for comprehensive string validation scenarios.
 *
 * <p>StringValidationMetadata supports various validation patterns:</p>
 * <ul>
 * <li><strong>Length validation</strong> - ensuring strings meet minimum, maximum, or exact length requirements</li>
 * <li><strong>Content validation</strong> - checking for blank/empty strings and required content</li>
 * <li><strong>Pattern validation</strong> - verifying strings match regular expression patterns</li>
 * <li><strong>Character composition</strong> - validating numeric, alphanumeric, and case requirements</li>
 * <li><strong>Substring validation</strong> - checking for prefixes, suffixes, and contained substrings</li>
 * <li><strong>Whitespace validation</strong> - controlling leading, trailing, and consecutive whitespace</li>
 * <li><strong>Format validation</strong> - ensuring proper spacing, trimming, and text formatting</li>
 * <li><strong>Enumeration validation</strong> - validating against predefined sets of allowed values</li>
 * </ul>
 *
 * <p>This class provides comprehensive string validation capabilities suitable for form validation,
 * data input validation, configuration validation, user input sanitization, and business rule
 * enforcement. Each validation type includes detailed constraint information and human-readable
 * descriptions for comprehensive error messaging and validation reporting.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationMetadata
 * @see DefaultValidationCode
 * @see Pattern
 */
public abstract class StringValidationMetadata extends ValidationMetadata {

    /**
     * Constructs StringValidationMetadata with the specified identifier and validation code.
     *
     * <p>This constructor initializes the base metadata with the validation identifier and
     * automatically adds the field identifier to the message parameters for error message
     * template substitution.</p>
     *
     * @param identifier the validation identifier specifying what failed validation
     * @param code the default validation code categorizing the specific string validation type
     */
    protected StringValidationMetadata(ValidationIdentifier identifier,
                                       DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    /**
     * Validation metadata for non-blank string constraints.
     *
     * <p>This class represents validation failures where a string must not be blank. A string
     * is considered blank if it is null, empty, or contains only whitespace characters.
     * This validation ensures that meaningful content is provided for required string fields.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that names, titles, and descriptions contain meaningful content</li>
     * <li>Ensuring that required form fields are not left empty or filled with only spaces</li>
     * <li>Checking that user input contains actual text content rather than whitespace</li>
     * <li>Verifying that configuration values contain meaningful settings</li>
     * <li>Validating that search terms and keywords are not blank</li>
     * <li>Ensuring that comments and feedback contain actual content</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on Java's String.isBlank() semantics,
     * which considers a string blank if it is null, empty (length 0), or contains only whitespace
     * characters. The validation inherits the field identifier from the parent class for error messaging.</li>
     * </ul>
     *
     * <p><strong>Validation Logic:</strong> A string passes this validation if it is not null,
     * not empty, and contains at least one non-whitespace character. Empty strings, strings
     * containing only spaces, tabs, newlines, or other whitespace characters will fail validation.</p>
     */
    public static final class NotBlank extends StringValidationMetadata {
        private NotBlank(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NOT_BLANK);
            // Field parameter already added in parent constructor
        }
    }

    /**
     * Validation metadata for maximum string length constraints.
     *
     * <p>This class represents validation failures where a string must not exceed a specified
     * maximum length. It provides upper bound length validation to prevent strings from
     * becoming too long for storage, processing, or display purposes.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that user names and titles fit within database column limits</li>
     * <li>Ensuring that descriptions and comments don't exceed maximum allowed lengths</li>
     * <li>Checking that input fields respect UI display constraints</li>
     * <li>Verifying that file names and paths don't exceed system limitations</li>
     * <li>Validating that URLs and email addresses fit within reasonable bounds</li>
     * <li>Ensuring that configuration values don't exceed processing limits</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>maximumLength</strong> - {@code int} - The maximum number of characters that the
     * string may contain to pass validation. This value represents the upper bound constraint
     * and is used in error messages to inform users of the limitation. Must be non-negative.
     * The length is calculated using String.length(), which counts Unicode code units.</li>
     * </ul>
     *
     * <p><strong>Character Counting:</strong> Length validation uses Java's String.length() method,
     * which counts Unicode code units (char values). For most common characters, this corresponds
     * to the visual character count, but some Unicode characters may require multiple code units.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class MaxLength extends StringValidationMetadata {
        private final int maximumLength;

        private MaxLength(ValidationIdentifier identifier, int maximumLength) {
            super(identifier, DefaultValidationCode.MAX_LENGTH);
            this.maximumLength = maximumLength;

            // Add message parameters
            addMessageParameter(MessageParameter.MAX_LENGTH, String.valueOf(maximumLength));
        }
    }

    /**
     * Validation metadata for minimum string length constraints.
     *
     * <p>This class represents validation failures where a string must contain at least a specified
     * minimum number of characters. It provides lower bound length validation to ensure strings
     * contain sufficient content for meaningful processing and storage.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that passwords meet minimum length requirements for security</li>
     * <li>Ensuring that names and descriptions contain sufficient detail</li>
     * <li>Checking that product codes and identifiers meet minimum length standards</li>
     * <li>Verifying that search terms are long enough to be meaningful</li>
     * <li>Validating that configuration values contain adequate information</li>
     * <li>Ensuring that user input provides enough context for processing</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>minimumLength</strong> - {@code int} - The minimum number of characters that the
     * string must contain to pass validation. This value represents the lower bound constraint
     * and is used in error messages to inform users of the requirement. Must be non-negative.
     * The length is calculated using String.length(), which counts Unicode code units.</li>
     * </ul>
     *
     * <p><strong>Character Counting:</strong> Length validation uses Java's String.length() method,
     * which counts Unicode code units (char values). For most common characters, this corresponds
     * to the visual character count, but some Unicode characters may require multiple code units.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class MinLength extends StringValidationMetadata {
        private final int minimumLength;

        private MinLength(ValidationIdentifier identifier, int minimumLength) {
            super(identifier, DefaultValidationCode.MIN_LENGTH);
            this.minimumLength = minimumLength;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN_LENGTH, String.valueOf(minimumLength));
        }
    }

    /**
     * Validation metadata for exact string length constraints.
     *
     * <p>This class represents validation failures where a string must contain exactly a specified
     * number of characters. It provides precise length validation for fixed-format strings
     * such as codes, identifiers, and structured data.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that product codes, serial numbers, and IDs have exact required lengths</li>
     * <li>Ensuring that formatted strings like postal codes match expected formats</li>
     * <li>Checking that fixed-width data fields contain the correct number of characters</li>
     * <li>Verifying that cryptographic keys and hashes have precise required lengths</li>
     * <li>Validating that coordinate strings and measurements use exact formatting</li>
     * <li>Ensuring that database key fields match exact length requirements</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>requiredSize</strong> - {@code int} - The exact number of characters that the
     * string must contain to pass validation. This represents the precise length constraint
     * and is used in error messages to inform users of the exact requirement. Must be non-negative.
     * The length is calculated using String.length(), which counts Unicode code units.</li>
     * </ul>
     *
     * <p><strong>Character Counting:</strong> Length validation uses Java's String.length() method,
     * which counts Unicode code units (char values). For most common characters, this corresponds
     * to the visual character count, but some Unicode characters may require multiple code units.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ExactLength extends StringValidationMetadata {
        private final int requiredSize;

        private ExactLength(ValidationIdentifier identifier, int requiredSize) {
            super(identifier, DefaultValidationCode.EXACT_LENGTH);
            this.requiredSize = requiredSize;

            // Add message parameters
            addMessageParameter(MessageParameter.EXACT_LENGTH, String.valueOf(requiredSize));
        }
    }

    /**
     * Validation metadata for regular expression pattern matching constraints.
     *
     * <p>This class represents validation failures where a string must match a specified regular
     * expression pattern. It provides powerful pattern-based validation for complex string
     * formats, structured data, and custom validation rules.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating email addresses, phone numbers, and other formatted contact information</li>
     * <li>Ensuring that URLs, IP addresses, and network identifiers follow correct formats</li>
     * <li>Checking that product codes, serial numbers, and identifiers match required patterns</li>
     * <li>Verifying that dates, times, and timestamps use expected formatting</li>
     * <li>Validating that credit card numbers, social security numbers, and financial data are properly formatted</li>
     * <li>Ensuring that custom business identifiers follow organizational standards</li>
     * <li>Checking that user input matches specific format requirements</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>pattern</strong> - {@code Pattern} - The compiled regular expression pattern that
     * the string must match to pass validation. This pattern is used for matching operations
     * and its string representation is included in error messages. The pattern is pre-compiled
     * for performance and reusability across multiple validation operations.</li>
     * </ul>
     *
     * <p><strong>Pattern Matching:</strong> Validation uses Pattern.matcher(string).matches(),
     * which requires the entire string to match the pattern. For partial matching, the pattern
     * should be designed accordingly. The pattern is compiled once during metadata creation
     * for optimal performance during validation operations.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Matches extends StringValidationMetadata {
        private final Pattern pattern;

        private Matches(ValidationIdentifier identifier, Pattern pattern) {
            super(identifier, DefaultValidationCode.MATCHES);
            this.pattern = pattern;

            // Add message parameters
            addMessageParameter(MessageParameter.PATTERN, pattern.pattern());
        }
    }

    /**
     * Validation metadata for case-sensitive enumeration constraints.
     *
     * <p>This class represents validation failures where a string must be exactly one of a specified
     * set of allowed values. It provides case-sensitive enumeration validation for controlled
     * vocabularies, option sets, and predefined choices.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that status values match exactly defined states (e.g., "ACTIVE", "INACTIVE")</li>
     * <li>Ensuring that category names match predefined classification options</li>
     * <li>Checking that configuration settings use exact allowed values</li>
     * <li>Verifying that user selections match available menu options</li>
     * <li>Validating that code values correspond to system-defined constants</li>
     * <li>Ensuring that API parameters use exact specified values</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>allowedValues</strong> - {@code String[]} - The array of exact string values that
     * are acceptable for validation. The string must match one of these values exactly, including
     * case sensitivity. This array is stored internally and a defensive copy is provided through
     * the getAllowedValues() method to prevent external modification.</li>
     * </ul>
     *
     * <p><strong>Matching Logic:</strong> Validation uses exact string equality (String.equals()),
     * which is case-sensitive and requires perfect character-by-character matching. For case-insensitive
     * matching, use {@link OneOfIgnoreCase} instead.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
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

    /**
     * Validation metadata for case-insensitive enumeration constraints.
     *
     * <p>This class represents validation failures where a string must be one of a specified
     * set of allowed values, ignoring case differences. It provides case-insensitive enumeration
     * validation for user-friendly option matching and flexible input handling.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating user input where case variations are acceptable (e.g., "yes", "YES", "Yes")</li>
     * <li>Ensuring that configuration values match options regardless of case</li>
     * <li>Checking that user selections match available choices with case flexibility</li>
     * <li>Verifying that status indicators work with various case formats</li>
     * <li>Validating that boolean-like strings match regardless of capitalization</li>
     * <li>Ensuring that command or action names are recognized in any case</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>allowedValues</strong> - {@code String[]} - The array of string values that
     * are acceptable for validation. The string must match one of these values ignoring case
     * differences. This array is stored internally and a defensive copy is provided through
     * the getAllowedValues() method to prevent external modification.</li>
     * </ul>
     *
     * <p><strong>Matching Logic:</strong> Validation uses case-insensitive string comparison
     * (String.equalsIgnoreCase()), which ignores differences in uppercase and lowercase letters
     * but still requires exact character matching otherwise. Unicode case folding rules are applied.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
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

    /**
     * Validation metadata for string prefix constraints.
     *
     * <p>This class represents validation failures where a string must begin with a specified
     * prefix. It provides prefix validation for structured identifiers, formatted strings,
     * and content that must start with specific patterns.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that URLs begin with required protocol prefixes (e.g., "https://")</li>
     * <li>Ensuring that product codes start with specific department or category prefixes</li>
     * <li>Checking that file names begin with required naming conventions</li>
     * <li>Verifying that user IDs or account numbers start with specific prefixes</li>
     * <li>Validating that phone numbers begin with required country or area codes</li>
     * <li>Ensuring that configuration keys start with expected namespace prefixes</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>prefix</strong> - {@code String} - The exact string that must appear at the
     * beginning of the validated string. The validation uses String.startsWith(), which performs
     * case-sensitive comparison. The prefix must not be null but can be empty (which would
     * make all strings pass validation).</li>
     * </ul>
     *
     * <p><strong>Matching Logic:</strong> Validation uses String.startsWith() method, which
     * performs case-sensitive prefix matching. The entire prefix string must appear at the
     * beginning of the validated string for the validation to pass.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class StartsWith extends StringValidationMetadata {
        private final String prefix;

        private StartsWith(ValidationIdentifier identifier, String prefix) {
            super(identifier, DefaultValidationCode.STARTS_WITH);
            this.prefix = prefix;

            // Add message parameters
            addMessageParameter(MessageParameter.PREFIX, prefix);
        }
    }

    /**
     * Validation metadata for string suffix constraints.
     *
     * <p>This class represents validation failures where a string must end with a specified
     * suffix. It provides suffix validation for file extensions, formatted identifiers,
     * and content that must conclude with specific patterns.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that file names end with required extensions (e.g., ".pdf", ".jpg")</li>
     * <li>Ensuring that URLs end with specific path suffixes or file types</li>
     * <li>Checking that email addresses end with approved domain suffixes</li>
     * <li>Verifying that product codes conclude with specific type or version suffixes</li>
     * <li>Validating that configuration values end with required unit indicators</li>
     * <li>Ensuring that identifiers end with expected format markers</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>suffix</strong> - {@code String} - The exact string that must appear at the
     * end of the validated string. The validation uses String.endsWith(), which performs
     * case-sensitive comparison. The suffix must not be null but can be empty (which would
     * make all strings pass validation).</li>
     * </ul>
     *
     * <p><strong>Matching Logic:</strong> Validation uses String.endsWith() method, which
     * performs case-sensitive suffix matching. The entire suffix string must appear at the
     * end of the validated string for the validation to pass.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class EndsWith extends StringValidationMetadata {
        private final String suffix;

        private EndsWith(ValidationIdentifier identifier, String suffix) {
            super(identifier, DefaultValidationCode.ENDS_WITH);
            this.suffix = suffix;

            // Add message parameters
            addMessageParameter(MessageParameter.SUFFIX, suffix);
        }
    }

    /**
     * Validation metadata for substring containment constraints.
     *
     * <p>This class represents validation failures where a string must contain a specified
     * substring anywhere within its content. It provides substring validation for content
     * requirements, keyword presence, and partial pattern matching.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that descriptions or comments contain required keywords</li>
     * <li>Ensuring that URLs contain specific domain names or path components</li>
     * <li>Checking that product names include required brand or model indicators</li>
     * <li>Verifying that user input contains mandatory information or terms</li>
     * <li>Validating that file paths contain required directory or file name components</li>
     * <li>Ensuring that configuration strings include necessary parameter indicators</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>substring</strong> - {@code String} - The exact string that must appear somewhere
     * within the validated string. The validation uses String.contains(), which performs
     * case-sensitive substring searching. The substring must not be null but can be empty
     * (which would make all strings pass validation).</li>
     * </ul>
     *
     * <p><strong>Matching Logic:</strong> Validation uses String.contains() method, which
     * performs case-sensitive substring searching. The substring can appear anywhere within
     * the validated string for the validation to pass.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Contains extends StringValidationMetadata {
        private final String substring;

        private Contains(ValidationIdentifier identifier, String substring) {
            super(identifier, DefaultValidationCode.CONTAINS);
            this.substring = substring;

            // Add message parameters
            addMessageParameter(MessageParameter.SUBSTRING, substring);
        }
    }

    /**
     * Validation metadata for numeric string constraints.
     *
     * <p>This class represents validation failures where a string must contain only numeric
     * digits (0-9). It provides numeric character composition validation for strings that
     * should represent numbers, codes, or identifiers containing only digits.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that ID numbers, account numbers, and reference codes contain only digits</li>
     * <li>Ensuring that zip codes, postal codes, and area codes are purely numeric</li>
     * <li>Checking that quantities and counts are provided as digit-only strings</li>
     * <li>Verifying that PIN codes and numeric passwords contain only numbers</li>
     * <li>Validating that serial numbers and part numbers are purely numeric</li>
     * <li>Ensuring that numeric input fields receive only digit characters</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on pattern matching against
     * digits (0-9) and inherits the field identifier from the parent class for error messaging.
     * The validation typically uses a regular expression like "\\d+" to match one or more digits.</li>
     * </ul>
     *
     * <p><strong>Character Validation:</strong> Only ASCII digits (0-9) are typically considered
     * valid. Unicode digits from other number systems are usually not accepted unless specifically
     * configured. Empty strings and strings containing non-digit characters will fail validation.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Numeric extends StringValidationMetadata {
        private Numeric(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NUMERIC);
            // Field parameter already added in parent constructor
        }
    }

    /**
     * Validation metadata for alphanumeric string constraints.
     *
     * <p>This class represents validation failures where a string must contain only alphanumeric
     * characters (letters and digits). It provides character composition validation for strings
     * that should contain only letters (a-z, A-Z) and digits (0-9) without special characters.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that usernames and account names contain only letters and numbers</li>
     * <li>Ensuring that product codes and identifiers use only alphanumeric characters</li>
     * <li>Checking that file names avoid special characters that might cause system issues</li>
     * <li>Verifying that API keys and tokens contain only safe alphanumeric characters</li>
     * <li>Validating that database identifiers and column names are alphanumeric</li>
     * <li>Ensuring that configuration keys use only letters and numbers</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on pattern matching against
     * alphanumeric characters and inherits the field identifier from the parent class for error
     * messaging. The validation typically uses a regular expression like "[a-zA-Z0-9]+" to match
     * one or more alphanumeric characters.</li>
     * </ul>
     *
     * <p><strong>Character Validation:</strong> Only ASCII letters (a-z, A-Z) and digits (0-9)
     * are typically considered valid. Unicode letters from other languages and special characters
     * like spaces, punctuation, and symbols are usually not accepted unless specifically configured.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Alphanumeric extends StringValidationMetadata {
        private Alphanumeric(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.ALPHANUMERIC);
            // Field parameter already added in parent constructor
        }
    }

    /**
     * Validation metadata for uppercase string constraints.
     *
     * <p>This class represents validation failures where a string must contain only uppercase
     * letters. It provides case validation for strings that must be entirely in uppercase
     * format according to formatting requirements or conventions.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that status codes and constants are in uppercase format</li>
     * <li>Ensuring that country codes, currency codes, and standard identifiers are uppercase</li>
     * <li>Checking that configuration keys follow uppercase naming conventions</li>
     * <li>Verifying that API enumeration values use uppercase formatting</li>
     * <li>Validating that database table names and column names are uppercase</li>
     * <li>Ensuring that acronyms and abbreviations maintain uppercase formatting</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on comparing the string with
     * its uppercase version and inherits the field identifier from the parent class for error
     * messaging. The validation typically uses String.equals(string.toUpperCase()) for comparison.</li>
     * </ul>
     *
     * <p><strong>Case Validation:</strong> The validation checks that the string is identical
     * to its uppercase version. This means all alphabetic characters must be uppercase, while
     * non-alphabetic characters (digits, punctuation, whitespace) are allowed and remain unchanged.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Uppercase extends StringValidationMetadata {
        private Uppercase(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.UPPERCASE);
            // Field parameter already added in parent constructor
        }
    }

    /**
     * Validation metadata for lowercase string constraints.
     *
     * <p>This class represents validation failures where a string must contain only lowercase
     * letters. It provides case validation for strings that must be entirely in lowercase
     * format according to formatting requirements or conventions.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that email addresses use lowercase formatting for consistency</li>
     * <li>Ensuring that URL paths and domain names are in lowercase</li>
     * <li>Checking that configuration parameters follow lowercase naming conventions</li>
     * <li>Verifying that database identifiers use lowercase formatting</li>
     * <li>Validating that CSS class names and HTML attributes are lowercase</li>
     * <li>Ensuring that file names follow lowercase naming standards</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on comparing the string with
     * its lowercase version and inherits the field identifier from the parent class for error
     * messaging. The validation typically uses String.equals(string.toLowerCase()) for comparison.</li>
     * </ul>
     *
     * <p><strong>Case Validation:</strong> The validation checks that the string is identical
     * to its lowercase version. This means all alphabetic characters must be lowercase, while
     * non-alphabetic characters (digits, punctuation, whitespace) are allowed and remain unchanged.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Lowercase extends StringValidationMetadata {
        private Lowercase(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.LOWERCASE);
            // Field parameter already added in parent constructor
        }
    }

    /**
     * Validation metadata for no whitespace constraints.
     *
     * <p>This class represents validation failures where a string must contain no whitespace
     * characters at all. It provides whitespace exclusion validation for strings that must
     * be compact and contain no spaces, tabs, newlines, or other whitespace characters.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that passwords and tokens contain no whitespace characters</li>
     * <li>Ensuring that identifiers and codes are compact without spaces</li>
     * <li>Checking that file names and system identifiers contain no whitespace</li>
     * <li>Verifying that API keys and configuration values are whitespace-free</li>
     * <li>Validating that URLs and network addresses contain no spaces</li>
     * <li>Ensuring that database keys and identifiers are compact</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on checking for the presence
     * of any whitespace characters and inherits the field identifier from the parent class
     * for error messaging. Whitespace includes spaces, tabs, newlines, and other Unicode
     * whitespace characters as defined by Character.isWhitespace().</li>
     * </ul>
     *
     * <p><strong>Whitespace Detection:</strong> The validation checks for any character that
     * is considered whitespace according to Java's Character.isWhitespace() method. This includes
     * spaces, tabs, newlines, carriage returns, and other Unicode whitespace characters.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NoWhitespace extends StringValidationMetadata {
        private NoWhitespace(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NO_WHITESPACE);
            // Field parameter already added in parent constructor
        }
    }

    /**
     * Validation metadata for no leading whitespace constraints.
     *
     * <p>This class represents validation failures where a string must not begin with whitespace
     * characters. It provides leading whitespace validation for strings that should start
     * immediately with meaningful content without preceding spaces or tabs.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that user input doesn't begin with accidental leading spaces</li>
     * <li>Ensuring that names and titles start immediately without leading whitespace</li>
     * <li>Checking that configuration values don't have leading spaces that could cause parsing issues</li>
     * <li>Verifying that search terms don't begin with whitespace that could affect results</li>
     * <li>Validating that file names and identifiers start with meaningful characters</li>
     * <li>Ensuring that formatted strings begin properly without leading spaces</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on checking the first character
     * for whitespace and inherits the field identifier from the parent class for error messaging.
     * Only the beginning of the string is checked; whitespace elsewhere is allowed.</li>
     * </ul>
     *
     * <p><strong>Leading Whitespace Detection:</strong> The validation checks if the first
     * character of a non-empty string is whitespace using Character.isWhitespace(). Empty
     * strings typically pass this validation as they have no leading characters to check.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NoLeadingWhitespace extends StringValidationMetadata {
        private NoLeadingWhitespace(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NO_LEADING_WHITESPACE);
            // Field parameter already added in parent constructor
        }
    }

    /**
     * Validation metadata for no trailing whitespace constraints.
     *
     * <p>This class represents validation failures where a string must not end with whitespace
     * characters. It provides trailing whitespace validation for strings that should conclude
     * with meaningful content without trailing spaces or tabs.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that user input doesn't end with accidental trailing spaces</li>
     * <li>Ensuring that names and descriptions end cleanly without trailing whitespace</li>
     * <li>Checking that configuration values don't have trailing spaces that could cause issues</li>
     * <li>Verifying that data entries end properly without trailing whitespace</li>
     * <li>Validating that file names and paths don't end with spaces</li>
     * <li>Ensuring that formatted strings conclude properly without trailing spaces</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on checking the last character
     * for whitespace and inherits the field identifier from the parent class for error messaging.
     * Only the end of the string is checked; whitespace elsewhere is allowed.</li>
     * </ul>
     *
     * <p><strong>Trailing Whitespace Detection:</strong> The validation checks if the last
     * character of a non-empty string is whitespace using Character.isWhitespace(). Empty
     * strings typically pass this validation as they have no trailing characters to check.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NoTrailingWhitespace extends StringValidationMetadata {
        private NoTrailingWhitespace(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NO_TRAILING_WHITESPACE);
            // Field parameter already added in parent constructor
        }
    }

    /**
     * Validation metadata for no consecutive whitespace constraints.
     *
     * <p>This class represents validation failures where a string must not contain consecutive
     * whitespace characters. It provides whitespace formatting validation for strings that
     * should have at most single spaces between words or elements.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that formatted text doesn't contain multiple consecutive spaces</li>
     * <li>Ensuring that names and descriptions use proper single-space formatting</li>
     * <li>Checking that user input doesn't contain excessive whitespace that could affect display</li>
     * <li>Verifying that search terms don't have multiple spaces that could impact matching</li>
     * <li>Validating that configuration values use consistent single-space formatting</li>
     * <li>Ensuring that data entries maintain clean formatting without extra spaces</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on pattern matching to detect
     * consecutive whitespace characters and inherits the field identifier from the parent class
     * for error messaging. The validation typically uses regular expressions to find sequences
     * of two or more whitespace characters.</li>
     * </ul>
     *
     * <p><strong>Consecutive Whitespace Detection:</strong> The validation identifies sequences
     * of two or more consecutive whitespace characters anywhere in the string. Single whitespace
     * characters are allowed, but multiple adjacent whitespace characters will cause validation failure.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class NoConsecutiveWhitespace extends StringValidationMetadata {
        private NoConsecutiveWhitespace(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NO_CONSECUTIVE_WHITESPACE);
            // Field parameter already added in parent constructor
        }
    }

    /**
     * Validation metadata for trimmed string constraints.
     *
     * <p>This class represents validation failures where a string must be identical to its
     * trimmed version. It provides trimming validation for strings that should not have
     * any leading or trailing whitespace, ensuring clean and consistent formatting.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that user input is clean without leading/trailing spaces</li>
     * <li>Ensuring that names and identifiers are properly trimmed</li>
     * <li>Checking that configuration values don't have extraneous whitespace</li>
     * <li>Verifying that data entries are clean and well-formatted</li>
     * <li>Validating that search terms don't have unnecessary whitespace</li>
     * <li>Ensuring that display strings are properly formatted without edge whitespace</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on comparing the string with
     * its trimmed version and inherits the field identifier from the parent class for error
     * messaging. The validation uses String.trim() which removes leading and trailing whitespace.</li>
     * </ul>
     *
     * <p><strong>Trimming Logic:</strong> The validation checks that the string is identical
     * to its trimmed version using String.trim().equals(originalString). This ensures no
     * leading or trailing whitespace exists while allowing internal whitespace.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Trimmed extends StringValidationMetadata {
        private Trimmed(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.TRIMMED);
            // Field parameter already added in parent constructor
        }
    }

    /**
     * Validation metadata for proper spacing constraints.
     *
     * <p>This class represents validation failures where a string must have proper spacing
     * throughout. It provides comprehensive spacing validation by combining trimming and
     * consecutive whitespace rules to ensure clean, well-formatted text.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that formatted text maintains consistent and clean spacing</li>
     * <li>Ensuring that user input follows proper text formatting standards</li>
     * <li>Checking that descriptions and content use appropriate spacing</li>
     * <li>Verifying that display strings are properly formatted for presentation</li>
     * <li>Validating that search terms and keywords have clean spacing</li>
     * <li>Ensuring that data entries maintain professional formatting standards</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation combines trimming and whitespace
     * normalization logic, inheriting the field identifier from the parent class for error
     * messaging. The validation typically normalizes whitespace and compares with the original.</li>
     * </ul>
     *
     * <p><strong>Proper Spacing Logic:</strong> The validation typically ensures the string
     * is trimmed (no leading/trailing whitespace) and has no consecutive whitespace characters,
     * creating a normalized version with single spaces between words and comparing it to the original.</p>
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class ProperSpacing extends StringValidationMetadata {
        private ProperSpacing(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.PROPER_SPACING);
            // Field parameter already added in parent constructor
        }
    }

    // Factory methods

    /**
     * Factory method for creating NotBlank validation metadata.
     *
     * <p>Creates metadata for validating that a string is not blank (not null, empty, or whitespace-only).</p>
     *
     * @param identifier the validation identifier
     * @return NotBlank metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static NotBlank notBlank(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new NotBlank(identifier);
    }

    /**
     * Factory method for creating MaxLength validation metadata.
     *
     * <p>Creates metadata for validating that a string does not exceed the specified maximum length.</p>
     *
     * @param identifier the validation identifier
     * @param maxLength the maximum allowed number of characters
     * @return MaxLength metadata instance
     * @throws NullPointerException if identifier is null
     * @throws IllegalArgumentException if maxLength is negative
     */
    public static MaxLength maxLength(ValidationIdentifier identifier, int maxLength) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        if (maxLength < 0) {
            throw new IllegalArgumentException("Maximum length cannot be negative");
        }

        return new MaxLength(identifier, maxLength);
    }

    /**
     * Factory method for creating MinLength validation metadata.
     *
     * <p>Creates metadata for validating that a string contains at least the specified minimum number of characters.</p>
     *
     * @param identifier the validation identifier
     * @param minLength the minimum required number of characters
     * @return MinLength metadata instance
     * @throws NullPointerException if identifier is null
     * @throws IllegalArgumentException if minLength is negative
     */
    public static MinLength minLength(ValidationIdentifier identifier, int minLength) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        if (minLength < 0) {
            throw new IllegalArgumentException("Minimum length cannot be negative");
        }

        return new MinLength(identifier, minLength);
    }

    /**
     * Factory method for creating ExactLength validation metadata.
     *
     * <p>Creates metadata for validating that a string contains exactly the specified number of characters.</p>
     *
     * @param identifier the validation identifier
     * @param exactLength the exact required number of characters
     * @return ExactLength metadata instance
     * @throws NullPointerException if identifier is null
     * @throws IllegalArgumentException if exactLength is negative
     */
    public static ExactLength exactLength(ValidationIdentifier identifier, int exactLength) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        if (exactLength < 0) {
            throw new IllegalArgumentException("Exact length cannot be negative");
        }

        return new ExactLength(identifier, exactLength);
    }

    /**
     * Factory method for creating Matches validation metadata with a compiled Pattern.
     *
     * <p>Creates metadata for validating that a string matches the specified regular expression pattern.</p>
     *
     * @param identifier the validation identifier
     * @param pattern the compiled regular expression pattern that the string must match
     * @return Matches metadata instance
     * @throws NullPointerException if identifier or pattern is null
     */
    public static Matches matches(ValidationIdentifier identifier, Pattern pattern) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(pattern, "Pattern must not be null");

        return new Matches(identifier, pattern);
    }

    /**
     * Factory method for creating Matches validation metadata with a pattern string.
     *
     * <p>Creates metadata for validating that a string matches the specified regular expression pattern.
     * The pattern string is compiled into a Pattern object for performance.</p>
     *
     * @param identifier the validation identifier
     * @param pattern the regular expression pattern string that the string must match
     * @return Matches metadata instance
     * @throws NullPointerException if identifier or pattern is null
     * @throws IllegalArgumentException if pattern is blank
     */
    public static Matches matches(ValidationIdentifier identifier, String pattern) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(pattern, "Pattern must not be null");
        if (pattern.isBlank()) {
            throw new IllegalArgumentException("Pattern must not be blank");
        }

        return matches(identifier, Pattern.compile(pattern));
    }

    /**
     * Factory method for creating OneOf validation metadata.
     *
     * <p>Creates metadata for validating that a string exactly matches one of the specified allowed values
     * (case-sensitive).</p>
     *
     * @param identifier the validation identifier
     * @param allowedValues the array of strings that are acceptable values
     * @return OneOf metadata instance
     * @throws NullPointerException if identifier or allowedValues is null
     * @throws IllegalArgumentException if allowedValues array is empty
     */
    public static OneOf oneOf(ValidationIdentifier identifier, String... allowedValues) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(allowedValues, "Allowed values must not be null");
        if (allowedValues.length == 0) {
            throw new IllegalArgumentException("At least one allowed value is required");
        }

        return new OneOf(identifier, allowedValues);
    }

    /**
     * Factory method for creating OneOfIgnoreCase validation metadata.
     *
     * <p>Creates metadata for validating that a string matches one of the specified allowed values
     * ignoring case differences.</p>
     *
     * @param identifier the validation identifier
     * @param allowedValues the array of strings that are acceptable values
     * @return OneOfIgnoreCase metadata instance
     * @throws NullPointerException if identifier or allowedValues is null
     * @throws IllegalArgumentException if allowedValues array is empty
     */
    public static OneOfIgnoreCase oneOfIgnoreCase(ValidationIdentifier identifier, String... allowedValues) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(allowedValues, "Allowed values must not be null");
        if (allowedValues.length == 0) {
            throw new IllegalArgumentException("At least one allowed value is required");
        }

        return new OneOfIgnoreCase(identifier, allowedValues);
    }

    /**
     * Factory method for creating StartsWith validation metadata.
     *
     * <p>Creates metadata for validating that a string begins with the specified prefix.</p>
     *
     * @param identifier the validation identifier
     * @param prefix the string that must appear at the beginning of the validated string
     * @return StartsWith metadata instance
     * @throws NullPointerException if identifier or prefix is null
     */
    public static StartsWith startsWith(ValidationIdentifier identifier, String prefix) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(prefix, "Prefix must not be null");

        return new StartsWith(identifier, prefix);
    }

    /**
     * Factory method for creating EndsWith validation metadata.
     *
     * <p>Creates metadata for validating that a string ends with the specified suffix.</p>
     *
     * @param identifier the validation identifier
     * @param suffix the string that must appear at the end of the validated string
     * @return EndsWith metadata instance
     * @throws NullPointerException if identifier or suffix is null
     */
    public static EndsWith endsWith(ValidationIdentifier identifier, String suffix) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(suffix, "Suffix must not be null");

        return new EndsWith(identifier, suffix);
    }

    /**
     * Factory method for creating Contains validation metadata.
     *
     * <p>Creates metadata for validating that a string contains the specified substring.</p>
     *
     * @param identifier the validation identifier
     * @param substring the string that must appear somewhere within the validated string
     * @return Contains metadata instance
     * @throws NullPointerException if identifier or substring is null
     */
    public static Contains contains(ValidationIdentifier identifier, String substring) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(substring, "Substring must not be null");

        return new Contains(identifier, substring);
    }

    /**
     * Factory method for creating Numeric validation metadata.
     *
     * <p>Creates metadata for validating that a string contains only numeric digits (0-9).</p>
     *
     * @param identifier the validation identifier
     * @return Numeric metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static Numeric numeric(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new Numeric(identifier);
    }

    /**
     * Factory method for creating Alphanumeric validation metadata.
     *
     * <p>Creates metadata for validating that a string contains only alphanumeric characters (letters and digits).</p>
     *
     * @param identifier the validation identifier
     * @return Alphanumeric metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static Alphanumeric alphanumeric(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new Alphanumeric(identifier);
    }

    /**
     * Factory method for creating Uppercase validation metadata.
     *
     * <p>Creates metadata for validating that a string contains only uppercase letters.</p>
     *
     * @param identifier the validation identifier
     * @return Uppercase metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static Uppercase uppercase(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new Uppercase(identifier);
    }

    /**
     * Factory method for creating Lowercase validation metadata.
     *
     * <p>Creates metadata for validating that a string contains only lowercase letters.</p>
     *
     * @param identifier the validation identifier
     * @return Lowercase metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static Lowercase lowercase(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new Lowercase(identifier);
    }

    /**
     * Factory method for creating NoWhitespace validation metadata.
     *
     * <p>Creates metadata for validating that a string contains no whitespace characters.</p>
     *
     * @param identifier the validation identifier
     * @return NoWhitespace metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static NoWhitespace noWhitespace(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new NoWhitespace(identifier);
    }

    /**
     * Factory method for creating NoLeadingWhitespace validation metadata.
     *
     * <p>Creates metadata for validating that a string does not begin with whitespace characters.</p>
     *
     * @param identifier the validation identifier
     * @return NoLeadingWhitespace metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static NoLeadingWhitespace noLeadingWhitespace(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new NoLeadingWhitespace(identifier);
    }

    /**
     * Factory method for creating NoTrailingWhitespace validation metadata.
     *
     * <p>Creates metadata for validating that a string does not end with whitespace characters.</p>
     *
     * @param identifier the validation identifier
     * @return NoTrailingWhitespace metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static NoTrailingWhitespace noTrailingWhitespace(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new NoTrailingWhitespace(identifier);
    }

    /**
     * Factory method for creating NoConsecutiveWhitespace validation metadata.
     *
     * <p>Creates metadata for validating that a string does not contain consecutive whitespace characters.</p>
     *
     * @param identifier the validation identifier
     * @return NoConsecutiveWhitespace metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static NoConsecutiveWhitespace noConsecutiveWhitespace(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new NoConsecutiveWhitespace(identifier);
    }

    /**
     * Factory method for creating Trimmed validation metadata.
     *
     * <p>Creates metadata for validating that a string is identical to its trimmed version
     * (no leading or trailing whitespace).</p>
     *
     * @param identifier the validation identifier
     * @return Trimmed metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static Trimmed trimmed(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new Trimmed(identifier);
    }

    /**
     * Factory method for creating ProperSpacing validation metadata.
     *
     * <p>Creates metadata for validating that a string has proper spacing throughout
     * (trimmed and no consecutive whitespace).</p>
     *
     * @param identifier the validation identifier
     * @return ProperSpacing metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static ProperSpacing properSpacing(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new ProperSpacing(identifier);
    }
}