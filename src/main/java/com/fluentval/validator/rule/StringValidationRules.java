package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.StringValidationMetadata;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.fluentval.validator.rule.ValidationRuleUtils.createRule;
import static com.fluentval.validator.rule.ValidationRuleUtils.createSkipNullRule;

/**
 * Utility class providing validation rules for String values. This class offers comprehensive validation
 * for string length, format, content, case sensitivity, whitespace handling, and pattern matching.
 *
 * <p>Most validation rules in this class automatically skip null values, meaning they will
 * pass validation if the input string is null. The notable exception is {@link #notBlank()}, which
 * explicitly validates against null values. Use in combination with {@code CommonValidationRules.notNull()}
 * if null values should be rejected for other string validations.</p>
 *
 * <p>String validation categories include:</p>
 * <ul>
 * <li><strong>Length validations</strong> - minimum, maximum, exact length constraints</li>
 * <li><strong>Content validations</strong> - blank/empty checks, pattern matching, allowed values</li>
 * <li><strong>Format validations</strong> - numeric, alphanumeric, case sensitivity</li>
 * <li><strong>Positional validations</strong> - prefix, suffix, substring matching</li>
 * <li><strong>Whitespace validations</strong> - leading, trailing, consecutive whitespace controls</li>
 * <li><strong>Pattern validations</strong> - regex matching for complex format requirements</li>
 * </ul>
 *
 * <p>Common use cases include:</p>
 * <ul>
 * <li>User input validation (names, emails, passwords, usernames)</li>
 * <li>Business data validation (product codes, identifiers, descriptions)</li>
 * <li>Format validation (phone numbers, postal codes, URLs)</li>
 * <li>Content validation (allowed values, prohibited content)</li>
 * <li>Data cleanliness (trimming, spacing, case consistency)</li>
 * </ul>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationRule
 * @see StringValidationMetadata
 * @see String
 * @see Pattern
 */
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

    /**
     * Creates a validation rule that checks if a string is not blank.
     *
     * <p>This rule validates that the string is not null, not empty, and contains at least one
     * non-whitespace character. Unlike other string validation rules, this rule does NOT skip
     * null values and will fail validation for null inputs.</p>
     *
     * <p><strong>Note:</strong> This is the only string validation rule that explicitly handles
     * null values. All other string validation rules skip null validation.</p>
     *
     * @return a ValidationRule that passes if the string is not null, not empty, and not blank
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that username is not blank
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("username"), User::getUsername)
     *         .validate(StringValidationRules.notBlank())
     *         .end()
     *     .getResult();
     *
     * // Validate that product name is not blank
     * ValidationResult productResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("name"), Product::getName)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.maxLength(100))
     *         .end()
     *     .getResult();
     *
     * // Validate that email is not blank before format validation
     * ValidationResult emailResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
     *         .end()
     *     .getResult();
     *
     * // Validate required form fields
     * ValidationResult formResult = Validator.of(contactForm)
     *     .property(ValidationIdentifier.ofField("firstName"), ContactForm::getFirstName)
     *         .validate(StringValidationRules.notBlank())
     *         .end()
     *     .property(ValidationIdentifier.ofField("lastName"), ContactForm::getLastName)
     *         .validate(StringValidationRules.notBlank())
     *         .end()
     *     .property(ValidationIdentifier.ofField("message"), ContactForm::getMessage)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.minLength(10))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> notBlank() {
        return createRule(
                ValidationFunctions::isNotBlank,
                StringValidationMetadata::notBlank
        );
    }

    /**
     * Creates a validation rule that checks if a string does not exceed the specified maximum length.
     *
     * <p>This rule validates that the trimmed string length is at most the specified maximum.
     * The rule automatically skips validation for null strings.</p>
     *
     * @param max the maximum allowed length (must be non-negative)
     * @return a ValidationRule that passes if the trimmed string length is <= max
     * @throws IllegalArgumentException if max is negative
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that username doesn't exceed maximum length
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("username"), User::getUsername)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.maxLength(20))
     *         .end()
     *     .getResult();
     *
     * // Validate that product description fits in database field
     * ValidationResult productResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("description"), Product::getDescription)
     *         .validate(StringValidationRules.maxLength(500))
     *         .end()
     *     .getResult();
     *
     * // Validate that comment doesn't exceed platform limits
     * ValidationResult commentResult = Validator.of(comment)
     *     .property(ValidationIdentifier.ofField("content"), Comment::getContent)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.maxLength(280)) // Twitter-like limit
     *         .end()
     *     .getResult();
     *
     * // Validate that address components fit standard formats
     * ValidationResult addressResult = Validator.of(address)
     *     .property(ValidationIdentifier.ofField("streetAddress"), Address::getStreetAddress)
     *         .validate(StringValidationRules.maxLength(100))
     *         .end()
     *     .property(ValidationIdentifier.ofField("city"), Address::getCity)
     *         .validate(StringValidationRules.maxLength(50))
     *         .end()
     *     .property(ValidationIdentifier.ofField("postalCode"), Address::getPostalCode)
     *         .validate(StringValidationRules.maxLength(10))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> maxLength(final int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Maximum length cannot be negative");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.hasMaxLength(value, max),
                identifier -> StringValidationMetadata.maxLength(identifier, max)
        );
    }

    /**
     * Creates a validation rule that checks if a string meets the specified minimum length requirement.
     *
     * <p>This rule validates that the trimmed string length is at least the specified minimum.
     * The rule automatically skips validation for null strings.</p>
     *
     * @param min the minimum required length (must be non-negative)
     * @return a ValidationRule that passes if the trimmed string length is >= min
     * @throws IllegalArgumentException if min is negative
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that password meets minimum length requirement
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("password"), User::getPassword)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.minLength(8))
     *         .end()
     *     .getResult();
     *
     * // Validate that product name is descriptive enough
     * ValidationResult productResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("name"), Product::getName)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.minLength(3))
     *         .validate(StringValidationRules.maxLength(100))
     *         .end()
     *     .getResult();
     *
     * // Validate that review content is substantial
     * ValidationResult reviewResult = Validator.of(review)
     *     .property(ValidationIdentifier.ofField("content"), Review::getContent)
     *         .validate(StringValidationRules.minLength(20))
     *         .end()
     *     .getResult();
     *
     * // Validate that search query is meaningful
     * ValidationResult searchResult = Validator.of(searchRequest)
     *     .property(ValidationIdentifier.ofField("query"), SearchRequest::getQuery)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.minLength(2))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> minLength(final int min) {
        if (min < 0) {
            throw new IllegalArgumentException("Minimum length cannot be negative");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.hasMinLength(value, min),
                identifier -> StringValidationMetadata.minLength(identifier, min)
        );
    }

    /**
     * Creates a validation rule that checks if a string has exactly the specified length.
     *
     * <p>This rule validates that the trimmed string length equals exactly the specified length.
     * The rule automatically skips validation for null strings.</p>
     *
     * @param length the exact required length (must be non-negative)
     * @return a ValidationRule that passes if the trimmed string length equals the specified length
     * @throws IllegalArgumentException if length is negative
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that country code is exactly 2 characters
     * ValidationResult result = Validator.of(address)
     *     .property(ValidationIdentifier.ofField("countryCode"), Address::getCountryCode)
     *         .validate(StringValidationRules.exactLength(2))
     *         .validate(StringValidationRules.uppercase())
     *         .end()
     *     .getResult();
     *
     * // Validate that product SKU has fixed length
     * ValidationResult skuResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("sku"), Product::getSku)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.exactLength(8))
     *         .validate(StringValidationRules.alphanumeric())
     *         .end()
     *     .getResult();
     *
     * // Validate that verification code has exact length
     * ValidationResult codeResult = Validator.of(verification)
     *     .property(ValidationIdentifier.ofField("code"), Verification::getCode)
     *         .validate(StringValidationRules.exactLength(6))
     *         .validate(StringValidationRules.numeric())
     *         .end()
     *     .getResult();
     *
     * // Validate that currency code follows ISO standard
     * ValidationResult currencyResult = Validator.of(price)
     *     .property(ValidationIdentifier.ofField("currencyCode"), Price::getCurrencyCode)
     *         .validate(StringValidationRules.exactLength(3))
     *         .validate(StringValidationRules.uppercase())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> exactLength(final int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Exact length cannot be negative");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.hasExactLength(value, length),
                identifier -> StringValidationMetadata.exactLength(identifier, length)
        );
    }

    /**
     * Creates a validation rule that checks if a string matches the specified regex pattern.
     *
     * <p>This rule validates that the entire string matches the given Pattern.
     * The rule automatically skips validation for null strings.</p>
     *
     * @param pattern the compiled Pattern that the string must match
     * @return a ValidationRule that passes if the string matches the pattern
     * @throws NullPointerException if pattern is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate email format using compiled pattern
     * Pattern emailPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.matches(emailPattern))
     *         .end()
     *     .getResult();
     *
     * // Validate phone number format
     * Pattern phonePattern = Pattern.compile("^\\+?[1-9]\\d{10,14}$");
     * ValidationResult phoneResult = Validator.of(contact)
     *     .property(ValidationIdentifier.ofField("phoneNumber"), Contact::getPhoneNumber)
     *         .validate(StringValidationRules.matches(phonePattern))
     *         .end()
     *     .getResult();
     *
     * // Validate credit card number format (basic)
     * Pattern cardPattern = Pattern.compile("^\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}$");
     * ValidationResult cardResult = Validator.of(payment)
     *     .property(ValidationIdentifier.ofField("cardNumber"), Payment::getCardNumber)
     *         .validate(StringValidationRules.matches(cardPattern))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> matches(final Pattern pattern) {
        Objects.requireNonNull(pattern, "Pattern must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.matchesPattern(value, pattern),
                identifier -> StringValidationMetadata.matches(identifier, pattern)
        );
    }

    /**
     * Creates a validation rule that checks if a string matches the specified regex pattern.
     *
     * <p>This rule validates that the entire string matches the given regex pattern string.
     * The pattern is compiled internally. The rule automatically skips validation for null strings.</p>
     *
     * @param pattern the regex pattern string that the string must match
     * @return a ValidationRule that passes if the string matches the pattern
     * @throws NullPointerException if pattern is null
     * @throws IllegalArgumentException if pattern is blank
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate email format using regex string
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
     *         .end()
     *     .getResult();
     *
     * // Validate postal code format
     * ValidationResult postalResult = Validator.of(address)
     *     .property(ValidationIdentifier.ofField("postalCode"), Address::getPostalCode)
     *         .validate(StringValidationRules.matches("^\\d{5}(-\\d{4})?$")) // US ZIP code
     *         .end()
     *     .getResult();
     *
     * // Validate username format (alphanumeric + underscore)
     * ValidationResult usernameResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("username"), User::getUsername)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.matches("^[a-zA-Z0-9_]{3,20}$"))
     *         .end()
     *     .getResult();
     *
     * // Validate hex color code
     * ValidationResult colorResult = Validator.of(theme)
     *     .property(ValidationIdentifier.ofField("primaryColor"), Theme::getPrimaryColor)
     *         .validate(StringValidationRules.matches("^#[0-9A-Fa-f]{6}$"))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> matches(final String pattern) {
        Objects.requireNonNull(pattern, "Pattern must not be null");
        if (pattern.isBlank()) {
            throw new IllegalArgumentException("Pattern must not be blank");
        }

        return matches(Pattern.compile(pattern));
    }

    /**
     * Creates a validation rule that checks if a string is one of the specified allowed values.
     *
     * <p>This rule validates that the string exactly matches one of the provided allowed values
     * using case-sensitive comparison. The rule automatically skips validation for null strings.</p>
     *
     * @param allowedValues the array of strings that are considered valid (must not be null or empty)
     * @return a ValidationRule that passes if the string equals one of the allowed values
     * @throws NullPointerException if allowedValues is null
     * @throws IllegalArgumentException if allowedValues is empty
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that status is one of allowed values
     * ValidationResult result = Validator.of(order)
     *     .property(ValidationIdentifier.ofField("status"), Order::getStatus)
     *         .validate(StringValidationRules.oneOf("PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"))
     *         .end()
     *     .getResult();
     *
     * // Validate that priority level is allowed
     * ValidationResult priorityResult = Validator.of(task)
     *     .property(ValidationIdentifier.ofField("priority"), Task::getPriority)
     *         .validate(StringValidationRules.oneOf("LOW", "MEDIUM", "HIGH", "CRITICAL"))
     *         .end()
     *     .getResult();
     *
     * // Validate that file extension is supported
     * ValidationResult fileResult = Validator.of(upload)
     *     .property(ValidationIdentifier.ofField("fileExtension"), Upload::getFileExtension)
     *         .validate(StringValidationRules.oneOf("jpg", "jpeg", "png", "gif", "pdf", "doc", "docx"))
     *         .end()
     *     .getResult();
     *
     * // Validate that country code is recognized
     * ValidationResult countryResult = Validator.of(address)
     *     .property(ValidationIdentifier.ofField("countryCode"), Address::getCountryCode)
     *         .validate(StringValidationRules.oneOf("US", "CA", "GB", "DE", "FR", "AU", "JP"))
     *         .end()
     *     .getResult();
     * }</pre>
     */
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

    /**
     * Creates a validation rule that checks if a string is one of the specified allowed values (case-insensitive).
     *
     * <p>This rule validates that the string matches one of the provided allowed values
     * using case-insensitive comparison. The rule automatically skips validation for null strings.</p>
     *
     * @param allowedValues the array of strings that are considered valid (must not be null or empty)
     * @return a ValidationRule that passes if the string equals one of the allowed values (ignoring case)
     * @throws NullPointerException if allowedValues is null
     * @throws IllegalArgumentException if allowedValues is empty
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that boolean-like string is recognized (case-insensitive)
     * ValidationResult result = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("enabled"), Config::getEnabled)
     *         .validate(StringValidationRules.oneOfIgnoreCase("true", "false", "yes", "no", "on", "off"))
     *         .end()
     *     .getResult();
     *
     * // Validate that size designation is valid (case-insensitive)
     * ValidationResult sizeResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("size"), Product::getSize)
     *         .validate(StringValidationRules.oneOfIgnoreCase("XS", "S", "M", "L", "XL", "XXL"))
     *         .end()
     *     .getResult();
     *
     * // Validate that gender option is accepted (case-insensitive)
     * ValidationResult genderResult = Validator.of(profile)
     *     .property(ValidationIdentifier.ofField("gender"), Profile::getGender)
     *         .validate(StringValidationRules.oneOfIgnoreCase("MALE", "FEMALE", "OTHER", "PREFER_NOT_TO_SAY"))
     *         .end()
     *     .getResult();
     *
     * // Validate that log level is valid (case-insensitive)
     * ValidationResult logResult = Validator.of(logger)
     *     .property(ValidationIdentifier.ofField("level"), Logger::getLevel)
     *         .validate(StringValidationRules.oneOfIgnoreCase("TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL"))
     *         .end()
     *     .getResult();
     * }</pre>
     */
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

    /**
     * Creates a validation rule that checks if a string starts with the specified prefix.
     *
     * <p>This rule validates that the string begins with the given prefix using case-sensitive matching.
     * The rule automatically skips validation for null strings.</p>
     *
     * @param prefix the prefix that the string must start with (must not be null)
     * @return a ValidationRule that passes if the string starts with the specified prefix
     * @throws NullPointerException if prefix is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that URL starts with protocol
     * ValidationResult result = Validator.of(website)
     *     .property(ValidationIdentifier.ofField("url"), Website::getUrl)
     *         .validate(StringValidationRules.startsWith("https://"))
     *         .end()
     *     .getResult();
     *
     * // Validate that product code starts with category prefix
     * ValidationResult productResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("productCode"), Product::getProductCode)
     *         .validate(StringValidationRules.startsWith("ELEC-")) // Electronics category
     *         .end()
     *     .getResult();
     *
     * // Validate that phone number starts with country code
     * ValidationResult phoneResult = Validator.of(contact)
     *     .property(ValidationIdentifier.ofField("phoneNumber"), Contact::getPhoneNumber)
     *         .validate(StringValidationRules.startsWith("+1")) // US country code
     *         .end()
     *     .getResult();
     *
     * // Validate that error code starts with system identifier
     * ValidationResult errorResult = Validator.of(errorLog)
     *     .property(ValidationIdentifier.ofField("errorCode"), ErrorLog::getErrorCode)
     *         .validate(StringValidationRules.startsWith("SYS_"))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> startsWith(final String prefix) {
        Objects.requireNonNull(prefix, "Prefix must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.startsWith(value, prefix),
                identifier -> StringValidationMetadata.startsWith(identifier, prefix)
        );
    }

    /**
     * Creates a validation rule that checks if a string ends with the specified suffix.
     *
     * <p>This rule validates that the string ends with the given suffix using case-sensitive matching.
     * The rule automatically skips validation for null strings.</p>
     *
     * @param suffix the suffix that the string must end with (must not be null)
     * @return a ValidationRule that passes if the string ends with the specified suffix
     * @throws NullPointerException if suffix is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that filename has correct extension
     * ValidationResult result = Validator.of(document)
     *     .property(ValidationIdentifier.ofField("filename"), Document::getFilename)
     *         .validate(StringValidationRules.endsWith(".pdf"))
     *         .end()
     *     .getResult();
     *
     * // Validate that email domain is from company
     * ValidationResult emailResult = Validator.of(employee)
     *     .property(ValidationIdentifier.ofField("email"), Employee::getEmail)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.endsWith("@company.com"))
     *         .end()
     *     .getResult();
     *
     * // Validate that API endpoint has correct suffix
     * ValidationResult apiResult = Validator.of(endpoint)
     *     .property(ValidationIdentifier.ofField("path"), Endpoint::getPath)
     *         .validate(StringValidationRules.endsWith("/api/v1"))
     *         .end()
     *     .getResult();
     *
     * // Validate that temporary file has temp suffix
     * ValidationResult tempResult = Validator.of(tempFile)
     *     .property(ValidationIdentifier.ofField("filename"), TempFile::getFilename)
     *         .validate(StringValidationRules.endsWith(".tmp"))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> endsWith(final String suffix) {
        Objects.requireNonNull(suffix, "Suffix must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.endsWith(value, suffix),
                identifier -> StringValidationMetadata.endsWith(identifier, suffix)
        );
    }

    /**
     * Creates a validation rule that checks if a string contains the specified substring.
     *
     * <p>This rule validates that the string includes the given substring using case-sensitive matching.
     * The rule automatically skips validation for null strings.</p>
     *
     * @param substring the substring that must be present in the string (must not be null)
     * @return a ValidationRule that passes if the string contains the specified substring
     * @throws NullPointerException if substring is null
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that error message contains specific keyword
     * ValidationResult result = Validator.of(errorLog)
     *     .property(ValidationIdentifier.ofField("message"), ErrorLog::getMessage)
     *         .validate(StringValidationRules.contains("ERROR"))
     *         .end()
     *     .getResult();
     *
     * // Validate that product description mentions key feature
     * ValidationResult productResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("description"), Product::getDescription)
     *         .validate(StringValidationRules.contains("warranty"))
     *         .end()
     *     .getResult();
     *
     * // Validate that URL contains required parameter
     * ValidationResult urlResult = Validator.of(request)
     *     .property(ValidationIdentifier.ofField("url"), Request::getUrl)
     *         .validate(StringValidationRules.contains("?version="))
     *         .end()
     *     .getResult();
     *
     * // Validate that comment contains required acknowledgment
     * ValidationResult commentResult = Validator.of(review)
     *     .property(ValidationIdentifier.ofField("comment"), Review::getComment)
     *         .validate(StringValidationRules.contains("I agree"))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> contains(final String substring) {
        Objects.requireNonNull(substring, "Substring must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.contains(value, substring),
                identifier -> StringValidationMetadata.contains(identifier, substring)
        );
    }

    /**
     * Creates a validation rule that checks if a string contains only numeric characters.
     *
     * <p>This rule validates that the string consists entirely of digits (0-9) with no other characters.
     * Empty strings will fail this validation. The rule automatically skips validation for null strings.</p>
     *
     * @return a ValidationRule that passes if the string contains only digits
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that ID is purely numeric
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("userId"), User::getUserId)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.numeric())
     *         .end()
     *     .getResult();
     *
     * // Validate that postal code is numeric
     * ValidationResult postalResult = Validator.of(address)
     *     .property(ValidationIdentifier.ofField("postalCode"), Address::getPostalCode)
     *         .validate(StringValidationRules.numeric())
     *         .validate(StringValidationRules.exactLength(5))
     *         .end()
     *     .getResult();
     *
     * // Validate that PIN is numeric
     * ValidationResult pinResult = Validator.of(security)
     *     .property(ValidationIdentifier.ofField("pin"), Security::getPin)
     *         .validate(StringValidationRules.numeric())
     *         .validate(StringValidationRules.exactLength(4))
     *         .end()
     *     .getResult();
     *
     * // Validate that phone extension is numeric
     * ValidationResult extensionResult = Validator.of(contact)
     *     .property(ValidationIdentifier.ofField("extension"), Contact::getExtension)
     *         .validate(StringValidationRules.numeric())
     *         .validate(StringValidationRules.maxLength(6))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> numeric() {
        return createSkipNullRule(
                ValidationFunctions::isNumeric,
                StringValidationMetadata::numeric
        );
    }

    /**
     * Creates a validation rule that checks if a string contains only alphanumeric characters.
     *
     * <p>This rule validates that the string consists entirely of letters (a-z, A-Z) and digits (0-9)
     * with no other characters including spaces or special characters. Empty strings will fail this validation.
     * The rule automatically skips validation for null strings.</p>
     *
     * @return a ValidationRule that passes if the string contains only letters and digits
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that username is alphanumeric
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("username"), User::getUsername)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.alphanumeric())
     *         .validate(StringValidationRules.minLength(3))
     *         .end()
     *     .getResult();
     *
     * // Validate that product SKU is alphanumeric
     * ValidationResult skuResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("sku"), Product::getSku)
     *         .validate(StringValidationRules.alphanumeric())
     *         .validate(StringValidationRules.exactLength(8))
     *         .end()
     *     .getResult();
     *
     * // Validate that session token is alphanumeric
     * ValidationResult tokenResult = Validator.of(session)
     *     .property(ValidationIdentifier.ofField("token"), Session::getToken)
     *         .validate(StringValidationRules.alphanumeric())
     *         .validate(StringValidationRules.minLength(16))
     *         .end()
     *     .getResult();
     *
     * // Validate that tracking number is alphanumeric
     * ValidationResult trackingResult = Validator.of(shipment)
     *     .property(ValidationIdentifier.ofField("trackingNumber"), Shipment::getTrackingNumber)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.alphanumeric())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> alphanumeric() {
        return createSkipNullRule(
                ValidationFunctions::isAlphanumeric,
                StringValidationMetadata::alphanumeric
        );
    }

    /**
     * Creates a validation rule that checks if a string is entirely in uppercase.
     *
     * <p>This rule validates that all alphabetic characters in the string are uppercase.
     * Non-alphabetic characters (numbers, symbols) are ignored in the validation.
     * The rule automatically skips validation for null strings.</p>
     *
     * @return a ValidationRule that passes if all alphabetic characters are uppercase
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that country code is uppercase
     * ValidationResult result = Validator.of(address)
     *     .property(ValidationIdentifier.ofField("countryCode"), Address::getCountryCode)
     *         .validate(StringValidationRules.exactLength(2))
     *         .validate(StringValidationRules.uppercase())
     *         .end()
     *     .getResult();
     *
     * // Validate that status code is uppercase
     * ValidationResult statusResult = Validator.of(order)
     *     .property(ValidationIdentifier.ofField("status"), Order::getStatus)
     *         .validate(StringValidationRules.oneOf("PENDING", "SHIPPED", "DELIVERED"))
     *         .validate(StringValidationRules.uppercase())
     *         .end()
     *     .getResult();
     *
     * // Validate that department code is uppercase
     * ValidationResult deptResult = Validator.of(employee)
     *     .property(ValidationIdentifier.ofField("departmentCode"), Employee::getDepartmentCode)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.uppercase())
     *         .validate(StringValidationRules.maxLength(4))
     *         .end()
     *     .getResult();
     *
     * // Validate that license plate is uppercase
     * ValidationResult plateResult = Validator.of(vehicle)
     *     .property(ValidationIdentifier.ofField("licensePlate"), Vehicle::getLicensePlate)
     *         .validate(StringValidationRules.uppercase())
     *         .validate(StringValidationRules.alphanumeric())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> uppercase() {
        return createSkipNullRule(
                ValidationFunctions::isUppercase,
                StringValidationMetadata::uppercase
        );
    }

    /**
     * Creates a validation rule that checks if a string is entirely in lowercase.
     *
     * <p>This rule validates that all alphabetic characters in the string are lowercase.
     * Non-alphabetic characters (numbers, symbols) are ignored in the validation.
     * The rule automatically skips validation for null strings.</p>
     *
     * @return a ValidationRule that passes if all alphabetic characters are lowercase
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that email address is lowercase
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.lowercase())
     *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
     *         .end()
     *     .getResult();
     *
     * // Validate that username is lowercase
     * ValidationResult usernameResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("username"), User::getUsername)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.lowercase())
     *         .validate(StringValidationRules.alphanumeric())
     *         .end()
     *     .getResult();
     *
     * // Validate that URL path is lowercase
     * ValidationResult urlResult = Validator.of(endpoint)
     *     .property(ValidationIdentifier.ofField("path"), Endpoint::getPath)
     *         .validate(StringValidationRules.lowercase())
     *         .validate(StringValidationRules.startsWith("/api/"))
     *         .end()
     *     .getResult();
     *
     * // Validate that CSS class name is lowercase
     * ValidationResult cssResult = Validator.of(styleRule)
     *     .property(ValidationIdentifier.ofField("className"), StyleRule::getClassName)
     *         .validate(StringValidationRules.lowercase())
     *         .validate(StringValidationRules.matches("^[a-z][a-z0-9-]*$"))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> lowercase() {
        return createSkipNullRule(
                ValidationFunctions::isLowercase,
                StringValidationMetadata::lowercase
        );
    }

    /**
     * Creates a validation rule that checks if a string contains no whitespace characters.
     *
     * <p>This rule validates that the string has no spaces, tabs, newlines, or any other whitespace characters.
     * The rule automatically skips validation for null strings.</p>
     *
     * @return a ValidationRule that passes if the string contains no whitespace characters
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that username has no spaces
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("username"), User::getUsername)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.noWhitespace())
     *         .end()
     *     .getResult();
     *
     * // Validate that password has no spaces
     * ValidationResult passwordResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("password"), User::getPassword)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.noWhitespace())
     *         .validate(StringValidationRules.minLength(8))
     *         .end()
     *     .getResult();
     *
     * // Validate that API key has no whitespace
     * ValidationResult apiResult = Validator.of(apiConfig)
     *     .property(ValidationIdentifier.ofField("apiKey"), ApiConfig::getApiKey)
     *         .validate(StringValidationRules.noWhitespace())
     *         .validate(StringValidationRules.alphanumeric())
     *         .end()
     *     .getResult();
     *
     * // Validate that hex color code has no spaces
     * ValidationResult colorResult = Validator.of(theme)
     *     .property(ValidationIdentifier.ofField("primaryColor"), Theme::getPrimaryColor)
     *         .validate(StringValidationRules.noWhitespace())
     *         .validate(StringValidationRules.matches("^#[0-9A-Fa-f]{6}$"))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> noWhitespace() {
        return createSkipNullRule(
                ValidationFunctions::hasNoWhitespace,
                StringValidationMetadata::noWhitespace
        );
    }

    /**
     * Creates a validation rule that checks if a string has no leading whitespace.
     *
     * <p>This rule validates that the string does not start with any whitespace characters
     * (spaces, tabs, newlines, etc.). The rule automatically skips validation for null strings.</p>
     *
     * @return a ValidationRule that passes if the string has no leading whitespace
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that user input has no leading spaces
     * ValidationResult result = Validator.of(form)
     *     .property(ValidationIdentifier.ofField("firstName"), Form::getFirstName)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.noLeadingWhitespace())
     *         .end()
     *     .getResult();
     *
     * // Validate that search query is clean
     * ValidationResult searchResult = Validator.of(searchRequest)
     *     .property(ValidationIdentifier.ofField("query"), SearchRequest::getQuery)
     *         .validate(StringValidationRules.noLeadingWhitespace())
     *         .validate(StringValidationRules.noTrailingWhitespace())
     *         .end()
     *     .getResult();
     *
     * // Validate that configuration value has no leading spaces
     * ValidationResult configResult = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("value"), Config::getValue)
     *         .validate(StringValidationRules.noLeadingWhitespace())
     *         .end()
     *     .getResult();
     *
     * // Validate that code snippet has proper formatting
     * ValidationResult codeResult = Validator.of(snippet)
     *     .property(ValidationIdentifier.ofField("code"), Snippet::getCode)
     *         .validate(StringValidationRules.noLeadingWhitespace())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> noLeadingWhitespace() {
        return createSkipNullRule(
                ValidationFunctions::hasNoLeadingWhitespace,
                StringValidationMetadata::noLeadingWhitespace
        );
    }

    /**
     * Creates a validation rule that checks if a string has no trailing whitespace.
     *
     * <p>This rule validates that the string does not end with any whitespace characters
     * (spaces, tabs, newlines, etc.). The rule automatically skips validation for null strings.</p>
     *
     * @return a ValidationRule that passes if the string has no trailing whitespace
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that user input has no trailing spaces
     * ValidationResult result = Validator.of(form)
     *     .property(ValidationIdentifier.ofField("lastName"), Form::getLastName)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.noTrailingWhitespace())
     *         .end()
     *     .getResult();
     *
     * // Validate that description is clean
     * ValidationResult descResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("description"), Product::getDescription)
     *         .validate(StringValidationRules.noTrailingWhitespace())
     *         .validate(StringValidationRules.maxLength(500))
     *         .end()
     *     .getResult();
     *
     * // Validate that email has no trailing spaces
     * ValidationResult emailResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.noTrailingWhitespace())
     *         .validate(StringValidationRules.lowercase())
     *         .end()
     *     .getResult();
     *
     * // Validate that configuration key is clean
     * ValidationResult keyResult = Validator.of(setting)
     *     .property(ValidationIdentifier.ofField("key"), Setting::getKey)
     *         .validate(StringValidationRules.noTrailingWhitespace())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> noTrailingWhitespace() {
        return createSkipNullRule(
                ValidationFunctions::hasNoTrailingWhitespace,
                StringValidationMetadata::noTrailingWhitespace
        );
    }

    /**
     * Creates a validation rule that checks if a string has no consecutive whitespace characters.
     *
     * <p>This rule validates that the string does not contain two or more consecutive whitespace
     * characters (spaces, tabs, newlines, etc.). The rule automatically skips validation for null strings.</p>
     *
     * @return a ValidationRule that passes if the string has no consecutive whitespace
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that text has proper spacing
     * ValidationResult result = Validator.of(article)
     *     .property(ValidationIdentifier.ofField("content"), Article::getContent)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.noConsecutiveWhitespace())
     *         .end()
     *     .getResult();
     *
     * // Validate that user name has single spaces only
     * ValidationResult nameResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("fullName"), User::getFullName)
     *         .validate(StringValidationRules.noConsecutiveWhitespace())
     *         .end()
     *     .getResult();
     *
     * // Validate that address has clean formatting
     * ValidationResult addressResult = Validator.of(address)
     *     .property(ValidationIdentifier.ofField("streetAddress"), Address::getStreetAddress)
     *         .validate(StringValidationRules.noConsecutiveWhitespace())
     *         .validate(StringValidationRules.trimmed())
     *         .end()
     *     .getResult();
     *
     * // Validate that search terms are properly spaced
     * ValidationResult searchResult = Validator.of(searchQuery)
     *     .property(ValidationIdentifier.ofField("terms"), SearchQuery::getTerms)
     *         .validate(StringValidationRules.noConsecutiveWhitespace())
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> noConsecutiveWhitespace() {
        return createSkipNullRule(
                ValidationFunctions::hasNoConsecutiveWhitespace,
                StringValidationMetadata::noConsecutiveWhitespace
        );
    }

    /**
     * Creates a validation rule that checks if a string is trimmed (no leading or trailing whitespace).
     *
     * <p>This rule validates that the string is equal to its trimmed version, meaning it has
     * no leading or trailing whitespace characters. The rule automatically skips validation for null strings.</p>
     *
     * @return a ValidationRule that passes if the string equals its trimmed version
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that form input is trimmed
     * ValidationResult result = Validator.of(form)
     *     .property(ValidationIdentifier.ofField("companyName"), Form::getCompanyName)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.trimmed())
     *         .end()
     *     .getResult();
     *
     * // Validate that configuration values are clean
     * ValidationResult configResult = Validator.of(config)
     *     .property(ValidationIdentifier.ofField("databaseUrl"), Config::getDatabaseUrl)
     *         .validate(StringValidationRules.trimmed())
     *         .validate(StringValidationRules.startsWith("jdbc:"))
     *         .end()
     *     .getResult();
     *
     * // Validate that API response fields are trimmed
     * ValidationResult apiResult = Validator.of(response)
     *     .property(ValidationIdentifier.ofField("message"), Response::getMessage)
     *         .validate(StringValidationRules.trimmed())
     *         .end()
     *     .getResult();
     *
     * // Validate that product name is properly formatted
     * ValidationResult productResult = Validator.of(product)
     *     .property(ValidationIdentifier.ofField("name"), Product::getName)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.trimmed())
     *         .validate(StringValidationRules.maxLength(100))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> trimmed() {
        return createSkipNullRule(
                ValidationFunctions::isTrimmed,
                StringValidationMetadata::trimmed
        );
    }

    /**
     * Creates a validation rule that checks if a string has proper spacing.
     *
     * <p>This rule validates that the string is trimmed and has no consecutive whitespace characters.
     * It ensures the string has proper, normalized spacing throughout. The rule automatically
     * skips validation for null strings.</p>
     *
     * @return a ValidationRule that passes if the string has proper spacing (trimmed with single spaces)
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate that article title has proper spacing
     * ValidationResult result = Validator.of(article)
     *     .property(ValidationIdentifier.ofField("title"), Article::getTitle)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.properSpacing())
     *         .end()
     *     .getResult();
     *
     * // Validate that user's full name is properly formatted
     * ValidationResult nameResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("fullName"), User::getFullName)
     *         .validate(StringValidationRules.properSpacing())
     *         .validate(StringValidationRules.minLength(2))
     *         .end()
     *     .getResult();
     *
     * // Validate that address is properly spaced
     * ValidationResult addressResult = Validator.of(address)
     *     .property(ValidationIdentifier.ofField("fullAddress"), Address::getFullAddress)
     *         .validate(StringValidationRules.properSpacing())
     *         .end()
     *     .getResult();
     *
     * // Validate that comment has clean formatting
     * ValidationResult commentResult = Validator.of(comment)
     *     .property(ValidationIdentifier.ofField("text"), Comment::getText)
     *         .validate(StringValidationRules.properSpacing())
     *         .validate(StringValidationRules.maxLength(1000))
     *         .end()
     *     .getResult();
     *
     * // Complex validation with multiple string constraints
     * ValidationResult complexResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("username"), User::getUsername)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.noWhitespace())
     *         .validate(StringValidationRules.lowercase())
     *         .validate(StringValidationRules.alphanumeric())
     *         .validate(StringValidationRules.minLength(3))
     *         .validate(StringValidationRules.maxLength(20))
     *         .end()
     *     .property(ValidationIdentifier.ofField("displayName"), User::getDisplayName)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.properSpacing())
     *         .validate(StringValidationRules.maxLength(50))
     *         .end()
     *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.trimmed())
     *         .validate(StringValidationRules.lowercase())
     *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static ValidationRule<String> properSpacing() {
        return createSkipNullRule(
                ValidationFunctions::hasProperSpacing,
                StringValidationMetadata::properSpacing
        );
    }
}