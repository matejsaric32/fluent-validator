package com.fluentval.validator;


import com.fluentval.validator.metadata.ValidationMetadata;
import lombok.EqualsAndHashCode;

/**
 * Represents an identifier for validation contexts, providing type-safe identification
 * of fields, paths, indices, and custom validation targets within the validation framework.
 *
 * <p>ValidationIdentifier serves as a key component for organizing and tracking validation
 * results. It associates validation failures with specific elements being validated,
 * enabling precise error reporting and field-specific validation logic.</p>
 *
 * <h3>Identifier Types:</h3>
 * <ul>
 * <li><strong>FIELD:</strong> Identifies object properties or form fields (most common)</li>
 * <li><strong>PATH:</strong> Identifies nested object paths using dot notation</li>
 * <li><strong>INDEX:</strong> Identifies array or collection elements by index</li>
 * <li><strong>CUSTOM:</strong> Identifies custom validation contexts or computed values</li>
 * </ul>
 *
 * <h3>Key Features:</h3>
 * <ul>
 * <li><strong>Type Safety:</strong> Distinguishes between different kinds of validation targets</li>
 * <li><strong>Immutability:</strong> Thread-safe and cacheable identifier instances</li>
 * <li><strong>Equality Support:</strong> Proper equals/hashCode for use in collections</li>
 * <li><strong>Factory Methods:</strong> Convenient creation methods for each identifier type</li>
 * </ul>
 *
 * <h3>Usage Examples:</h3>
 *
 * <p><b>Basic Field Validation:</b>
 * <pre>{@code
 * // Simple field identifiers for object properties
 * ValidationIdentifier nameId = ValidationIdentifier.ofField("name");
 * ValidationIdentifier emailId = ValidationIdentifier.ofField("email");
 * ValidationIdentifier ageId = ValidationIdentifier.ofField("age");
 *
 * // Use in validation chains
 * ValidationResult result = Validator.of(user)
 *     .property(nameId, User::getName)
 *         .validate(StringValidationRules.notBlank())
 *         .end()
 *     .property(emailId, User::getEmail)
 *         .validate(StringValidationRules.notBlank())
 *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
 *         .end()
 *     .property(ageId, User::getAge)
 *         .validate(NumberValidationRules.min(0))
 *         .validate(NumberValidationRules.max(120))
 *         .end()
 *     .getResult();
 * }</pre>
 *
 * <p><b>Nested Object Path Validation:</b>
 * <pre>{@code
 * // Path identifiers for nested object validation
 * ValidationIdentifier addressStreetId = ValidationIdentifier.ofPath("address.street");
 * ValidationIdentifier addressCityId = ValidationIdentifier.ofPath("address.city");
 * ValidationIdentifier addressZipId = ValidationIdentifier.ofPath("address.zipCode");
 *
 * // Use with nested property extraction
 * ValidationResult result = Validator.of(user)
 *     .property(addressStreetId, u -> u.getAddress().getStreet())
 *         .validate(StringValidationRules.notBlank())
 *         .validate(StringValidationRules.minLength(5))
 *         .end()
 *     .property(addressCityId, u -> u.getAddress().getCity())
 *         .validate(StringValidationRules.notBlank())
 *         .end()
 *     .property(addressZipId, u -> u.getAddress().getZipCode())
 *         .validate(StringValidationRules.matches("\\d{5}(-\\d{4})?"))
 *         .end()
 *     .getResult();
 * }</pre>
 *
 * <p><b>Collection Index Validation:</b>
 * <pre>{@code
 * // Index identifiers for array/collection elements
 * List<String> items = Arrays.asList("item1", "", "item3");
 * ValidationResult result = new ValidationResult();
 *
 * for (int i = 0; i < items.size(); i++) {
 *     ValidationIdentifier itemId = ValidationIdentifier.ofIndex("items[" + i + "]");
 *     StringValidationRules.notBlank().validate(items.get(i), result, itemId);
 * }
 *
 * // Check specific index errors
 * ValidationIdentifier secondItemId = ValidationIdentifier.ofIndex("items[1]");
 * if (result.hasErrorForIdentifier(secondItemId)) {
 *     System.out.println("Second item is invalid");
 * }
 * }</pre>
 *
 * <p><b>Custom Validation Contexts:</b>
 * <pre>{@code
 * // Custom identifiers for computed values or business rules
 * ValidationIdentifier passwordStrengthId = ValidationIdentifier.ofCustom("passwordStrength");
 * ValidationIdentifier businessRuleId = ValidationIdentifier.ofCustom("creditLimitCheck");
 * ValidationIdentifier computedValueId = ValidationIdentifier.ofCustom("totalPrice");
 *
 * // Use with custom validation logic
 * ValidationResult result = Validator.of(user)
 *     .property(passwordStrengthId, u -> calculatePasswordStrength(u.getPassword()))
 *         .validate(NumberValidationRules.min(3)) // Minimum strength level
 *         .end()
 *     .property(businessRuleId, u -> u.getRequestedCreditLimit())
 *         .validate(amount -> amount <= calculateMaxCreditLimit(u))
 *         .end()
 *     .getResult();
 * }</pre>
 *
 * <p><b>Cross-Field Validation Dependencies:</b>
 * <pre>{@code
 * public class PasswordValidation {
 *     public static ValidationResult validatePasswords(String password, String confirmPassword) {
 *         ValidationIdentifier passwordId = ValidationIdentifier.ofField("password");
 *         ValidationIdentifier confirmId = ValidationIdentifier.ofField("confirmPassword");
 *
 *         ValidationResult result = new ValidationResult();
 *
 *         // Validate password first
 *         StringValidationRules.notBlank()
 *             .and(StringValidationRules.minLength(8))
 *             .validate(password, result, passwordId);
 *
 *         // Validate confirmation only if password is valid
 *         if (!result.hasErrorForIdentifier(passwordId)) {
 *             CommonValidationRules.isEqual(password)
 *                 .validate(confirmPassword, result, confirmId);
 *         }
 *
 *         return result;
 *     }
 * }
 * }</pre>
 *
 * <p><b>Form Processing with Identifiers:</b>
 * <pre>{@code
 * public class FormValidator {
 *     private static final ValidationIdentifier FIRST_NAME = ValidationIdentifier.ofField("firstName");
 *     private static final ValidationIdentifier LAST_NAME = ValidationIdentifier.ofField("lastName");
 *     private static final ValidationIdentifier EMAIL = ValidationIdentifier.ofField("email");
 *     private static final ValidationIdentifier PHONE = ValidationIdentifier.ofField("phone");
 *
 *     public Map<String, List<String>> validateForm(FormData form) {
 *         ValidationResult result = Validator.of(form)
 *             .property(FIRST_NAME, FormData::getFirstName)
 *                 .validate(StringValidationRules.notBlank())
 *                 .validate(StringValidationRules.minLength(2))
 *                 .end()
 *             .property(LAST_NAME, FormData::getLastName)
 *                 .validate(StringValidationRules.notBlank())
 *                 .validate(StringValidationRules.minLength(2))
 *                 .end()
 *             .property(EMAIL, FormData::getEmail)
 *                 .validate(StringValidationRules.notBlank())
 *                 .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
 *                 .end()
 *             .property(PHONE, FormData::getPhone)
 *                 .validateWhen(phone -> phone != null && !phone.trim().isEmpty(),
 *                              StringValidationRules.matches("\\d{3}-\\d{3}-\\d{4}"))
 *                 .end()
 *             .getResult();
 *
 *         // Convert to field-name -> error-messages map for UI
 *         return convertToFieldErrorMap(result);
 *     }
 *
 *     private Map<String, List<String>> convertToFieldErrorMap(ValidationResult result) {
 *         Map<String, List<String>> fieldErrors = new HashMap<>();
 *
 *         result.getFailuresByIdentifier().forEach((identifier, failures) -> {
 *             List<String> errorMessages = failures.stream()
 *                 .map(failure -> getErrorMessage(failure.getValidationMetadata()))
 *                 .collect(Collectors.toList());
 *             fieldErrors.put(identifier.value(), errorMessages);
 *         });
 *
 *         return fieldErrors;
 *     }
 * }
 * }</pre>
 *
 * <p><b>Dynamic Validation with Computed Identifiers:</b>
 * <pre>{@code
 * public class DynamicValidator {
 *
 *     public ValidationResult validateMap(Map<String, Object> data, Map<String, ValidationRule<Object>> rules) {
 *         ValidationResult result = new ValidationResult();
 *
 *         rules.forEach((fieldName, rule) -> {
 *             ValidationIdentifier identifier = ValidationIdentifier.ofField(fieldName);
 *             Object value = data.get(fieldName);
 *             rule.validate(value, result, identifier);
 *         });
 *
 *         return result;
 *     }
 *
 *     public ValidationResult validateList(List<String> items) {
 *         ValidationResult result = new ValidationResult();
 *
 *         for (int i = 0; i < items.size(); i++) {
 *             ValidationIdentifier identifier = ValidationIdentifier.ofIndex("item[" + i + "]");
 *             StringValidationRules.notBlank().validate(items.get(i), result, identifier);
 *         }
 *
 *         return result;
 *     }
 * }
 * }</pre>
 *
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationResult
 * @see ValidationMetadata
 * @see Validator
 * @see ValidationRule
 */
@EqualsAndHashCode
public class ValidationIdentifier {

    /**
     * Type constant for path-based identifiers (nested object paths).
     */
    private static final byte TYPE_PATH = 1;

    /**
     * Type constant for index-based identifiers (array/collection elements).
     */
    private static final byte TYPE_INDEX = 2;

    /**
     * Type constant for custom identifiers (computed values, business rules).
     */
    private static final byte TYPE_CUSTOM = 3;

    /**
     * Type constant for field-based identifiers (object properties, form fields).
     */
    private static final byte TYPE_FIELD = 4;

    /**
     * The string value that identifies the validation target.
     */
    private final String value;

    /**
     * The type of identifier, determining how the value should be interpreted.
     */
    private final byte type;

    /**
     * Private constructor to enforce use of factory methods.
     *
     * @param value the identifier value
     * @param type the identifier type
     */
    private ValidationIdentifier(String value, byte type) {
        this.value = value;
        this.type = type;
    }

    /**
     * Creates a path-based validation identifier for nested object validation.
     *
     * <p>Path identifiers are typically used for validating nested object properties
     * using dot notation or similar path expressions. They help organize validation
     * results for complex object hierarchies.</p>
     *
     * <p>Common use cases:</p>
     * <ul>
     * <li>Nested object property validation</li>
     * <li>Complex form field grouping</li>
     * <li>Hierarchical data structure validation</li>
     * <li>JSON path-based validation</li>
     * </ul>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Nested object validation
     * ValidationIdentifier streetId = ValidationIdentifier.ofPath("address.street");
     * ValidationIdentifier cityId = ValidationIdentifier.ofPath("address.city");
     * ValidationIdentifier zipId = ValidationIdentifier.ofPath("address.zipCode");
     *
     * // Complex nested paths
     * ValidationIdentifier contactEmailId = ValidationIdentifier.ofPath("contact.primaryEmail");
     * ValidationIdentifier billingAddressId = ValidationIdentifier.ofPath("billing.address.street");
     *
     * // Use in validation
     * ValidationResult result = Validator.of(order)
     *     .property(streetId, o -> o.getShippingAddress().getStreet())
     *         .validate(StringValidationRules.notBlank())
     *         .end()
     *     .property(cityId, o -> o.getShippingAddress().getCity())
     *         .validate(StringValidationRules.notBlank())
     *         .end()
     *     .getResult();
     * }</pre>
     *
     * @param value the path expression identifying the validation target
     * @return a new ValidationIdentifier of path type
     * @throws NullPointerException if value is null
     */
    public static ValidationIdentifier ofPath(String value) {
        return new ValidationIdentifier(value, TYPE_PATH);
    }

    /**
     * Creates an index-based validation identifier for array or collection element validation.
     *
     * <p>Index identifiers are used when validating elements within arrays, lists,
     * or other indexed collections. They typically include the index position to
     * precisely identify which element failed validation.</p>
     *
     * <p>Common use cases:</p>
     * <ul>
     * <li>Array element validation</li>
     * <li>List item validation</li>
     * <li>Collection member validation</li>
     * <li>Tabular data row validation</li>
     * </ul>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Array/List validation
     * List<String> emails = Arrays.asList("valid@email.com", "invalid-email", "another@valid.com");
     * ValidationResult result = new ValidationResult();
     *
     * for (int i = 0; i < emails.size(); i++) {
     *     ValidationIdentifier emailId = ValidationIdentifier.ofIndex("emails[" + i + "]");
     *     StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
     *         .validate(emails.get(i), result, emailId);
     * }
     *
     * // Check specific index
     * ValidationIdentifier secondEmailId = ValidationIdentifier.ofIndex("emails[1]");
     * if (result.hasErrorForIdentifier(secondEmailId)) {
     *     System.out.println("Second email is invalid");
     * }
     *
     * // Matrix/Table validation
     * ValidationIdentifier cellId = ValidationIdentifier.ofIndex("matrix[2][3]");
     * ValidationIdentifier rowId = ValidationIdentifier.ofIndex("table.row[5]");
     * }</pre>
     *
     * @param value the index expression identifying the validation target
     * @return a new ValidationIdentifier of index type
     * @throws NullPointerException if value is null
     */
    public static ValidationIdentifier ofIndex(String value) {
        return new ValidationIdentifier(value, TYPE_INDEX);
    }

    /**
     * Creates a custom validation identifier for specialized or computed validation contexts.
     *
     * <p>Custom identifiers provide flexibility for validation scenarios that don't
     * fit the standard field, path, or index patterns. They're useful for business
     * rules, computed values, cross-field validations, and domain-specific validation logic.</p>
     *
     * <p>Common use cases:</p>
     * <ul>
     * <li>Business rule validation</li>
     * <li>Computed value validation</li>
     * <li>Cross-field dependency validation</li>
     * <li>Domain-specific validation contexts</li>
     * <li>Algorithm or calculation validation</li>
     * </ul>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Business rule validation
     * ValidationIdentifier creditCheckId = ValidationIdentifier.ofCustom("creditLimitCheck");
     * ValidationIdentifier eligibilityId = ValidationIdentifier.ofCustom("loanEligibility");
     *
     * // Computed value validation
     * ValidationIdentifier totalPriceId = ValidationIdentifier.ofCustom("calculatedTotal");
     * ValidationIdentifier discountId = ValidationIdentifier.ofCustom("appliedDiscount");
     *
     * // Cross-field validation
     * ValidationIdentifier passwordMatchId = ValidationIdentifier.ofCustom("passwordConfirmation");
     * ValidationIdentifier dateRangeId = ValidationIdentifier.ofCustom("startEndDateRange");
     *
     * // Use in validation
     * ValidationResult result = Validator.of(loan)
     *     .property(creditCheckId, l -> calculateCreditScore(l.getApplicant()))
     *         .validate(NumberValidationRules.min(650))
     *         .end()
     *     .property(totalPriceId, l -> l.getAmount() + l.getFees())
     *         .validate(NumberValidationRules.max(l.getMaxAllowedAmount()))
     *         .end()
     *     .getResult();
     *
     * // Domain-specific validation
     * ValidationIdentifier inventoryId = ValidationIdentifier.ofCustom("stockAvailability");
     * ValidationIdentifier complianceId = ValidationIdentifier.ofCustom("regulatoryCompliance");
     * }</pre>
     *
     * @param value the custom identifier describing the validation context
     * @return a new ValidationIdentifier of custom type
     * @throws NullPointerException if value is null
     */
    public static ValidationIdentifier ofCustom(String value) {
        return new ValidationIdentifier(value, TYPE_CUSTOM);
    }

    /**
     * Creates a field-based validation identifier for object properties or form fields.
     *
     * <p>Field identifiers are the most commonly used type, representing direct
     * properties of objects or fields in forms. They provide a simple and intuitive
     * way to identify validation targets in typical validation scenarios.</p>
     *
     * <p>Common use cases:</p>
     * <ul>
     * <li>Object property validation</li>
     * <li>Form field validation</li>
     * <li>DTO/Entity field validation</li>
     * <li>Simple validation scenarios</li>
     * </ul>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * // Basic object property validation
     * ValidationIdentifier nameId = ValidationIdentifier.ofField("name");
     * ValidationIdentifier emailId = ValidationIdentifier.ofField("email");
     * ValidationIdentifier ageId = ValidationIdentifier.ofField("age");
     * ValidationIdentifier phoneId = ValidationIdentifier.ofField("phone");
     *
     * // Form field validation
     * ValidationResult result = Validator.of(user)
     *     .property(nameId, User::getName)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.minLength(2))
     *         .validate(StringValidationRules.maxLength(50))
     *         .end()
     *     .property(emailId, User::getEmail)
     *         .validate(StringValidationRules.notBlank())
     *         .validate(StringValidationRules.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
     *         .end()
     *     .property(ageId, User::getAge)
     *         .validate(NumberValidationRules.min(0))
     *         .validate(NumberValidationRules.max(120))
     *         .end()
     *     .getResult();
     *
     * // Constants for reusability
     * public static final ValidationIdentifier USERNAME = ValidationIdentifier.ofField("username");
     * public static final ValidationIdentifier PASSWORD = ValidationIdentifier.ofField("password");
     * }</pre>
     *
     * @param value the field name identifying the validation target
     * @return a new ValidationIdentifier of field type
     * @throws NullPointerException if value is null
     */
    public static ValidationIdentifier ofField(String value) {
        return new ValidationIdentifier(value, TYPE_FIELD);
    }

    /**
     * Returns the string value that identifies the validation target.
     *
     * <p>The value's interpretation depends on the identifier type:</p>
     * <ul>
     * <li><strong>FIELD:</strong> Property or field name (e.g., "name", "email")</li>
     * <li><strong>PATH:</strong> Dot-notation path (e.g., "address.street", "contact.phone")</li>
     * <li><strong>INDEX:</strong> Index expression (e.g., "items[0]", "matrix[1][2]")</li>
     * <li><strong>CUSTOM:</strong> Custom context (e.g., "businessRule", "calculatedValue")</li>
     * </ul>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationIdentifier fieldId = ValidationIdentifier.ofField("username");
     * ValidationIdentifier pathId = ValidationIdentifier.ofPath("user.profile.name");
     * ValidationIdentifier indexId = ValidationIdentifier.ofIndex("items[5]");
     * ValidationIdentifier customId = ValidationIdentifier.ofCustom("passwordStrength");
     *
     * System.out.println(fieldId.value());  // "username"
     * System.out.println(pathId.value());   // "user.profile.name"
     * System.out.println(indexId.value());  // "items[5]"
     * System.out.println(customId.value()); // "passwordStrength"
     *
     * // Use in error reporting
     * ValidationResult result = performValidation();
     * result.getFailuresByIdentifier().forEach((identifier, failures) -> {
     *     System.out.println("Validation failed for: " + identifier.value());
     *     failures.forEach(failure ->
     *         System.out.println("  Error: " + failure.getValidationMetadata().getErrorCode())
     *     );
     * });
     * }</pre>
     *
     * @return the identifier value string
     */
    public String value() {
        return value;
    }

    /**
     * Returns the type of this validation identifier as a byte constant.
     *
     * <p>The type determines how the identifier value should be interpreted
     * and can be used for specialized processing of different identifier types.</p>
     *
     * <p>Type constants:</p>
     * <ul>
     * <li><strong>1 (TYPE_PATH):</strong> Path-based identifier</li>
     * <li><strong>2 (TYPE_INDEX):</strong> Index-based identifier</li>
     * <li><strong>3 (TYPE_CUSTOM):</strong> Custom identifier</li>
     * <li><strong>4 (TYPE_FIELD):</strong> Field-based identifier</li>
     * </ul>
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * ValidationIdentifier fieldId = ValidationIdentifier.ofField("name");
     * ValidationIdentifier pathId = ValidationIdentifier.ofPath("address.street");
     * ValidationIdentifier indexId = ValidationIdentifier.ofIndex("items[0]");
     * ValidationIdentifier customId = ValidationIdentifier.ofCustom("businessRule");
     *
     * System.out.println(fieldId.type());  // 4
     * System.out.println(pathId.type());   // 1
     * System.out.println(indexId.type());  // 2
     * System.out.println(customId.type()); // 3
     *
     * // Type-based processing
     * public void processIdentifier(ValidationIdentifier identifier) {
     *     switch (identifier.type()) {
     *         case 1: // TYPE_PATH
     *             handlePathIdentifier(identifier);
     *             break;
     *         case 2: // TYPE_INDEX
     *             handleIndexIdentifier(identifier);
     *             break;
     *         case 3: // TYPE_CUSTOM
     *             handleCustomIdentifier(identifier);
     *             break;
     *         case 4: // TYPE_FIELD
     *             handleFieldIdentifier(identifier);
     *             break;
     *     }
     * }
     * }</pre>
     *
     * @return the identifier type as a byte constant
     */
    public byte type() {
        return type;
    }
}