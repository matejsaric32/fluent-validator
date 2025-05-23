package com.fluentval.validator;

import lombok.Getter;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A fluent validation framework that provides a chainable API for validating objects and their properties.
 * The Validator class serves as the main entry point for building validation chains with support for
 * conditional validation, short-circuiting, and nested property validation.
 *
 * <p>This class follows the builder pattern to allow for readable and maintainable validation code.
 * It supports both simple property validation and complex nested validation scenarios.</p>
 *
 * <h3>Basic Usage Examples:</h3>
 *
 * <h4>Simple Property Validation:</h4>
 * <pre>{@code
 * public class User {
 *     private String name;
 *     private int age;
 *     private String email;
 *     // getters and setters...
 * }
 *
 * User user = new User("John", 25, "john@example.com");
 *
 * ValidationResult result = Validator.of(user)
 *     .property(ValidationIdentifier.ofField("name"), User::getName)
 *         .validate(StringValidationRules.notBlank())
 *         .validate(StringValidationRules.minLength(2))
 *         .end()
 *     .property(ValidationIdentifier.ofField("age"), User::getAge)
 *         .validate(NumberValidationRules.min(18))
 *         .validate(NumberValidationRules.max(120))
 *         .end()
 *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
 *         .validate(StringValidationRules.notBlank())
 *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
 *         .end()
 *     .getResult();
 *
 * if (result.hasErrors()) {
 *     result.getFailures().forEach(failure ->
 *         System.out.println(failure.getValidationMetadata().getErrorCode()));
 * }
 * }</pre>
 *
 * <h4>Conditional Validation:</h4>
 * <pre>{@code
 * ValidationResult result = Validator.of(user)
 *     .property(ValidationIdentifier.ofField("age"), User::getAge)
 *         .validateWhen(age -> age != null && age >= 18,
 *                      StringValidationRules.notBlank()) // Only validate if adult
 *         .end()
 *     .getResult();
 * }</pre>
 *
 * <h4>Short-Circuit Validation:</h4>
 * <pre>{@code
 * ValidationResult result = Validator.of(user)
 *     .property(ValidationIdentifier.ofField("name"), User::getName)
 *         .validate(StringValidationRules.notBlank())
 *         .shortCircuitIfErrors() // Stop validation if name is blank
 *         .validate(StringValidationRules.minLength(2))
 *         .end()
 *     .getResult();
 * }</pre>
 *
 * <h4>Nested Object Validation:</h4>
 * <pre>{@code
 * public class Address {
 *     private String street;
 *     private String city;
 *     // getters and setters...
 * }
 *
 * public class User {
 *     private String name;
 *     private Address address;
 *     // getters and setters...
 * }
 *
 * ValidationResult result = Validator.of(user)
 *     .property(ValidationIdentifier.ofField("name"), User::getName)
 *         .validate(StringValidationRules.notBlank())
 *         .end()
 *     .property(ValidationIdentifier.ofField("address"), User::getAddress)
 *         .property(ValidationIdentifier.ofField("street"), Address::getStreet)
 *             .validate(StringValidationRules.notBlank())
 *             .end()
 *         .property(ValidationIdentifier.ofField("city"), Address::getCity)
 *             .validate(StringValidationRules.notBlank())
 *             .end()
 *         .end()
 *     .getResult();
 * }</pre>
 *
 * <h4>Using Existing ValidationResult:</h4>
 * <pre>{@code
 * ValidationResult parentResult = new ValidationResult();
 * // ... add some failures to parentResult
 *
 * ValidationResult combinedResult = Validator.withExistingResult(user, parentResult)
 *     .property(ValidationIdentifier.ofField("name"), User::getName)
 *         .validate(StringValidationRules.notBlank())
 *         .end()
 *     .getResult();
 *
 * // combinedResult will contain both parent and new validation failures
 * }</pre>
 *
 * @param <T> the type of object being validated
 * @author Fluent Validator Team
 * @since 1.0.0
 * @see PropertyValidator
 * @see ValidationResult
 * @see ValidationRule
 * @see ValidationIdentifier
 */
@Getter
public class Validator<T> {

    /**
     * The target object being validated.
     */
    private final T target;

    /**
     * The result object that accumulates validation failures.
     */
    private final ValidationResult result;

    /**
     * Flag indicating whether validation should be short-circuited.
     * When true, subsequent validations will be skipped.
     */
    private boolean shortCircuit = false;

    /**
     * Creates a new Validator instance for the specified target object.
     * This constructor initializes a new ValidationResult internally.
     *
     * @param target the object to be validated
     */
    protected Validator(T target) {
        this(target, new ValidationResult(), false);
    }

    /**
     * Creates a new Validator instance with the specified target, result, and short-circuit flag.
     * This constructor is used internally for creating nested validators and managing state.
     *
     * @param target the object to be validated
     * @param result the ValidationResult to accumulate failures
     * @param shortCircuit whether validation should be short-circuited
     */
    protected Validator(T target, ValidationResult result, boolean shortCircuit) {
        this.target = target;
        this.result = result;
        this.shortCircuit = shortCircuit;
    }

    /**
     * Creates a new Validator instance for the specified target object.
     * This is the main entry point for starting a validation chain.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * User user = new User("John", 25);
     * Validator<User> validator = Validator.of(user);
     *
     * ValidationResult result = validator
     *     .property(ValidationIdentifier.ofField("name"), User::getName)
     *         .validate(StringValidationRules.notBlank())
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @param <T> the type of object to validate
     * @param target the object to be validated
     * @return a new Validator instance for the target object
     */
    public static <T> Validator<T> of(final T target) {
        return new Validator<>(target);
    }

    /**
     * Creates a new Validator instance that uses an existing ValidationResult as its parent.
     * This is useful for combining validation results from multiple validation chains.
     * The new validator will create a ScopedValidationResult that includes failures from both
     * the parent result and any new validations performed.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult parentResult = someOtherValidation();
     *
     * ValidationResult combinedResult = Validator.withExistingResult(user, parentResult)
     *     .property(ValidationIdentifier.ofField("name"), User::getName)
     *         .validate(StringValidationRules.notBlank())
     *         .end()
     *     .getResult();
     *
     * // combinedResult contains failures from both parentResult and new validations
     * }</pre>
     *
     * @param <T> the type of object to validate
     * @param target the object to be validated
     * @param parentResult the existing ValidationResult to include
     * @return a new Validator instance with the existing result as parent
     * @throws IllegalArgumentException if parentResult is null
     */
    public static <T> Validator<T> withExistingResult(final T target, final ValidationResult parentResult) {
        if (parentResult == null) {
            throw new IllegalArgumentException("ValidationResult cannot be null");
        }

        ScopedValidationResult childResult = new ScopedValidationResult(parentResult);
        return new Validator<>(target, childResult, false);
    }

    /**
     * Creates a PropertyValidator for validating a specific property of the target object.
     * The property value is extracted using the provided function.
     *
     * <p>This method is the primary way to start validating individual properties of an object.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("name"), User::getName)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.minLength(2))
     *         .end()
     *     .property(ValidationIdentifier.ofField("age"), User::getAge)
     *         .validate(NumberValidationRules.min(0))
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @param <V> the type of the property being validated
     * @param identifier the identifier for the property (used in error messages)
     * @param extractor a function that extracts the property value from the target object
     * @return a PropertyValidator for the specified property
     * @see PropertyValidator
     * @see ValidationIdentifier
     */
    public <V> PropertyValidator<T, V> property(final ValidationIdentifier identifier, final Function<T, V> extractor) {
        return new PropertyValidator<>(this, identifier, target != null ? extractor.apply(target) : null);
    }

    /**
     * Creates a PropertyValidator for validating a specific value associated with the target object.
     * Unlike the extractor-based method, this allows you to provide the value directly.
     *
     * <p>This method is useful when you have pre-computed values or when the property extraction
     * logic is more complex than a simple getter method.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * String computedValue = someComplexComputation(user);
     *
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofCustom("computed"), computedValue)
     *         .validate(StringValidationRules.notBlank())
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @param <V> the type of the value being validated
     * @param identifier the identifier for the property (used in error messages)
     * @param value the value to be validated
     * @return a PropertyValidator for the specified value
     * @see PropertyValidator
     * @see ValidationIdentifier
     */
    public <V> PropertyValidator<T, V> property(final ValidationIdentifier identifier, final V value) {
        return new PropertyValidator<>(this, identifier, target != null ? value : null);
    }

    /**
     * Validates a value with a rule and immediately short-circuits the validation chain if the rule fails.
     * This method is useful when you need to perform a critical validation that should stop all
     * subsequent validations if it fails.
     *
     * <p>The validation is performed using a temporary ValidationResult, and if any failures occur,
     * they are added to the main result and the short-circuit flag is set to true.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = Validator.of(user)
     *     .validateWithCircuitBreaker(
     *         user.getName(),
     *         ValidationIdentifier.ofField("name"),
     *         StringValidationRules.notBlank()
     *     )
     *     .property(ValidationIdentifier.ofField("age"), User::getAge)
     *         .validate(NumberValidationRules.min(0)) // This won't run if name is blank
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @param <V> the type of the value being validated
     * @param value the value to validate
     * @param identifier the identifier for the value (used in error messages)
     * @param rule the validation rule to apply
     * @return this Validator instance for method chaining
     * @see ValidationRule
     * @see ValidationIdentifier
     */
    public <V> Validator<T> validateWithCircuitBreaker(final V value, final ValidationIdentifier identifier, final ValidationRule<V> rule) {
        ValidationResult tempResult = new ValidationResult();
        rule.validate(value, tempResult, identifier);

        if (tempResult.hasErrors()) {
            tempResult.getFailures().forEach(result::addFailure);
            shortCircuit = true;
        }

        return this;
    }

    /**
     * Sets the short-circuit flag based on a condition applied to the current ValidationResult.
     * When the condition is met, all subsequent validations in the chain will be skipped.
     *
     * <p>This method provides flexible control over when to stop validation based on the
     * current state of the validation result.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("name"), User::getName)
     *         .validate(StringValidationRules.notBlank())
     *         .end()
     *     .shortCircuitIf(validationResult -> validationResult.getFailures().size() > 2)
     *     .property(ValidationIdentifier.ofField("age"), User::getAge)
     *         .validate(NumberValidationRules.min(0)) // Skipped if more than 2 failures
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @param condition a predicate that determines whether to short-circuit based on the ValidationResult
     * @return this Validator instance for method chaining
     */
    public Validator<T> shortCircuitIf(final Predicate<ValidationResult> condition) {
        if (condition.test(result)) {
            shortCircuit = true;
        }
        return this;
    }

    /**
     * Sets the short-circuit flag if there are any validation errors in the current result.
     * This is a convenience method equivalent to {@code shortCircuitIf(ValidationResult::hasErrors)}.
     *
     * <p>This method is commonly used when you want to stop validation as soon as any error occurs.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("name"), User::getName)
     *         .validate(StringValidationRules.notBlank())
     *         .end()
     *     .shortCircuitIfErrors() // Stop if name validation failed
     *     .property(ValidationIdentifier.ofField("age"), User::getAge)
     *         .validate(NumberValidationRules.min(0)) // Skipped if name validation failed
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @return this Validator instance for method chaining
     */
    public Validator<T> shortCircuitIfErrors() {
        return shortCircuitIf(ValidationResult::hasErrors);
    }

    /**
     * Merges scoped failures from another validator into this validator's result.
     * This method is useful when you have performed nested validations and want to
     * consolidate the results.
     *
     * <p>If the other validator's result is a ScopedValidationResult, only the scoped failures
     * (failures specific to that validation scope) are merged, not the parent failures.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * Validator<Address> addressValidator = Validator.of(user.getAddress())
     *     .property(ValidationIdentifier.ofField("street"), Address::getStreet)
     *         .validate(StringValidationRules.notBlank())
     *         .end();
     *
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("name"), User::getName)
     *         .validate(StringValidationRules.notBlank())
     *         .end()
     *     .mergeScopedFailures(addressValidator)
     *     .getResult();
     * }</pre>
     *
     * @param otherValidator the validator whose scoped failures should be merged
     * @return this Validator instance for method chaining
     * @see ScopedValidationResult
     */
    public Validator<T> mergeScopedFailures(Validator<?> otherValidator) {
        if (otherValidator.getResult() instanceof ScopedValidationResult scopedResult) {
            scopedResult.getScopedFailures().forEach(this.result::addFailure);
        }
        return this;
    }

    /**
     * Checks whether this validator is currently in a short-circuited state.
     * This method is used internally by the validation framework to determine
     * whether to skip subsequent validations.
     *
     * <p>This method is package-private as it's intended for internal use by
     * the validation framework components.</p>
     *
     * @return true if the validator is short-circuited, false otherwise
     */
    boolean isShortCircuited() {
        return shortCircuit;
    }
}