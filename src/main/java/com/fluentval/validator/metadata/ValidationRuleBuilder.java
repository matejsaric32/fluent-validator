package com.fluentval.validator.metadata;

import com.fluentval.validator.ValidationResult;
import com.fluentval.validator.ValidationRule;

/**
 * Builder class for enhancing ValidationRule instances with additional metadata configuration.
 * This builder provides a fluent API for wrapping existing validation rules with enhanced
 * metadata properties such as severity levels, categories, validation groups, and blocking behavior.
 *
 * <p>The ValidationRuleBuilder follows the Builder pattern, allowing for flexible and readable
 * configuration of validation rule metadata without modifying the original rule implementation.
 * It creates a new ValidationRule that decorates the original rule with metadata enhancement
 * capabilities.</p>
 *
 * <p>This builder is particularly useful in scenarios where:</p>
 * <ul>
 * <li>Different validation contexts require different severity levels</li>
 * <li>Validation rules need to be categorized for reporting or filtering</li>
 * <li>Conditional validation groups are used based on business context</li>
 * <li>Some validations should be advisory rather than blocking</li>
 * </ul>
 *
 * <p><strong>Usage Example:</strong></p>
 * <pre>{@code
 * ValidationRule<String> enhancedRule = ValidationRuleBuilder
 *     .of(StringValidationRules.notBlank())
 *     .withSeverity(ValidationMetadata.ValidationSeverity.WARNING)
 *     .withCategory("DATA_QUALITY")
 *     .withGroup("OPTIONAL_CHECKS")
 *     .blocking(false)
 *     .build();
 * }</pre>
 *
 * @param <T> the type of value that the validation rule validates
 * @author Matej Šarić
 * @since 1.2.3
 * @see ValidationRule
 * @see ValidationMetadata
 * @see ValidationMetadata.ValidationSeverity
 */
public class ValidationRuleBuilder<T> {

    /**
     * The original validation rule to be enhanced with metadata.
     *
     * <p>This field holds the base validation rule that will be wrapped with
     * metadata enhancement functionality. The original rule's validation logic
     * remains unchanged, but any validation failures it produces will be
     * enhanced with the configured metadata properties.</p>
     */
    private ValidationRule<T> rule;

    /**
     * Optional severity level to apply to validation failures.
     *
     * <p>When set, all validation failures produced by the wrapped rule will
     * have their severity updated to this value. This allows the same base
     * validation rule to be used with different severity levels in different
     * contexts (e.g., strict validation vs. advisory validation).</p>
     *
     * <p><strong>Default:</strong> null (no severity override)</p>
     */
    private ValidationMetadata.ValidationSeverity severity;

    /**
     * Optional category classification to apply to validation failures.
     *
     * <p>When set, all validation failures produced by the wrapped rule will
     * be assigned to this category. Categories provide a way to organize
     * validation failures for reporting, filtering, or specialized handling
     * based on business domains or functional areas.</p>
     *
     * <p><strong>Examples:</strong> "SECURITY", "DATA_FORMAT", "BUSINESS_RULES"</p>
     * <p><strong>Default:</strong> null (no category assignment)</p>
     */
    private String category;

    /**
     * Optional validation group identifier to apply to validation failures.
     *
     * <p>When set, all validation failures produced by the wrapped rule will
     * be assigned to this validation group. Groups enable conditional validation
     * scenarios where different sets of validations are applied based on
     * context, user roles, or processing phases.</p>
     *
     * <p><strong>Examples:</strong> "CREATE", "UPDATE", "ADMIN_ONLY", "BASIC_VALIDATION"</p>
     * <p><strong>Default:</strong> null (no group assignment)</p>
     */
    private String group;

    /**
     * Optional blocking behavior flag to apply to validation failures.
     *
     * <p>When set, all validation failures produced by the wrapped rule will
     * have their blocking behavior updated to this value. This allows the same
     * validation rule to be blocking in some contexts (preventing further processing)
     * and non-blocking in others (allowing processing to continue with warnings).</p>
     *
     * <p><strong>Default:</strong> null (no blocking behavior override)</p>
     */
    private Boolean blocking;

    /**
     * Private constructor to enforce the use of the factory method.
     *
     * <p>This constructor initializes the builder with the base validation rule
     * that will be enhanced. All metadata configuration fields are left in their
     * default null state, indicating no overrides will be applied unless explicitly set.</p>
     *
     * @param rule the base validation rule to enhance
     */
    private ValidationRuleBuilder(ValidationRule<T> rule) {
        this.rule = rule;
    }

    /**
     * Factory method to create a new ValidationRuleBuilder instance.
     *
     * <p>This method provides the entry point for the builder pattern, creating
     * a new builder instance initialized with the specified validation rule.
     * The builder can then be configured with various metadata properties
     * before building the final enhanced validation rule.</p>
     *
     * @param <T> the type of value that the validation rule validates
     * @param rule the base validation rule to enhance with metadata
     * @return a new ValidationRuleBuilder instance for fluent configuration
     * @throws NullPointerException if rule is null
     */
    public static <T> ValidationRuleBuilder<T> of(ValidationRule<T> rule) {
        if (rule == null) {
            throw new NullPointerException("Validation rule cannot be null");
        }
        return new ValidationRuleBuilder<>(rule);
    }

    /**
     * Configures the severity level for validation failures produced by the enhanced rule.
     *
     * <p>This method sets the severity level that will be applied to all validation
     * failures generated by the wrapped validation rule. The severity level affects
     * how validation failures are processed and can influence error handling,
     * user interface presentation, and system response strategies.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Downgrading critical validations to warnings in development environments</li>
     * <li>Upgrading advisory validations to errors in production environments</li>
     * <li>Creating different validation profiles for different user types</li>
     * </ul>
     *
     * @param severity the validation severity level to apply
     * @return this builder instance for method chaining
     */
    public ValidationRuleBuilder<T> withSeverity(ValidationMetadata.ValidationSeverity severity) {
        this.severity = severity;
        return this;
    }

    /**
     * Configures the category classification for validation failures produced by the enhanced rule.
     *
     * <p>This method sets the category that will be applied to all validation failures
     * generated by the wrapped validation rule. Categories provide organizational
     * structure for validation results, enabling filtering, reporting, and specialized
     * handling based on functional domains or business areas.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Grouping security-related validations for audit reporting</li>
     * <li>Separating data format validations from business rule validations</li>
     * <li>Organizing validations by business domain (e.g., FINANCIAL, CUSTOMER_DATA)</li>
     * </ul>
     *
     * @param category the validation category to apply
     * @return this builder instance for method chaining
     */
    public ValidationRuleBuilder<T> withCategory(String category) {
        this.category = category;
        return this;
    }

    /**
     * Configures the validation group for validation failures produced by the enhanced rule.
     *
     * <p>This method sets the validation group that will be applied to all validation
     * failures generated by the wrapped validation rule. Validation groups enable
     * conditional validation scenarios where different sets of validations are
     * activated based on context, processing phase, or user authorization level.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Different validation rules for create vs. update operations</li>
     * <li>Role-based validation where admins have different rules than regular users</li>
     * <li>Phased validation where basic checks run before advanced checks</li>
     * </ul>
     *
     * @param group the validation group identifier to apply
     * @return this builder instance for method chaining
     */
    public ValidationRuleBuilder<T> withGroup(String group) {
        this.group = group;
        return this;
    }

    /**
     * Configures the blocking behavior for validation failures produced by the enhanced rule.
     *
     * <p>This method sets whether validation failures from the wrapped rule should
     * block further processing. Blocking validations halt execution when they fail,
     * while non-blocking validations allow processing to continue, typically recording
     * the failure as a warning or informational message.</p>
     *
     * <p><strong>Use Cases:</strong></p>
     * <ul>
     * <li>Making optional validations non-blocking while keeping required validations blocking</li>
     * <li>Creating different processing modes (strict vs. lenient)</li>
     * <li>Allowing data quality checks to be advisory rather than mandatory</li>
     * </ul>
     *
     * @param blocking true if validation failures should block processing, false otherwise
     * @return this builder instance for method chaining
     */
    public ValidationRuleBuilder<T> blocking(boolean blocking) {
        this.blocking = blocking;
        return this;
    }

    /**
     * Builds and returns the enhanced validation rule with configured metadata properties.
     *
     * <p>This method creates the final ValidationRule instance that wraps the original rule
     * with metadata enhancement capabilities. If no metadata properties have been configured,
     * the original rule is returned unchanged for optimal performance. Otherwise, a new
     * ValidationRule is created that applies the configured metadata to any validation
     * failures produced by the original rule.</p>
     *
     * <p>The enhancement process works by:</p>
     * <ol>
     * <li>Recording the number of existing failures before validation</li>
     * <li>Executing the original validation rule</li>
     * <li>Applying configured metadata to any new failures that were added</li>
     * </ol>
     *
     * <p>This approach ensures that only failures from the wrapped rule are affected,
     * and existing failures in the validation result remain unchanged.</p>
     *
     * @return a ValidationRule enhanced with the configured metadata properties
     */
    public ValidationRule<T> build() {
        ValidationRule<T> configuredRule = rule;

        // Only create a wrapper if metadata configuration is present
        if (severity != null || category != null || group != null || blocking != null) {
            configuredRule = (value, result, identifier) -> {
                // Record the initial number of failures to identify new ones
                int initialFailureCount = result.getFailures().size();

                // Execute the original validation rule
                rule.validate(value, result, identifier);

                // Apply metadata enhancements to any new failures
                for (int i = initialFailureCount; i < result.getFailures().size(); i++) {
                    ValidationResult.Failure failure = result.getFailures().get(i);
                    ValidationMetadata metadata = failure.getValidationMetadata();

                    // Apply configured metadata properties
                    if (severity != null) metadata.setSeverity(severity);
                    if (category != null) metadata.setCategory(category);
                    if (group != null) metadata.setValidationGroup(group);
                    if (blocking != null) metadata.setBlocking(blocking);
                }
            };
        }

        return configuredRule;
    }
}