package com.fluentval.validator;

import com.fluentval.validator.metadata.ValidationMetadata;
import com.fluentval.validator.metadata.ValidationRuleBuilder;
import lombok.Getter;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Represents a validation rule that can be applied to a value of type T.
 * This functional interface serves as the core abstraction for all validation logic
 * in the fluent validation framework.
 *
 * <p>ValidationRule provides a rich set of composition methods that allow rules to be
 * combined, chained, and conditionally applied. This enables building complex validation
 * logic through simple method chaining.</p>
 *
 * <h3>Core Concepts:</h3>
 * <ul>
 *   <li><strong>Validation:</strong> The primary {@link #validate} method performs the actual validation</li>
 *   <li><strong>Composition:</strong> Rules can be combined using methods like {@link #and}, {@link #andAlways}</li>
 *   <li><strong>Conditional Logic:</strong> Rules can be applied conditionally using {@link #andIf}, {@link #breakIf}</li>
 *   <li><strong>Error Handling:</strong> Sophisticated error handling with {@link #andIfNoErrorFor}</li>
 *   <li><strong>Debugging:</strong> Built-in debugging support with {@link #peek} and {@link #debug}</li>
 * </ul>
 *
 * <h3>Basic Usage Examples:</h3>
 *
 * <h4>Simple Rule Creation and Usage:</h4>
 * <pre>{@code
 * // Create a simple validation rule
 * ValidationRule<String> notBlankRule = StringValidationRules.notBlank();
 *
 * // Apply the rule
 * ValidationResult result = new ValidationResult();
 * ValidationIdentifier identifier = ValidationIdentifier.ofField("username");
 * notBlankRule.validate("john_doe", result, identifier);
 *
 * if (result.hasErrors()) {
 *     System.out.println("Validation failed");
 * }
 * }</pre>
 *
 * <h4>Rule Composition with 'and':</h4>
 * <pre>{@code
 * // Combine multiple rules - second rule only runs if first passes
 * ValidationRule<String> combinedRule = StringValidationRules.notBlank()
 *     .and(StringValidationRules.minLength(3))
 *     .and(StringValidationRules.maxLength(20));
 *
 * ValidationResult result = new ValidationResult();
 * combinedRule.validate("ab", result, ValidationIdentifier.ofField("username"));
 * // Will fail at minLength check and won't proceed to maxLength
 * }</pre>
 *
 * <h4>Always Execute All Rules:</h4>
 * <pre>{@code
 * // All rules execute regardless of failures
 * ValidationRule<String> allRulesRule = StringValidationRules.notBlank()
 *     .andAlways(StringValidationRules.minLength(3))
 *     .andAlways(StringValidationRules.maxLength(20));
 *
 * ValidationResult result = new ValidationResult();
 * allRulesRule.validate("", result, ValidationIdentifier.ofField("username"));
 * // Will collect all validation failures
 * }</pre>
 *
 * <h4>Conditional Validation:</h4>
 * <pre>{@code
 * // Apply additional validation only when condition is met
 * ValidationRule<String> conditionalRule = StringValidationRules.notBlank()
 *     .andIf(value -> value.startsWith("admin_"),
 *            StringValidationRules.minLength(10));
 *
 * ValidationResult result = new ValidationResult();
 * conditionalRule.validate("admin_user", result, ValidationIdentifier.ofField("username"));
 * // minLength(10) will only be checked because value starts with "admin_"
 * }</pre>
 *
 * <h4>Cross-Field Validation:</h4>
 * <pre>{@code
 * ValidationIdentifier passwordId = ValidationIdentifier.ofField("password");
 * ValidationIdentifier confirmId = ValidationIdentifier.ofField("confirmPassword");
 *
 * ValidationRule<String> passwordRule = StringValidationRules.notBlank()
 *     .and(StringValidationRules.minLength(8));
 *
 * ValidationRule<String> confirmRule = StringValidationRules.notBlank()
 *     .andIfNoErrorFor(passwordId, CommonValidationRules.isEqual(password));
 *
 * // Confirm password validation only runs if password validation passes
 * }</pre>
 *
 * <h4>Debugging and Monitoring:</h4>
 * <pre>{@code
 * ValidationRule<String> debugRule = StringValidationRules.notBlank()
 *     .peek(state -> System.out.println("Validating: " + state.getValue()))
 *     .and(StringValidationRules.minLength(3))
 *     .debug("username-validation");
 *
 * // Will print debug information during validation
 * }</pre>
 *
 * @param <T> the type of value this rule can validate
 * @author Fluent Validator Team
 * @since 1.0.0
 * @see ValidationResult
 * @see ValidationIdentifier
 * @see ValidationMetadata
 * @see ValidationRuleBuilder
 */
@FunctionalInterface
public interface ValidationRule<T> {

    /**
     * Validates the given value and records any validation failures in the provided result.
     * This is the core method that must be implemented by all validation rules.
     *
     * <p>The validation logic should examine the value and, if validation fails,
     * add appropriate failure records to the ValidationResult using the provided identifier.</p>
     *
     * <p>Example implementation:</p>
     * <pre>{@code
     * ValidationRule<String> customRule = (value, result, identifier) -> {
     *     if (value == null || value.trim().isEmpty()) {
     *         result.addFailure(new ValidationResult.Failure(
     *             StringValidationMetadata.notBlank(identifier)
     *         ));
     *     }
     * };
     * }</pre>
     *
     * @param value the value to validate (may be null)
     * @param result the ValidationResult to record failures in
     * @param identifier the identifier for this validation (used in error messages)
     */
    void validate(final T value,
        final ValidationResult result,
        final ValidationIdentifier identifier);

    /**
     * Combines this rule with another rule, where the second rule only executes
     * if this rule passes (no errors are recorded for the current identifier).
     *
     * <p>This method implements short-circuit logic - if the first rule fails,
     * the second rule is skipped. This is useful for dependent validations where
     * the second rule only makes sense if the first passes.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationRule<String> chainedRule = StringValidationRules.notBlank()
     *     .and(StringValidationRules.minLength(3))    // Only runs if not blank
     *     .and(StringValidationRules.matches("\\w+")); // Only runs if min length met
     *
     * ValidationResult result = new ValidationResult();
     * chainedRule.validate("ab", result, ValidationIdentifier.ofField("field"));
     * // Will fail at minLength and not proceed to regex check
     * }</pre>
     *
     * @param other the rule to execute after this one (if this one passes)
     * @return a new ValidationRule that represents the combination
     */
    default ValidationRule<T> and(final ValidationRule<T> other) {
        return (value, result, identifier) -> {
            validate(value, result, identifier);
            if (!result.hasErrorForIdentifier(identifier)) {
                other.validate(value, result, identifier);
            }
        };
    }

    /**
     * Combines this rule with another rule, where both rules always execute
     * regardless of whether the first rule passes or fails.
     *
     * <p>This method ensures that all validation rules are executed and all
     * possible validation errors are collected. This is useful when you want
     * comprehensive validation feedback.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationRule<String> comprehensiveRule = StringValidationRules.notBlank()
     *     .andAlways(StringValidationRules.minLength(3))
     *     .andAlways(StringValidationRules.maxLength(20))
     *     .andAlways(StringValidationRules.matches("[a-zA-Z0-9]+"));
     *
     * ValidationResult result = new ValidationResult();
     * comprehensiveRule.validate("", result, ValidationIdentifier.ofField("field"));
     * // Will collect all validation failures, not just the first one
     * }</pre>
     *
     * @param other the rule to execute after this one (always executes)
     * @return a new ValidationRule that represents the combination
     */
    default ValidationRule<T> andAlways(final ValidationRule<T> other) {
        return (value, result, identifier) -> {
            validate(value, result, identifier);
            other.validate(value, result, identifier);
        };
    }

    /**
     * Conditionally applies another rule based on a predicate evaluated against the value.
     * The second rule only executes if this rule passes AND the condition is true.
     *
     * <p>This method enables conditional validation logic where certain rules only
     * apply under specific circumstances.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationRule<String> conditionalRule = StringValidationRules.notBlank()
     *     .andIf(value -> value.startsWith("temp_"),
     *            StringValidationRules.maxLength(50))
     *     .andIf(value -> value.startsWith("perm_"),
     *            StringValidationRules.minLength(10));
     *
     * ValidationResult result = new ValidationResult();
     * conditionalRule.validate("temp_user", result, ValidationIdentifier.ofField("username"));
     * // maxLength check will apply because value starts with "temp_"
     * }</pre>
     *
     * @param condition a predicate that determines whether to apply the other rule
     * @param other the rule to execute conditionally
     * @return a new ValidationRule that represents the conditional combination
     */
    default ValidationRule<T> andIf(final Predicate<T> condition,
        final ValidationRule<T> other) {
        return (value, result, identifier) -> {
            validate(value, result, identifier);
            if (!result.hasErrorForIdentifier(identifier) && condition.test(value)) {
                other.validate(value, result, identifier);
            }
        };
    }

    /**
     * Applies another rule only if there are no validation errors for the specified identifiers.
     * This enables cross-field validation dependencies where one field's validation
     * depends on the successful validation of other fields.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationIdentifier passwordId = ValidationIdentifier.ofField("password");
     * ValidationIdentifier emailId = ValidationIdentifier.ofField("email");
     *
     * ValidationRule<String> dependentRule = StringValidationRules.notBlank()
     *     .andIfNoErrorsFor(new ValidationIdentifier[]{passwordId, emailId},
     *                       StringValidationRules.minLength(5));
     *
     * // minLength check only runs if both password and email validations passed
     * }</pre>
     *
     * @param identifiers array of identifiers to check for errors
     * @param other the rule to execute if no errors exist for the specified identifiers
     * @return a new ValidationRule that represents the conditional combination
     */

    default ValidationRule<T> andIfNoErrorsFor(ValidationIdentifier[] identifiers, ValidationRule<T> other) {
        return (value, result, identifier) -> {
            validate(value, result, identifier);

            boolean hasNoErrors = true;
            for (ValidationIdentifier id : identifiers) {
                if (result.hasErrorForIdentifier(id)) {
                    hasNoErrors = false;
                    break;
                }
            }

            if (hasNoErrors) {
                other.validate(value, result, identifier);
            }
        };
    }

    /**
     * Convenience method for {@link #andIfNoErrorsFor(ValidationIdentifier[], ValidationRule)}
     * with varargs parameter ordering.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationRule<String> rule = StringValidationRules.notBlank()
     *     .andIfNoErrorsFor(StringValidationRules.minLength(5),
     *                       ValidationIdentifier.ofField("password"),
     *                       ValidationIdentifier.ofField("email"));
     * }</pre>
     *
     * @param other the rule to execute if no errors exist for the specified identifiers
     * @param identifiers varargs of identifiers to check for errors
     * @return a new ValidationRule that represents the conditional combination
     */
    default ValidationRule<T> andIfNoErrorsFor(ValidationRule<T> other, ValidationIdentifier... identifiers) {
        return andIfNoErrorsFor(identifiers, other);
    }

    /**
     * Applies another rule only if there are no validation errors for a specific identifier.
     * This is a convenience method for single-field dependency validation.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationIdentifier passwordId = ValidationIdentifier.ofField("password");
     *
     * ValidationRule<String> confirmPasswordRule = StringValidationRules.notBlank()
     *     .andIfNoErrorFor(passwordId,
     *                      CommonValidationRules.isEqual(originalPassword));
     *
     * // Equality check only runs if password field has no errors
     * }</pre>
     *
     * @param identifier the identifier to check for errors
     * @param other the rule to execute if no errors exist for the specified identifier
     * @return a new ValidationRule that represents the conditional combination
     */
    default ValidationRule<T> andIfNoErrorFor(ValidationIdentifier identifier, ValidationRule<T> other) {
        return (value, result, currentIdentifier) -> {
            validate(value, result, currentIdentifier);

            if (!result.hasErrorForIdentifier(identifier)) {
                other.validate(value, result, currentIdentifier);
            }
        };
    }

    /**
     * Creates a rule that only executes if the break condition is false.
     * This provides a way to skip validation based on the value being validated.
     *
     * <p>This is useful for implementing rules that should not apply to certain values,
     * such as skipping validation for default or placeholder values.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationRule<String> rule = StringValidationRules.minLength(5)
     *     .breakIf(value -> "DEFAULT".equals(value));
     *
     * ValidationResult result = new ValidationResult();
     * rule.validate("DEFAULT", result, ValidationIdentifier.ofField("field"));
     * // Validation is skipped because break condition is true
     *
     * rule.validate("abc", result, ValidationIdentifier.ofField("field"));
     * // Validation runs and fails because "abc" is less than 5 characters
     * }</pre>
     *
     * @param breakCondition a predicate that determines whether to skip validation
     * @return a new ValidationRule that conditionally executes based on the break condition
     */
    default ValidationRule<T> breakIf(final Predicate<T> breakCondition) {
        return (value, result, identifier) -> {
            if (!breakCondition.test(value)) {
                validate(value, result, identifier);
            }
        };
    }

    /**
     * Adds a side-effect to the validation rule that executes after validation completes.
     * The peek consumer receives a ValidationState object containing the validation context.
     *
     * <p>This method is useful for debugging, logging, or other side-effects that should
     * occur during validation without affecting the validation logic itself.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationRule<String> rule = StringValidationRules.notBlank()
     *     .peek(state -> {
     *         System.out.println("Validated: " + state.getValue());
     *         System.out.println("Has errors: " + state.hasError());
     *         if (state.hasError()) {
     *             System.out.println("Error count: " + state.getErrors().size());
     *         }
     *     })
     *     .and(StringValidationRules.minLength(3));
     * }</pre>
     *
     * @param peekConsumer a consumer that receives the validation state after validation
     * @return a new ValidationRule that includes the peek side-effect
     * @see ValidationState
     */
    default ValidationRule<T> peek(final Consumer<ValidationState<T>> peekConsumer) {
        return (value, result, identifier) -> {
            validate(value, result, identifier);
            peekConsumer.accept(new ValidationState<>(value, result, identifier));
        };
    }

    /**
     * Adds debug output to the validation rule for troubleshooting purposes.
     * This method prints detailed information about the validation process to System.out.
     *
     * <p><strong>Note:</strong> This method is deprecated and intended for debugging only.
     * It should not be used in production code as it produces console output.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationRule<String> rule = StringValidationRules.notBlank()
     *     .debug("blank-check")
     *     .and(StringValidationRules.minLength(3))
     *     .debug("length-check");
     *
     * // Will print debug information for each validation step
     * }</pre>
     *
     * @param name a descriptive name for this validation step (used in debug output)
     * @return a new ValidationRule that includes debugging output
     * @deprecated This method is for debugging only and should not be used in production
     */
    @Deprecated(since = "This method is for debugging only")
    default ValidationRule<T> debug(final String name) {
        return (value, result, identifier) -> {

            System.out.println(
                "DEBUG [" + name + "] Before validation for identifier: " + identifier.value());
            System.out.println("Value: " + (value == null ? "null" : value.toString()));
            System.out.println("Has errors: " + result.hasErrorForIdentifier(identifier));

            validate(value, result, identifier);

            System.out.println(
                "DEBUG [" + name + "] After validation for identifier: " + identifier.value());
            System.out.println("Has errors: " + result.hasErrorForIdentifier(identifier));
            if (result.hasErrorForIdentifier(identifier)) {
                System.out.println("Errors: " + result.getErrorsForIdentifier(identifier));
            }
            System.out.println("---");
        };
    }

    /**
     * Wraps this validation rule with error handling and gives it a descriptive name.
     * If an exception occurs during validation, it will be wrapped in a RuntimeException
     * with context information about which rule failed.
     *
     * <p>This method is useful for debugging complex validation chains and provides
     * better error messages when validation rules throw unexpected exceptions.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationRule<String> rule = StringValidationRules.notBlank()
     *     .named("username-not-blank")
     *     .and(StringValidationRules.minLength(3))
     *     .named("username-min-length")
     *     .and(customComplexRule)
     *     .named("username-custom-validation");
     *
     * // If customComplexRule throws an exception, you'll get a clear error message
     * // indicating which specific rule failed
     * }</pre>
     *
     * @param name a descriptive name for this validation rule
     * @return a new ValidationRule with error handling and naming
     */
    default ValidationRule<T> named(final String name) {
        return (value, result, identifier) -> {
            ValidationRule<T> original = this;
            try {
                original.validate(value, result, identifier);
            } catch (Exception e) {
                throw new RuntimeException(
                    "Error in validation rule '" + name + "' for identifier '" + identifier.value()
                    + "'", e);
            }
        };
    }

    /**
     * Creates a validation rule that always fails with the specified validation metadata.
     * This is useful for creating custom failure conditions or for testing purposes.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationRule<String> alwaysFailRule = ValidationRule.fail(
     *     StringValidationMetadata.notBlank(ValidationIdentifier.ofField("test"))
     * );
     *
     * ValidationResult result = new ValidationResult();
     * alwaysFailRule.validate("any value", result, ValidationIdentifier.ofField("test"));
     * // Will always add a failure to the result
     * }</pre>
     *
     * @param <V> the type of value the rule validates
     * @param validationMetadata the metadata for the validation failure
     * @return a ValidationRule that always fails with the specified metadata
     */
    static <V> ValidationRule<V> fail(final ValidationMetadata validationMetadata) {
        return (value, result, identifier) -> result.addFailure(
            new ValidationResult.Failure(validationMetadata));
    }

    /**
     * Creates a validation rule that does nothing (always passes).
     * This is useful as a placeholder or for conditional rule building.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationRule<String> conditionalRule = someCondition
     *     ? StringValidationRules.notBlank()
     *     : ValidationRule.noop();
     *
     * // Or as a starting point for building complex rules
     * ValidationRule<String> rule = ValidationRule.<String>noop()
     *     .andIf(condition1, StringValidationRules.minLength(5))
     *     .andIf(condition2, StringValidationRules.maxLength(20));
     * }</pre>
     *
     * @param <V> the type of value the rule validates
     * @return a ValidationRule that never fails
     */
    static <V> ValidationRule<V> noop() {
        return (value, result, identifier) -> {
        };
    }

    /**
     * Enriches the validation metadata of any failures produced by this rule.
     * The enricher consumer is called for each validation failure, allowing you to
     * add additional context, modify severity, or set custom properties.
     *
     * <p>This method is useful for adding contextual information to validation failures
     * or for modifying the presentation of validation errors.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationRule<String> rule = StringValidationRules.notBlank()
     *     .withMetadata(metadata -> {
     *         metadata.setSeverity(ValidationMetadata.ValidationSeverity.WARNING);
     *         metadata.setCategory("user-input");
     *         metadata.setSource("registration-form");
     *     });
     *
     * // Any failures from this rule will have the additional metadata
     * }</pre>
     *
     * @param enricher a consumer that modifies the validation metadata
     * @return a new ValidationRule that enriches failure metadata
     * @see ValidationMetadata
     */
    default ValidationRule<T> withMetadata(Consumer<ValidationMetadata> enricher) {
        return (value, result, identifier) -> {
            int initialFailureCount = result.getFailures().size();

            validate(value, result, identifier);

            for (int i = initialFailureCount; i < result.getFailures().size(); i++) {
                result.getFailures().get(i).withEnrichedMetadata(enricher);
            }
        };
    }

    /**
     * Creates a ValidationRuleBuilder for advanced rule configuration.
     * The builder provides additional methods for configuring validation rules
     * with metadata, severity levels, categories, and other advanced features.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationRule<String> configuredRule = ValidationRule
     *     .configure(StringValidationRules.notBlank())
     *     .withSeverity(ValidationMetadata.ValidationSeverity.WARNING)
     *     .withCategory("user-input")
     *     .withGroup("basic-validation")
     *     .blocking(false)
     *     .build();
     * }</pre>
     *
     * @param <T> the type of value the rule validates
     * @param rule the base rule to configure
     * @return a ValidationRuleBuilder for advanced configuration
     * @see ValidationRuleBuilder
     */
    static <T> ValidationRuleBuilder<T> configure(ValidationRule<T> rule) {
        return ValidationRuleBuilder.of(rule);
    }

    /**
     * Represents the state of a validation at a specific point in time.
     * This class provides access to the value being validated, the current validation result,
     * and the identifier being used for validation.
     *
     * <p>ValidationState is primarily used with the {@link #peek} method to provide
     * insight into the validation process without affecting the validation logic.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationRule<String> rule = StringValidationRules.notBlank()
     *     .peek(state -> {
     *         System.out.println("Validating field: " + state.getIdentifier().value());
     *         System.out.println("Current value: " + state.getValue());
     *         System.out.println("Has errors so far: " + state.hasError());
     *
     *         if (state.hasError()) {
     *             state.getErrors().forEach(error ->
     *                 System.out.println("Error: " + error.getValidationMetadata().getErrorCode())
     *             );
     *         }
     *     });
     * }</pre>
     *
     * @param <T> the type of value being validated
     */
    @Getter
    class ValidationState<T> {

        /**
         * The value being validated.
         */
        private final T value;
        /**
         * The current validation result containing any accumulated failures.
         */
        private final ValidationResult result;
        /**
         * The identifier for the current validation.
         */
        private final ValidationIdentifier identifier;

        /**
         * Creates a new ValidationState with the specified parameters.
         *
         * @param value the value being validated
         * @param result the current validation result
         * @param identifier the identifier for the validation
         */
        public ValidationState(final T value,
            final ValidationResult result,
            final ValidationIdentifier identifier) {
            this.value = value;
            this.result = result;
            this.identifier = identifier;
        }

        /**
         * Checks if there are any validation errors for the current identifier.
         *
         * @return true if there are validation errors for this identifier, false otherwise
         */
        public boolean hasError() {
            return result.hasErrorForIdentifier(identifier);
        }

        /**
         * Gets all validation errors for the current identifier.
         *
         * @return a list of validation failures for this identifier
         */
        public List<ValidationResult.Failure> getErrors() {
            return result.getErrorsForIdentifier(identifier);
        }

    }

}