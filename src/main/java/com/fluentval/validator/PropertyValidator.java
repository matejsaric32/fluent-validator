package com.fluentval.validator;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A fluent validator for individual properties that provides chainable validation
 * operations with support for conditional validation, short-circuiting, and nested
 * property validation.
 *
 * <p>PropertyValidator serves as the core component for property-level validation
 * within the fluent validation framework. It maintains a reference to the parent
 * validator context while focusing on a specific property or value, enabling
 * sophisticated validation chains with conditional logic and error handling.</p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 * <li><strong>Fluent API:</strong> Chainable method calls for readable validation logic</li>
 * <li><strong>Conditional Validation:</strong> Support for conditional rule application</li>
 * <li><strong>Short-Circuit Logic:</strong> Ability to stop validation on first failure or custom conditions</li>
 * <li><strong>Cross-Field Dependencies:</strong> Validation based on other field states</li>
 * <li><strong>Nested Property Support:</strong> Deep validation of complex object hierarchies</li>
 * <li><strong>Scoped Validation:</strong> Integration with context-aware validation rules</li>
 * <li><strong>Debugging Support:</strong> Built-in peek functionality for inspection</li>
 * </ul>
 *
 * <h3>Usage Examples:</h3>
 *
 * <p><b>Basic Property Validation:</b>
 * <pre>{@code
 * // Simple property validation with multiple rules
 * ValidationResult result = Validator.of(user)
 *     .property(ValidationIdentifier.ofField("name"), User::getName)
 *         .validate(StringValidationRules.notBlank())
 *         .validate(StringValidationRules.minLength(2))
 *         .validate(StringValidationRules.maxLength(50))
 *         .end()
 *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
 *         .validate(StringValidationRules.notBlank())
 *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
 *         .end()
 *     .getResult();
 * }</pre>
 *
 * <p><b>Conditional Validation:</b>
 * <pre>{@code
 * // Apply different validation rules based on property values
 * ValidationResult result = Validator.of(user)
 *     .property(ValidationIdentifier.ofField("age"), User::getAge)
 *         .validate(NumberValidationRules.min(0))
 *         .validate(NumberValidationRules.max(120))
 *         .validateWhen(age -> age != null && age >= 18,
 *                      StringValidationRules.notBlank()) // Adult-specific validation
 *         .validateWhen(age -> age != null && age < 18,
 *                      ValidationRule.noop()) // Minor-specific validation
 *         .end()
 *     .property(ValidationIdentifier.ofField("licenseNumber"), User::getLicenseNumber)
 *         .validateWhen(license -> user.getAge() != null && user.getAge() >= 16,
 *                      StringValidationRules.notBlank())
 *         .end()
 *     .getResult();
 * }</pre>
 *
 * <p><b>Cross-Field Validation Dependencies:</b>
 * <pre>{@code
 * // Password confirmation validation
 * ValidationIdentifier passwordId = ValidationIdentifier.ofField("password");
 * ValidationIdentifier confirmId = ValidationIdentifier.ofField("confirmPassword");
 *
 * ValidationResult result = Validator.of(user)
 *     .property(passwordId, User::getPassword)
 *         .validate(StringValidationRules.notBlank())
 *         .validate(StringValidationRules.minLength(8))
 *         .validate(StringValidationRules.matches(".*[A-Z].*")) // Must contain uppercase
 *         .validate(StringValidationRules.matches(".*[0-9].*")) // Must contain number
 *         .end()
 *     .property(confirmId, User::getConfirmPassword)
 *         .validate(StringValidationRules.notBlank())
 *         .validateIfNoErrorFor(passwordId,
 *                              CommonValidationRules.isEqual(user.getPassword()))
 *         .end()
 *     .getResult();
 * }</pre>
 *
 * <p><b>Short-Circuit Validation:</b>
 * <pre>{@code
 * // Stop validation chain on first error for critical fields
 * ValidationResult result = Validator.of(user)
 *     .property(ValidationIdentifier.ofField("username"), User::getUsername)
 *         .validate(StringValidationRules.notBlank())
 *         .shortCircuitIfErrors() // Stop if username is blank
 *         .validate(StringValidationRules.minLength(3))
 *         .validate(StringValidationRules.matches("[a-zA-Z0-9_]+"))
 *         .end()
 *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
 *         .validate(StringValidationRules.notBlank())
 *         .shortCircuitIf(result -> result.getFailures().size() > 3) // Stop if too many errors
 *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
 *         .end()
 *     .getResult();
 * }</pre>
 *
 * <p><b>Nested Property Validation:</b>
 * <pre>{@code
 * // Deep validation of nested objects
 * ValidationResult result = Validator.of(user)
 *     .property(ValidationIdentifier.ofField("address"), User::getAddress)
 *         .validate(CommonValidationRules.notNull())
 *         .validateIfNoError(ValidationRule.noop()) // Proceed only if address exists
 *         .property(ValidationIdentifier.ofPath("address.street"), Address::getStreet)
 *             .validate(StringValidationRules.notBlank())
 *             .validate(StringValidationRules.minLength(5))
 *             .end()
 *         .property(ValidationIdentifier.ofPath("address.city"), Address::getCity)
 *             .validate(StringValidationRules.notBlank())
 *             .end()
 *         .property(ValidationIdentifier.ofPath("address.zipCode"), Address::getZipCode)
 *             .validate(StringValidationRules.matches("\\d{5}(-\\d{4})?"))
 *             .end()
 *         .end()
 *     .getResult();
 * }</pre>
 *
 * <p><b>Scoped Validation Integration:</b>
 * <pre>{@code
 * // Using scoped validation rules for complex business logic
 * ScopedValidationRule<User> businessRules = (user, validator) -> {
 *     // Complex business validation that needs access to full context
 *     if (user.getRole() == UserRole.ADMIN &&
 *         (user.getSecurityClearance() == null ||
 *          user.getSecurityClearance().getLevel() < SecurityLevel.HIGH)) {
 *         validator.getResult().addFailure(new ValidationResult.Failure(
 *             CommonValidationMetadata.satisfies(
 *                 ValidationIdentifier.ofCustom("adminSecurityClearance"),
 *                 u -> hasRequiredSecurityClearance(u),
 *                 "Admin users require high security clearance"
 *             )
 *         ));
 *     }
 * };
 *
 * ValidationResult result = Validator.of(user)
 *     .property(ValidationIdentifier.ofField("role"), User::getRole)
 *         .validate(CommonValidationRules.notNull())
 *         .end()
 *     .property(ValidationIdentifier.ofCustom("businessValidation"), user)
 *         .validateScoped(businessRules)
 *         .end()
 *     .getResult();
 * }</pre>
 *
 * <p><b>Debugging and Monitoring:</b>
 * <pre>{@code
 * // Using peek for debugging and monitoring validation flow
 * ValidationResult result = Validator.of(user)
 *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
 *         .peek(email -> System.out.println("Validating email: " + email))
 *         .validate(StringValidationRules.notBlank())
 *         .peek(email -> System.out.println("Email not blank check passed"))
 *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
 *         .peek(email -> System.out.println("Email format validation completed"))
 *         .end()
 *     .getResult();
 * }</pre>
 *
 * <p><b>Complex Workflow with Multiple Patterns:</b>
 * <pre>{@code
 * public class UserRegistrationValidator {
 *
 *     public ValidationResult validateRegistration(UserRegistration registration) {
 *         ValidationIdentifier usernameId = ValidationIdentifier.ofField("username");
 *         ValidationIdentifier emailId = ValidationIdentifier.ofField("email");
 *         ValidationIdentifier passwordId = ValidationIdentifier.ofField("password");
 *         ValidationIdentifier confirmPasswordId = ValidationIdentifier.ofField("confirmPassword");
 *         ValidationIdentifier termsId = ValidationIdentifier.ofField("acceptedTerms");
 *
 *         return Validator.of(registration)
 *             // Username validation with short-circuit
 *             .property(usernameId, UserRegistration::getUsername)
 *                 .validate(StringValidationRules.notBlank())
 *                 .shortCircuitIfErrors() // Critical field - stop if blank
 *                 .validate(StringValidationRules.minLength(3))
 *                 .validate(StringValidationRules.maxLength(20))
 *                 .validate(StringValidationRules.matches("[a-zA-Z0-9_]+"))
 *                 .validateWhen(username -> username != null,
 *                              this::validateUsernameAvailability)
 *                 .end()
 *
 *             // Email validation with conditional logic
 *             .property(emailId, UserRegistration::getEmail)
 *                 .validate(StringValidationRules.notBlank())
 *                 .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
 *                 .validateIfNoError(this::validateEmailUniqueness)
 *                 .end()
 *
 *             // Password validation
 *             .property(passwordId, UserRegistration::getPassword)
 *                 .validate(StringValidationRules.notBlank())
 *                 .validate(StringValidationRules.minLength(8))
 *                 .validate(StringValidationRules.matches(".*[A-Z].*")) // Uppercase required
 *                 .validate(StringValidationRules.matches(".*[a-z].*")) // Lowercase required
 *                 .validate(StringValidationRules.matches(".*[0-9].*")) // Number required
 *                 .validate(StringValidationRules.matches(".*[!@#$%^&*].*")) // Special char required
 *                 .end()
 *
 *             // Password confirmation - depends on password validation
 *             .property(confirmPasswordId, UserRegistration::getConfirmPassword)
 *                 .validate(StringValidationRules.notBlank())
 *                 .validateIfNoErrorFor(passwordId,
 *                                      CommonValidationRules.isEqual(registration.getPassword()))
 *                 .end()
 *
 *             // Terms acceptance - business rule
 *             .property(termsId, UserRegistration::getAcceptedTerms)
 *                 .validate(CommonValidationRules.notNull())
 *                 .validateWhen(accepted -> accepted != null,
 *                              this::validateTermsAcceptance)
 *                 .end()
 *
 *             // Optional profile validation
 *             .property(ValidationIdentifier.ofField("profile"), UserRegistration::getProfile)
 *                 .validateWhen(profile -> profile != null,
 *                              this::validateProfileCompleteness)
 *                 .end()
 *
 *             .getResult();
 *     }
 *
 *     private ValidationRule<String> validateUsernameAvailability =
 *         CommonValidationRules.satisfies(
 *             username -> !isUsernameTaken(username),
 *             "Username must be available"
 *         );
 *
 *     private ValidationRule<String> validateEmailUniqueness =
 *         CommonValidationRules.satisfies(
 *             email -> !isEmailRegistered(email),
 *             "Email address must be unique"
 *         );
 *
 *     private ValidationRule<Boolean> validateTermsAcceptance =
 *         CommonValidationRules.satisfies(
 *             accepted -> accepted == Boolean.TRUE,
 *             "Terms and conditions must be accepted"
 *         );
 *
 *     private ValidationRule<UserProfile> validateProfileCompleteness =
 *         CommonValidationRules.satisfies(
 *             profile -> calculateCompletenessScore(profile) >= 60,
 *             "Profile must be at least 60% complete"
 *         );
 *
 *     // Helper methods...
 *     private boolean isUsernameTaken(String username) { return false; }
 *     private boolean isEmailRegistered(String email) { return false; }
 *     private int calculateCompletenessScore(UserProfile profile) { return 70; }
 * }
 * }</pre>
 *
 * @param <T> the type of the parent object being validated
 * @param <V> the type of the property value being validated
 * @author Matej Šarić
 * @since 1.2.3
 * @see Validator
 * @see ValidationRule
 * @see ScopedValidationRule
 * @see ValidationResult
 * @see ValidationIdentifier
 */
public class PropertyValidator<T, V> {

    /**
     * The parent validator that owns this property validation context.
     */
    private final Validator<T> parent;

    /**
     * The identifier for this property, used in validation results and error messages.
     */
    private final ValidationIdentifier identifier;

    /**
     * The actual value of the property being validated.
     */
    private final V value;

    /**
     * Flag indicating whether this property validator should short-circuit further validation.
     */
    private boolean shortCircuit = false;

    /**
     * Package-private constructor for creating PropertyValidator instances.
     * This constructor is called by the Validator class when creating property validation contexts.
     *
     * @param parent the parent validator context
     * @param identifier the identifier for this property
     * @param value the value to be validated
     */
    PropertyValidator(final Validator<T> parent,
                      final ValidationIdentifier identifier,
                      final V value) {
        this.parent = parent;
        this.identifier = identifier;
        this.value = value;
    }

    /**
     * Applies a validation rule to this property's value.
     *
     * <p>The rule is only executed if neither this property validator nor its parent
     * validator is in a short-circuited state. This method is the primary way to
     * add validation logic to a property.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("name"), User::getName)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.minLength(2))
     *         .validate(StringValidationRules.maxLength(50))
     *         .validate(StringValidationRules.matches("[a-zA-Z\\s]+"))
     *         .end()
     *     .getResult();
     *
     * // Custom validation rule
     * ValidationRule<String> customEmailRule = (email, result, identifier) -> {
     *     if (email != null && !isValidEmailDomain(email)) {
     *         result.addFailure(new ValidationResult.Failure(
     *             StringValidationMetadata.satisfies(identifier,
     *                 e -> isValidEmailDomain(e),
     *                 "Email domain not allowed")
     *         ));
     *     }
     * };
     *
     * ValidationResult emailResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
     *         .validate(customEmailRule)
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @param rule the validation rule to apply to this property's value
     * @return this PropertyValidator instance for method chaining
     * @see ValidationRule
     */
    public PropertyValidator<T, V> validate(final ValidationRule<V> rule) {
        if (!shortCircuit && !parent.isShortCircuited()) {
            rule.validate(value, parent.getResult(), identifier);
        }
        return this;
    }

    /**
     * Conditionally applies a validation rule based on a predicate evaluated against the property value.
     *
     * <p>The rule is only executed if the condition returns true and neither this property
     * validator nor its parent validator is short-circuited. This enables conditional
     * validation logic based on the property's value or state.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("age"), User::getAge)
     *         .validate(NumberValidationRules.min(0))
     *         .validate(NumberValidationRules.max(120))
     *         .validateWhen(age -> age != null && age >= 18,
     *                      StringValidationRules.notBlank()) // Adult-specific validation
     *         .validateWhen(age -> age != null && age < 13,
     *                      CommonValidationRules.satisfies(
     *                          a -> hasParentalConsent(user),
     *                          "Parental consent required for users under 13"))
     *         .end()
     *     .property(ValidationIdentifier.ofField("driversLicense"), User::getDriversLicense)
     *         .validateWhen(license -> user.getAge() != null && user.getAge() >= 16,
     *                      StringValidationRules.notBlank())
     *         .validateWhen(license -> license != null && !license.trim().isEmpty(),
     *                      StringValidationRules.matches("[A-Z]{2}\\d{6}"))
     *         .end()
     *     .getResult();
     *
     * // Business logic conditional validation
     * ValidationResult orderResult = Validator.of(order)
     *     .property(ValidationIdentifier.ofField("shippingAddress"), Order::getShippingAddress)
     *         .validateWhen(address -> order.requiresShipping(),
     *                      CommonValidationRules.notNull())
     *         .validateWhen(address -> address != null && order.isInternational(),
     *                      this::validateInternationalAddress)
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @param condition a predicate that determines whether to apply the validation rule
     * @param rule the validation rule to apply conditionally
     * @return this PropertyValidator instance for method chaining
     * @see ValidationRule
     */
    public PropertyValidator<T, V> validateWhen(final Predicate<V> condition,
                                                final ValidationRule<V> rule) {
        if (!shortCircuit && !parent.isShortCircuited() && condition.test(value)) {
            rule.validate(value, parent.getResult(), identifier);
        }
        return this;
    }

    /**
     * Applies a validation rule only if there are no existing validation errors for this property.
     *
     * <p>This method enables sequential validation where subsequent rules only execute
     * if previous rules for the same property have passed. This is useful for dependent
     * validation logic where later rules assume earlier validations were successful.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
     *         .validate(StringValidationRules.notBlank())
     *         .validateIfNoError(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
     *         .validateIfNoError(this::validateEmailUniqueness) // Only check if format is valid
     *         .validateIfNoError(this::validateEmailDomainPolicy) // Only if unique
     *         .end()
     *     .property(ValidationIdentifier.ofField("username"), User::getUsername)
     *         .validate(StringValidationRules.notBlank())
     *         .validateIfNoError(StringValidationRules.minLength(3))
     *         .validateIfNoError(StringValidationRules.matches("[a-zA-Z0-9_]+"))
     *         .validateIfNoError(this::validateUsernameAvailability)
     *         .end()
     *     .getResult();
     *
     * // File validation example
     * ValidationResult fileResult = Validator.of(uploadedFile)
     *     .property(ValidationIdentifier.ofField("content"), UploadedFile::getContent)
     *         .validate(CommonValidationRules.notNull())
     *         .validateIfNoError(this::validateFileSize)
     *         .validateIfNoError(this::validateFileFormat)
     *         .validateIfNoError(this::validateFileContent) // Only if format is valid
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @param rule the validation rule to apply if no errors exist for this property
     * @return this PropertyValidator instance for method chaining
     * @see ValidationRule
     */
    public PropertyValidator<T, V> validateIfNoError(final ValidationRule<V> rule) {
        if (!shortCircuit && !parent.isShortCircuited() &&
                !parent.getResult().hasErrorForIdentifier(identifier)) {
            rule.validate(value, parent.getResult(), identifier);
        }
        return this;
    }

    /**
     * Applies a validation rule only if there are no validation errors for the specified identifier.
     *
     * <p>This method enables cross-field validation dependencies where the validation of one
     * property depends on the successful validation of another property. This is particularly
     * useful for related fields like password confirmation, address validation, or business rule validation.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationIdentifier passwordId = ValidationIdentifier.ofField("password");
     * ValidationIdentifier confirmPasswordId = ValidationIdentifier.ofField("confirmPassword");
     *
     * ValidationResult result = Validator.of(user)
     *     .property(passwordId, User::getPassword)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.minLength(8))
     *         .validate(StringValidationRules.matches(".*[A-Z].*")) // Uppercase required
     *         .validate(StringValidationRules.matches(".*[0-9].*")) // Number required
     *         .end()
     *     .property(confirmPasswordId, User::getConfirmPassword)
     *         .validate(StringValidationRules.notBlank())
     *         .validateIfNoErrorFor(passwordId, // Only check equality if password is valid
     *                              CommonValidationRules.isEqual(user.getPassword()))
     *         .end()
     *     .getResult();
     *
     * // Address validation dependency
     * ValidationIdentifier countryId = ValidationIdentifier.ofField("country");
     * ValidationIdentifier stateId = ValidationIdentifier.ofField("state");
     * ValidationIdentifier zipId = ValidationIdentifier.ofField("zipCode");
     *
     * ValidationResult addressResult = Validator.of(address)
     *     .property(countryId, Address::getCountry)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.oneOf("US", "CA", "MX"))
     *         .end()
     *     .property(stateId, Address::getState)
     *         .validate(StringValidationRules.notBlank())
     *         .validateIfNoErrorFor(countryId, this::validateStateForCountry)
     *         .end()
     *     .property(zipId, Address::getZipCode)
     *         .validate(StringValidationRules.notBlank())
     *         .validateIfNoErrorFor(countryId, this::validateZipForCountry)
     *         .validateIfNoErrorFor(stateId, this::validateZipForState)
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @param otherIdentifier the identifier to check for validation errors
     * @param rule the validation rule to apply if no errors exist for the specified identifier
     * @return this PropertyValidator instance for method chaining
     * @see ValidationRule
     * @see ValidationIdentifier
     */
    public PropertyValidator<T, V> validateIfNoErrorFor(final ValidationIdentifier otherIdentifier,
                                                        final ValidationRule<V> rule) {
        if (!shortCircuit && !parent.isShortCircuited() &&
                !parent.getResult().hasErrorForIdentifier(otherIdentifier)) {
            rule.validate(value, parent.getResult(), identifier);
        }
        return this;
    }

    /**
     * Applies a scoped validation rule that has access to the full validation context.
     *
     * <p>Scoped validation rules receive the parent Validator instance, providing access
     * to the complete validation context including other validation results, the full
     * object being validated, and the ability to perform complex cross-field validation.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Business rule validation with full context access
     * ScopedValidationRule<User> adminValidationRule = (user, validator) -> {
     *     if (user.getRole() == UserRole.ADMIN) {
     *         // Admin users require high security clearance
     *         if (user.getSecurityClearance() == null ||
     *             user.getSecurityClearance().getLevel() < SecurityLevel.HIGH) {
     *             validator.getResult().addFailure(new ValidationResult.Failure(
     *                 CommonValidationMetadata.satisfies(
     *                     ValidationIdentifier.ofCustom("adminSecurityRequirement"),
     *                     u -> hasRequiredSecurityClearance(u),
     *                     "Admin users require high security clearance"
     *                 )
     *             ));
     *         }
     *
     *         // Check if basic user validation passed before applying admin rules
     *         ValidationIdentifier emailId = ValidationIdentifier.ofField("email");
     *         if (!validator.getResult().hasErrorForIdentifier(emailId)) {
     *             // Additional admin-specific email validation
     *             String email = user.getEmail();
     *             if (email != null && !isFromApprovedDomain(email)) {
     *                 validator.getResult().addFailure(new ValidationResult.Failure(
     *                     StringValidationMetadata.satisfies(emailId,
     *                         e -> isFromApprovedDomain(e),
     *                         "Admin users must use approved email domain")
     *                 ));
     *             }
     *         }
     *     }
     * };
     *
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
     *         .end()
     *     .property(ValidationIdentifier.ofField("role"), User::getRole)
     *         .validate(CommonValidationRules.notNull())
     *         .end()
     *     .property(ValidationIdentifier.ofCustom("adminValidation"), user)
     *         .validateScoped(adminValidationRule)
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @param rule the scoped validation rule to apply
     * @return this PropertyValidator instance for method chaining
     * @see ScopedValidationRule
     * @see Validator
     */
    public PropertyValidator<T, V> validateScoped(final ScopedValidationRule<V> rule) {
        if (!shortCircuit && !parent.isShortCircuited()) {
            rule.validate(value, parent);
        }
        return this;
    }

    /**
     * Executes a side-effect consumer with the property value for debugging or monitoring purposes.
     *
     * <p>The consumer is only executed if the property value is not null and neither this
     * property validator nor its parent validator is short-circuited. This method is useful
     * for debugging validation flows, logging, or other side-effects that don't affect validation logic.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
     *         .peek(email -> System.out.println("Validating email: " + email))
     *         .validate(StringValidationRules.notBlank())
     *         .peek(email -> System.out.println("Email blank check passed for: " + email))
     *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
     *         .peek(email -> {
     *             if (isValidEmailFormat(email)) {
     *                 System.out.println("Email format valid: " + email);
     *             } else {
     *                 System.out.println("Email format invalid: " + email);
     *             }
     *         })
     *         .end()
     *     .getResult();
     *
     * // Audit logging example
     * ValidationResult auditResult = Validator.of(sensitiveData)
     *     .property(ValidationIdentifier.ofField("ssn"), SensitiveData::getSsn)
     *         .peek(ssn -> auditLogger.log("SSN validation attempted"))
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.matches("\\d{3}-\\d{2}-\\d{4}"))
     *         .peek(ssn -> auditLogger.log("SSN validation completed"))
     *         .end()
     *     .getResult();
     *
     * // Performance monitoring
     * long startTime = System.currentTimeMillis();
     * ValidationResult perfResult = Validator.of(complexObject)
     *     .property(ValidationIdentifier.ofField("data"), ComplexObject::getData)
     *         .peek(data -> System.out.println("Starting complex validation at: " +
     *                                         (System.currentTimeMillis() - startTime) + "ms"))
     *         .validate(expensiveValidationRule)
     *         .peek(data -> System.out.println("Complex validation completed at: " +
     *                                         (System.currentTimeMillis() - startTime) + "ms"))
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @param consumer a consumer that will be executed with the property value
     * @return this PropertyValidator instance for method chaining
     */
    public PropertyValidator<T, V> peek(final Consumer<V> consumer) {
        if (!shortCircuit && !parent.isShortCircuited() && value != null) {
            consumer.accept(value);
        }
        return this;
    }

    /**
     * Sets the short-circuit flag based on a condition evaluated against the current validation result.
     *
     * <p>When the condition is met, this property validator will skip all subsequent validation
     * operations. This provides fine-grained control over validation flow based on the
     * current validation state.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
     *         .shortCircuitIf(r -> r.getFailures().size() > 5) // Stop if too many total errors
     *         .validate(expensiveEmailValidationRule) // Won't run if condition met
     *         .end()
     *     .property(ValidationIdentifier.ofField("phone"), User::getPhone)
     *         .validate(StringValidationRules.notBlank())
     *         .shortCircuitIf(r -> r.hasErrorForIdentifier(ValidationIdentifier.ofField("email")))
     *         .validate(StringValidationRules.matches("\\d{3}-\\d{3}-\\d{4}")) // Skip if email failed
     *         .end()
     *     .getResult();
     *
     * // Business logic short-circuiting
     * ValidationResult businessResult = Validator.of(order)
     *     .property(ValidationIdentifier.ofField("customerId"), Order::getCustomerId)
     *         .validate(CommonValidationRules.notNull())
     *         .shortCircuitIf(r -> !isCustomerActive(order.getCustomerId()))
     *         .validate(this::validateCustomerCreditLimit) // Skip if customer inactive
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @param condition a predicate that determines whether to short-circuit based on ValidationResult
     * @return this PropertyValidator instance for method chaining
     * @see ValidationResult
     */
    public PropertyValidator<T, V> shortCircuitIf(final Predicate<ValidationResult> condition) {
        if (condition.test(parent.getResult())) {
            shortCircuit = true;
        }
        return this;
    }

    /**
     * Sets the short-circuit flag if there are any validation errors for this property.
     *
     * <p>This is a convenience method equivalent to
     * {@code shortCircuitIf(r -> r.hasErrorForIdentifier(identifier))}.
     * It's commonly used when you want to stop validating a property as soon as
     * the first validation rule fails.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("username"), User::getUsername)
     *         .validate(StringValidationRules.notBlank())
     *         .shortCircuitIfErrors() // Stop if username is blank
     *         .validate(StringValidationRules.minLength(3)) // Won't run if blank
     *         .validate(StringValidationRules.matches("[a-zA-Z0-9_]+")) // Won't run if blank
     *         .validate(expensiveUsernameAvailabilityCheck) // Won't run if blank
     *         .end()
     *     .property(ValidationIdentifier.ofField("password"), User::getPassword)
     *         .validate(StringValidationRules.notBlank())
     *         .shortCircuitIfErrors() // Stop on first password error
     *         .validate(StringValidationRules.minLength(8))
     *         .validate(complexPasswordStrengthRule) // Only if basic rules pass
     *         .end()
     *     .getResult();
     *
     * // File validation with short-circuit
     * ValidationResult fileResult = Validator.of(uploadedFile)
     *     .property(ValidationIdentifier.ofField("content"), UploadedFile::getContent)
     *         .validate(CommonValidationRules.notNull())
     *         .shortCircuitIfErrors() // Stop if no content
     *         .validate(this::validateFileSize)
     *         .shortCircuitIfErrors() // Stop if size invalid
     *         .validate(this::validateFileFormat)
     *         .validate(expensiveContentAnalysis) // Only if basic checks pass
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @return this PropertyValidator instance for method chaining
     */
    public PropertyValidator<T, V> shortCircuitIfErrors() {
        return shortCircuitIf(r -> r.hasErrorForIdentifier(identifier));
    }

    /**
     * Creates a nested PropertyValidator for validating a sub-property of the current property value.
     *
     * <p>This method enables deep validation of complex object hierarchies by creating
     * a new property validation context for a nested property. The nested validator
     * inherits the current validation state and can contribute to the overall validation result.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Deep nested object validation
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("address"), User::getAddress)
     *         .validate(CommonValidationRules.notNull())
     *         .property(ValidationIdentifier.ofPath("address.street"), Address::getStreet)
     *             .validate(StringValidationRules.notBlank())
     *             .validate(StringValidationRules.minLength(5))
     *             .end()
     *         .property(ValidationIdentifier.ofPath("address.city"), Address::getCity)
     *             .validate(StringValidationRules.notBlank())
     *             .validate(StringValidationRules.minLength(2))
     *             .end()
     *         .property(ValidationIdentifier.ofPath("address.zipCode"), Address::getZipCode)
     *             .validate(StringValidationRules.matches("\\d{5}(-\\d{4})?"))
     *             .validateWhen(zip -> user.getAddress().getCountry().equals("US"),
     *                          this::validateUSZipCode)
     *             .end()
     *         .property(ValidationIdentifier.ofPath("address.coordinates"), Address::getCoordinates)
     *             .validate(CommonValidationRules.notNull())
     *             .property(ValidationIdentifier.ofPath("address.coordinates.latitude"),
     *                      Coordinates::getLatitude)
     *                 .validate(NumberValidationRules.range(-90.0, 90.0))
     *                 .end()
     *             .property(ValidationIdentifier.ofPath("address.coordinates.longitude"),
     *                      Coordinates::getLongitude)
     *                 .validate(NumberValidationRules.range(-180.0, 180.0))
     *                 .end()
     *             .end()
     *         .end()
     *     .getResult();
     *
     * // Complex business object validation
     * ValidationResult orderResult = Validator.of(order)
     *     .property(ValidationIdentifier.ofField("customer"), Order::getCustomer)
     *         .validate(CommonValidationRules.notNull())
     *         .property(ValidationIdentifier.ofPath("customer.contactInfo"), Customer::getContactInfo)
     *             .validate(CommonValidationRules.notNull())
     *             .property(ValidationIdentifier.ofPath("customer.contactInfo.email"),
     *                      ContactInfo::getEmail)
     *                 .validate(StringValidationRules.notBlank())
     *                 .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
     *                 .end()
     *             .property(ValidationIdentifier.ofPath("customer.contactInfo.phone"),
     *                      ContactInfo::getPhone)
     *                 .validate(StringValidationRules.notBlank())
     *                 .validate(StringValidationRules.matches("\\d{3}-\\d{3}-\\d{4}"))
     *                 .end()
     *             .end()
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @param <K> the type of the nested property value
     * @param nestedIdentifier the identifier for the nested property
     * @param extractor a function that extracts the nested property value
     * @return a new PropertyValidator for the nested property
     * @see ValidationIdentifier
     */
    public <K> PropertyValidator<V, K> property(final ValidationIdentifier nestedIdentifier,
                                                final Function<V, K> extractor) {
        return new PropertyValidator<>(
                new Validator<>(value, parent.getResult(), shortCircuit),
                nestedIdentifier,
                value != null ? extractor.apply(value) : null
        );
    }

    /**
     * Completes the property validation and returns the parent Validator for continued validation.
     *
     * <p>This method marks the end of the current property validation chain and returns
     * control to the parent validator, allowing for additional property validations or
     * completion of the overall validation process.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("name"), User::getName)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.minLength(2))
     *         .end() // Return to parent validator
     *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
     *         .end() // Return to parent validator
     *     .property(ValidationIdentifier.ofField("age"), User::getAge)
     *         .validate(NumberValidationRules.min(0))
     *         .validate(NumberValidationRules.max(120))
     *         .end() // Return to parent validator
     *     .getResult(); // Complete validation and get result
     *
     * // Conditional property validation with end()
     * Validator<User> validator = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("username"), User::getUsername)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.minLength(3))
     *         .end(); // Get back parent validator
     *
     * // Conditionally add more validation
     * if (requiresEmailValidation) {
     *     validator = validator
     *         .property(ValidationIdentifier.ofField("email"), User::getEmail)
     *             .validate(StringValidationRules.notBlank())
     *             .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
     *             .end();
     * }
     *
     * ValidationResult result = validator.getResult();
     * }</pre>
     *
     * @return the parent Validator instance
     * @see Validator
     */
    public Validator<T> end() {
        return parent;
    }
}