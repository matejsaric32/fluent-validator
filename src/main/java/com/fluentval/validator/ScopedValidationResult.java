package com.fluentval.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * A specialized ValidationResult that maintains a hierarchical relationship with a parent
 * ValidationResult, enabling scoped validation scenarios where validation results need
 * to be composed and isolated while still maintaining access to parent context.
 *
 * <p>ScopedValidationResult extends the standard {@link ValidationResult} by providing
 * a two-tier validation result structure:</p>
 * <ul>
 * <li><strong>Parent Scope:</strong> Validation failures from the parent context</li>
 * <li><strong>Local Scope:</strong> Validation failures specific to the current validation scope</li>
 * <li><strong>Combined View:</strong> A unified view of both parent and local failures</li>
 * </ul>
 *
 * <h3>Key Features:</h3>
 * <ul>
 * <li><strong>Hierarchical Results:</strong> Maintains parent-child relationship between validation results</li>
 * <li><strong>Scope Isolation:</strong> Can distinguish between local and inherited failures</li>
 * <li><strong>Transparent Access:</strong> Standard ValidationResult methods work seamlessly with combined results</li>
 * <li><strong>Flexible Composition:</strong> Enables complex validation workflows with nested contexts</li>
 * </ul>
 *
 * <h3>Common Use Cases:</h3>
 * <ul>
 * <li>Nested object validation where parent context matters</li>
 * <li>Multi-stage validation workflows</li>
 * <li>Conditional validation based on parent validation state</li>
 * <li>Validation result composition and merging</li>
 * <li>Scoped validation rules with context awareness</li>
 * </ul>
 *
 * <h3>Usage Examples:</h3>
 *
 * <p><b>Basic Scoped Validation:</b>
 * <pre>{@code
 * // Parent validation result with some failures
 * ValidationResult parentResult = Validator.of(user)
 *     .property(ValidationIdentifier.ofField("name"), User::getName)
 *         .validate(StringValidationRules.notBlank())
 *         .end()
 *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
 *         .validate(StringValidationRules.notBlank())
 *         .end()
 *     .getResult();
 *
 * // Create scoped validation result for address validation
 * ScopedValidationResult scopedResult = new ScopedValidationResult(parentResult);
 *
 * // Add address-specific failures
 * scopedResult.addFailure(new ValidationResult.Failure(
 *     StringValidationMetadata.notBlank(ValidationIdentifier.ofField("street"))
 * ));
 *
 * // Check combined results
 * System.out.println("Has any errors: " + scopedResult.hasErrors());
 * System.out.println("Total failures: " + scopedResult.getFailures().size());
 * System.out.println("Scoped failures only: " + scopedResult.getScopedFailures().size());
 * }</pre>
 *
 * <p><b>Nested Object Validation:</b>
 * <pre>{@code
 * public class OrderValidator {
 *
 *     public ValidationResult validateOrder(Order order) {
 *         // Validate basic order properties
 *         ValidationResult orderResult = Validator.of(order)
 *             .property(ValidationIdentifier.ofField("orderNumber"), Order::getOrderNumber)
 *                 .validate(StringValidationRules.notBlank())
 *                 .end()
 *             .property(ValidationIdentifier.ofField("customerId"), Order::getCustomerId)
 *                 .validate(CommonValidationRules.notNull())
 *                 .end()
 *             .getResult();
 *
 *         // Validate shipping address with parent context
 *         if (order.getShippingAddress() != null) {
 *             ValidationResult addressResult = validateAddress(
 *                 order.getShippingAddress(),
 *                 orderResult
 *             );
 *
 *             // Merge scoped failures back to main result
 *             if (addressResult instanceof ScopedValidationResult scopedResult) {
 *                 scopedResult.getScopedFailures().forEach(orderResult::addFailure);
 *             }
 *         }
 *
 *         return orderResult;
 *     }
 *
 *     private ValidationResult validateAddress(Address address, ValidationResult parentResult) {
 *         // Create scoped validator with parent context
 *         Validator<Address> addressValidator = Validator.withExistingResult(address, parentResult);
 *
 *         return addressValidator
 *             .property(ValidationIdentifier.ofPath("shippingAddress.street"), Address::getStreet)
 *                 .validate(StringValidationRules.notBlank())
 *                 .validate(StringValidationRules.minLength(5))
 *                 .end()
 *             .property(ValidationIdentifier.ofPath("shippingAddress.city"), Address::getCity)
 *                 .validate(StringValidationRules.notBlank())
 *                 .end()
 *             .property(ValidationIdentifier.ofPath("shippingAddress.zipCode"), Address::getZipCode)
 *                 .validate(StringValidationRules.matches("\\d{5}(-\\d{4})?"))
 *                 .end()
 *             .getResult();
 *     }
 * }
 * }</pre>
 *
 * <p><b>Conditional Validation Based on Parent State:</b>
 * <pre>{@code
 * public class UserProfileValidator {
 *
 *     public ValidationResult validateUserProfile(User user) {
 *         // Validate basic user information first
 *         ValidationResult userResult = Validator.of(user)
 *             .property(ValidationIdentifier.ofField("username"), User::getUsername)
 *                 .validate(StringValidationRules.notBlank())
 *                 .validate(StringValidationRules.minLength(3))
 *                 .end()
 *             .property(ValidationIdentifier.ofField("email"), User::getEmail)
 *                 .validate(StringValidationRules.notBlank())
 *                 .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
 *                 .end()
 *             .getResult();
 *
 *         // Validate profile only if basic user validation passed
 *         if (user.getProfile() != null) {
 *             ValidationResult profileResult = validateProfile(user.getProfile(), userResult);
 *
 *             // The profile result is scoped - it contains both user and profile failures
 *             return profileResult;
 *         }
 *
 *         return userResult;
 *     }
 *
 *     private ValidationResult validateProfile(UserProfile profile, ValidationResult parentResult) {
 *         ScopedValidationResult scopedResult = new ScopedValidationResult(parentResult);
 *
 *         // Only validate profile if parent validation passed
 *         if (!parentResult.hasErrors()) {
 *             // Validate profile-specific fields
 *             if (profile.getBio() != null && profile.getBio().length() > 500) {
 *                 scopedResult.addFailure(new ValidationResult.Failure(
 *                     StringValidationMetadata.maxLength(
 *                         ValidationIdentifier.ofPath("profile.bio"), 500
 *                     )
 *                 ));
 *             }
 *
 *             if (profile.getAge() != null && profile.getAge() < 13) {
 *                 scopedResult.addFailure(new ValidationResult.Failure(
 *                     NumberValidationMetadata.min(
 *                         ValidationIdentifier.ofPath("profile.age"), 13
 *                     )
 *                 ));
 *             }
 *         } else {
 *             // Add a scoped failure indicating profile validation was skipped
 *             scopedResult.addFailure(new ValidationResult.Failure(
 *                 CommonValidationMetadata.satisfies(
 *                     ValidationIdentifier.ofCustom("profileValidation"),
 *                     p -> false,
 *                     "Profile validation skipped due to user validation failures"
 *                 )
 *             ));
 *         }
 *
 *         return scopedResult;
 *     }
 * }
 * }</pre>
 *
 * <p><b>Multi-Stage Validation Workflow:</b>
 * <pre>{@code
 * public class DocumentProcessingValidator {
 *
 *     public ValidationResult processDocument(Document document) {
 *         // Stage 1: Structure validation
 *         ValidationResult structureResult = validateStructure(document);
 *
 *         // Stage 2: Content validation (depends on structure)
 *         ValidationResult contentResult = validateContent(document, structureResult);
 *
 *         // Stage 3: Business rules validation (depends on both structure and content)
 *         ValidationResult businessResult = validateBusinessRules(document, contentResult);
 *
 *         return businessResult; // Contains all failures from all stages
 *     }
 *
 *     private ValidationResult validateStructure(Document document) {
 *         return Validator.of(document)
 *             .property(ValidationIdentifier.ofField("title"), Document::getTitle)
 *                 .validate(StringValidationRules.notBlank())
 *                 .end()
 *             .property(ValidationIdentifier.ofField("sections"), Document::getSections)
 *                 .validate(CollectionValidationRules.notEmpty())
 *                 .end()
 *             .getResult();
 *     }
 *
 *     private ValidationResult validateContent(Document document, ValidationResult parentResult) {
 *         ScopedValidationResult contentResult = new ScopedValidationResult(parentResult);
 *
 *         // Only validate content if structure is valid
 *         if (!parentResult.hasErrors()) {
 *             // Validate each section's content
 *             List<Section> sections = document.getSections();
 *             if (sections != null) {
 *                 for (int i = 0; i < sections.size(); i++) {
 *                     Section section = sections.get(i);
 *                     ValidationIdentifier sectionId = ValidationIdentifier.ofIndex("sections[" + i + "]");
 *
 *                     if (section.getContent() == null || section.getContent().trim().isEmpty()) {
 *                         contentResult.addFailure(new ValidationResult.Failure(
 *                             StringValidationMetadata.notBlank(
 *                                 ValidationIdentifier.ofPath("sections[" + i + "].content")
 *                             )
 *                         ));
 *                     }
 *                 }
 *             }
 *         }
 *
 *         return contentResult;
 *     }
 *
 *     private ValidationResult validateBusinessRules(Document document, ValidationResult parentResult) {
 *         ScopedValidationResult businessResult = new ScopedValidationResult(parentResult);
 *
 *         // Only apply business rules if no structural or content errors
 *         if (!parentResult.hasErrors()) {
 *             // Check document completeness
 *             int completenessScore = calculateCompletenessScore(document);
 *             if (completenessScore < 80) {
 *                 businessResult.addFailure(new ValidationResult.Failure(
 *                     NumberValidationMetadata.min(
 *                         ValidationIdentifier.ofCustom("documentCompleteness"), 80
 *                     )
 *                 ));
 *             }
 *
 *             // Check business-specific rules
 *             if (!meetsBusinessStandards(document)) {
 *                 businessResult.addFailure(new ValidationResult.Failure(
 *                     CommonValidationMetadata.satisfies(
 *                         ValidationIdentifier.ofCustom("businessStandards"),
 *                         doc -> meetsBusinessStandards(doc),
 *                         "Document must meet business standards"
 *                     )
 *                 ));
 *             }
 *         }
 *
 *         return businessResult;
 *     }
 *
 *     private int calculateCompletenessScore(Document document) {
 *         // Implementation details...
 *         return 85; // Example score
 *     }
 *
 *     private boolean meetsBusinessStandards(Document document) {
 *         // Implementation details...
 *         return true; // Example result
 *     }
 * }
 * }</pre>
 *
 * @author Matej Šarić
 * @since 1.0.0
 * @see ValidationResult
 * @see Validator#withExistingResult(Object, ValidationResult)
 * @see ScopedValidationRule
 * @see ValidationIdentifier
 */
public class ScopedValidationResult extends ValidationResult {

    /**
     * The parent ValidationResult that provides the broader validation context.
     */
    private final ValidationResult parentResult;

    /**
     * Creates a new ScopedValidationResult with the specified parent ValidationResult.
     *
     * <p>The parent result provides the broader validation context, while this scoped
     * result will collect additional validation failures specific to the current scope.
     * All query methods will consider both parent and local failures.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Parent validation result from user validation
     * ValidationResult userResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("name"), User::getName)
     *         .validate(StringValidationRules.notBlank())
     *         .end()
     *     .getResult();
     *
     * // Create scoped result for address validation
     * ScopedValidationResult addressResult = new ScopedValidationResult(userResult);
     *
     * // Add address-specific failures
     * addressResult.addFailure(new ValidationResult.Failure(
     *     StringValidationMetadata.notBlank(ValidationIdentifier.ofField("street"))
     * ));
     *
     * // Query methods consider both parent and scoped failures
     * boolean hasAnyErrors = addressResult.hasErrors(); // true if user OR address has errors
     * int totalFailures = addressResult.getFailures().size(); // user + address failures
     * int scopedFailures = addressResult.getScopedFailures().size(); // address failures only
     * }</pre>
     *
     * @param parentResult the parent ValidationResult to inherit from
     * @throws NullPointerException if parentResult is null
     */
    public ScopedValidationResult(ValidationResult parentResult) {
        this.parentResult = parentResult;
    }

    /**
     * Checks if this validation result or its parent contains any failures.
     *
     * <p>This method returns true if either the current scope has validation failures
     * or the parent scope has validation failures. This provides a comprehensive
     * view of the validation state across both scopes.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationResult parentResult = validateBasicInfo(user);
     * ScopedValidationResult scopedResult = new ScopedValidationResult(parentResult);
     *
     * // Add some scoped failures
     * scopedResult.addFailure(new ValidationResult.Failure(
     *     StringValidationMetadata.notBlank(ValidationIdentifier.ofField("address"))
     * ));
     *
     * // Check combined error state
     * if (scopedResult.hasErrors()) {
     *     // Will be true if EITHER parent OR scoped validation failed
     *     System.out.println("Validation failed in parent or current scope");
     * }
     *
     * // Compare with scoped-only errors
     * if (super.hasErrors()) {
     *     System.out.println("Current scope has errors");
     * }
     *
     * if (parentResult.hasErrors()) {
     *     System.out.println("Parent scope has errors");
     * }
     * }</pre>
     *
     * @return true if either this scope or the parent scope has validation failures
     */
    @Override
    public boolean hasErrors() {
        return super.hasErrors() || parentResult.hasErrors();
    }

    /**
     * Checks if there are any validation failures for the specified identifier
     * in either this scope or the parent scope.
     *
     * <p>This method searches for failures with the given identifier in both
     * the current scope and the parent scope, providing a comprehensive view
     * of field-specific validation state.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationIdentifier emailId = ValidationIdentifier.ofField("email");
     * ValidationIdentifier nameId = ValidationIdentifier.ofField("name");
     *
     * // Parent validation with email error
     * ValidationResult parentResult = Validator.of(user)
     *     .property(emailId, User::getEmail)
     *         .validate(StringValidationRules.notBlank())
     *         .end()
     *     .getResult();
     *
     * // Scoped validation with name error
     * ScopedValidationResult scopedResult = new ScopedValidationResult(parentResult);
     * scopedResult.addFailure(new ValidationResult.Failure(
     *     StringValidationMetadata.notBlank(nameId)
     * ));
     *
     * // Check for errors across both scopes
     * boolean hasEmailError = scopedResult.hasErrorForIdentifier(emailId); // true (from parent)
     * boolean hasNameError = scopedResult.hasErrorForIdentifier(nameId);   // true (from scope)
     *
     * // Field-specific error handling
     * if (scopedResult.hasErrorForIdentifier(emailId)) {
     *     System.out.println("Email validation failed (could be from parent or current scope)");
     * }
     * }</pre>
     *
     * @param identifier the validation identifier to check for failures
     * @return true if there are failures for the specified identifier in either scope
     */
    @Override
    public boolean hasErrorForIdentifier(final ValidationIdentifier identifier) {
        return super.hasErrorForIdentifier(identifier) ||
                parentResult.hasErrorForIdentifier(identifier);
    }

    /**
     * Returns a combined list of all validation failures from both this scope and the parent scope.
     *
     * <p>The returned list contains all failures from the parent scope followed by all failures
     * from the current scope. This provides a comprehensive view of all validation failures
     * across the entire validation hierarchy.</p>
     *
     * <p>The returned list is a defensive copy and can be safely modified without affecting
     * the internal state of this ScopedValidationResult.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Parent validation with failures
     * ValidationResult parentResult = Validator.of(user)
     *     .property(ValidationIdentifier.ofField("email"), User::getEmail)
     *         .validate(StringValidationRules.notBlank())
     *         .end()
     *     .getResult();
     *
     * // Scoped validation with additional failures
     * ScopedValidationResult scopedResult = new ScopedValidationResult(parentResult);
     * scopedResult.addFailure(new ValidationResult.Failure(
     *     StringValidationMetadata.notBlank(ValidationIdentifier.ofField("address"))
     * ));
     *
     * // Get all failures from both scopes
     * List<ValidationResult.Failure> allFailures = scopedResult.getFailures();
     * System.out.println("Total failures: " + allFailures.size());
     *
     * // Process all failures
     * allFailures.forEach(failure -> {
     *     ValidationMetadata metadata = failure.getValidationMetadata();
     *     System.out.println("Field: " + metadata.getIdentifier().value());
     *     System.out.println("Error: " + metadata.getErrorCode());
     * });
     *
     * // Compare with scoped-only failures
     * List<ValidationResult.Failure> scopedOnly = scopedResult.getScopedFailures();
     * System.out.println("Scoped failures: " + scopedOnly.size());
     * System.out.println("Parent failures: " + (allFailures.size() - scopedOnly.size()));
     * }</pre>
     *
     * @return a new list containing all failures from both parent and current scope
     */
    @Override
    public List<Failure> getFailures() {
        List<Failure> allFailures = new ArrayList<>(parentResult.getFailures());
        allFailures.addAll(super.getFailures());
        return allFailures;
    }

    /**
     * Returns a defensive copy of validation failures specific to this scope only,
     * excluding any failures from the parent scope.
     *
     * <p>This method provides access to only the validation failures that were added
     * directly to this ScopedValidationResult, allowing for fine-grained analysis
     * of validation results and scope-specific error handling.</p>
     *
     * <p>The returned list is a defensive copy and can be safely modified without
     * affecting the internal state of this ScopedValidationResult.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Create scoped validation result
     * ValidationResult parentResult = validateParent();
     * ScopedValidationResult scopedResult = new ScopedValidationResult(parentResult);
     *
     * // Add scope-specific failures
     * scopedResult.addFailure(new ValidationResult.Failure(
     *     StringValidationMetadata.notBlank(ValidationIdentifier.ofField("street"))
     * ));
     * scopedResult.addFailure(new ValidationResult.Failure(
     *     StringValidationMetadata.notBlank(ValidationIdentifier.ofField("city"))
     * ));
     *
     * // Get only the failures added to this scope
     * List<ValidationResult.Failure> scopedFailures = scopedResult.getScopedFailures();
     * System.out.println("Scoped failures: " + scopedFailures.size()); // 2
     *
     * // Compare with total failures (parent + scoped)
     * List<ValidationResult.Failure> allFailures = scopedResult.getFailures();
     * System.out.println("Total failures: " + allFailures.size()); // parent + 2
     *
     * // Process only scope-specific failures
     * scopedFailures.forEach(failure -> {
     *     System.out.println("Scoped error: " +
     *         failure.getValidationMetadata().getErrorCode());
     * });
     *
     * // Use in error reporting
     * if (!scopedFailures.isEmpty()) {
     *     System.out.println("Address validation failed with " +
     *                       scopedFailures.size() + " errors");
     * }
     * }</pre>
     *
     * <p>This method is particularly useful for:</p>
     * <ul>
     * <li>Scope-specific error reporting and UI feedback</li>
     * <li>Analyzing which validation stage contributed specific failures</li>
     * <li>Merging scoped failures back to parent validation results</li>
     * <li>Conditional processing based on scope-specific validation state</li>
     * </ul>
     *
     * @return a new list containing only the failures specific to this scope
     */
    public List<Failure> getScopedFailures() {
        return new ArrayList<>(super.getFailures());
    }
}