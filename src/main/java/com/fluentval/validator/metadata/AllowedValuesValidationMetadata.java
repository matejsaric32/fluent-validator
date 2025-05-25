package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract base class for validation metadata related to allowed and disallowed value constraints.
 * This class serves as the foundation for all validation metadata types that deal with value
 * inclusion/exclusion validation scenarios, providing common infrastructure for value set
 * validations, enum validations, and constraint-based value checking.
 *
 * <p>AllowedValuesValidationMetadata supports various validation patterns:</p>
 * <ul>
 * <li><strong>Inclusion validation</strong> - ensuring values are within allowed sets</li>
 * <li><strong>Exclusion validation</strong> - ensuring values are not within prohibited sets</li>
 * <li><strong>Enum validation</strong> - ensuring values match enum constants</li>
 * <li><strong>Array-based validation</strong> - working with varargs value specifications</li>
 * <li><strong>Set-based validation</strong> - working with collection-based value specifications</li>
 * </ul>
 *
 * <p>Each concrete implementation provides specific metadata for different validation scenarios,
 * including the actual constraint values and human-readable representations for error messaging.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationMetadata
 * @see DefaultValidationCode
 */
public abstract class AllowedValuesValidationMetadata extends ValidationMetadata {

    /**
     * Constructs AllowedValuesValidationMetadata with the specified identifier and validation code.
     *
     * <p>This constructor initializes the base metadata with the validation identifier and
     * automatically adds the field identifier to the message parameters for error message
     * template substitution.</p>
     *
     * @param identifier the validation identifier specifying what failed validation
     * @param code the default validation code categorizing the specific allowed values validation type
     */
    protected AllowedValuesValidationMetadata(ValidationIdentifier identifier,
                                              DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    /**
     * Validation metadata for set-based inclusion constraints.
     *
     * <p>This class represents validation failures where a value must be contained within
     * a specific set of allowed values. It maintains both the actual constraint set and
     * a human-readable string representation for error message generation.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that user roles are from a predefined set</li>
     * <li>Ensuring configuration values match allowed options</li>
     * <li>Checking that status codes are within acceptable ranges</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>allowedValues</strong> - {@code Set<T>} - The immutable set of values that are
     * considered valid for the validation. This set contains all values that would pass the
     * validation constraint. The set is defensively copied during construction to prevent
     * external modification and ensure validation consistency.</li>
     * <li><strong>allowedValuesString</strong> - {@code String} - Human-readable string
     * representation of the allowed values used in error message templates to provide clear
     * information to users about what values are acceptable. It typically contains a formatted
     * list of the allowed values or a description of the constraint.</li>
     * </ul>
     *
     * @param <T> the type of values in the allowed set
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class Contains<T> extends AllowedValuesValidationMetadata {

        /**
         * The set of values that are considered valid for the validation.
         *
         * <p>This set contains all values that would pass the validation constraint.
         * The set is defensively copied during construction to prevent external
         * modification and ensure validation consistency.</p>
         */
        private final Set<T> allowedValues;

        /**
         * Human-readable string representation of the allowed values.
         *
         * <p>This string is used in error message templates to provide clear
         * information to users about what values are acceptable. It typically
         * contains a formatted list of the allowed values or a description
         * of the constraint.</p>
         */
        private final String allowedValuesString;

        /**
         * Private constructor for creating Contains metadata instances.
         *
         * <p>Creates a defensive copy of the allowed values set and automatically
         * adds the allowed values string to the message parameters for template
         * substitution in error messages.</p>
         *
         * @param identifier the validation identifier
         * @param allowedValues the set of allowed values
         * @param allowedValuesString human-readable representation of allowed values
         */
        private Contains(ValidationIdentifier identifier, Set<T> allowedValues, String allowedValuesString) {
            super(identifier, DefaultValidationCode.ALLOWED_VALUES_CONTAINS);
            this.allowedValues = new HashSet<>(allowedValues);
            this.allowedValuesString = allowedValuesString;

            // Add message parameters
            addMessageParameter(MessageParameter.ALLOWED_VALUES, allowedValuesString);
        }
    }

    /**
     * Validation metadata for array-based inclusion constraints.
     *
     * <p>This class represents validation failures where a value must be one of
     * a specific array of allowed values. It provides array-based value constraints
     * with varargs support for convenient value specification.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating enumeration-like string values</li>
     * <li>Checking discrete numeric values or codes</li>
     * <li>Ensuring values match predefined constants</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>allowedValues</strong> - {@code T[]} - The array of values that are
     * considered valid for the validation. This array contains all values that would pass
     * the validation constraint. The array reference is stored directly but access is
     * controlled through the {@link #getAllowedValues()} method which returns a defensive copy.</li>
     * <li><strong>allowedValuesString</strong> - {@code String} - Human-readable string
     * representation of the allowed values used in error message templates to provide clear
     * information to users about what values are acceptable.</li>
     * </ul>
     *
     * @param <T> the type of values in the allowed array
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class OneOf<T> extends AllowedValuesValidationMetadata {

        /**
         * The array of values that are considered valid for the validation.
         *
         * <p>This array contains all values that would pass the validation constraint.
         * The array reference is stored directly but access is controlled through
         * the {@link #getAllowedValues()} method which returns a defensive copy.</p>
         */
        private final T[] allowedValues;

        /**
         * Human-readable string representation of the allowed values.
         *
         * <p>This string is used in error message templates to provide clear
         * information to users about what values are acceptable.</p>
         */
        private final String allowedValuesString;

        /**
         * Private constructor for creating OneOf metadata instances.
         *
         * <p>Stores the allowed values array and automatically adds the allowed
         * values string to the message parameters for template substitution.</p>
         *
         * @param identifier the validation identifier
         * @param allowedValuesString human-readable representation of allowed values
         * @param allowedValues the array of allowed values
         */
        @SafeVarargs
        private OneOf(ValidationIdentifier identifier, String allowedValuesString, T... allowedValues) {
            super(identifier, DefaultValidationCode.ALLOWED_VALUES_ONE_OF);
            this.allowedValues = allowedValues;
            this.allowedValuesString = allowedValuesString;

            // Add message parameters
            addMessageParameter(MessageParameter.ALLOWED_VALUES, allowedValuesString);
        }

        /**
         * Returns a defensive copy of the allowed values array.
         *
         * <p>This method prevents external modification of the internal allowed
         * values array while providing access to the constraint values for
         * validation logic and error reporting purposes.</p>
         *
         * @return a copy of the allowed values array
         */
        public T[] getAllowedValues() {
            return allowedValues.clone();
        }
    }

    /**
     * Validation metadata for set-based exclusion constraints.
     *
     * <p>This class represents validation failures where a value must NOT be contained
     * within a specific set of disallowed values. It maintains both the constraint set
     * and a human-readable representation for error messaging.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Preventing use of reserved keywords or system values</li>
     * <li>Blocking inappropriate or banned content</li>
     * <li>Ensuring values don't conflict with existing data</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>disallowedValues</strong> - {@code Set<T>} - The set of values that are
     * NOT allowed for the validation. This set contains all values that would fail the
     * validation constraint. Any value found in this set is considered invalid. The set is
     * defensively copied during construction to prevent external modification.</li>
     * <li><strong>disallowedValuesString</strong> - {@code String} - Human-readable string
     * representation of the disallowed values used in error message templates to inform
     * users about what values are not acceptable.</li>
     * </ul>
     *
     * @param <T> the type of values in the disallowed set
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class NotContains<T> extends AllowedValuesValidationMetadata {

        /**
         * The set of values that are NOT allowed for the validation.
         *
         * <p>This set contains all values that would fail the validation constraint.
         * Any value found in this set is considered invalid. The set is defensively
         * copied during construction to prevent external modification.</p>
         */
        private final Set<T> disallowedValues;

        /**
         * Human-readable string representation of the disallowed values.
         *
         * <p>This string is used in error message templates to inform users
         * about what values are not acceptable.</p>
         */
        private final String disallowedValuesString;

        /**
         * Private constructor for creating NotContains metadata instances.
         *
         * @param identifier the validation identifier
         * @param disallowedValues the set of disallowed values
         * @param disallowedValuesString human-readable representation of disallowed values
         */
        private NotContains(ValidationIdentifier identifier, Set<T> disallowedValues, String disallowedValuesString) {
            super(identifier, DefaultValidationCode.NOT_CONTAINS);
            this.disallowedValues = new HashSet<>(disallowedValues);
            this.disallowedValuesString = disallowedValuesString;

            // Add message parameters
            addMessageParameter(MessageParameter.ALLOWED_VALUES, disallowedValuesString);
        }

        /**
         * Returns a defensive copy of the disallowed values set.
         *
         * <p>This method prevents external modification of the internal disallowed
         * values set while providing access to the constraint values.</p>
         *
         * @return a copy of the disallowed values set
         */
        public Set<T> getDisallowedValues() {
            return new HashSet<>(disallowedValues);
        }
    }

    /**
     * Validation metadata for array-based exclusion constraints.
     *
     * <p>This class represents validation failures where a value must NOT be one of
     * a specific array of disallowed values. It provides array-based exclusion
     * constraints with varargs support.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Preventing use of specific forbidden values or codes</li>
     * <li>Blocking deprecated or obsolete options</li>
     * <li>Ensuring values don't match problematic constants</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>disallowedValues</strong> - {@code T[]} - The array of values that are
     * NOT allowed for the validation. This array contains all values that would fail the
     * validation constraint. Access is controlled through the {@link #getDisallowedValues()}
     * method which returns a defensive copy.</li>
     * <li><strong>disallowedValuesString</strong> - {@code String} - Human-readable string
     * representation of the disallowed values used in error message templates to inform
     * users about what values are not acceptable.</li>
     * </ul>
     *
     * @param <T> the type of values in the disallowed array
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class NoneOf<T> extends AllowedValuesValidationMetadata {

        /**
         * The array of values that are NOT allowed for the validation.
         *
         * <p>This array contains all values that would fail the validation constraint.
         * Access is controlled through the {@link #getDisallowedValues()} method.</p>
         */
        private final T[] disallowedValues;

        /**
         * Human-readable string representation of the disallowed values.
         *
         * <p>This string is used in error message templates to inform users
         * about what values are not acceptable.</p>
         */
        private final String disallowedValuesString;

        /**
         * Private constructor for creating NoneOf metadata instances.
         *
         * @param identifier the validation identifier
         * @param disallowedValuesString human-readable representation of disallowed values
         * @param disallowedValues the array of disallowed values
         */
        @SafeVarargs
        private NoneOf(ValidationIdentifier identifier, String disallowedValuesString, T... disallowedValues) {
            super(identifier, DefaultValidationCode.NONE_OF);
            this.disallowedValues = disallowedValues;
            this.disallowedValuesString = disallowedValuesString;

            // Add message parameters
            addMessageParameter(MessageParameter.ALLOWED_VALUES, disallowedValuesString);
        }

        /**
         * Returns a defensive copy of the disallowed values array.
         *
         * <p>This method prevents external modification of the internal disallowed
         * values array while providing access to the constraint values.</p>
         *
         * @return a copy of the disallowed values array
         */
        public T[] getDisallowedValues() {
            return disallowedValues.clone();
        }
    }

    /**
     * Validation metadata for enum constant validation constraints.
     *
     * <p>This class represents validation failures where a value must be a valid
     * constant of a specific enum type. It provides specialized support for enum
     * validation including automatic value enumeration and intelligent string
     * representation for large enum types.</p>
     *
     * <p><strong>Features:</strong></p>
     * <ul>
     * <li>Automatic enumeration of all enum constants</li>
     * <li>Intelligent string representation (abbreviated for large enums)</li>
     * <li>Support for both enum instance and string-based validation</li>
     * <li>Class name information for detailed error messages</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>enumClass</strong> - {@code Class<E>} - The enum class defining the
     * allowed values. This class reference is used to determine valid enum constants and
     * provide class name information for error messages.</li>
     * <li><strong>enumValues</strong> - {@code Set<E>} - The complete set of valid enum
     * constants. This set contains all possible enum values that would pass validation.
     * It is automatically populated with all constants from the enum class using
     * {@code EnumSet.allOf()}.</li>
     * </ul>
     *
     * @param <E> the enum type being validated against
     */
    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static final class IsInEnum<E extends Enum<E>> extends AllowedValuesValidationMetadata {

        /**
         * The enum class defining the allowed values.
         *
         * <p>This class reference is used to determine valid enum constants
         * and provide class name information for error messages.</p>
         */
        private final Class<E> enumClass;

        /**
         * The complete set of valid enum constants.
         *
         * <p>This set contains all possible enum values that would pass
         * validation. It is automatically populated with all constants
         * from the enum class.</p>
         */
        private final Set<E> enumValues;

        /**
         * Private constructor for creating IsInEnum metadata instances.
         *
         * <p>Automatically populates the enum values set and creates an
         * appropriate string representation of the allowed values, with
         * intelligent abbreviation for large enum types.</p>
         *
         * @param identifier the validation identifier
         * @param enumClass the enum class defining allowed values
         */
        private IsInEnum(ValidationIdentifier identifier, Class<E> enumClass) {
            super(identifier, DefaultValidationCode.IS_IN_ENUM);
            this.enumClass = enumClass;
            this.enumValues = EnumSet.allOf(enumClass);

            // Add message parameters
            String valuesString = createEnumValuesString(enumClass);
            addMessageParameter(MessageParameter.ALLOWED_VALUES, valuesString);
            addMessageParameter(MessageParameter.CLASS_NAME, enumClass.getSimpleName());
        }

        /**
         * Returns a defensive copy of the enum values set.
         *
         * <p>This method provides access to all valid enum constants while
         * preventing modification of the internal constraint set.</p>
         *
         * @return a copy of the enum values set
         */
        public Set<E> getEnumValues() {
            return EnumSet.copyOf(enumValues);
        }

        /**
         * Returns the string representation of allowed enum values.
         *
         * <p>This method provides the human-readable representation of the
         * enum constraints, with intelligent abbreviation for enums with
         * many constants.</p>
         *
         * @return string representation of allowed enum values
         */
        public String getValuesString() {
            return createEnumValuesString(enumClass);
        }

        /**
         * Creates an intelligent string representation of enum values.
         *
         * <p>For enums with 20 or fewer constants, all values are listed.
         * For larger enums, a summary format is used to avoid overwhelming
         * error messages with extensive value lists.</p>
         *
         * @param <T> the enum type
         * @param enumClass the enum class
         * @return formatted string representation of enum values
         */
        private static <T extends Enum<T>> String createEnumValuesString(Class<T> enumClass) {
            Set<T> values = EnumSet.allOf(enumClass);
            if (values.size() <= 20) {
                return values.stream().map(Enum::name).collect(Collectors.joining(", "));
            } else {
                return "one of " + values.size() + " possible values";
            }
        }
    }

    // Factory methods

    /**
     * Factory method for creating Contains validation metadata.
     *
     * @param <T> the type of values in the constraint set
     * @param identifier the validation identifier
     * @param allowedValues the set of allowed values
     * @param allowedValuesString human-readable representation of allowed values
     * @return Contains metadata instance
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if allowedValues is empty
     */
    public static <T> Contains<T> contains(ValidationIdentifier identifier, Set<T> allowedValues, String allowedValuesString) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(allowedValues, "Allowed values set must not be null");
        Objects.requireNonNull(allowedValuesString, "Allowed values string must not be null");

        if (allowedValues.isEmpty()) {
            throw new IllegalArgumentException("Allowed values set must not be empty");
        }

        return new Contains<>(identifier, allowedValues, allowedValuesString);
    }

    /**
     * Factory method for creating OneOf validation metadata.
     *
     * @param <T> the type of values in the constraint array
     * @param identifier the validation identifier
     * @param allowedValuesString human-readable representation of allowed values
     * @param allowedValues the array of allowed values
     * @return OneOf metadata instance
     * @throws NullPointerException if identifier, allowedValuesString, or allowedValues is null
     * @throws IllegalArgumentException if allowedValues is empty
     */
    @SafeVarargs
    public static <T> OneOf<T> oneOf(ValidationIdentifier identifier, String allowedValuesString, T... allowedValues) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(allowedValuesString, "Allowed values string must not be null");
        Objects.requireNonNull(allowedValues, "Allowed values array must not be null");

        if (allowedValues.length == 0) {
            throw new IllegalArgumentException("Allowed values array must not be empty");
        }

        return new OneOf<>(identifier, allowedValuesString, allowedValues);
    }

    /**
     * Factory method for creating NotContains validation metadata.
     *
     * @param <T> the type of values in the constraint set
     * @param identifier the validation identifier
     * @param disallowedValues the set of disallowed values
     * @param disallowedValuesString human-readable representation of disallowed values
     * @return NotContains metadata instance
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if disallowedValues is empty
     */
    public static <T> NotContains<T> notContains(ValidationIdentifier identifier, Set<T> disallowedValues, String disallowedValuesString) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(disallowedValues, "Disallowed values set must not be null");
        Objects.requireNonNull(disallowedValuesString, "Disallowed values string must not be null");

        if (disallowedValues.isEmpty()) {
            throw new IllegalArgumentException("Disallowed values set must not be empty");
        }

        return new NotContains<>(identifier, disallowedValues, disallowedValuesString);
    }

    /**
     * Factory method for creating NoneOf validation metadata.
     *
     * @param <T> the type of values in the constraint array
     * @param identifier the validation identifier
     * @param disallowedValuesString human-readable representation of disallowed values
     * @param disallowedValues the array of disallowed values
     * @return NoneOf metadata instance
     * @throws NullPointerException if identifier, disallowedValuesString, or disallowedValues is null
     * @throws IllegalArgumentException if disallowedValues is empty
     */
    @SafeVarargs
    public static <T> NoneOf<T> noneOf(ValidationIdentifier identifier, String disallowedValuesString, T... disallowedValues) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(disallowedValuesString, "Disallowed values string must not be null");
        Objects.requireNonNull(disallowedValues, "Disallowed values array must not be null");

        if (disallowedValues.length == 0) {
            throw new IllegalArgumentException("Disallowed values array must not be empty");
        }

        return new NoneOf<>(identifier, disallowedValuesString, disallowedValues);
    }

    /**
     * Factory method for creating IsInEnum validation metadata.
     *
     * @param <E> the enum type
     * @param identifier the validation identifier
     * @param enumClass the enum class defining allowed values
     * @return IsInEnum metadata instance
     * @throws NullPointerException if identifier or enumClass is null
     */
    public static <E extends Enum<E>> IsInEnum<E> isInEnum(ValidationIdentifier identifier, Class<E> enumClass) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(enumClass, "Enum class must not be null");

        return new IsInEnum<>(identifier, enumClass);
    }
}