package com.fluentval.validator;

/**
 * A specialized validation rule that operates within a validation scope, having access
 * to the broader validation context through a Validator instance.
 *
 * <p>ScopedValidationRule differs from standard {@link ValidationRule} by providing
 * access to the entire validation context, enabling complex validation scenarios such as:</p>
 * <ul>
 * <li>Cross-field validation that depends on multiple properties</li>
 * <li>Conditional validation based on other validation results</li>
 * <li>Nested object validation with context awareness</li>
 * <li>Business rule validation that requires access to the entire object</li>
 * <li>Validation orchestration and workflow management</li>
 * </ul>
 *
 * <h3>Key Characteristics:</h3>
 * <ul>
 * <li><strong>Context Access:</strong> Full access to the validation context via Validator</li>
 * <li><strong>Scope Awareness:</strong> Can examine other validation results and object state</li>
 * <li><strong>Composability:</strong> Support for chaining multiple scoped rules</li>
 * <li><strong>Flexibility:</strong> Can perform complex multi-field validation logic</li>
 * </ul>
 *
 * <h3>Usage Examples:</h3>
 *
 * <p><b>Cross-Field Validation:</b>
 * <pre>{@code
 * // Password confirmation validation that checks multiple fields
 * ScopedValidationRule<User> passwordConfirmationRule = (user, validator) -> {
 *     String password = user.getPassword();
 *     String confirmPassword = user.getConfirmPassword();
 *
 *     // Only validate confirmation if password is present and valid
 *     ValidationIdentifier passwordId = ValidationIdentifier.ofField("password");
 *     if (!validator.getResult().hasErrorForIdentifier(passwordId) && password != null) {
 *         ValidationIdentifier confirmId = ValidationIdentifier.ofField("confirmPassword");
 *
 *         if (!password.equals(confirmPassword)) {
 *             validator.getResult().addFailure(new ValidationResult.Failure(
 *                 CommonValidationMetadata.isEqual(confirmId, password)
 *             ));
 *         }
 *     }
 * };
 *
 * // Use in validation chain
 * ValidationResult result = Validator.of(user)
 *     .property(ValidationIdentifier.ofField("password"), User::getPassword)
 *         .validate(StringValidationRules.notBlank())
 *         .validate(StringValidationRules.minLength(8))
 *         .validateScoped(passwordConfirmationRule)
 *         .end()
 *     .getResult();
 * }</pre>
 *
 * <p><b>Conditional Business Rule Validation:</b>
 * <pre>{@code
 * // Credit limit validation that depends on user type and credit score
 * ScopedValidationRule<LoanApplication> creditLimitRule = (loan, validator) -> {
 *     User applicant = loan.getApplicant();
 *     BigDecimal requestedAmount = loan.getRequestedAmount();
 *
 *     // Different rules based on user type
 *     ValidationIdentifier amountId = ValidationIdentifier.ofField("requestedAmount");
 *
 *     if (applicant.getUserType() == UserType.PREMIUM) {
 *         // Premium users can request up to $100,000
 *         if (requestedAmount.compareTo(new BigDecimal("100000")) > 0) {
 *             validator.getResult().addFailure(new ValidationResult.Failure(
 *                 NumberValidationMetadata.max(amountId, new BigDecimal("100000"))
 *             ));
 *         }
 *     } else if (applicant.getCreditScore() >= 700) {
 *         // High credit score users can request up to $50,000
 *         if (requestedAmount.compareTo(new BigDecimal("50000")) > 0) {
 *             validator.getResult().addFailure(new ValidationResult.Failure(
 *                 NumberValidationMetadata.max(amountId, new BigDecimal("50000"))
 *             ));
 *         }
 *     } else {
 *         // Regular users limited to $25,000
 *         if (requestedAmount.compareTo(new BigDecimal("25000")) > 0) {
 *             validator.getResult().addFailure(new ValidationResult.Failure(
 *                 NumberValidationMetadata.max(amountId, new BigDecimal("25000"))
 *             ));
 *         }
 *     }
 * };
 * }</pre>
 *
 * <p><b>Nested Object Validation with Context:</b>
 * <pre>{@code
 * // Address validation that considers the parent order context
 * ScopedValidationRule<Order> shippingAddressRule = (order, validator) -> {
 *     Address shippingAddress = order.getShippingAddress();
 *
 *     if (shippingAddress != null) {
 *         // Create nested validator for address with parent context
 *         Validator<Address> addressValidator = Validator.withExistingResult(
 *             shippingAddress, validator.getResult()
 *         );
 *
 *         // Validate address fields
 *         ValidationResult addressResult = addressValidator
 *             .property(ValidationIdentifier.ofPath("shippingAddress.street"), Address::getStreet)
 *                 .validate(StringValidationRules.notBlank())
 *                 .end()
 *             .property(ValidationIdentifier.ofPath("shippingAddress.city"), Address::getCity)
 *                 .validate(StringValidationRules.notBlank())
 *                 .end()
 *             .property(ValidationIdentifier.ofPath("shippingAddress.zipCode"), Address::getZipCode)
 *                 .validate(StringValidationRules.matches("\\d{5}(-\\d{4})?"))
 *                 .end()
 *             .getResult();
 *
 *         // Special validation for international orders
 *         if (order.isInternational()) {
 *             ValidationIdentifier countryId = ValidationIdentifier.ofPath("shippingAddress.country");
 *             if (shippingAddress.getCountry() == null || shippingAddress.getCountry().trim().isEmpty()) {
 *                 validator.getResult().addFailure(new ValidationResult.Failure(
 *                     StringValidationMetadata.notBlank(countryId)
 *                 ));
 *             }
 *         }
 *
 *         // Merge any scoped failures back to main validator
 *         validator.mergeScopedFailures(addressValidator);
 *     }
 * };
 * }</pre>
 *
 * <p><b>Multi-Step Validation Workflow:</b>
 * <pre>{@code
 * // Complex user registration validation with multiple stages
 * ScopedValidationRule<UserRegistration> registrationWorkflow = (registration, validator) -> {
 *     // Stage 1: Basic field validation (already done by individual field validators)
 *
 *     // Stage 2: Cross-field validation
 *     ValidationIdentifier emailId = ValidationIdentifier.ofField("email");
 *     ValidationIdentifier usernameId = ValidationIdentifier.ofField("username");
 *
 *     // Only proceed if basic validations passed
 *     if (!validator.getResult().hasErrorForIdentifier(emailId) &&
 *         !validator.getResult().hasErrorForIdentifier(usernameId)) {
 *
 *         // Stage 3: Business rule validation
 *         if (isEmailAlreadyRegistered(registration.getEmail())) {
 *             validator.getResult().addFailure(new ValidationResult.Failure(
 *                 CommonValidationMetadata.satisfies(emailId,
 *                     email -> !isEmailAlreadyRegistered(email),
 *                     "Email must not be already registered")
 *             ));
 *         }
 *
 *         if (isUsernameAlreadyTaken(registration.getUsername())) {
 *             validator.getResult().addFailure(new ValidationResult.Failure(
 *                 CommonValidationMetadata.satisfies(usernameId,
 *                     username -> !isUsernameAlreadyTaken(username),
 *                     "Username must be available")
 *             ));
 *         }
 *
 *         // Stage 4: Profile completeness validation
 *         if (registration.getProfile() != null) {
 *             validateProfileCompleteness(registration.getProfile(), validator);
 *         }
 *     }
 * };
 *
 * private void validateProfileCompleteness(UserProfile profile, Validator<?> validator) {
 *     // Custom profile validation logic
 *     ValidationIdentifier profileId = ValidationIdentifier.ofCustom("profileCompleteness");
 *
 *     int completenessScore = calculateCompletenessScore(profile);
 *     if (completenessScore < 60) { // Require at least 60% completion
 *         validator.getResult().addFailure(new ValidationResult.Failure(
 *             NumberValidationMetadata.min(profileId, 60)
 *         ));
 *     }
 * }
 * }</pre>
 *
 * <p><b>Chaining Scoped Rules:</b>
 * <pre>{@code
 * // Compose multiple scoped validation rules
 * ScopedValidationRule<Order> basicOrderValidation = (order, validator) -> {
 *     // Basic order validation logic
 *     if (order.getItems().isEmpty()) {
 *         ValidationIdentifier itemsId = ValidationIdentifier.ofField("items");
 *         validator.getResult().addFailure(new ValidationResult.Failure(
 *             CollectionValidationMetadata.notEmpty(itemsId)
 *         ));
 *     }
 * };
 *
 * ScopedValidationRule<Order> paymentValidation = (order, validator) -> {
 *     // Payment method validation
 *     if (order.getPaymentMethod() == null) {
 *         ValidationIdentifier paymentId = ValidationIdentifier.ofField("paymentMethod");
 *         validator.getResult().addFailure(new ValidationResult.Failure(
 *             CommonValidationMetadata.notNull(paymentId)
 *         ));
 *     }
 * };
 *
 * ScopedValidationRule<Order> shippingValidation = (order, validator) -> {
 *     // Shipping validation logic
 *     if (order.getShippingMethod() == null && order.requiresShipping()) {
 *         ValidationIdentifier shippingId = ValidationIdentifier.ofField("shippingMethod");
 *         validator.getResult().addFailure(new ValidationResult.Failure(
 *             CommonValidationMetadata.notNull(shippingId)
 *         ));
 *     }
 * };
 *
 * // Chain the scoped rules together
 * ScopedValidationRule<Order> completeOrderValidation = basicOrderValidation
 *     .and(paymentValidation)
 *     .and(shippingValidation);
 *
 * // Use in validation
 * ValidationResult result = Validator.of(order)
 *     .property(ValidationIdentifier.ofCustom("orderValidation"), order)
 *         .validateScoped(completeOrderValidation)
 *         .end()
 *     .getResult();
 * }</pre>
 *
 * <p><b>Integration with Standard Validation Rules:</b>
 * <pre>{@code
 * public class UserValidationService {
 *
 *     public ValidationResult validateUser(User user) {
 *         // Define scoped rule for complex validation
 *         ScopedValidationRule<User> complexUserValidation = (u, validator) -> {
 *             // Age-based validation rules
 *             if (u.getAge() != null && u.getAge() < 18) {
 *                 // Minors require parental consent
 *                 ValidationIdentifier consentId = ValidationIdentifier.ofField("parentalConsent");
 *                 if (u.getParentalConsent() == null || !u.getParentalConsent()) {
 *                     validator.getResult().addFailure(new ValidationResult.Failure(
 *                         CommonValidationMetadata.satisfies(consentId,
 *                             consent -> consent != null && consent,
 *                             "Parental consent required for users under 18")
 *                     ));
 *                 }
 *             }
 *
 *             // Role-based validation
 *             if (u.getRole() == UserRole.ADMIN) {
 *                 validateAdminRequirements(u, validator);
 *             }
 *         };
 *
 *         return Validator.of(user)
 *             // Standard field validations
 *             .property(ValidationIdentifier.ofField("name"), User::getName)
 *                 .validate(StringValidationRules.notBlank())
 *                 .validate(StringValidationRules.minLength(2))
 *                 .end()
 *             .property(ValidationIdentifier.ofField("email"), User::getEmail)
 *                 .validate(StringValidationRules.notBlank())
 *                 .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
 *                 .end()
 *             .property(ValidationIdentifier.ofField("age"), User::getAge)
 *                 .validate(NumberValidationRules.min(0))
 *                 .validate(NumberValidationRules.max(120))
 *                 .end()
 *             // Complex scoped validation
 *             .property(ValidationIdentifier.ofCustom("userComplexValidation"), user)
 *                 .validateScoped(complexUserValidation)
 *                 .end()
 *             .getResult();
 *     }
 *
 *     private void validateAdminRequirements(User user, Validator<?> validator) {
 *         // Admin-specific validation logic
 *         ValidationIdentifier adminId = ValidationIdentifier.ofCustom("adminRequirements");
 *
 *         if (user.getSecurityClearance() == null ||
 *             user.getSecurityClearance().getLevel() < SecurityLevel.HIGH) {
 *             validator.getResult().addFailure(new ValidationResult.Failure(
 *                 CommonValidationMetadata.satisfies(adminId,
 *                     u -> u.getSecurityClearance() != null &&
 *                          u.getSecurityClearance().getLevel() >= SecurityLevel.HIGH,
 *                     "Admin users require high security clearance")
 *             ));
 *         }
 *     }
 * }
 * }</pre>
 *
 * @param <T> the type of value this scoped rule can validate
 * @author Fluent Validator Team
 * @since 1.0.0
 * @see ValidationRule
 * @see Validator
 * @see ValidationResult
 * @see ValidationIdentifier
 */
@FunctionalInterface
public interface ScopedValidationRule<T> {

    /**
     * Validates the given value within the context of the provided Validator.
     *
     * <p>This method performs validation with full access to the validation context,
     * including the ability to examine other validation results, access the complete
     * object being validated, and add failures to the validation result.</p>
     *
     * <p>Unlike standard {@link ValidationRule#validate}, this method receives a
     * Validator instance that provides:</p>
     * <ul>
     * <li>Access to the current ValidationResult via {@code validator.getResult()}</li>
     * <li>Access to the target object being validated via {@code validator.getTarget()}</li>
     * <li>Ability to check validation state and short-circuit conditions</li>
     * <li>Context for nested validation scenarios</li>
     * </ul>
     *
     * <p>Example implementation:</p>
     * <pre>{@code
     * ScopedValidationRule<User> emailUniqueRule = (user, validator) -> {
     *     // Only validate if email field passed basic validation
     *     ValidationIdentifier emailId = ValidationIdentifier.ofField("email");
     *
     *     if (!validator.getResult().hasErrorForIdentifier(emailId)) {
     *         String email = user.getEmail();
     *
     *         if (email != null && isEmailAlreadyRegistered(email)) {
     *             validator.getResult().addFailure(new ValidationResult.Failure(
     *                 CommonValidationMetadata.satisfies(emailId,
     *                     e -> !isEmailAlreadyRegistered(e),
     *                     "Email address must be unique")
     *             ));
     *         }
     *     }
     * };
     * }</pre>
     *
     * <p>Advanced example with nested validation:</p>
     * <pre>{@code
     * ScopedValidationRule<Order> orderItemsRule = (order, validator) -> {
     *     List<OrderItem> items = order.getItems();
     *
     *     if (items != null) {
     *         for (int i = 0; i < items.size(); i++) {
     *             OrderItem item = items.get(i);
     *             ValidationIdentifier itemId = ValidationIdentifier.ofIndex("items[" + i + "]");
     *
     *             // Validate individual item
     *             if (item.getQuantity() <= 0) {
     *                 validator.getResult().addFailure(new ValidationResult.Failure(
     *                     NumberValidationMetadata.positive(
     *                         ValidationIdentifier.ofPath("items[" + i + "].quantity")
     *                     )
     *                 ));
     *             }
     *
     *             // Business rule: Check inventory availability
     *             if (!isInventoryAvailable(item.getProductId(), item.getQuantity())) {
     *                 validator.getResult().addFailure(new ValidationResult.Failure(
     *                     CommonValidationMetadata.satisfies(
     *                         ValidationIdentifier.ofPath("items[" + i + "].availability"),
     *                         itm -> isInventoryAvailable(itm.getProductId(), itm.getQuantity()),
     *                         "Insufficient inventory for requested quantity"
     *                     )
     *                 ));
     *             }
     *         }
     *     }
     * };
     * }</pre>
     *
     * @param value the value to validate
     * @param validator the validator providing validation context and result access
     */
    void validate(final T value, final Validator<?> validator);

    /**
     * Combines this scoped validation rule with another scoped rule.
     * Both rules will always execute in sequence, regardless of whether
     * the first rule encounters validation failures.
     *
     * <p>This method enables composition of complex validation logic by
     * chaining multiple scoped rules together. Each rule in the chain
     * has access to the cumulative validation state, allowing for
     * sophisticated validation workflows.</p>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Define individual scoped rules
     * ScopedValidationRule<User> basicInfoRule = (user, validator) -> {
     *     // Validate basic user information
     *     if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
     *         ValidationIdentifier firstNameId = ValidationIdentifier.ofField("firstName");
     *         validator.getResult().addFailure(new ValidationResult.Failure(
     *             StringValidationMetadata.notBlank(firstNameId)
     *         ));
     *     }
     * };
     *
     * ScopedValidationRule<User> contactInfoRule = (user, validator) -> {
     *     // Validate contact information
     *     if (user.getEmail() != null && !isValidEmailFormat(user.getEmail())) {
     *         ValidationIdentifier emailId = ValidationIdentifier.ofField("email");
     *         validator.getResult().addFailure(new ValidationResult.Failure(
     *             StringValidationMetadata.matches(emailId, Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
     *         ));
     *     }
     * };
     *
     * ScopedValidationRule<User> businessRuleRule = (user, validator) -> {
     *     // Apply business rules only if basic validation passed
     *     if (!validator.getResult().hasErrors()) {
     *         validateBusinessRules(user, validator);
     *     }
     * };
     *
     * // Chain the rules together
     * ScopedValidationRule<User> completeValidation = basicInfoRule
     *     .and(contactInfoRule)
     *     .and(businessRuleRule);
     *
     * // Use the composed rule
     * ValidationResult result = Validator.of(user)
     *     .property(ValidationIdentifier.ofCustom("userValidation"), user)
     *         .validateScoped(completeValidation)
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * <p>Multi-stage validation example:</p>
     * <pre>{@code
     * // Stage 1: Structural validation
     * ScopedValidationRule<Document> structureRule = (doc, validator) -> {
     *     if (doc.getSections() == null || doc.getSections().isEmpty()) {
     *         ValidationIdentifier sectionsId = ValidationIdentifier.ofField("sections");
     *         validator.getResult().addFailure(new ValidationResult.Failure(
     *             CollectionValidationMetadata.notEmpty(sectionsId)
     *         ));
     *     }
     * };
     *
     * // Stage 2: Content validation
     * ScopedValidationRule<Document> contentRule = (doc, validator) -> {
     *     if (doc.getSections() != null) {
     *         for (Section section : doc.getSections()) {
     *             validateSectionContent(section, validator);
     *         }
     *     }
     * };
     *
     * // Stage 3: Business rules validation
     * ScopedValidationRule<Document> businessRule = (doc, validator) -> {
     *     // Only apply business rules if structure and content are valid
     *     if (!validator.getResult().hasErrors()) {
     *         validateDocumentBusinessRules(doc, validator);
     *     }
     * };
     *
     * // Compose all stages
     * ScopedValidationRule<Document> fullDocumentValidation = structureRule
     *     .and(contentRule)
     *     .and(businessRule);
     * }</pre>
     *
     * @param other the scoped validation rule to execute after this one
     * @return a new ScopedValidationRule that represents the combination of both rules
     */

    default ScopedValidationRule<T> and(final ScopedValidationRule<T> other) {
        return (value, validator) -> {
            validate(value, validator);
            other.validate(value, validator);
        };
    }
    
}