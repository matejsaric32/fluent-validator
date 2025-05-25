package com.fluentval.validator.rule;

import com.fluentval.validator.ValidationIdentifier;
import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;
import com.fluentval.validator.metadata.ValidationMetadata;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utility class providing factory methods for creating ValidationRule instances with standardized
 * behavior patterns. This class centralizes the creation logic for validation rules and ensures
 * consistent handling of validation failures and metadata generation.
 *
 * <p>This utility class provides two primary patterns for validation rule creation:</p>
 * <ul>
 * <li><strong>Standard rules</strong> - validate all values including null</li>
 * <li><strong>Skip-null rules</strong> - skip validation when the value is null</li>
 * </ul>
 *
 * <p>Most validation rule classes in the framework use these utility methods to ensure
 * consistent behavior and reduce code duplication across different validation types.</p>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationRule
 * @see ValidationMetadata
 * @see ValidationResult
 */
public class ValidationRuleUtils {

    private ValidationRuleUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a validation rule that validates all values, including null values.
     *
     * <p>This method creates a validation rule that applies the validation function to all
     * input values without any null checking. If the validation function returns false,
     * a validation failure is added to the result using the provided metadata factory.</p>
     *
     * <p>Use this method when:</p>
     * <ul>
     * <li>Null values should be explicitly validated (e.g., notNull validation)</li>
     * <li>The validation function can safely handle null inputs</li>
     * <li>You want to enforce strict validation on all values</li>
     * </ul>
     *
     * @param <T> the type of value to validate
     * @param validationFunction the predicate that determines if a value is valid
     * @param metadataFactory function that creates validation metadata for failures
     * @return a ValidationRule that validates all values including null
     * @throws NullPointerException if validationFunction or metadataFactory is null
     */
    public static <T> ValidationRule<T> createRule(
            final Predicate<T> validationFunction,
            final Function<ValidationIdentifier, ValidationMetadata> metadataFactory) {

        return (value, result, identifier) -> {
            if (!validationFunction.test(value)) {
                result.addFailure(new ValidationResult.Failure(
                        metadataFactory.apply(identifier)
                ));
            }
        };
    }

    /**
     * Creates a validation rule that skips validation for null values.
     *
     * <p>This method creates a validation rule that first checks if the input value is null.
     * If the value is null, validation is skipped entirely. Otherwise, the validation function
     * is applied, and failures are recorded using the provided metadata factory.</p>
     *
     * <p>Use this method when:</p>
     * <ul>
     * <li>Null values should be considered valid (common pattern in most validations)</li>
     * <li>You want to combine with separate null-checking validations</li>
     * <li>The validation function cannot safely handle null inputs</li>
     * <li>You want optional validation behavior</li>
     * </ul>
     *
     * <p>This is the most commonly used pattern in the validation framework, as it allows
     * for flexible composition where null-checking can be handled separately when needed.</p>
     *
     * @param <T> the type of value to validate
     * @param validationFunction the predicate that determines if a non-null value is valid
     * @param metadataFactory function that creates validation metadata for failures
     * @return a ValidationRule that skips validation for null values
     * @throws NullPointerException if validationFunction or metadataFactory is null
     */
    public static <T> ValidationRule<T> createSkipNullRule(
            final Predicate<T> validationFunction,
            final Function<ValidationIdentifier, ValidationMetadata> metadataFactory) {

        return (value, result, identifier) -> {
            if (value == null) {
                return; // Skip validation for null value
            }

            if (!validationFunction.test(value)) {
                result.addFailure(new ValidationResult.Failure(
                        metadataFactory.apply(identifier)
                ));
            }
        };
    }
}