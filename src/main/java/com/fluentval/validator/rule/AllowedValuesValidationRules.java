package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.AllowedValuesValidationMetadata;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import static com.fluentval.validator.rule.ValidationRuleUtils.createSkipNullRule;

/**
 * Utility class providing validation rules for checking values against allowed or disallowed sets.
 * This class offers methods to validate that values are within permitted ranges, enum values,
 * or excluded from forbidden sets.
 *
 * <p>All validation rules in this class automatically skip null values, meaning they will
 * pass validation if the input is null. Use in combination with {@code CommonValidationRules.notNull()}
 * if null values should be rejected.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationRule
 * @see AllowedValuesValidationMetadata
 */
public final class AllowedValuesValidationRules {

    private AllowedValuesValidationRules() {
        // Utility class
    }

    // Pure validation functions
    private static class ValidationFunctions {
        static <T> boolean containsValue(final T value, final Set<T> allowedValues) {
            return allowedValues.contains(value);
        }

        static <T> boolean isOneOf(final T value, final T[] allowedValues) {
            return Arrays.stream(allowedValues).anyMatch(value::equals);
        }

        static <T> boolean notContainsValue(final T value, final Set<T> disallowedValues) {
            return !disallowedValues.contains(value);
        }

        static <T> boolean isNoneOf(final T value, final T[] disallowedValues) {
            return Arrays.stream(disallowedValues).noneMatch(value::equals);
        }

        static <T, E extends Enum<E>> boolean isInEnum(final T value, final Class<E> enumClass) {
            final Set<E> enumValues = EnumSet.allOf(enumClass);

            if (value instanceof String str) {
                return enumValues.stream()
                        .map(Enum::name)
                        .anyMatch(name -> name.equals(str));
            } else {
                return enumValues.contains(value);
            }
        }
    }

    /**
     * Creates a validation rule that checks if a value is contained within a set of allowed values.
     *
     * <p>This rule validates that the input value exists in the provided set of allowed values.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of value to validate
     * @param allowedValuesString a string representation of allowed values for error messages
     * @param allowedValues the set of values that are considered valid
     * @return a ValidationRule that passes if the value is in the allowed set
     * @throws NullPointerException if allowedValues or allowedValuesString is null
     * @throws IllegalArgumentException if allowedValues is empty
     *
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate country codes
     * Set<String> validCountries = Set.of("US", "CA", "MX", "UK", "DE");
     * ValidationRule<String> countryRule = AllowedValuesValidationRules.contains(
     *     "US, CA, MX, UK, DE",
     *     validCountries
     * );
     *
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("country"), User::getCountry)
     *         .validate(countryRule)
     *         .end()
     *     .getResult();
     *
     * // Validate status codes
     * Set<Integer> validStatuses = Set.of(100, 200, 201, 400, 404, 500);
     * ValidationRule<Integer> statusRule = AllowedValuesValidationRules.contains(
     *     "100, 200, 201, 400, 404, 500",
     *     validStatuses
     * );
     *
     * // Validate user roles
     * Set<UserRole> allowedRoles = Set.of(UserRole.USER, UserRole.MODERATOR);
     * ValidationRule<UserRole> roleRule = AllowedValuesValidationRules.contains(
     *     "USER, MODERATOR",
     *     allowedRoles
     * );
     * }</pre>
     */
    public static <T> ValidationRule<T> contains(final String allowedValuesString, final Set<T> allowedValues) {
        Objects.requireNonNull(allowedValues, "Allowed values set must not be null");
        Objects.requireNonNull(allowedValuesString, "Allowed values string must not be null");

        if (allowedValues.isEmpty()) {
            throw new IllegalArgumentException("Allowed values set must not be empty");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.containsValue(value, allowedValues),
                identifier -> AllowedValuesValidationMetadata.contains(identifier, allowedValues, allowedValuesString)
        );
    }

    /**
     * Creates a validation rule that checks if a value is one of the specified allowed values.
     *
     * <p>This rule validates that the input value matches one of the provided allowed values using
     * {@code equals()} comparison. The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of value to validate
     * @param allowedValuesString a string representation of allowed values for error messages
     * @param allowedValues the array of values that are considered valid
     * @return a ValidationRule that passes if the value equals one of the allowed values
     * @throws NullPointerException if allowedValues or allowedValuesString is null
     * @throws IllegalArgumentException if allowedValues is empty
     *
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate file extensions
     * ValidationRule<String> extensionRule = AllowedValuesValidationRules.oneOf(
     *     "jpg, png, gif, pdf",
     *     "jpg", "png", "gif", "pdf"
     * );
     *
     * ValidationResult result = Validator.of(uploadedFile)
     *     .property(ValidationIdentifier.ofField("extension"), UploadedFile::getExtension)
     *         .validate(extensionRule)
     *         .end()
     *     .getResult();
     *
     * // Validate priority levels
     * ValidationRule<String> priorityRule = AllowedValuesValidationRules.oneOf(
     *     "LOW, MEDIUM, HIGH, CRITICAL",
     *     "LOW", "MEDIUM", "HIGH", "CRITICAL"
     * );
     *
     * // Validate numeric grades
     * ValidationRule<Integer> gradeRule = AllowedValuesValidationRules.oneOf(
     *     "1, 2, 3, 4, 5",
     *     1, 2, 3, 4, 5
     * );
     *
     * // Validate boolean-like strings
     * ValidationRule<String> booleanRule = AllowedValuesValidationRules.oneOf(
     *     "true, false, yes, no",
     *     "true", "false", "yes", "no"
     * );
     * }</pre>
     */
    @SafeVarargs
    public static <T> ValidationRule<T> oneOf(final String allowedValuesString, final T... allowedValues) {
        Objects.requireNonNull(allowedValuesString, "Allowed values string must not be null");
        Objects.requireNonNull(allowedValues, "Allowed values array must not be null");

        if (allowedValues.length == 0) {
            throw new IllegalArgumentException("Allowed values array must not be empty");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isOneOf(value, allowedValues),
                identifier -> AllowedValuesValidationMetadata.oneOf(identifier, allowedValuesString, allowedValues)
        );
    }

    /**
     * Creates a validation rule that checks if a value is NOT contained within a set of disallowed values.
     *
     * <p>This rule validates that the input value does not exist in the provided set of disallowed values.
     * The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of value to validate
     * @param disallowedValuesString a string representation of disallowed values for error messages
     * @param disallowedValues the set of values that are considered invalid
     * @return a ValidationRule that passes if the value is NOT in the disallowed set
     * @throws NullPointerException if disallowedValues or disallowedValuesString is null
     * @throws IllegalArgumentException if disallowedValues is empty
     *
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate against banned usernames
     * Set<String> bannedUsernames = Set.of("admin", "root", "system", "test");
     * ValidationRule<String> usernameRule = AllowedValuesValidationRules.notContains(
     *     "admin, root, system, test",
     *     bannedUsernames
     * );
     *
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("username"), User::getUsername)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(usernameRule)
     *         .end()
     *     .getResult();
     *
     * // Validate against restricted file types
     * Set<String> restrictedTypes = Set.of("exe", "bat", "com", "scr");
     * ValidationRule<String> fileTypeRule = AllowedValuesValidationRules.notContains(
     *     "exe, bat, com, scr",
     *     restrictedTypes
     * );
     *
     * // Validate against blocked IP addresses
     * Set<String> blockedIps = Set.of("192.168.1.100", "10.0.0.50");
     * ValidationRule<String> ipRule = AllowedValuesValidationRules.notContains(
     *     "192.168.1.100, 10.0.0.50",
     *     blockedIps
     * );
     * }</pre>
     */
    public static <T> ValidationRule<T> notContains(final String disallowedValuesString, final Set<T> disallowedValues) {
        Objects.requireNonNull(disallowedValues, "Disallowed values set must not be null");
        Objects.requireNonNull(disallowedValuesString, "Disallowed values string must not be null");

        if (disallowedValues.isEmpty()) {
            throw new IllegalArgumentException("Disallowed values set must not be empty");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.notContainsValue(value, disallowedValues),
                identifier -> AllowedValuesValidationMetadata.notContains(identifier, disallowedValues, disallowedValuesString)
        );
    }

    /**
     * Creates a validation rule that checks if a value is none of the specified disallowed values.
     *
     * <p>This rule validates that the input value does not match any of the provided disallowed values
     * using {@code equals()} comparison. The rule automatically skips validation for null values.</p>
     *
     * @param <T> the type of value to validate
     * @param disallowedValuesString a string representation of disallowed values for error messages
     * @param disallowedValues the array of values that are considered invalid
     * @return a ValidationRule that passes if the value does not equal any of the disallowed values
     * @throws NullPointerException if disallowedValues or disallowedValuesString is null
     * @throws IllegalArgumentException if disallowedValues is empty
     *
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate against reserved keywords
     * ValidationRule<String> keywordRule = AllowedValuesValidationRules.noneOf(
     *     "class, public, private, static, final",
     *     "class", "public", "private", "static", "final"
     * );
     *
     * ValidationResult result = Validator.of(variable)
     *     .property(ValidationIdentifier.ofField("name"), Variable::getName)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(keywordRule)
     *         .end()
     *     .getResult();
     *
     * // Validate against forbidden ports
     * ValidationRule<Integer> portRule = AllowedValuesValidationRules.noneOf(
     *     "22, 23, 25, 53, 80, 443",
     *     22, 23, 25, 53, 80, 443
     * );
     *
     * // Validate against invalid status codes
     * ValidationRule<String> statusRule = AllowedValuesValidationRules.noneOf(
     *     "INVALID, ERROR, UNKNOWN",
     *     "INVALID", "ERROR", "UNKNOWN"
     * );
     *
     * // Validate against test values
     * ValidationRule<String> valueRule = AllowedValuesValidationRules.noneOf(
     *     "test, demo, sample, example",
     *     "test", "demo", "sample", "example"
     * );
     * }</pre>
     */
    @SafeVarargs
    public static <T> ValidationRule<T> noneOf(final String disallowedValuesString, final T... disallowedValues) {
        Objects.requireNonNull(disallowedValuesString, "Disallowed values string must not be null");
        Objects.requireNonNull(disallowedValues, "Disallowed values array must not be null");

        if (disallowedValues.length == 0) {
            throw new IllegalArgumentException("Disallowed values array must not be empty");
        }

        return createSkipNullRule(
                value -> ValidationFunctions.isNoneOf(value, disallowedValues),
                identifier -> AllowedValuesValidationMetadata.noneOf(identifier, disallowedValuesString, disallowedValues)
        );
    }

    /**
     * Creates a validation rule that checks if a value is a valid enum constant.
     *
     * <p>This rule validates that the input value is a valid constant of the specified enum class.
     * For String values, it checks if the string matches any enum constant name. For enum values,
     * it checks if the value is contained in the enum's constants. The rule automatically skips
     * validation for null values.</p>
     *
     * @param <T> the type of value to validate
     * @param <E> the enum type to validate against
     * @param enumClass the enum class containing valid constants
     * @return a ValidationRule that passes if the value is a valid enum constant
     * @throws NullPointerException if enumClass is null
     *
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Validate user role enum
     * ValidationRule<String> roleStringRule = AllowedValuesValidationRules.isInEnum(UserRole.class);
     * ValidationRule<UserRole> roleEnumRule = AllowedValuesValidationRules.isInEnum(UserRole.class);
     *
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("roleString"), User::getRoleString)
     *         .validate(roleStringRule) // Validates "ADMIN", "USER", "MODERATOR"
     *         .end()
     *     .property(ValidationIdentifier.ofField("role"), User::getRole)
     *         .validate(roleEnumRule) // Validates UserRole enum values
     *         .end()
     *     .getResult();
     *
     * // Validate order status
     * enum OrderStatus { PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED }
     * ValidationRule<String> statusRule = AllowedValuesValidationRules.isInEnum(OrderStatus.class);
     *
     * // Validate priority level
     * enum Priority { LOW, MEDIUM, HIGH, CRITICAL }
     * ValidationRule<Priority> priorityRule = AllowedValuesValidationRules.isInEnum(Priority.class);
     *
     * // Validate day of week
     * ValidationRule<String> dayRule = AllowedValuesValidationRules.isInEnum(DayOfWeek.class);
     *
     * // Usage in complex validation
     * ValidationResult orderResult = Validator.of(order)
     *     .property(ValidationIdentifier.ofField("status"), Order::getStatusString)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(AllowedValuesValidationRules.isInEnum(OrderStatus.class))
     *         .end()
     *     .property(ValidationIdentifier.ofField("priority"), Order::getPriority)
     *         .validate(CommonValidationRules.notNull())
     *         .validate(AllowedValuesValidationRules.isInEnum(Priority.class))
     *         .end()
     *     .getResult();
     * }</pre>
     */
    public static <T, E extends Enum<E>> ValidationRule<T> isInEnum(final Class<E> enumClass) {
        Objects.requireNonNull(enumClass, "Enum class must not be null");

        return createSkipNullRule(
                value -> ValidationFunctions.isInEnum(value, enumClass),
                identifier -> AllowedValuesValidationMetadata.isInEnum(identifier, enumClass)
        );
    }
}