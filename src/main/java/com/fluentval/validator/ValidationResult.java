package com.fluentval.validator;

import com.fluentval.validator.metadata.ValidationMetadata;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents the result of validation operations, containing all validation failures
 * that occurred during the validation process.
 *
 * <p>ValidationResult serves as a container for collecting and organizing validation
 * failures. It provides efficient access to failures both globally and by specific
 * field identifiers, making it easy to process validation results for error reporting,
 * UI feedback, and business logic decisions.</p>
 *
 * <h3>Key Features:</h3>
 * <ul>
 * <li><strong>Failure Collection:</strong> Accumulates validation failures as they occur</li>
 * <li><strong>Identifier-based Access:</strong> Quick lookup of failures by field identifier</li>
 * <li><strong>Immutable Access:</strong> All getter methods return defensive copies</li>
 * <li><strong>Factory Methods:</strong> Convenient creation of success and failure results</li>
 * <li><strong>Metadata Enrichment:</strong> Support for enhancing failure metadata post-validation</li>
 * </ul>
 *
 * <h3>Usage Examples:</h3>
 *
 * <p><b>Basic Validation Result Processing:</b>
 * <pre>{@code
 * // Perform validation and get result
 * ValidationResult result = Validator.of(user)
 *     .property(ValidationIdentifier.ofField("name"), User::getName)
 *         .validate(StringValidationRules.notBlank())
 *         .validate(StringValidationRules.minLength(2))
 *         .end()
 *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
 *         .validate(StringValidationRules.notBlank())
 *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
 *         .end()
 *     .getResult();
 *
 * // Check if validation passed
 * if (result.hasErrors()) {
 *     System.out.println("Validation failed with " + result.getFailures().size() + " errors");
 * } else {
 *     System.out.println("Validation successful");
 * }
 * }</pre>
 *
 * <p><b>Processing Failures by Field:</b>
 * <pre>{@code
 * ValidationResult result = performValidation();
 *
 * ValidationIdentifier nameField = ValidationIdentifier.ofField("name");
 * ValidationIdentifier emailField = ValidationIdentifier.ofField("email");
 *
 * // Check specific field errors
 * if (result.hasErrorForIdentifier(nameField)) {
 *     List<ValidationResult.Failure> nameErrors = result.getErrorsForIdentifier(nameField);
 *     System.out.println("Name field has " + nameErrors.size() + " errors:");
 *
 *     nameErrors.forEach(failure ->
 *         System.out.println("- " + failure.getValidationMetadata().getErrorCode())
 *     );
 * }
 *
 * // Process all field errors
 * Map<ValidationIdentifier, List<ValidationResult.Failure>> errorsByField =
 *     result.getFailuresByIdentifier();
 *
 * errorsByField.forEach((identifier, failures) -> {
 *     System.out.println("Field " + identifier.value() + " has " + failures.size() + " errors");
 * });
 * }</pre>
 *
 * <p><b>Error Reporting and UI Integration:</b>
 * <pre>{@code
 * public class ValidationErrorReporter {
 *
 *     public void reportErrors(ValidationResult result) {
 *         if (!result.hasErrors()) {
 *             return;
 *         }
 *
 *         System.out.println("=== Validation Report ===");
 *         System.out.println("Total errors: " + result.getFailures().size());
 *         System.out.println();
 *
 *         // Group errors by field
 *         result.getFailuresByIdentifier().forEach((identifier, failures) -> {
 *             System.out.println("Field: " + identifier.value());
 *             failures.forEach(failure -> {
 *                 ValidationMetadata metadata = failure.getValidationMetadata();
 *                 System.out.println("  - " + metadata.getErrorCode());
 *                 System.out.println("    Message: " + getErrorMessage(metadata));
 *             });
 *             System.out.println();
 *         });
 *     }
 *
 *     private String getErrorMessage(ValidationMetadata metadata) {
 *         // Use message registry to get human-readable message
 *         return "Validation failed"; // Simplified
 *     }
 * }
 * }</pre>
 *
 * <p><b>Conditional Processing Based on Validation Results:</b>
 * <pre>{@code
 * public class UserRegistrationService {
 *
 *     public RegistrationResult registerUser(User user) {
 *         ValidationResult validationResult = validateUser(user);
 *
 *         if (!validationResult.hasErrors()) {
 *             // Validation passed - proceed with registration
 *             return saveUser(user);
 *         }
 *
 *         // Check for critical vs non-critical errors
 *         ValidationIdentifier emailField = ValidationIdentifier.ofField("email");
 *         ValidationIdentifier passwordField = ValidationIdentifier.ofField("password");
 *
 *         boolean hasCriticalErrors = validationResult.hasErrorForIdentifier(emailField) ||
 *                                   validationResult.hasErrorForIdentifier(passwordField);
 *
 *         if (hasCriticalErrors) {
 *             return RegistrationResult.failure("Critical validation errors", validationResult);
 *         } else {
 *             // Only minor errors - proceed with warnings
 *             return RegistrationResult.warningSuccess("Registration completed with warnings",
 *                                                    validationResult);
 *         }
 *     }
 * }
 * }</pre>
 *
 * <p><b>Creating and Manipulating Results:</b>
 * <pre>{@code
 * // Create success result
 * ValidationResult success = ValidationResult.success();
 * assert !success.hasErrors();
 *
 * // Create failure result
 * ValidationResult failure = ValidationResult.failure(
 *     StringValidationMetadata.notBlank(ValidationIdentifier.ofField("name"))
 * );
 * assert failure.hasErrors();
 * assert failure.getFailures().size() == 1;
 *
 * // Programmatically add failures
 * ValidationResult result = new ValidationResult();
 * result.addFailure(new ValidationResult.Failure(
 *     StringValidationMetadata.minLength(ValidationIdentifier.ofField("name"), 3)
 * ));
 * result.addFailure(new ValidationResult.Failure(
 *     StringValidationMetadata.notBlank(ValidationIdentifier.ofField("email"))
 * ));
 *
 * System.out.println("Total failures: " + result.getFailures().size()); // 2
 * }</pre>
 *
 * <p><b>Advanced Error Processing:</b>
 * <pre>{@code
 * public class ValidationResultAnalyzer {
 *
 *     public ValidationSummary analyze(ValidationResult result) {
 *         if (!result.hasErrors()) {
 *             return ValidationSummary.success();
 *         }
 *
 *         Map<String, Integer> errorsByCategory = new HashMap<>();
 *         Map<ValidationMetadata.ValidationSeverity, Integer> errorsBySeverity = new HashMap<>();
 *
 *         // Analyze all failures
 *         result.getFailures().forEach(failure -> {
 *             ValidationMetadata metadata = failure.getValidationMetadata();
 *
 *             // Count by category
 *             String category = metadata.getCategory();
 *             if (category != null) {
 *                 errorsByCategory.merge(category, 1, Integer::sum);
 *             }
 *
 *             // Count by severity
 *             ValidationMetadata.ValidationSeverity severity = metadata.getSeverity();
 *             errorsBySeverity.merge(severity, 1, Integer::sum);
 *         });
 *
 *         return new ValidationSummary(
 *             result.getFailures().size(),
 *             result.getFailuresByIdentifier().size(),
 *             errorsByCategory,
 *             errorsBySeverity
 *         );
 *     }
 * }
 * }</pre>
 *
 * @author Fluent Validator Team
 * @since 1.0.0
 * @see ValidationIdentifier
 * @see ValidationMetadata
 * @see Validator
 * @see ValidationRule
 */
public class ValidationResult {

    /**
     * List of all validation failures in the order they were added.
     */
    private final List<Failure> failures = new ArrayList<>();

    /**
     * Map of validation failures organized by their identifier for quick lookup.
     */
    private final Map<ValidationIdentifier, List<Failure>> failuresByIdentifier = new HashMap<>();

    /**
     * Adds a validation failure to this result.
     * The failure is added to both the global failure list and the identifier-specific lookup map.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = new ValidationResult();
     * ValidationMetadata metadata = StringValidationMetadata.notBlank(
     *     ValidationIdentifier.ofField("username")
     * );
     *
     * result.addFailure(new ValidationResult.Failure(metadata));
     *
     * assert result.hasErrors();
     * assert result.getFailures().size() == 1;
     * }</pre>
     *
     * @param failure the validation failure to add
     * @throws NullPointerException if failure is null
     */
    public void addFailure(final Failure failure) {
        failures.add(failure);
        failuresByIdentifier
                .computeIfAbsent(failure.getValidationMetadata().getIdentifier(), k -> new ArrayList<>())
                .add(failure);
    }

    /**
     * Checks if this validation result contains any failures.
     *
     * <p>This is the primary method for determining if validation was successful.
     * A validation is considered successful if and only if there are no failures.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = performValidation(user);
     *
     * if (result.hasErrors()) {
     *     // Handle validation failures
     *     displayErrors(result.getFailures());
     * } else {
     *     // Proceed with business logic
     *     processValidUser(user);
     * }
     * }</pre>
     *
     * @return true if there are validation failures, false if validation was successful
     */
    public boolean hasErrors() {
        return !failures.isEmpty();
    }

    /**
     * Checks if there are any validation failures for the specified identifier.
     *
     * <p>This method enables field-specific error checking, which is useful for
     * conditional processing, UI error display, and field-level validation logic.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = validateUser(user);
     * ValidationIdentifier emailField = ValidationIdentifier.ofField("email");
     * ValidationIdentifier passwordField = ValidationIdentifier.ofField("password");
     *
     * if (result.hasErrorForIdentifier(emailField)) {
     *     System.out.println("Email validation failed");
     *     // Handle email-specific errors
     * }
     *
     * if (result.hasErrorForIdentifier(passwordField)) {
     *     System.out.println("Password validation failed");
     *     // Handle password-specific errors
     * }
     *
     * // Check for multiple field dependencies
     * boolean hasAuthenticationErrors = result.hasErrorForIdentifier(emailField) ||
     *                                  result.hasErrorForIdentifier(passwordField);
     * }</pre>
     *
     * @param identifier the validation identifier to check for failures
     * @return true if there are failures for the specified identifier, false otherwise
     */
    public boolean hasErrorForIdentifier(final ValidationIdentifier identifier) {
        return failuresByIdentifier.containsKey(identifier) &&
                !failuresByIdentifier.get(identifier).isEmpty();
    }

    /**
     * Returns a defensive copy of all validation failures.
     *
     * <p>The returned list contains failures in the order they were added to the result.
     * Modifying the returned list will not affect the internal state of this ValidationResult.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = performValidation();
     *
     * if (result.hasErrors()) {
     *     List<ValidationResult.Failure> allFailures = result.getFailures();
     *
     *     System.out.println("Found " + allFailures.size() + " validation errors:");
     *
     *     allFailures.forEach(failure -> {
     *         ValidationMetadata metadata = failure.getValidationMetadata();
     *         System.out.println("- Field: " + metadata.getIdentifier().value());
     *         System.out.println("  Error: " + metadata.getErrorCode());
     *         System.out.println("  Severity: " + metadata.getSeverity());
     *     });
     * }
     * }</pre>
     *
     * @return a new list containing all validation failures
     */
    public List<Failure> getFailures() {
        return new ArrayList<>(failures);
    }

    /**
     * Returns a defensive copy of all validation failures for the specified identifier.
     *
     * <p>If no failures exist for the identifier, an empty list is returned.
     * The returned list contains failures in the order they were added.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = performValidation();
     * ValidationIdentifier nameField = ValidationIdentifier.ofField("name");
     *
     * List<ValidationResult.Failure> nameErrors = result.getErrorsForIdentifier(nameField);
     *
     * if (!nameErrors.isEmpty()) {
     *     System.out.println("Name field validation errors:");
     *     nameErrors.forEach(failure -> {
     *         String errorCode = failure.getValidationMetadata().getErrorCode();
     *         System.out.println("- " + errorCode);
     *     });
     * } else {
     *     System.out.println("Name field validation passed");
     * }
     * }</pre>
     *
     * @param identifier the validation identifier to get failures for
     * @return a new list containing failures for the specified identifier, or empty list if none exist
     */
    public List<Failure> getErrorsForIdentifier(final ValidationIdentifier identifier) {
        return failuresByIdentifier.containsKey(identifier)
                ? new ArrayList<>(failuresByIdentifier.get(identifier))
                : List.of();
    }

    /**
     * Returns a defensive copy of all failures organized by their identifiers.
     *
     * <p>This method provides a complete view of all validation failures grouped by
     * the field or property that failed validation. Both the outer map and inner
     * lists are defensive copies that can be safely modified without affecting
     * this ValidationResult.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult result = performValidation();
     * Map<ValidationIdentifier, List<ValidationResult.Failure>> errorsByField =
     *     result.getFailuresByIdentifier();
     *
     * if (!errorsByField.isEmpty()) {
     *     System.out.println("Validation failed for " + errorsByField.size() + " fields:");
     *
     *     errorsByField.forEach((identifier, failures) -> {
     *         System.out.println("Field '" + identifier.value() + "':");
     *         failures.forEach(failure -> {
     *             String code = failure.getValidationMetadata().getErrorCode();
     *             System.out.println("  - " + code);
     *         });
     *     });
     * }
     *
     * // Safe to modify returned collections
     * errorsByField.clear(); // Does not affect the ValidationResult
     * }</pre>
     *
     * @return a new map containing all failures organized by identifier
     */
    public Map<ValidationIdentifier, List<Failure>> getFailuresByIdentifier() {
        Map<ValidationIdentifier, List<Failure>> copy = new HashMap<>();
        failuresByIdentifier.forEach((k, v) -> copy.put(k, new ArrayList<>(v)));
        return copy;
    }

    /**
     * Creates a new ValidationResult representing successful validation (no failures).
     *
     * <p>This factory method provides a convenient way to create a ValidationResult
     * that indicates successful validation. The returned result will have no failures
     * and {@link #hasErrors()} will return false.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * public ValidationResult validateSimpleRule(String value) {
     *     if (value != null && !value.trim().isEmpty()) {
     *         return ValidationResult.success();
     *     } else {
     *         return ValidationResult.failure(
     *             StringValidationMetadata.notBlank(ValidationIdentifier.ofField("value"))
     *         );
     *     }
     * }
     *
     * // Usage
     * ValidationResult result = validateSimpleRule("Hello");
     * assert !result.hasErrors();
     * assert result.getFailures().isEmpty();
     * }</pre>
     *
     * @return a new ValidationResult with no failures
     */
    public static ValidationResult success() {
        return new ValidationResult(); // Empty result means success
    }

    /**
     * Creates a new ValidationResult with a single failure based on the provided metadata.
     *
     * <p>This factory method provides a convenient way to create a ValidationResult
     * that represents a single validation failure. This is useful for simple validation
     * scenarios or when programmatically creating validation results.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Create a failure result for a specific validation
     * ValidationResult result = ValidationResult.failure(
     *     StringValidationMetadata.minLength(
     *         ValidationIdentifier.ofField("password"),
     *         8
     *     )
     * );
     *
     * assert result.hasErrors();
     * assert result.getFailures().size() == 1;
     *
     * // Use in custom validation methods
     * public ValidationResult validatePassword(String password) {
     *     if (password == null || password.length() < 8) {
     *         return ValidationResult.failure(
     *             StringValidationMetadata.minLength(
     *                 ValidationIdentifier.ofField("password"), 8
     *             )
     *         );
     *     }
     *     return ValidationResult.success();
     * }
     * }</pre>
     *
     * @param validationMetadata the metadata describing the validation failure
     * @return a new ValidationResult containing a single failure
     * @throws NullPointerException if validationMetadata is null
     */
    public static ValidationResult failure(final ValidationMetadata validationMetadata) {
        ValidationResult result = new ValidationResult();
        result.addFailure(new Failure(validationMetadata));
        return result;
    }

    /**
     * Represents a single validation failure with associated metadata.
     *
     * <p>A Failure encapsulates the information about what validation rule failed,
     * including the validation metadata that provides details about the failure
     * such as error codes, field identifiers, and validation parameters.</p>
     *
     * <p>Failures support metadata enrichment, allowing additional context to be
     * added after the failure is created. This is useful for adding contextual
     * information or modifying failure properties based on validation results.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Create a failure
     * ValidationMetadata metadata = StringValidationMetadata.notBlank(
     *     ValidationIdentifier.ofField("username")
     * );
     * ValidationResult.Failure failure = new ValidationResult.Failure(metadata);
     *
     * // Access failure information
     * String errorCode = failure.getValidationMetadata().getErrorCode();
     * String fieldName = failure.getValidationMetadata().getIdentifier().value();
     *
     * // Enrich with additional metadata
     * failure.withEnrichedMetadata(meta -> {
     *     meta.setSeverity(ValidationMetadata.ValidationSeverity.WARNING);
     *     meta.setCategory("user-input");
     * });
     * }</pre>
     */
    @Getter
    @ToString
    public static final class Failure {

        /**
         * The validation metadata containing details about this failure.
         */
        private final ValidationMetadata validationMetadata;

        /**
         * Creates a new Failure with the specified validation metadata.
         *
         * @param validationMetadata the metadata describing this validation failure
         * @throws NullPointerException if validationMetadata is null
         */
        public Failure(ValidationMetadata validationMetadata) {
            this.validationMetadata = validationMetadata;
        }

        /**
         * Enriches the validation metadata of this failure with additional information.
         *
         * <p>This method allows modification of the failure's metadata after creation,
         * which is useful for adding contextual information, adjusting severity levels,
         * or setting additional properties based on validation context.</p>
         *
         * <p>The enricher consumer receives the ValidationMetadata object and can modify
         * its properties directly. The same Failure instance is returned to support
         * method chaining.</p>
         *
         * <p>Example usage:</p>
         * <pre>{@code
         * ValidationResult.Failure failure = new ValidationResult.Failure(metadata);
         *
         * // Enrich with additional context
         * failure.withEnrichedMetadata(meta -> {
         *         meta.setSeverity(ValidationMetadata.ValidationSeverity.WARNING);
         *         meta.setCategory("business-rules");
         *         meta.setSource("user-registration");
         *     })
         *     .withEnrichedMetadata(meta -> {
         *         // Additional enrichment
         *         meta.setValidationGroup("critical");
         *     });
         *
         * // Enrichment in validation rules
         * ValidationRule<String> enrichedRule = StringValidationRules.notBlank()
         *     .withMetadata(meta -> {
         *         meta.setSeverity(ValidationMetadata.ValidationSeverity.ERROR);
         *         meta.setCategory("required-fields");
         *     });
         * }</pre>
         *
         * @param enricher a consumer that modifies the validation metadata
         * @return this Failure instance for method chaining
         * @see ValidationMetadata#enrich(Consumer)
         */
        public Failure withEnrichedMetadata(Consumer<ValidationMetadata> enricher) {
            validationMetadata.enrich(enricher);
            return this;
        }
    }
}