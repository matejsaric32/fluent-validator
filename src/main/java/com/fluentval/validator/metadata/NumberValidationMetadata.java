package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.message.MessageParameter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

/**
 * Abstract base class for validation metadata related to numeric constraints and validations.
 * This class serves as the foundation for all validation metadata types that deal with numeric
 * value validation, range validation, boundary checking, and mathematical property validation,
 * providing common infrastructure for comprehensive numeric validation scenarios.
 *
 * <p>NumberValidationMetadata supports various validation patterns:</p>
 * <ul>
 * <li><strong>Boundary validation</strong> - ensuring numbers meet minimum and maximum constraints</li>
 * <li><strong>Range validation</strong> - checking that numbers fall within specified inclusive ranges</li>
 * <li><strong>Sign validation</strong> - verifying that numbers are positive, negative, or non-zero</li>
 * <li><strong>Mathematical property validation</strong> - applying mathematical constraints and rules</li>
 * <li><strong>Comparative validation</strong> - validating numbers against reference values or thresholds</li>
 * </ul>
 *
 * <p>This class works with any numeric type that extends {@code Number} and implements {@code Comparable},
 * including {@code Integer}, {@code Long}, {@code Double}, {@code Float}, {@code BigDecimal}, and
 * {@code BigInteger}. The generic type system ensures type safety while maintaining flexibility
 * for different numeric types and their specific validation requirements.</p>
 *
 * <p>Each concrete implementation provides specific metadata for different numeric validation scenarios,
 * including constraint values, boundary information, and validation context for comprehensive
 * error messaging and validation reporting.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationMetadata
 * @see DefaultValidationCode
 * @see Number
 * @see Comparable
 */
public abstract class NumberValidationMetadata extends ValidationMetadata {

    /**
     * Constructs NumberValidationMetadata with the specified identifier and validation code.
     *
     * <p>This constructor initializes the base metadata with the validation identifier and
     * automatically adds the field identifier to the message parameters for error message
     * template substitution.</p>
     *
     * @param identifier the validation identifier specifying what failed validation
     * @param code the default validation code categorizing the specific numeric validation type
     */
    protected NumberValidationMetadata(ValidationIdentifier identifier,
                                       DefaultValidationCode code) {
        super(identifier, code.getCode());

        // Always add the field identifier as a parameter
        addMessageParameter(MessageParameter.FIELD, identifier.value());
    }

    /**
     * Validation metadata for minimum value constraints.
     *
     * <p>This class represents validation failures where a numeric value must be greater than or equal to
     * a specified minimum threshold. It provides lower bound validation with precise constraint
     * specification for error messaging and supports all comparable numeric types.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that ages are at least a minimum value (e.g., 18 for adult verification)</li>
     * <li>Ensuring quantities are not negative or below minimum order amounts</li>
     * <li>Checking that prices meet minimum pricing policies</li>
     * <li>Verifying that scores or ratings meet minimum acceptable thresholds</li>
     * <li>Validating that array indices are non-negative</li>
     * <li>Ensuring that timeout values meet minimum duration requirements</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>minimum</strong> - {@code T extends Number & Comparable<T>} - The minimum value
     * (inclusive) that the validated number must meet or exceed. This value represents the lower
     * bound constraint and is used in error messages to inform users of the requirement.
     * The type parameter ensures type safety and allows for precise numeric comparisons.</li>
     * </ul>
     *
     * @param <T> the specific numeric type that extends Number and implements Comparable
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Min<T extends Number & Comparable<T>> extends NumberValidationMetadata {
        private final T minimum;

        private Min(ValidationIdentifier identifier, T minimum) {
            super(identifier, DefaultValidationCode.MIN);
            this.minimum = minimum;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN, minimum.toString());
        }
    }

    /**
     * Validation metadata for maximum value constraints.
     *
     * <p>This class represents validation failures where a numeric value must be less than or equal to
     * a specified maximum threshold. It provides upper bound validation to prevent values from
     * exceeding acceptable limits and supports all comparable numeric types.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that quantities do not exceed inventory limits or maximum order amounts</li>
     * <li>Ensuring that percentages do not exceed 100% or other maximum thresholds</li>
     * <li>Checking that prices do not exceed maximum pricing policies or budgets</li>
     * <li>Verifying that scores or ratings do not exceed maximum possible values</li>
     * <li>Validating that array sizes do not exceed memory or processing limitations</li>
     * <li>Ensuring that timeout values do not exceed maximum allowable durations</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>maximum</strong> - {@code T extends Number & Comparable<T>} - The maximum value
     * (inclusive) that the validated number must not exceed. This value represents the upper
     * bound constraint and is used in error messages to inform users of the limitation.
     * The type parameter ensures type safety and allows for precise numeric comparisons.</li>
     * </ul>
     *
     * @param <T> the specific numeric type that extends Number and implements Comparable
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Max<T extends Number & Comparable<T>> extends NumberValidationMetadata {
        private final T maximum;

        private Max(ValidationIdentifier identifier, T maximum) {
            super(identifier, DefaultValidationCode.MAX);
            this.maximum = maximum;

            // Add message parameters
            addMessageParameter(MessageParameter.MAX, maximum.toString());
        }
    }

    /**
     * Validation metadata for numeric range constraints.
     *
     * <p>This class represents validation failures where a numeric value must fall within a specified
     * range (inclusive bounds). It provides both lower and upper bound validation with detailed
     * constraint information for comprehensive error reporting and supports all comparable numeric types.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that ages fall within acceptable ranges for specific services or products</li>
     * <li>Ensuring that quantities are within reasonable processing or inventory limits</li>
     * <li>Checking that prices fall within acceptable market or policy ranges</li>
     * <li>Verifying that scores or ratings are within valid scoring ranges</li>
     * <li>Validating that coordinates are within geographic or system boundaries</li>
     * <li>Ensuring that measurements fall within acceptable tolerance ranges</li>
     * <li>Checking that percentages are within meaningful ranges (e.g., 0-100)</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><strong>min</strong> - {@code T extends Number & Comparable<T>} - The minimum value
     * (inclusive) that defines the lower bound of the acceptable range. The validated number
     * must be greater than or equal to this value.</li>
     * <li><strong>max</strong> - {@code T extends Number & Comparable<T>} - The maximum value
     * (inclusive) that defines the upper bound of the acceptable range. The validated number
     * must be less than or equal to this value.</li>
     * </ul>
     *
     * <p><strong>Range Validation:</strong> The range is inclusive on both ends, meaning that values
     * exactly equal to the minimum or maximum are considered valid. The factory method ensures
     * that the minimum value is not greater than the maximum value to maintain logical consistency.</p>
     *
     * @param <T> the specific numeric type that extends Number and implements Comparable
     */
    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static final class Range<T extends Number & Comparable<T>> extends NumberValidationMetadata {
        private final T min;
        private final T max;

        private Range(ValidationIdentifier identifier, T min, T max) {
            super(identifier, DefaultValidationCode.RANGE);
            this.min = min;
            this.max = max;

            // Add message parameters
            addMessageParameter(MessageParameter.MIN, min.toString());
            addMessageParameter(MessageParameter.MAX, max.toString());
        }
    }

    /**
     * Validation metadata for positive number constraints.
     *
     * <p>This class represents validation failures where a numeric value must be strictly positive
     * (greater than zero). It provides mathematical sign validation to ensure numbers represent
     * positive quantities, amounts, or measurements.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that quantities, amounts, or counts are positive values</li>
     * <li>Ensuring that prices, costs, or monetary values are positive</li>
     * <li>Checking that measurements, distances, or sizes are positive</li>
     * <li>Verifying that rates, speeds, or frequencies are positive</li>
     * <li>Validating that identifiers or IDs are positive integers</li>
     * <li>Ensuring that mathematical calculations produce positive results where required</li>
     * <li>Checking that percentage increases or growth rates are positive</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on comparing the number to zero
     * using the number's natural comparison semantics. The validation inherits the field identifier
     * from the parent class for error messaging. Zero is explicitly excluded from positive values.</li>
     * </ul>
     *
     * <p><strong>Mathematical Definition:</strong> A number is considered positive if it is strictly
     * greater than zero. This validation does not consider zero as positive, following standard
     * mathematical conventions.</p>
     */
    public static final class Positive extends NumberValidationMetadata {
        private Positive(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.POSITIVE);
            // No additional parameters needed for this validation
        }
    }

    /**
     * Validation metadata for negative number constraints.
     *
     * <p>This class represents validation failures where a numeric value must be strictly negative
     * (less than zero). It provides mathematical sign validation to ensure numbers represent
     * negative quantities, decreases, or values below a baseline.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that losses, decreases, or reductions are negative values</li>
     * <li>Ensuring that debt, liabilities, or negative balances are properly negative</li>
     * <li>Checking that temperature readings below zero are negative</li>
     * <li>Verifying that coordinate values below origin points are negative</li>
     * <li>Validating that percentage decreases or declines are negative</li>
     * <li>Ensuring that mathematical calculations produce negative results where required</li>
     * <li>Checking that adjustments or corrections are negative values</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on comparing the number to zero
     * using the number's natural comparison semantics. The validation inherits the field identifier
     * from the parent class for error messaging. Zero is explicitly excluded from negative values.</li>
     * </ul>
     *
     * <p><strong>Mathematical Definition:</strong> A number is considered negative if it is strictly
     * less than zero. This validation does not consider zero as negative, following standard
     * mathematical conventions.</p>
     */
    public static final class Negative extends NumberValidationMetadata {
        private Negative(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NEGATIVE);
            // No additional parameters needed for this validation
        }
    }

    /**
     * Validation metadata for non-zero number constraints.
     *
     * <p>This class represents validation failures where a numeric value must not be equal to zero.
     * It provides mathematical non-zero validation to ensure numbers represent meaningful quantities
     * or values that are not neutral or empty.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Validating that divisors are not zero to prevent division by zero errors</li>
     * <li>Ensuring that multipliers or factors are not zero for meaningful calculations</li>
     * <li>Checking that quantities or amounts are not zero when transactions are required</li>
     * <li>Verifying that rates, speeds, or frequencies are not zero for active processes</li>
     * <li>Validating that measurements or distances are not zero when precision is required</li>
     * <li>Ensuring that identifiers or keys are not zero when zero represents null or empty</li>
     * <li>Checking that percentage changes are not zero when variation is expected</li>
     * </ul>
     *
     * <p><strong>Fields:</strong></p>
     * <ul>
     * <li><em>No additional fields</em> - This validation relies on comparing the number to zero
     * using the number's natural comparison semantics. The validation inherits the field identifier
     * from the parent class for error messaging. Both positive and negative values are considered
     * valid, as long as they are not zero.</li>
     * </ul>
     *
     * <p><strong>Mathematical Definition:</strong> A number is considered non-zero if it is not equal
     * to zero, meaning it can be either positive or negative. This validation allows any numeric
     * value except zero, providing flexibility for both positive and negative meaningful values.</p>
     */
    public static final class NotZero extends NumberValidationMetadata {
        private NotZero(ValidationIdentifier identifier) {
            super(identifier, DefaultValidationCode.NOT_ZERO);
            // No additional parameters needed for this validation
        }
    }

    // Factory methods

    /**
     * Factory method for creating Min validation metadata.
     *
     * <p>Creates metadata for validating that a numeric value is greater than or equal to
     * the specified minimum value.</p>
     *
     * @param <T> the specific numeric type that extends Number and implements Comparable
     * @param identifier the validation identifier
     * @param min the minimum value (inclusive) that the number must meet or exceed
     * @return Min metadata instance
     * @throws NullPointerException if identifier or min is null
     */
    public static <T extends Number & Comparable<T>> Min<T> min(ValidationIdentifier identifier, T min) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(min, "Minimum value must not be null");

        return new Min<>(identifier, min);
    }

    /**
     * Factory method for creating Max validation metadata.
     *
     * <p>Creates metadata for validating that a numeric value is less than or equal to
     * the specified maximum value.</p>
     *
     * @param <T> the specific numeric type that extends Number and implements Comparable
     * @param identifier the validation identifier
     * @param max the maximum value (inclusive) that the number must not exceed
     * @return Max metadata instance
     * @throws NullPointerException if identifier or max is null
     */
    public static <T extends Number & Comparable<T>> Max<T> max(ValidationIdentifier identifier, T max) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(max, "Maximum value must not be null");

        return new Max<>(identifier, max);
    }

    /**
     * Factory method for creating Range validation metadata.
     *
     * <p>Creates metadata for validating that a numeric value falls within the specified
     * range (inclusive bounds). This method ensures that the minimum value is not greater
     * than the maximum value to maintain logical consistency.</p>
     *
     * @param <T> the specific numeric type that extends Number and implements Comparable
     * @param identifier the validation identifier
     * @param min the minimum value (inclusive) that defines the lower bound of the range
     * @param max the maximum value (inclusive) that defines the upper bound of the range
     * @return Range metadata instance
     * @throws NullPointerException if identifier, min, or max is null
     * @throws IllegalArgumentException if min is greater than max
     */
    public static <T extends Number & Comparable<T>> Range<T> range(ValidationIdentifier identifier, T min, T max) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);
        Objects.requireNonNull(min, "Minimum value must not be null");
        Objects.requireNonNull(max, "Maximum value must not be null");

        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Minimum value must be less than or equal to maximum value");
        }

        return new Range<>(identifier, min, max);
    }

    /**
     * Factory method for creating Positive validation metadata.
     *
     * <p>Creates metadata for validating that a numeric value is strictly positive (greater than zero).</p>
     *
     * @param identifier the validation identifier
     * @return Positive metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static Positive positive(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new Positive(identifier);
    }

    /**
     * Factory method for creating Negative validation metadata.
     *
     * <p>Creates metadata for validating that a numeric value is strictly negative (less than zero).</p>
     *
     * @param identifier the validation identifier
     * @return Negative metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static Negative negative(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new Negative(identifier);
    }

    /**
     * Factory method for creating NotZero validation metadata.
     *
     * <p>Creates metadata for validating that a numeric value is not equal to zero.</p>
     *
     * @param identifier the validation identifier
     * @return NotZero metadata instance
     * @throws NullPointerException if identifier is null
     */
    public static NotZero notZero(ValidationIdentifier identifier) {
        Objects.requireNonNull(identifier, MetadataUtils.IDENTIFIER_MUST_NOT_BE_NULL_MSG);

        return new NotZero(identifier);
    }
}